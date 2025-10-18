/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FlightUtil {
    private static FlightUtil instance;
    private EnemiesTask enemiesTask;

    private FlightUtil() {
        double d;
        double d2 = FactionsPlugin.getInstance().conf().commands().fly().getRadiusCheck() * 20;
        if (d2 > 0.0) {
            this.enemiesTask = new EnemiesTask();
            this.enemiesTask.runTaskTimer((Plugin)FactionsPlugin.getInstance(), 0L, (long)d2);
        }
        if ((d = FactionsPlugin.getInstance().conf().commands().fly().particles().getSpawnRate() * 20.0) > 0.0) {
            new ParticleTrailsTask().runTaskTimer((Plugin)FactionsPlugin.getInstance(), 0L, (long)d);
        }
    }

    public static void start() {
        instance = new FlightUtil();
    }

    public static FlightUtil instance() {
        return instance;
    }

    public boolean enemiesNearby(FPlayer fPlayer, int n) {
        if (this.enemiesTask == null) {
            return false;
        }
        return this.enemiesTask.enemiesNearby(fPlayer, n);
    }

    public static class EnemiesTask
    extends BukkitRunnable {
        public void run() {
            Collection<FPlayer> collection = FPlayers.getInstance().getOnlinePlayers();
            for (Player player : Bukkit.getOnlinePlayers()) {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                if (!fPlayer.isFlying() || fPlayer.isAdminBypassing() || !this.enemiesNearby(fPlayer, FactionsPlugin.getInstance().conf().commands().fly().getEnemyRadius(), collection)) continue;
                fPlayer.msg(TL.COMMAND_FLY_ENEMY_DISABLE, new Object[0]);
                fPlayer.setFlying(false);
                if (!fPlayer.isAutoFlying()) continue;
                fPlayer.setAutoFlying(false);
            }
        }

        public boolean enemiesNearby(FPlayer fPlayer, int n) {
            return this.enemiesNearby(fPlayer, n, FPlayers.getInstance().getOnlinePlayers());
        }

        public boolean enemiesNearby(FPlayer fPlayer, int n, Collection<FPlayer> collection) {
            if (!FactionsPlugin.getInstance().worldUtil().isEnabled(fPlayer.getPlayer().getWorld())) {
                return false;
            }
            int n2 = n * n;
            Location location = fPlayer.getPlayer().getLocation();
            Location location2 = new Location(location.getWorld(), 0.0, 0.0, 0.0);
            for (FPlayer fPlayer2 : collection) {
                if (fPlayer2 == fPlayer || fPlayer2.isAdminBypassing()) continue;
                fPlayer2.getPlayer().getLocation(location2);
                if (!location2.getWorld().getUID().equals(location.getWorld().getUID()) || !(location2.distanceSquared(location) <= (double)n2) || fPlayer2.getRelationTo(fPlayer) != Relation.ENEMY || !fPlayer.getPlayer().canSee(fPlayer2.getPlayer())) continue;
                return true;
            }
            return false;
        }
    }

    public static class ParticleTrailsTask
    extends BukkitRunnable {
        private final int amount = FactionsPlugin.getInstance().conf().commands().fly().particles().getAmount();
        private final float speed = (float)FactionsPlugin.getInstance().conf().commands().fly().particles().getSpeed();

        private ParticleTrailsTask() {
        }

        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                if (!fPlayer.isFlying() || fPlayer.getFlyTrailsEffect() == null || !Permission.FLY_TRAILS.has((CommandSender)player) || !fPlayer.getFlyTrailsState()) continue;
                Particle particle = FactionsPlugin.getInstance().getParticleProvider().effectFromString(fPlayer.getFlyTrailsEffect());
                FactionsPlugin.getInstance().getParticleProvider().spawn(particle, player.getLocation(), this.amount, this.speed, 0.0, 0.0, 0.0);
            }
        }
    }
}

