/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.earth2me.essentials.AsyncTeleport
 *  com.earth2me.essentials.IEssentials
 *  com.earth2me.essentials.IUser
 *  com.earth2me.essentials.Trade
 *  net.ess3.api.IEssentials
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.integration;

import com.earth2me.essentials.AsyncTeleport;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.Trade;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.listeners.EssentialsListener;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class Essentials {
    private static net.ess3.api.IEssentials essentials;

    public static boolean setup(Plugin plugin) {
        essentials = (net.ess3.api.IEssentials)plugin;
        FactionsPlugin factionsPlugin = FactionsPlugin.getInstance();
        factionsPlugin.getLogger().info("Found and connected to Essentials");
        if (factionsPlugin.conf().factions().other().isDeleteEssentialsHomes()) {
            factionsPlugin.getLogger().info("Based on main.conf will delete Essentials player homes in their old faction when they leave");
            factionsPlugin.getServer().getPluginManager().registerEvents((Listener)new EssentialsListener((IEssentials)essentials), (Plugin)factionsPlugin);
        }
        if (factionsPlugin.conf().factions().homes().isTeleportCommandEssentialsIntegration()) {
            factionsPlugin.getLogger().info("Using Essentials for teleportation");
        }
        return true;
    }

    public static boolean handleTeleport(Player player, Location location) {
        if (!FactionsPlugin.getInstance().conf().factions().homes().isTeleportCommandEssentialsIntegration() || essentials == null) {
            return false;
        }
        AsyncTeleport asyncTeleport = essentials.getUser(player).getAsyncTeleport();
        Trade trade = new Trade(BigDecimal.valueOf(FactionsPlugin.getInstance().conf().economy().getCostHome()), essentials);
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.exceptionally(throwable -> {
            player.sendMessage(String.valueOf(ChatColor.RED) + throwable.getMessage());
            return false;
        });
        asyncTeleport.teleport(location, trade, PlayerTeleportEvent.TeleportCause.PLUGIN, completableFuture);
        return true;
    }

    public static boolean isVanished(Player player) {
        return essentials != null && player != null && essentials.getUser(player).isVanished();
    }

    public static boolean isIgnored(Player player, Player player2) {
        return essentials != null && essentials.getUser(player).isIgnoredPlayer((IUser)essentials.getUser(player2));
    }

    public static boolean isAfk(Player player) {
        return essentials != null && player != null && essentials.getUser(player).isAfk();
    }

    public static boolean isOverBalCap(double d) {
        if (essentials == null) {
            return false;
        }
        return d > essentials.getSettings().getMaxMoney().doubleValue();
    }

    public static Plugin getEssentials() {
        return essentials;
    }
}

