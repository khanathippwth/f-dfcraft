/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.Event
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FactionSetHomeEvent;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public class CmdSethome
extends FCommand {
    public CmdSethome() {
        this.aliases.add("sethome");
        this.requirements = new CommandRequirements.Builder(Permission.SETHOME).memberOnly().withAction(PermissibleActions.SETHOME).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (!FactionsPlugin.getInstance().conf().factions().homes().isEnabled()) {
            commandContext.msg(TL.COMMAND_SETHOME_DISABLED, new Object[0]);
            return;
        }
        if (!Permission.BYPASS.has((CommandSender)commandContext.player) && FactionsPlugin.getInstance().conf().factions().homes().isMustBeInClaimedTerritory() && Board.getInstance().getFactionAt(new FLocation(commandContext.player)) != commandContext.faction) {
            commandContext.msg(TL.COMMAND_SETHOME_NOTCLAIMED, new Object[0]);
            return;
        }
        if (!commandContext.canAffordCommand(FactionsPlugin.getInstance().conf().economy().getCostSethome(), TL.COMMAND_SETHOME_TOSET.toString())) {
            return;
        }
        FactionSetHomeEvent factionSetHomeEvent = new FactionSetHomeEvent(commandContext.fPlayer, commandContext.player.getLocation());
        Bukkit.getServer().getPluginManager().callEvent((Event)factionSetHomeEvent);
        if (factionSetHomeEvent.isCancelled()) {
            return;
        }
        if (!commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostSethome(), TL.COMMAND_SETHOME_TOSET, TL.COMMAND_SETHOME_FORSET)) {
            return;
        }
        commandContext.faction.setHome(commandContext.player.getLocation());
        commandContext.faction.msg(TL.COMMAND_SETHOME_SET, commandContext.fPlayer.describeTo(commandContext.faction, true));
        commandContext.faction.sendMessage(FCmdRoot.getInstance().cmdHome.getUsageTemplate(commandContext));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETHOME_DESCRIPTION;
    }
}

