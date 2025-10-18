/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 */
package moss.factions.shade.io.paperlib.features.asyncchunks;

import java.util.concurrent.CompletableFuture;
import moss.factions.shade.io.paperlib.features.asyncchunks.AsyncChunks;
import org.bukkit.Chunk;
import org.bukkit.World;

public class AsyncChunksPaper_15
implements AsyncChunks {
    @Override
    public CompletableFuture<Chunk> getChunkAtAsync(World world, int n, int n2, boolean bl, boolean bl2) {
        return world.getChunkAtAsync(n, n2, bl, bl2);
    }
}

