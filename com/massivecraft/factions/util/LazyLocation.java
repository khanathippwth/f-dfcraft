/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package com.massivecraft.factions.util;

import java.io.Serializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LazyLocation
implements Serializable {
    private static final long serialVersionUID = -6049901271320963314L;
    private transient Location location = null;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public LazyLocation(Location location) {
        this.setLocation(location);
    }

    public LazyLocation(String string, double d, double d2, double d3) {
        this(string, d, d2, d3, 0.0f, 0.0f);
    }

    public LazyLocation(String string, double d, double d2, double d3, float f, float f2) {
        this.worldName = string;
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.yaw = f;
        this.pitch = f2;
    }

    public final Location getLocation() {
        this.initLocation();
        return this.location;
    }

    public final void setLocation(Location location) {
        this.location = location;
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    private void initLocation() {
        if (this.location != null) {
            return;
        }
        World world = Bukkit.getWorld((String)this.worldName);
        if (world == null) {
            return;
        }
        this.location = new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public final String getWorldName() {
        return this.worldName;
    }

    public final double getX() {
        return this.x;
    }

    public final double getY() {
        return this.y;
    }

    public final double getZ() {
        return this.z;
    }

    public final double getPitch() {
        return this.pitch;
    }

    public final double getYaw() {
        return this.yaw;
    }
}

