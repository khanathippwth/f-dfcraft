/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.event;

import moss.factions.shade.net.kyori.adventure.nbt.api.BinaryTagHolder;
import moss.factions.shade.net.kyori.adventure.text.event.RemovedDataComponentValueImpl;
import moss.factions.shade.net.kyori.examination.Examinable;
import org.jetbrains.annotations.NotNull;

public interface DataComponentValue
extends Examinable {
    public static @NotNull Removed removed() {
        return RemovedDataComponentValueImpl.REMOVED;
    }

    public static interface Removed
    extends DataComponentValue {
    }

    public static interface TagSerializable
    extends DataComponentValue {
        @NotNull
        public BinaryTagHolder asBinaryTag();
    }
}

