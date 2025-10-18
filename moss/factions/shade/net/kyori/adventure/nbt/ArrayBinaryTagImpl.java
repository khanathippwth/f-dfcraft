/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.net.kyori.adventure.nbt;

import moss.factions.shade.net.kyori.adventure.nbt.AbstractBinaryTag;
import moss.factions.shade.net.kyori.adventure.nbt.ArrayBinaryTag;

abstract class ArrayBinaryTagImpl
extends AbstractBinaryTag
implements ArrayBinaryTag {
    ArrayBinaryTagImpl() {
    }

    static void checkIndex(int n, int n2) {
        if (n < 0 || n >= n2) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + n);
        }
    }
}

