/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.nbt;

import moss.factions.shade.net.kyori.adventure.nbt.BinaryTagType;
import moss.factions.shade.net.kyori.adventure.nbt.BinaryTagTypes;
import moss.factions.shade.net.kyori.adventure.nbt.IntBinaryTagImpl;
import moss.factions.shade.net.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.NotNull;

public interface IntBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static IntBinaryTag of(int value) {
        return new IntBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<IntBinaryTag> type() {
        return BinaryTagTypes.INT;
    }

    public int value();
}

