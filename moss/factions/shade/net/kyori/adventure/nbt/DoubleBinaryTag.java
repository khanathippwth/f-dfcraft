/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.nbt;

import moss.factions.shade.net.kyori.adventure.nbt.BinaryTagType;
import moss.factions.shade.net.kyori.adventure.nbt.BinaryTagTypes;
import moss.factions.shade.net.kyori.adventure.nbt.DoubleBinaryTagImpl;
import moss.factions.shade.net.kyori.adventure.nbt.NumberBinaryTag;
import org.jetbrains.annotations.NotNull;

public interface DoubleBinaryTag
extends NumberBinaryTag {
    @NotNull
    public static DoubleBinaryTag of(double value) {
        return new DoubleBinaryTagImpl(value);
    }

    @NotNull
    default public BinaryTagType<DoubleBinaryTag> type() {
        return BinaryTagTypes.DOUBLE;
    }

    public double value();
}

