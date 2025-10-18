/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockState
 */
package moss.factions.shade.io.paperlib.features.blockstatesnapshot;

import org.bukkit.block.BlockState;

public class BlockStateSnapshotResult {
    private final boolean isSnapshot;
    private final BlockState state;

    public BlockStateSnapshotResult(boolean bl, BlockState blockState) {
        this.isSnapshot = bl;
        this.state = blockState;
    }

    public boolean isSnapshot() {
        return this.isSnapshot;
    }

    public BlockState getState() {
        return this.state;
    }
}

