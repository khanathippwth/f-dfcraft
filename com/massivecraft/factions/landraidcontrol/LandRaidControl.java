/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.landraidcontrol;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.landraidcontrol.PowerControl;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TL;
import java.util.stream.Stream;
import org.bukkit.entity.Player;

public interface LandRaidControl {
    public static LandRaidControl getByName(String name) {
        return switch (name.toLowerCase()) {
            case "dtr" -> new DTRControl();
            default -> new PowerControl();
        };
    }

    public boolean isRaidable(Faction var1);

    public boolean hasLandInflation(Faction var1);

    public int getLandLimit(Faction var1);

    default public int getPossibleClaimCount(Faction faction) {
        return this.getLandLimit(faction) - faction.getLandRounded();
    }

    public boolean canJoinFaction(Faction var1, FPlayer var2, CommandContext var3);

    public boolean canLeaveFaction(FPlayer var1);

    public boolean canDisbandFaction(Faction var1, CommandContext var2);

    public boolean canKick(FPlayer var1, CommandContext var2);

    public void onRespawn(FPlayer var1);

    public void onDeath(Player var1);

    public void onQuit(FPlayer var1);

    public void onJoin(FPlayer var1);

    public void update(FPlayer var1);

    default public void announceRaidable(Faction faction) {
        if (FactionsPlugin.getInstance().conf().factions().landRaidControl().isAnnounceRaidable()) {
            Stream<FPlayer> stream = FPlayers.getInstance().getOnlinePlayers().stream();
            if (FactionsPlugin.getInstance().conf().factions().landRaidControl().isAnnounceToEnemyOnly()) {
                stream = stream.filter(fp -> fp.getFaction() == faction || fp.getRelationTo(faction) == Relation.ENEMY);
            }
            stream.forEach(fp -> fp.sendMessage(TL.RAIDABLE_NOWRAIDABLE.format(faction.getTag((FPlayer)fp))));
        }
    }

    default public void announceNotRaidable(Faction faction) {
        if (FactionsPlugin.getInstance().conf().factions().landRaidControl().isAnnounceRaidable()) {
            Stream<FPlayer> stream = FPlayers.getInstance().getOnlinePlayers().stream();
            if (FactionsPlugin.getInstance().conf().factions().landRaidControl().isAnnounceToEnemyOnly()) {
                stream = stream.filter(fp -> fp.getFaction() == faction || fp.getRelationTo(faction) == Relation.ENEMY);
            }
            stream.forEach(fp -> fp.sendMessage(TL.RAIDABLE_NOLONGERRAIDABLE.format(faction.getTag((FPlayer)fp))));
        }
    }
}

