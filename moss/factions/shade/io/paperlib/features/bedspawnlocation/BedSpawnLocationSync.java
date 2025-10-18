/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package moss.factions.shade.io.paperlib.features.bedspawnlocation;

import java.util.concurrent.CompletableFuture;
import moss.factions.shade.io.paperlib.features.bedspawnlocation.BedSpawnLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BedSpawnLocationSync
implements BedSpawnLocation {
    @Override
    public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean bl) {
        return CompletableFuture.completedFuture(player.getBedSpawnLocation());
    }
}

