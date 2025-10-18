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
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.factions.landraidcontrol.LandRaidControl;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PowerControl
implements LandRaidControl {
    @Override
    public boolean isRaidable(Faction faction) {
        return this.isRaidable(faction, faction.getPowerRounded());
    }

    public boolean isRaidable(Faction faction, int n) {
        return FactionsPlugin.getInstance().conf().factions().landRaidControl().power().isRaidability() && faction.isNormal() && !faction.isPeaceful() && (FactionsPlugin.getInstance().conf().factions().landRaidControl().power().isRaidabilityOnEqualLandAndPower() ? faction.getLandRounded() >= n : faction.getLandRounded() > n);
    }

    @Override
    public boolean hasLandInflation(Faction faction) {
        return !faction.isPeaceful() && faction.getLandRounded() > faction.getPowerRounded();
    }

    @Override
    public int getLandLimit(Faction faction) {
        return faction.getPowerRounded();
    }

    @Override
    public boolean canJoinFaction(Faction faction, FPlayer fPlayer, CommandContext commandContext) {
        if (!FactionsPlugin.getInstance().conf().factions().landRaidControl().power().canLeaveWithNegativePower() && fPlayer.getPower() < 0.0) {
            if (commandContext != null) {
                commandContext.msg(TL.COMMAND_JOIN_NEGATIVEPOWER, fPlayer.describeTo(commandContext.fPlayer, true));
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean canLeaveFaction(FPlayer fPlayer) {
        if (!FactionsPlugin.getInstance().conf().factions().landRaidControl().power().canLeaveWithNegativePower() && fPlayer.getPower() < 0.0) {
            fPlayer.msg(TL.LEAVE_NEGATIVEPOWER, new Object[0]);
            return false;
        }
        return true;
    }

    @Override
    public boolean canDisbandFaction(Faction faction, CommandContext commandContext) {
        return true;
    }

    @Override
    public boolean canKick(FPlayer fPlayer, CommandContext commandContext) {
        if (!FactionsPlugin.getInstance().conf().factions().landRaidControl().power().canLeaveWithNegativePower() && fPlayer.getPower() < 0.0) {
            commandContext.msg(TL.COMMAND_KICK_NEGATIVEPOWER, new Object[0]);
            return false;
        }
        if (fPlayer.isOnline() && !FactionsPlugin.getInstance().conf().commands().kick().isAllowKickInEnemyTerritory() && Board.getInstance().getFactionAt(fPlayer.getLastStoodAt()).getRelationTo(fPlayer.getFaction()) == Relation.ENEMY) {
            commandContext.msg(TL.COMMAND_KICK_ENEMYTERRITORY, new Object[0]);
            return false;
        }
        return true;
    }

    @Override
    public void onRespawn(FPlayer fPlayer) {
        this.update(fPlayer);
    }

    @Override
    public void onQuit(FPlayer fPlayer) {
        this.update(fPlayer);
    }

    @Override
    public void update(FPlayer fPlayer) {
        fPlayer.updatePower();
    }

    @Override
    public void onJoin(FPlayer fPlayer) {
        fPlayer.losePowerFromBeingOffline();
    }

    @Override
    public void onDeath(Player player) {
        String string;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        MainConfig.Factions.LandRaidControl.Power power = FactionsPlugin.getInstance().conf().factions().landRaidControl().power();
        PowerLossEvent powerLossEvent = new PowerLossEvent(faction, fPlayer);
        if (FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().isNoLossFlag(player)) {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_REGION.toString());
            powerLossEvent.setCancelled(true);
        } else if (faction.isWarZone()) {
            if (!power.isWarZonePowerLoss()) {
                powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WARZONE.toString());
                powerLossEvent.setCancelled(true);
            }
            if (power.getWorldsNoPowerLoss().contains(player.getWorld().getName())) {
                powerLossEvent.setMessage(TL.PLAYER_POWER_LOSS_WARZONE.toString());
            }
        } else if (faction.isWilderness() && !power.isWildernessPowerLoss() && !FactionsPlugin.getInstance().conf().factions().protection().getWorldsNoWildernessProtection().contains(player.getWorld().getName())) {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WILDERNESS.toString());
            powerLossEvent.setCancelled(true);
        } else if (power.getWorldsNoPowerLoss().contains(player.getWorld().getName())) {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WORLD.toString());
            powerLossEvent.setCancelled(true);
        } else if (power.isPeacefulMembersDisablePowerLoss() && fPlayer.hasFaction() && fPlayer.getFaction().isPeaceful()) {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_PEACEFUL.toString());
            powerLossEvent.setCancelled(true);
        } else {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOW.toString());
        }
        Bukkit.getPluginManager().callEvent((Event)powerLossEvent);
        fPlayer.onDeath();
        if (!powerLossEvent.isCancelled()) {
            double d = fPlayer.getPower();
            fPlayer.alterPower(-power.getLossPerDeath());
            double d2 = fPlayer.getPower() - d;
            double d3 = power.getVampirism();
            Player player2 = player.getKiller();
            if (player2 != null && d3 != 0.0 && d2 > 0.0) {
                double d4 = d3 * d2;
                FPlayer fPlayer2 = FPlayers.getInstance().getByPlayer(player2);
                fPlayer2.alterPower(d4);
                fPlayer2.msg(TL.PLAYER_POWER_VAMPIRISM_GAIN, d4, fPlayer.describeTo(fPlayer2), fPlayer2.getPowerRounded(), fPlayer2.getPowerMaxRounded());
            }
        }
        if ((string = powerLossEvent.getMessage()) != null && !string.isEmpty()) {
            fPlayer.msg(string, fPlayer.getPowerRounded(), fPlayer.getPowerMaxRounded());
        }
    }

    public void onPowerChange(Faction faction, int n, int n2) {
        boolean bl = this.isRaidable(faction, n);
        boolean bl2 = this.isRaidable(faction, n2);
        if (bl2 && !bl) {
            this.announceRaidable(faction);
        } else if (bl && !bl2) {
            this.announceNotRaidable(faction);
        }
    }
}

