/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.net.kyori.adventure.nbt;

import moss.factions.shade.net.kyori.adventure.nbt.AbstractBinaryTag;
import moss.factions.shade.net.kyori.adventure.nbt.EndBinaryTag;

final class EndBinaryTagImpl
extends AbstractBinaryTag
implements EndBinaryTag {
    static final EndBinaryTagImpl INSTANCE = new EndBinaryTagImpl();

    EndBinaryTagImpl() {
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public int hashCode() {
        return 0;
    }
}

