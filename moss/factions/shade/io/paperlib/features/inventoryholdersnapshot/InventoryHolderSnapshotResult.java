/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.InventoryHolder
 */
package moss.factions.shade.io.paperlib.features.inventoryholdersnapshot;

import org.bukkit.inventory.InventoryHolder;

public class InventoryHolderSnapshotResult {
    private final boolean isSnapshot;
    private final InventoryHolder holder;

    public InventoryHolderSnapshotResult(boolean bl, InventoryHolder inventoryHolder) {
        this.isSnapshot = bl;
        this.holder = inventoryHolder;
    }

    public boolean isSnapshot() {
        return this.isSnapshot;
    }

    public InventoryHolder getHolder() {
        return this.holder;
    }
}

