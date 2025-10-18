/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.HashMap;
import java.util.Map;
import moss.factions.shade.net.kyori.adventure.text.format.NamedTextColor;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ColorTagResolver
implements TagResolver,
SerializableResolver.Single {
    private static final String COLOR_3 = "c";
    private static final String COLOR_2 = "colour";
    private static final String COLOR = "color";
    static final TagResolver INSTANCE = new ColorTagResolver();
    private static final StyleClaim<TextColor> STYLE = StyleClaim.claim("color", Style::color, (textColor, tokenEmitter) -> {
        if (textColor instanceof NamedTextColor) {
            tokenEmitter.tag(NamedTextColor.NAMES.key((NamedTextColor)textColor));
        } else {
            tokenEmitter.tag(textColor.asHexString());
        }
    });
    private static final Map<String, TextColor> COLOR_ALIASES = new HashMap<String, TextColor>();

    private static boolean isColorOrAbbreviation(String string) {
        return string.equals(COLOR) || string.equals(COLOR_2) || string.equals(COLOR_3);
    }

    ColorTagResolver() {
    }

    @Override
    @Nullable
    public Tag resolve(@NotNull String string, @NotNull ArgumentQueue argumentQueue, @NotNull Context context) {
        if (!this.has(string)) {
            return null;
        }
        String string2 = ColorTagResolver.isColorOrAbbreviation(string) ? argumentQueue.popOr("Expected to find a color parameter: <name>|#RRGGBB").lowerValue() : string;
        TextColor textColor = ColorTagResolver.resolveColor(string2, context);
        return Tag.styling(textColor);
    }

    @NotNull
    static TextColor resolveColor(@NotNull String string, @NotNull Context context) {
        TextColor textColor = COLOR_ALIASES.containsKey(string) ? COLOR_ALIASES.get(string) : (string.charAt(0) == '#' ? TextColor.fromHexString(string) : (TextColor)NamedTextColor.NAMES.value(string));
        if (textColor == null) {
            throw context.newException(String.format("Unable to parse a color from '%s'. Please use named colours or hex (#RRGGBB) colors.", string));
        }
        return textColor;
    }

    @Override
    public boolean has(@NotNull String string) {
        return ColorTagResolver.isColorOrAbbreviation(string) || TextColor.fromHexString(string) != null || NamedTextColor.NAMES.value(string) != null || COLOR_ALIASES.containsKey(string);
    }

    @Override
    @Nullable
    public StyleClaim<?> claimStyle() {
        return STYLE;
    }

    static {
        COLOR_ALIASES.put("dark_grey", NamedTextColor.DARK_GRAY);
        COLOR_ALIASES.put("grey", NamedTextColor.GRAY);
    }
}

