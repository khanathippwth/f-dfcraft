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
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.AbstractTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Inserting;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class InsertingImpl
extends AbstractTag
implements Inserting {
    private final boolean allowsChildren;
    private final Component value;

    InsertingImpl(boolean bl, Component component) {
        this.allowsChildren = bl;
        this.value = component;
    }

    @Override
    public boolean allowsChildren() {
        return this.allowsChildren;
    }

    @Override
    @NotNull
    public Component value() {
        return this.value;
    }

    public int hashCode() {
        return Objects.hash(this.allowsChildren, this.value);
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof InsertingImpl)) {
            return false;
        }
        InsertingImpl insertingImpl = (InsertingImpl)object;
        return this.allowsChildren == insertingImpl.allowsChildren && Objects.equals(this.value, insertingImpl.value);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("allowsChildren", this.allowsChildren), ExaminableProperty.of("value", this.value));
    }
}

