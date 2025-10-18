/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ComponentLike;
import moss.factions.shade.net.kyori.adventure.text.format.StyleBuilderApplicable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.TagPattern;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public final class Placeholder {
    private Placeholder() {
    }

    public static @NotNull TagResolver.Single parsed(@TagPattern @NotNull String string, @NotNull String string2) {
        return TagResolver.resolver(string, Tag.preProcessParsed(string2));
    }

    public static @NotNull TagResolver.Single unparsed(@TagPattern @NotNull String string, @NotNull String string2) {
        Objects.requireNonNull(string2, "value");
        return Placeholder.component(string, Component.text(string2));
    }

    public static @NotNull TagResolver.Single component(@TagPattern @NotNull String string, @NotNull ComponentLike componentLike) {
        return TagResolver.resolver(string, Tag.selfClosingInserting(componentLike));
    }

    public static @NotNull TagResolver.Single styling(@TagPattern @NotNull String string, @NotNull @NotNull StyleBuilderApplicable @NotNull ... styleBuilderApplicableArray) {
        return TagResolver.resolver(string, Tag.styling(styleBuilderApplicableArray));
    }
}

