/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.nbt;

import moss.factions.shade.net.kyori.adventure.nbt.BinaryTag;
import moss.factions.shade.net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;

abstract class AbstractBinaryTag
implements BinaryTag {
    AbstractBinaryTag() {
    }

    @Override
    @NotNull
    public final String examinableName() {
        return this.type().toString();
    }

    public final String toString() {
        return this.examine(StringExaminer.simpleEscaping());
    }
}

