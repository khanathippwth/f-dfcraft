/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.format;

import moss.factions.shade.net.kyori.adventure.text.ComponentBuilder;
import moss.factions.shade.net.kyori.adventure.text.ComponentBuilderApplicable;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StyleBuilderApplicable
extends ComponentBuilderApplicable {
    @Contract(mutates="param")
    public void styleApply(@NotNull Style.Builder var1);

    @Override
    default public void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component) {
        component.style(this::styleApply);
    }
}

