/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class CmdClaimFill
extends FCommand {
    public CmdClaimFill() {
        this.aliases.add("claimfill");
        this.aliases.add("cf");
        this.optionalArgs.put("limit", String.valueOf(FactionsPlugin.getInstance().conf().factions().claims().getFillClaimMaxClaims()));
        this.optionalArgs.put("faction", "you");
        this.requirements = new CommandRequirements.Builder(Permission.CLAIM_FILL).playerOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        int n = commandContext.argAsInt(0, FactionsPlugin.getInstance().conf().factions().claims().getFillClaimMaxClaims());
        if (n > FactionsPlugin.getInstance().conf().factions().claims().getFillClaimMaxClaims()) {
            commandContext.msg(TL.COMMAND_CLAIMFILL_ABOVEMAX, FactionsPlugin.getInstance().conf().factions().claims().getFillClaimMaxClaims());
            return;
        }
        Faction faction = commandContext.argAsFaction(1, commandContext.faction);
        Location location = commandContext.player.getLocation();
        FLocation fLocation = new FLocation(location);
        boolean bl = commandContext.fPlayer.isAdminBypassing();
        Faction faction2 = Board.getInstance().getFactionAt(fLocation);
        if (faction2.equals(faction)) {
            commandContext.msg(TL.CLAIM_ALREADYOWN, faction.describeTo(commandContext.fPlayer, true));
            return;
        }
        if (!bl && !faction2.isWilderness()) {
            commandContext.msg(TL.COMMAND_CLAIMFILL_ALREADYCLAIMED, new Object[0]);
            return;
        }
        if (!bl && (faction.isNormal() && !faction.hasAccess(commandContext.fPlayer, PermissibleActions.TERRITORY, null) || faction.isWarZone() && !Permission.MANAGE_WAR_ZONE.has((CommandSender)commandContext.player) || faction.isSafeZone() && !Permission.MANAGE_SAFE_ZONE.has((CommandSender)commandContext.player))) {
            commandContext.msg(TL.CLAIM_CANTCLAIM, faction.describeTo(commandContext.fPlayer));
            return;
        }
        double d = FactionsPlugin.getInstance().conf().factions().claims().getFillClaimMaxDistance();
        long l = fLocation.getX();
        long l2 = fLocation.getZ();
        LinkedHashSet<FLocation> linkedHashSet = new LinkedHashSet<FLocation>();
        LinkedList<FLocation> linkedList = new LinkedList<FLocation>();
        linkedList.add(fLocation);
        linkedHashSet.add(fLocation);
        while (!linkedList.isEmpty() && linkedHashSet.size() <= n) {
            FLocation fLocation2 = (FLocation)linkedList.poll();
            if ((double)Math.abs(fLocation2.getX() - l) > d || (double)Math.abs(fLocation2.getZ() - l2) > d) {
                commandContext.msg(TL.COMMAND_CLAIMFILL_TOOFAR, d);
                return;
            }
            this.addIf(linkedHashSet, linkedList, fLocation2.getRelative(0, 1), faction2);
            this.addIf(linkedHashSet, linkedList, fLocation2.getRelative(0, -1), faction2);
            this.addIf(linkedHashSet, linkedList, fLocation2.getRelative(1, 0), faction2);
            this.addIf(linkedHashSet, linkedList, fLocation2.getRelative(-1, 0), faction2);
        }
        if (linkedHashSet.size() > n) {
            commandContext.msg(TL.COMMAND_CLAIMFILL_PASTLIMIT, new Object[0]);
            return;
        }
        if (faction.isNormal() && linkedHashSet.size() > this.plugin.getLandRaidControl().getPossibleClaimCount(faction)) {
            commandContext.msg(TL.COMMAND_CLAIMFILL_NOTENOUGHLANDLEFT, faction.describeTo(commandContext.fPlayer), linkedHashSet.size());
            return;
        }
        int n2 = FactionsPlugin.getInstance().conf().factions().claims().getRadiusClaimFailureLimit();
        int n3 = 0;
        for (FLocation fLocation3 : linkedHashSet) {
            if (!commandContext.fPlayer.attemptClaim(faction, fLocation3, true)) {
                ++n3;
            }
            if (n3 < n2) continue;
            commandContext.msg(TL.COMMAND_CLAIMFILL_TOOMUCHFAIL, n3);
            return;
        }
    }

    private void addIf(Set<FLocation> set, Queue<FLocation> queue, FLocation fLocation, Faction faction) {
        if (Board.getInstance().getFactionAt(fLocation) == faction && !set.contains(fLocation)) {
            set.add(fLocation);
            queue.add(fLocation);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CLAIMFILL_DESCRIPTION;
    }
}

