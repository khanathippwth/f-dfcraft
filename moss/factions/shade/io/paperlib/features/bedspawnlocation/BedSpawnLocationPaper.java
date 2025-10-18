/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package moss.factions.shade.io.paperlib.features.bedspawnlocation;

import java.util.concurrent.CompletableFuture;
import moss.factions.shade.io.paperlib.PaperLib;
import moss.factions.shade.io.paperlib.features.bedspawnlocation.BedSpawnLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BedSpawnLocationPaper
implements BedSpawnLocation {
    @Override
    public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean bl) {
        Location location = player.getPotentialBedLocation();
        if (location == null || location.getWorld() == null) {
            return CompletableFuture.completedFuture(null);
        }
        return PaperLib.getChunkAtAsync(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, false, bl).thenCompose(chunk -> CompletableFuture.completedFuture(player.getBedSpawnLocation()));
    }
}

