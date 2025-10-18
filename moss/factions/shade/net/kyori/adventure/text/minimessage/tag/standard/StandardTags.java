/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import moss.factions.shade.net.kyori.adventure.text.format.TextDecoration;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.ClickTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.ColorTagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.DecorationTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.FontTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.GradientTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.HoverTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.InsertionTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.KeybindTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.NbtTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.NewlineTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.RainbowTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.ResetTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.ScoreTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.SelectorTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.TransitionTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.TranslatableFallbackTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.TranslatableTag;
import org.jetbrains.annotations.NotNull;

public final class StandardTags {
    private static final TagResolver ALL = TagResolver.builder().resolvers(HoverTag.RESOLVER, ClickTag.RESOLVER, ColorTagResolver.INSTANCE, KeybindTag.RESOLVER, TranslatableTag.RESOLVER, TranslatableFallbackTag.RESOLVER, InsertionTag.RESOLVER, FontTag.RESOLVER, DecorationTag.RESOLVER, GradientTag.RESOLVER, RainbowTag.RESOLVER, ResetTag.RESOLVER, NewlineTag.RESOLVER, TransitionTag.RESOLVER, SelectorTag.RESOLVER, ScoreTag.RESOLVER, NbtTag.RESOLVER).build();

    private StandardTags() {
    }

    @NotNull
    public static TagResolver decorations(@NotNull TextDecoration textDecoration) {
        return Objects.requireNonNull(DecorationTag.RESOLVERS.get(textDecoration), "No resolver found for decoration (this should not be possible?)");
    }

    @NotNull
    public static TagResolver decorations() {
        return DecorationTag.RESOLVER;
    }

    @NotNull
    public static TagResolver color() {
        return ColorTagResolver.INSTANCE;
    }

    @NotNull
    public static TagResolver hoverEvent() {
        return HoverTag.RESOLVER;
    }

    @NotNull
    public static TagResolver clickEvent() {
        return ClickTag.RESOLVER;
    }

    @NotNull
    public static TagResolver keybind() {
        return KeybindTag.RESOLVER;
    }

    @NotNull
    public static TagResolver translatable() {
        return TranslatableTag.RESOLVER;
    }

    @NotNull
    public static TagResolver translatableFallback() {
        return TranslatableFallbackTag.RESOLVER;
    }

    @NotNull
    public static TagResolver insertion() {
        return InsertionTag.RESOLVER;
    }

    @NotNull
    public static TagResolver font() {
        return FontTag.RESOLVER;
    }

    @NotNull
    public static TagResolver gradient() {
        return GradientTag.RESOLVER;
    }

    @NotNull
    public static TagResolver rainbow() {
        return RainbowTag.RESOLVER;
    }

    public static TagResolver transition() {
        return TransitionTag.RESOLVER;
    }

    @NotNull
    public static TagResolver reset() {
        return ResetTag.RESOLVER;
    }

    @NotNull
    public static TagResolver newline() {
        return NewlineTag.RESOLVER;
    }

    @NotNull
    public static TagResolver selector() {
        return SelectorTag.RESOLVER;
    }

    @NotNull
    public static TagResolver score() {
        return ScoreTag.RESOLVER;
    }

    @NotNull
    public static TagResolver nbt() {
        return NbtTag.RESOLVER;
    }

    @NotNull
    public static TagResolver defaults() {
        return ALL;
    }

    static Set<String> names(String ... stringArray) {
        return new HashSet<String>(Arrays.asList(stringArray));
    }
}

