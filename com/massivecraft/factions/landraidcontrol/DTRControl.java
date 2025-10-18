/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package com.massivecraft.factions.landraidcontrol;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.event.DTRLossEvent;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.landraidcontrol.LandRaidControl;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TL;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class DTRControl
implements LandRaidControl {
    private static FactionsPlugin plugin;

    public static String round(double d) {
        return BigDecimal.valueOf(d).setScale(DTRControl.conf().getDecimalDigits(), RoundingMode.UP).toPlainString();
    }

    private static MainConfig.Factions.LandRaidControl.DTR conf() {
        return plugin.conf().factions().landRaidControl().dtr();
    }

    public DTRControl() {
        plugin = FactionsPlugin.getInstance();
    }

    @Override
    public boolean isRaidable(Faction faction) {
        return this.isRaidable(faction, faction.getDTR());
    }

    public boolean isRaidable(Faction faction, double d) {
        return !faction.isPeaceful() && d <= 0.0;
    }

    @Override
    public boolean hasLandInflation(Faction faction) {
        return false;
    }

    @Override
    public int getLandLimit(Faction faction) {
        return DTRControl.conf().getLandStarting() + faction.getFPlayers().size() * DTRControl.conf().getLandPerPlayer();
    }

    @Override
    public boolean canJoinFaction(Faction faction, FPlayer fPlayer, CommandContext commandContext) {
        if (faction.isFrozenDTR() && DTRControl.conf().isFreezePreventsJoin()) {
            commandContext.msg(TL.DTR_CANNOT_FROZEN, new Object[0]);
            return false;
        }
        return true;
    }

    @Override
    public boolean canLeaveFaction(FPlayer fPlayer) {
        if (fPlayer.getFaction().isFrozenDTR() && DTRControl.conf().isFreezePreventsLeave()) {
            fPlayer.msg(TL.DTR_CANNOT_FROZEN, new Object[0]);
            return false;
        }
        return true;
    }

    @Override
    public boolean canDisbandFaction(Faction faction, CommandContext commandContext) {
        if (faction.isFrozenDTR() && DTRControl.conf().isFreezePreventsDisband()) {
            commandContext.msg(TL.DTR_CANNOT_FROZEN, new Object[0]);
            return false;
        }
        return true;
    }

    @Override
    public boolean canKick(FPlayer fPlayer, CommandContext commandContext) {
        if (fPlayer.getFaction().isNormal()) {
            Faction faction = fPlayer.getFaction();
            if (!FactionsPlugin.getInstance().conf().commands().kick().isAllowKickInEnemyTerritory() && Board.getInstance().getFactionAt(fPlayer.getLastStoodAt()).getRelationTo(faction) == Relation.ENEMY) {
                commandContext.msg(TL.COMMAND_KICK_ENEMYTERRITORY, new Object[0]);
                return false;
            }
            if (faction.isFrozenDTR() && DTRControl.conf().getFreezeKickPenalty() > 0.0) {
                faction.setDTR(Math.max(DTRControl.conf().getMinDTR(), faction.getDTR() - DTRControl.conf().getFreezeKickPenalty()));
                commandContext.msg(TL.DTR_KICK_PENALTY, new Object[0]);
            }
        }
        return true;
    }

    @Override
    public void onRespawn(FPlayer fPlayer) {
    }

    @Override
    public void update(FPlayer fPlayer) {
        if (fPlayer.getFaction().isNormal()) {
            this.updateDTR(fPlayer.getFaction());
        }
    }

    @Override
    public void onDeath(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = fPlayer.getFaction();
        if (!faction.isNormal()) {
            return;
        }
        DTRLossEvent dTRLossEvent = new DTRLossEvent(faction, fPlayer);
        if (FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().isNoLossFlag(player)) {
            dTRLossEvent.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent((Event)dTRLossEvent);
        if (!dTRLossEvent.isCancelled()) {
            FPlayer fPlayer2;
            double d = faction.getDTR();
            faction.setDTR(Math.max(DTRControl.conf().getMinDTR(), faction.getDTR() - DTRControl.conf().getLossPerDeath(player.getWorld())));
            double d2 = faction.getDTR() - d;
            double d3 = DTRControl.conf().getVampirism();
            if (player.getKiller() != null && d3 != 0.0 && d2 > 0.0 && faction != (fPlayer2 = FPlayers.getInstance().getByPlayer(player.getKiller())).getFaction()) {
                double d4 = d3 * d2;
                double d5 = fPlayer2.getFaction().getDTR();
                fPlayer2.getFaction().setDTR(Math.min(DTRControl.conf().getMaxDTR(), faction.getDTR() + d4));
                double d6 = fPlayer2.getFaction().getDTR() - d5;
                fPlayer2.msg(TL.DTR_VAMPIRISM_GAIN, d6, fPlayer.describeTo(fPlayer2), fPlayer2.getFaction().getDTR());
            }
            faction.setFrozenDTR(System.currentTimeMillis() + (long)DTRControl.conf().getFreezeTime() * 1000L);
        }
    }

    @Override
    public void onQuit(FPlayer fPlayer) {
        this.update(fPlayer);
    }

    @Override
    public void onJoin(FPlayer fPlayer) {
        if (fPlayer.getFaction().isNormal()) {
            this.updateDTR(fPlayer.getFaction(), 1);
        }
    }

    public void updateDTR(Faction faction) {
        this.updateDTR(faction, 0);
    }

    public void updateDTR(Faction faction, int n) {
        long l = System.currentTimeMillis();
        if (faction.getFrozenDTRUntilTime() > l) {
            return;
        }
        long l2 = l - Math.max(faction.getLastDTRUpdateTime(), faction.getFrozenDTRUntilTime());
        Stream<Player> stream = faction.getOnlinePlayers().stream().filter(player -> plugin.worldUtil().isEnabled(player.getWorld()));
        if (FactionsPlugin.getInstance().conf().plugins().essentialsX().isPreventRegenWhileAfk()) {
            stream = stream.filter(Essentials::isAfk);
        }
        long l3 = stream.count();
        double d = Math.min(DTRControl.conf().getRegainPerMinuteMaxRate(), (double)Math.max(0L, l3 - (long)n) * DTRControl.conf().getRegainPerMinutePerPlayer());
        double d2 = (double)l2 / 60000.0 * d;
        faction.setDTR(Math.min(faction.getDTRWithoutUpdate() + d2, this.getMaxDTR(faction)));
    }

    public double getMaxDTR(Faction faction) {
        return Math.min(DTRControl.conf().getStartingDTR() + DTRControl.conf().getPerPlayer() * (double)faction.getFPlayers().size(), DTRControl.conf().getMaxDTR());
    }

    public void onDTRChange(Faction faction, double d, double d2) {
        boolean bl = this.isRaidable(faction, d);
        boolean bl2 = this.isRaidable(faction, d2);
        if (bl2 && !bl) {
            this.announceRaidable(faction);
        } else if (bl && !bl2) {
            this.announceNotRaidable(faction);
        }
    }
}

