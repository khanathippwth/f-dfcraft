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

public class CmdWarp
extends FCommand {
    public CmdWarp() {
        this.aliases.add("warp");
        this.aliases.add("warps");
        this.optionalArgs.put("warp", "warp");
        this.optionalArgs.put("password", "password");
        this.requirements = new CommandRequirements.Builder(Permission.WARP).memberOnly().withAction(PermissibleActions.WARP).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (!commandContext.faction.hasAccess(commandContext.fPlayer, PermissibleActions.WARP, commandContext.fPlayer.getLastStoodAt())) {
            commandContext.msg(TL.COMMAND_FWARP_NOACCESS, commandContext.faction.getTag(commandContext.fPlayer));
            return;
        }
        if (commandContext.args.isEmpty()) {
            WarpGUI warpGUI = new WarpGUI(commandContext.fPlayer, commandContext.faction);
            warpGUI.open();
        } else if (commandContext.args.size() > 2) {
            commandContext.msg(TL.COMMAND_FWARP_COMMANDFORMAT, new Object[0]);
        } else {
            String string = commandContext.argAsString(0);
            String string2 = commandContext.argAsString(1);
            if (commandContext.faction.isWarp(commandContext.argAsString(0))) {
                if (!commandContext.fPlayer.isAdminBypassing() && commandContext.faction.hasWarpPassword(string) && !commandContext.faction.isWarpPassword(string, string2)) {
                    commandContext.fPlayer.msg(TL.COMMAND_FWARP_INVALID_PASSWORD, new Object[0]);
                    return;
                }
                FPlayerTeleportEvent fPlayerTeleportEvent = new FPlayerTeleportEvent(commandContext.fPlayer, commandContext.fPlayer.getFaction().getWarp(string).getLocation(), FPlayerTeleportEvent.PlayerTeleportReason.WARP);
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
                        FactionsPlugin.getInstance().teleport(player, fPlayer.getFaction().getWarp(string).getLocation()).thenAccept(bl -> {
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

