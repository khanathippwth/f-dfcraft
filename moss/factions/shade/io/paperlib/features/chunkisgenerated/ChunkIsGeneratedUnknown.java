/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package moss.factions.shade.io.paperlib.features.chunkisgenerated;

import moss.factions.shade.io.paperlib.features.chunkisgenerated.ChunkIsGenerated;
import org.bukkit.World;

public class ChunkIsGeneratedUnknown
implements ChunkIsGenerated {
    @Override
    public boolean isChunkGenerated(World world, int n, int n2) {
        return true;
    }
}

