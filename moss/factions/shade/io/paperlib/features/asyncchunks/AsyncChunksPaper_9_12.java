/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.World$ChunkLoadCallback
 */
package moss.factions.shade.io.paperlib.features.asyncchunks;

import java.util.concurrent.CompletableFuture;
import moss.factions.shade.io.paperlib.PaperLib;
import moss.factions.shade.io.paperlib.features.asyncchunks.AsyncChunks;
import org.bukkit.Chunk;
import org.bukkit.World;

public class AsyncChunksPaper_9_12
implements AsyncChunks {
    @Override
    public CompletableFuture<Chunk> getChunkAtAsync(World world, int n, int n2, boolean bl, boolean bl2) {
        CompletableFuture<Chunk> completableFuture = new CompletableFuture<Chunk>();
        if (!bl && PaperLib.getMinecraftVersion() >= 12 && !world.isChunkGenerated(n, n2)) {
            completableFuture.complete(null);
        } else {
            World.ChunkLoadCallback chunkLoadCallback = completableFuture::complete;
            world.getChunkAtAsync(n, n2, chunkLoadCallback);
        }
        return completableFuture;
    }
}

