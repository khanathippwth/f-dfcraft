/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FPlayerTeleportEvent;
import com.massivecraft.factions.gui.WarpGUI;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.WarmUpUtil;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CmdWarpOther
extends FCommand {
    public CmdWarpOther() {
        this.aliases.add("warpother");
        this.requiredArgs.add("faction");
        this.optionalArgs.put("warp", "warp");
        this.optionalArgs.put("password", "password");
        this.requirements = new CommandRequirements.Builder(Permission.WARP).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (commandContext.args.isEmpty()) {
            commandContext.msg(TL.COMMAND_WARPOTHER_COMMANDFORMAT, new Object[0]);
            return;
        }
        Faction faction = commandContext.argAsFaction(0);
        if (faction == null) {
            commandContext.msg(TL.GENERIC_NOFACTIONMATCH, commandContext.argAsString(0));
            return;
        }
        if (!commandContext.fPlayer.isAdminBypassing() && !faction.hasAccess(commandContext.fPlayer, PermissibleActions.WARP, commandContext.fPlayer.getLastStoodAt())) {
            commandContext.msg(TL.COMMAND_FWARP_NOACCESS, faction.getTag(commandContext.fPlayer));
            return;
        }
        if (commandContext.args.size() == 1) {
            WarpGUI warpGUI = new WarpGUI(commandContext.fPlayer, faction);
            warpGUI.open();
        } else {
            String string = commandContext.argAsString(1);
            String string2 = commandContext.argAsString(2);
            if (faction.isWarp(string)) {
                if (!commandContext.fPlayer.isAdminBypassing() && faction.hasWarpPassword(string) && !faction.isWarpPassword(string, string2)) {
                    commandContext.fPlayer.msg(TL.COMMAND_FWARP_INVALID_PASSWORD, new Object[0]);
                    return;
                }
                FPlayerTeleportEvent fPlayerTeleportEvent = new FPlayerTeleportEvent(commandContext.fPlayer, faction.getWarp(string).getLocation(), FPlayerTeleportEvent.PlayerTeleportReason.WARP);
                Bukkit.getServer().getPluginManager().callEvent((Event)fPlayerTeleportEvent);
                if (fPlayerTeleportEvent.isCancelled()) {
                    return;
                }
                if (!this.transact(commandContext.fPlayer, commandContext)) {
                    return;
                }
                FPlayer fPlayer = commandContext.fPlayer;
                UUID uUID = commandContext.fPlayer.getPlayer().getUniqueId();
                commandContext.doWarmUp(WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, string, () -> {
                    Player player = Bukkit.getPlayer((UUID)uUID);
                    if (player != null) {
                        FactionsPlugin.getInstance().teleport(player, faction.getWarp(string).getLocation()).thenAccept(bl -> {
                            if (bl.booleanValue()) {
                                fPlayer.msg(TL.COMMAND_FWARP_WARPED, string);
                            }
                        });
                    }
                }, this.plugin.conf().commands().warp().getDelay());
            } else {
                commandContext.fPlayer.msg(TL.COMMAND_FWARP_INVALID_WARP, string);
            }
        }
    }

    private boolean transact(FPlayer fPlayer, CommandContext commandContext) {
        return fPlayer.isAdminBypassing() || commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostWarp(), TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FWARP_DESCRIPTION;
    }
}

