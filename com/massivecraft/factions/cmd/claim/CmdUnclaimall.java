/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class CmdUnclaimall
extends FCommand {
    public CmdUnclaimall() {
        this.aliases.add("unclaimall");
        this.aliases.add("declaimall");
        this.requirements = new CommandRequirements.Builder(Permission.UNCLAIM_ALL).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (!commandContext.faction.hasAccess(commandContext.fPlayer, PermissibleActions.TERRITORY, commandContext.fPlayer.getLastStoodAt())) {
            commandContext.msg(TL.CLAIM_CANTCLAIM, commandContext.faction.describeTo(commandContext.fPlayer));
            return;
        }
        if (Econ.shouldBeUsed()) {
            double d = Econ.calculateTotalLandRefund(commandContext.faction.getLandRounded());
            if (FactionsPlugin.getInstance().conf().economy().isBankEnabled() && FactionsPlugin.getInstance().conf().economy().isBankFactionPaysLandCosts() ? !Econ.modifyMoney(commandContext.faction, d, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString()) : !Econ.modifyMoney(commandContext.fPlayer, d, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
                return;
            }
        }
        LandUnclaimAllEvent landUnclaimAllEvent = new LandUnclaimAllEvent(commandContext.faction, commandContext.fPlayer);
        Bukkit.getServer().getPluginManager().callEvent((Event)landUnclaimAllEvent);
        if (landUnclaimAllEvent.isCancelled()) {
            return;
        }
        Board.getInstance().unclaimAll(commandContext.faction);
        commandContext.faction.msg(TL.COMMAND_UNCLAIMALL_UNCLAIMED, commandContext.fPlayer.describeTo(commandContext.faction, true));
        if (FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
            FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIMALL_LOG.format(commandContext.fPlayer.getName(), commandContext.faction.getTag()));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIMALL_DESCRIPTION;
    }
}

