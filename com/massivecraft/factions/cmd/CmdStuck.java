/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FPlayerTeleportEvent;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.IntegrationManager;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.util.TL;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CmdStuck
extends FCommand {
    public CmdStuck() {
        this.aliases.add("stuck");
        this.aliases.add("halp!");
        this.requirements = new CommandRequirements.Builder(Permission.STUCK).build();
    }

    @Override
    public void perform(final CommandContext commandContext) {
        final Player player = commandContext.fPlayer.getPlayer();
        final Location location = player.getLocation();
        final FLocation fLocation = commandContext.fPlayer.getLastStoodAt();
        long l = FactionsPlugin.getInstance().conf().commands().stuck().getDelay();
        final int n = FactionsPlugin.getInstance().conf().commands().stuck().getRadius();
        final int n2 = FactionsPlugin.getInstance().conf().commands().stuck().getSearchRadius();
        if (FactionsPlugin.getInstance().getStuckMap().containsKey(player.getUniqueId())) {
            long l2 = FactionsPlugin.getInstance().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String string = DurationFormatUtils.formatDuration(l2, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            commandContext.msg(TL.COMMAND_STUCK_EXISTS, string);
        } else {
            FPlayerTeleportEvent fPlayerTeleportEvent = new FPlayerTeleportEvent(commandContext.fPlayer, null, FPlayerTeleportEvent.PlayerTeleportReason.STUCK);
            Bukkit.getServer().getPluginManager().callEvent((Event)fPlayerTeleportEvent);
            if (fPlayerTeleportEvent.isCancelled()) {
                return;
            }
            if (!commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostStuck(), TL.COMMAND_STUCK_TOSTUCK.format(commandContext.fPlayer.getName()), TL.COMMAND_STUCK_FORSTUCK.format(commandContext.fPlayer.getName()))) {
                return;
            }
            int n3 = new BukkitRunnable(this){

                public void run() {
                    if (!FactionsPlugin.getInstance().getStuckMap().containsKey(player.getUniqueId())) {
                        return;
                    }
                    final World world = fLocation.getWorld();
                    if (world.getUID() != player.getWorld().getUID() || location.distance(player.getLocation()) > (double)n) {
                        commandContext.msg(TL.COMMAND_STUCK_OUTSIDE.format(n), new Object[0]);
                        FactionsPlugin.getInstance().getTimers().remove(player.getUniqueId());
                        FactionsPlugin.getInstance().getStuckMap().remove(player.getUniqueId());
                        return;
                    }
                    final Board board = Board.getInstance();
                    new SpiralTask(new FLocation(commandContext.player), n2){
                        final int buffer;
                        {
                            super(fLocation, n);
                            this.buffer = FactionsPlugin.getInstance().conf().worldBorder().getBuffer();
                        }

                        @Override
                        public boolean work() {
                            FLocation fLocation = this.currentFLocation();
                            if (fLocation.isOutsideWorldBorder(this.buffer)) {
                                return true;
                            }
                            Faction faction = board.getFactionAt(fLocation);
                            if (faction.isWilderness()) {
                                int n = FLocation.chunkToBlock((int)fLocation.getX());
                                int n2 = FLocation.chunkToBlock((int)fLocation.getZ());
                                int n3 = world.getHighestBlockYAt(n, n2);
                                Location location = new Location(world, (double)n, (double)n3, (double)n2);
                                commandContext.msg(TL.COMMAND_STUCK_TELEPORT, location.getBlockX(), location.getBlockY(), location.getBlockZ());
                                FactionsPlugin.getInstance().getTimers().remove(player.getUniqueId());
                                FactionsPlugin.getInstance().getStuckMap().remove(player.getUniqueId());
                                if (FactionsPlugin.getInstance().getIntegrationManager().isEnabled(IntegrationManager.Integration.ESS) && !Essentials.handleTeleport(player, location)) {
                                    FactionsPlugin.getInstance().teleport(player, location);
                                    FactionsPlugin.getInstance().debug("/f stuck used regular teleport, not essentials!");
                                }
                                this.stop();
                                return false;
                            }
                            return true;
                        }

                        @Override
                        public void finish() {
                            commandContext.msg(TL.COMMAND_STUCK_FAILED, new Object[0]);
                        }
                    };
                }
            }.runTaskLater((Plugin)FactionsPlugin.getInstance(), l * 20L).getTaskId();
            FactionsPlugin.getInstance().getTimers().put(player.getUniqueId(), System.currentTimeMillis() + l * 1000L);
            long l3 = FactionsPlugin.getInstance().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String string = DurationFormatUtils.formatDuration(l3, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            commandContext.msg(TL.COMMAND_STUCK_START, string);
            FactionsPlugin.getInstance().getStuckMap().put(player.getUniqueId(), n3);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STUCK_DESCRIPTION;
    }
}

