/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag;

import java.util.Objects;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.AbstractTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.PreProcess;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PreProcessTagImpl
extends AbstractTag
implements PreProcess {
    private final String value;

    PreProcessTagImpl(String string) {
        this.value = string;
    }

    @Override
    @NotNull
    public String value() {
        return this.value;
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PreProcessTagImpl)) {
            return false;
        }
        PreProcessTagImpl preProcessTagImpl = (PreProcessTagImpl)object;
        return Objects.equals(this.value, preProcessTagImpl.value);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("value", this.value));
    }
}

