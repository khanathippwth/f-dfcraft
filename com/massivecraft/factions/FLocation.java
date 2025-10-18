/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.WorldBorder
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.util.MiscUtil;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FLocation
implements Serializable {
    private static final long serialVersionUID = -8292915234027387983L;
    private static final boolean worldBorderSupport;
    private final String worldName;
    private final int x;
    private final int z;

    public FLocation() {
        this("world", 0, 0);
    }

    public FLocation(String string, int n, int n2) {
        this.worldName = string;
        this.x = n;
        this.z = n2;
    }

    public FLocation(Location location) {
        this(location.getWorld().getName(), FLocation.blockToChunk(location.getBlockX()), FLocation.blockToChunk(location.getBlockZ()));
    }

    public FLocation(Chunk chunk) {
        this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public FLocation(Player player) {
        this(player.getLocation());
    }

    public FLocation(FPlayer fPlayer) {
        this(fPlayer.getPlayer());
    }

    public FLocation(Block block) {
        this(block.getLocation());
    }

    public String getWorldName() {
        return this.worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld((String)this.worldName);
    }

    public long getX() {
        return this.x;
    }

    public long getZ() {
        return this.z;
    }

    public String getCoordString() {
        return this.x + "," + this.z;
    }

    public Chunk getChunk() {
        return new Location(this.getWorld(), (double)FLocation.chunkToBlock(this.x), 0.0, (double)FLocation.chunkToBlock(this.z)).getChunk();
    }

    public String toString() {
        return "[" + this.getWorldName() + "," + this.getCoordString() + "]";
    }

    public static FLocation fromString(String string) {
        int n = string.indexOf(44);
        int n2 = 1;
        String string2 = string.substring(n2, n);
        n2 = n + 1;
        n = string.indexOf(44, n2);
        int n3 = Integer.parseInt(string.substring(n2, n));
        int n4 = Integer.parseInt(string.substring(n + 1, string.length() - 1));
        return new FLocation(string2, n3, n4);
    }

    public static int blockToChunk(int n) {
        return n >> 4;
    }

    public static int blockToRegion(int n) {
        return n >> 9;
    }

    public static int chunkToRegion(int n) {
        return n >> 5;
    }

    public static int chunkToBlock(int n) {
        return n << 4;
    }

    public static int regionToBlock(int n) {
        return n << 9;
    }

    public static int regionToChunk(int n) {
        return n << 5;
    }

    public FLocation getRelative(int n, int n2) {
        return new FLocation(this.worldName, this.x + n, this.z + n2);
    }

    public double getDistanceTo(FLocation fLocation) {
        double d = fLocation.x - this.x;
        double d2 = fLocation.z - this.z;
        return Math.sqrt(d * d + d2 * d2);
    }

    public double getDistanceSquaredTo(FLocation fLocation) {
        double d = fLocation.x - this.x;
        double d2 = fLocation.z - this.z;
        return d * d + d2 * d2;
    }

    public boolean isInChunk(Location location) {
        if (location == null) {
            return false;
        }
        Chunk chunk = location.getChunk();
        return location.getWorld().getName().equalsIgnoreCase(this.getWorldName()) && chunk.getX() == this.x && chunk.getZ() == this.z;
    }

    public boolean isOutsideWorldBorder(int n) {
        if (!worldBorderSupport) {
            return false;
        }
        WorldBorder worldBorder = this.getWorld().getWorldBorder();
        Location location = worldBorder.getCenter();
        double d = worldBorder.getSize();
        int n2 = n << 4;
        double d2 = location.getX() - d / 2.0 + (double)n2;
        double d3 = location.getZ() - d / 2.0 + (double)n2;
        double d4 = location.getX() + d / 2.0 - (double)n2;
        double d5 = location.getZ() + d / 2.0 - (double)n2;
        int n3 = this.x << 4;
        int n4 = n3 | 0xF;
        int n5 = this.z << 4;
        int n6 = n5 | 0xF;
        return (double)n3 >= d4 || (double)n5 >= d5 || (double)n4 <= d2 || (double)n6 <= d3;
    }

    public Set<FLocation> getCircle(double d) {
        double d2 = d * d;
        LinkedHashSet<FLocation> linkedHashSet = new LinkedHashSet<FLocation>();
        if (d <= 0.0) {
            return linkedHashSet;
        }
        int n = (int)Math.floor((double)this.x - d);
        int n2 = (int)Math.ceil((double)this.x + d);
        int n3 = (int)Math.floor((double)this.z - d);
        int n4 = (int)Math.ceil((double)this.z + d);
        for (int i = n; i <= n2; ++i) {
            for (int j = n3; j <= n4; ++j) {
                FLocation fLocation = new FLocation(this.worldName, i, j);
                if (!(this.getDistanceSquaredTo(fLocation) <= d2)) continue;
                linkedHashSet.add(fLocation);
            }
        }
        return linkedHashSet;
    }

    public static HashSet<FLocation> getArea(FLocation fLocation, FLocation fLocation2) {
        HashSet<FLocation> hashSet = new HashSet<FLocation>();
        for (long l : MiscUtil.range(fLocation.getX(), fLocation2.getX())) {
            for (long l2 : MiscUtil.range(fLocation.getZ(), fLocation2.getZ())) {
                hashSet.add(new FLocation(fLocation.getWorldName(), (int)l, (int)l2));
            }
        }
        return hashSet;
    }

    public int hashCode() {
        return (this.x << 9) + this.z + (this.worldName != null ? this.worldName.hashCode() : 0);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof FLocation)) {
            return false;
        }
        FLocation fLocation = (FLocation)object;
        return this.x == fLocation.x && this.z == fLocation.z && Objects.equals(this.worldName, fLocation.worldName);
    }

    static {
        boolean bl = false;
        try {
            Class.forName("org.bukkit.WorldBorder");
            bl = true;
        } catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        worldBorderSupport = bl;
    }
}

