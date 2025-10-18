/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text;

import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.ComponentLike;
import moss.factions.shade.net.kyori.adventure.text.TranslationArgumentImpl;
import moss.factions.shade.net.kyori.adventure.text.TranslationArgumentLike;
import moss.factions.shade.net.kyori.examination.Examinable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface TranslationArgument
extends TranslationArgumentLike,
Examinable {
    @NotNull
    public static TranslationArgument bool(boolean value) {
        return new TranslationArgumentImpl(value);
    }

    @NotNull
    public static TranslationArgument numeric(@NotNull Number value) {
        return new TranslationArgumentImpl(Objects.requireNonNull(value, "value"));
    }

    @NotNull
    public static TranslationArgument component(@NotNull ComponentLike value) {
        if (value instanceof TranslationArgumentLike) {
            return ((TranslationArgumentLike)value).asTranslationArgument();
        }
        return new TranslationArgumentImpl(Objects.requireNonNull(Objects.requireNonNull(value, "value").asComponent(), "value.asComponent()"));
    }

    @NotNull
    public Object value();

    @Override
    @NotNull
    default public TranslationArgument asTranslationArgument() {
        return this;
    }
}

