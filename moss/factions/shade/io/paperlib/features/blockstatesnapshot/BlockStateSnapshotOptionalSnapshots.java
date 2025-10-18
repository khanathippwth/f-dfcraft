/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package moss.factions.shade.io.paperlib.features.blockstatesnapshot;

import moss.factions.shade.io.paperlib.features.blockstatesnapshot.BlockStateSnapshot;
import moss.factions.shade.io.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import org.bukkit.block.Block;

public class BlockStateSnapshotOptionalSnapshots
implements BlockStateSnapshot {
    @Override
    public BlockStateSnapshotResult getBlockState(Block block, boolean bl) {
        return new BlockStateSnapshotResult(bl, block.getState(bl));
    }
}

