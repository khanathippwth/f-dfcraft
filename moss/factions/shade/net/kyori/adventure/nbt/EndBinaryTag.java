/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.nbt;

import moss.factions.shade.net.kyori.adventure.nbt.BinaryTag;
import moss.factions.shade.net.kyori.adventure.nbt.BinaryTagType;
import moss.factions.shade.net.kyori.adventure.nbt.BinaryTagTypes;
import moss.factions.shade.net.kyori.adventure.nbt.EndBinaryTagImpl;
import org.jetbrains.annotations.NotNull;

public interface EndBinaryTag
extends BinaryTag {
    @NotNull
    public static EndBinaryTag get() {
        return EndBinaryTagImpl.INSTANCE;
    }

    @NotNull
    default public BinaryTagType<EndBinaryTag> type() {
        return BinaryTagTypes.END;
    }
}

