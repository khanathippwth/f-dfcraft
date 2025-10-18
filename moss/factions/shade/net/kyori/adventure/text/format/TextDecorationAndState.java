/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.format;

import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.format.StyleBuilderApplicable;
import moss.factions.shade.net.kyori.adventure.text.format.TextDecoration;
import moss.factions.shade.net.kyori.examination.Examinable;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface TextDecorationAndState
extends Examinable,
StyleBuilderApplicable {
    @NotNull
    public TextDecoration decoration();

    public @NotNull TextDecoration.State state();

    @Override
    default public void styleApply(@NotNull Style.Builder style) {
        style.decoration(this.decoration(), this.state());
    }

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("decoration", this.decoration()), ExaminableProperty.of("state", (Object)this.state()));
    }
}

