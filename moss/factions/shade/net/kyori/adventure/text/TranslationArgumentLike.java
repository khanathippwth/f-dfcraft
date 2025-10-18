/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text;

import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ComponentLike;
import moss.factions.shade.net.kyori.adventure.text.TranslationArgument;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TranslationArgumentLike
extends ComponentLike {
    @NotNull
    public TranslationArgument asTranslationArgument();

    @Override
    @NotNull
    default public Component asComponent() {
        return this.asTranslationArgument().asComponent();
    }
}

