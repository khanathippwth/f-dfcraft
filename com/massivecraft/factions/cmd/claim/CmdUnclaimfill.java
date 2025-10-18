/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.Event
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public class CmdUnclaimfill
extends FCommand {
    public CmdUnclaimfill() {
        this.aliases.add("unclaimfill");
        this.aliases.add("ucf");
        this.optionalArgs.put("limit", String.valueOf(FactionsPlugin.getInstance().conf().factions().claims().getFillUnClaimMaxClaims()));
        this.optionalArgs.put("faction", "you");
        this.requirements = new CommandRequirements.Builder(Permission.UNCLAIM_FILL).playerOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        int n = commandContext.argAsInt(0, FactionsPlugin.getInstance().conf().factions().claims().getFillUnClaimMaxClaims());
        if (n > FactionsPlugin.getInstance().conf().factions().claims().getFillUnClaimMaxClaims()) {
            commandContext.msg(TL.COMMAND_UNCLAIMFILL_ABOVEMAX, FactionsPlugin.getInstance().conf().factions().claims().getFillUnClaimMaxClaims());
            return;
        }
        Faction faction = commandContext.argAsFaction(1, commandContext.faction);
        Location location = commandContext.player.getLocation();
        FLocation fLocation = new FLocation(location);
        boolean bl = commandContext.fPlayer.isAdminBypassing();
        Faction faction2 = Board.getInstance().getFactionAt(fLocation);
        if (faction2 != faction) {
            commandContext.msg(TL.COMMAND_UNCLAIMFILL_NOTCLAIMED, new Object[0]);
            return;
        }
        if (!bl && (faction.isNormal() && !faction.hasAccess(commandContext.fPlayer, PermissibleActions.TERRITORY, fLocation) || faction.isWarZone() && !Permission.MANAGE_WAR_ZONE.has((CommandSender)commandContext.player) || faction.isSafeZone() && !Permission.MANAGE_SAFE_ZONE.has((CommandSender)commandContext.player))) {
            commandContext.msg(TL.CLAIM_CANTUNCLAIM, faction.describeTo(commandContext.fPlayer));
            return;
        }
        double d = FactionsPlugin.getInstance().conf().factions().claims().getFillUnClaimMaxDistance();
        long l = fLocation.getX();
        long l2 = fLocation.getZ();
        LinkedHashSet<FLocation> linkedHashSet = new LinkedHashSet<FLocation>();
        LinkedList<FLocation> linkedList = new LinkedList<FLocation>();
        linkedList.add(fLocation);
        linkedHashSet.add(fLocation);
        while (!linkedList.isEmpty() && linkedHashSet.size() <= n) {
            FLocation fLocation2 = (FLocation)linkedList.poll();
            if ((double)Math.abs(fLocation2.getX() - l) > d || (double)Math.abs(fLocation2.getZ() - l2) > d) {
                commandContext.msg(TL.COMMAND_UNCLAIMFILL_TOOFAR, d);
                return;
            }
            this.addIf(linkedHashSet, linkedList, fLocation2.getRelative(0, 1), faction2);
            this.addIf(linkedHashSet, linkedList, fLocation2.getRelative(0, -1), faction2);
            this.addIf(linkedHashSet, linkedList, fLocation2.getRelative(1, 0), faction2);
            this.addIf(linkedHashSet, linkedList, fLocation2.getRelative(-1, 0), faction2);
        }
        if (linkedHashSet.size() > n) {
            commandContext.msg(TL.COMMAND_UNCLAIMFILL_PASTLIMIT, new Object[0]);
            return;
        }
        int n2 = FactionsPlugin.getInstance().conf().factions().claims().getRadiusClaimFailureLimit();
        Tracker tracker = new Tracker();
        long l3 = 0L;
        long l4 = 0L;
        for (FLocation fLocation3 : linkedHashSet) {
            if (this.attemptUnclaim(commandContext, fLocation3, faction2, tracker)) {
                ++tracker.successes;
                l3 += fLocation3.getX();
                l4 += fLocation3.getZ();
            } else {
                ++tracker.fails;
            }
            if (tracker.fails < n2) continue;
            commandContext.msg(TL.COMMAND_UNCLAIMFILL_TOOMUCHFAIL, tracker.fails);
            break;
        }
        if (tracker.successes == 0) {
            commandContext.msg(TL.COMMAND_UNCLAIMFILL_BYPASSCOMPLETE, 0);
            return;
        }
        l3 /= (long)tracker.successes;
        l4 /= (long)tracker.successes;
        if (bl) {
            commandContext.msg(TL.COMMAND_UNCLAIMFILL_BYPASSCOMPLETE, tracker.count());
        } else {
            if (tracker.refund != 0.0) {
                if (FactionsPlugin.getInstance().conf().economy().isBankEnabled() && FactionsPlugin.getInstance().conf().economy().isBankFactionPaysLandCosts()) {
                    Econ.modifyMoney(commandContext.faction, tracker.refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString());
                } else {
                    Econ.modifyMoney(commandContext.fPlayer, tracker.refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString());
                }
            }
            faction2.msg(TL.COMMAND_UNCLAIMFILL_UNCLAIMED, commandContext.fPlayer.describeTo(faction2, true), tracker.count(), l3 + "," + l4);
        }
    }

    private void addIf(Set<FLocation> set, Queue<FLocation> queue, FLocation fLocation, Faction faction) {
        if (Board.getInstance().getFactionAt(fLocation) == faction && !set.contains(fLocation)) {
            set.add(fLocation);
            queue.add(fLocation);
        }
    }

    private boolean attemptUnclaim(CommandContext commandContext, FLocation fLocation, Faction faction, Tracker tracker) {
        if (faction.isSafeZone() || faction.isWarZone()) {
            Board.getInstance().removeAt(fLocation);
            if (FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
                FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIM_LOG.format(commandContext.fPlayer.getName(), fLocation.getCoordString(), faction.getTag()));
            }
            return true;
        }
        if (!commandContext.fPlayer.isAdminBypassing() && !faction.hasAccess(commandContext.fPlayer, PermissibleActions.TERRITORY, fLocation)) {
            commandContext.msg(TL.CLAIM_CANTUNCLAIM, faction.describeTo(commandContext.fPlayer));
            return false;
        }
        LandUnclaimEvent landUnclaimEvent = new LandUnclaimEvent(fLocation, faction, commandContext.fPlayer);
        Bukkit.getServer().getPluginManager().callEvent((Event)landUnclaimEvent);
        if (landUnclaimEvent.isCancelled()) {
            return false;
        }
        if (!commandContext.fPlayer.isAdminBypassing() && Econ.shouldBeUsed()) {
            tracker.refund += Econ.calculateClaimRefund(commandContext.faction.getLandRounded());
        }
        Board.getInstance().removeAt(fLocation);
        if (FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
            FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIM_LOG.format(commandContext.fPlayer.getName(), fLocation.getCoordString(), faction.getTag()));
        }
        return true;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIMFILL_DESCRIPTION;
    }

    private static class Tracker {
        private int successes;
        private int fails;
        private double refund;

        private Tracker() {
        }

        private int count() {
            return this.successes + this.fails;
        }
    }
}

