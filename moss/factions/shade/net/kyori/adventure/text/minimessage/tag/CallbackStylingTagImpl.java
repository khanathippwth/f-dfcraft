/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.AbstractTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Inserting;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

final class CallbackStylingTagImpl
extends AbstractTag
implements Inserting {
    private final Consumer<Style.Builder> styles;

    CallbackStylingTagImpl(Consumer<Style.Builder> consumer) {
        this.styles = consumer;
    }

    @Override
    @NotNull
    public Component value() {
        return Component.text("", Style.style(this.styles));
    }

    public int hashCode() {
        return Objects.hash(this.styles);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CallbackStylingTagImpl)) {
            return false;
        }
        CallbackStylingTagImpl callbackStylingTagImpl = (CallbackStylingTagImpl)object;
        return Objects.equals(this.styles, callbackStylingTagImpl.styles);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("styles", this.styles));
    }
}

