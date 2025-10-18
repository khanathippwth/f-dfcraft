/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.Particle$DustOptions
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.util.particle;

import com.massivecraft.factions.util.particle.ParticleColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BukkitParticleProvider {
    private final Particle fallback = Particle.REDSTONE;

    public void spawn(Particle particle, Location location, int n) {
        location.getWorld().spawnParticle(particle, location, n);
    }

    public void playerSpawn(Player player, Particle particle, Location location, int n) {
        player.spawnParticle(particle, location, n);
    }

    public void spawn(Particle particle, Location location, int n, double d, double d2, double d3, double d4) {
        location.getWorld().spawnParticle(particle, location, n, d2, d3, d4, d);
    }

    public void playerSpawn(Player player, Particle particle, Location location, int n, double d, double d2, double d3, double d4) {
        player.spawnParticle(particle, location, n, d2, d3, d4, d);
    }

    public void spawn(Particle particle, Location location, ParticleColor particleColor) {
        if (particle.getDataType().equals(Particle.DustOptions.class)) {
            location.getWorld().spawnParticle(particle, location, 1, (Object)new Particle.DustOptions(particleColor.getColor(), 1.0f));
        } else if (particle.getDataType() == Void.class) {
            location.getWorld().spawnParticle(particle, location, 1, null);
        }
    }

    public void playerSpawn(Player player, Particle particle, Location location, ParticleColor particleColor) {
        if (particle.getDataType().equals(Particle.DustOptions.class)) {
            player.spawnParticle(particle, location, 1, (Object)new Particle.DustOptions(particleColor.getColor(), 1.5f));
        } else if (particle.getDataType() == Void.class) {
            player.spawnParticle(particle, location, 1, null);
        }
    }

    public Particle effectFromString(String string) {
        for (Particle particle : Particle.values()) {
            if (!particle.name().equalsIgnoreCase(string)) continue;
            return particle;
        }
        return this.fallback;
    }

    public String effectName(Particle particle) {
        return particle.name();
    }
}

