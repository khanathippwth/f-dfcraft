/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 */
package moss.factions.shade.io.paperlib.features.inventoryholdersnapshot;

import moss.factions.shade.io.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshot;
import moss.factions.shade.io.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
import org.bukkit.inventory.Inventory;

public class InventoryHolderSnapshotOptionalSnapshots
implements InventoryHolderSnapshot {
    @Override
    public InventoryHolderSnapshotResult getHolder(Inventory inventory, boolean bl) {
        return new InventoryHolderSnapshotResult(bl, inventory.getHolder(bl));
    }
}

