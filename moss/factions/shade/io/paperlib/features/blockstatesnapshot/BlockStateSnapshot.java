/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package moss.factions.shade.io.paperlib.features.blockstatesnapshot;

import moss.factions.shade.io.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import org.bukkit.block.Block;

public interface BlockStateSnapshot {
    public BlockStateSnapshotResult getBlockState(Block var1, boolean var2);
}

