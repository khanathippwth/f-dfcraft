/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.nbt;

import moss.factions.shade.net.kyori.adventure.nbt.BinaryTagType;
import moss.factions.shade.net.kyori.adventure.nbt.BinaryTagTypes;
import moss.factions.shade.net.kyori.adventure.nbt.NumberBinaryTag;
import moss.factions.shade.net.kyori.adventure.nbt.ShortBinaryTagImpl;
import org.jetbrains.annotations.NotNull;

public interface ShortBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static ShortBinaryTag of(short value) {
        return new ShortBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<ShortBinaryTag> type() {
        return BinaryTagTypes.SHORT;
    }

    public short value();
}

