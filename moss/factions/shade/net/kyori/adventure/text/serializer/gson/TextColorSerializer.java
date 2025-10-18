/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.util.Locale;
import moss.factions.shade.net.kyori.adventure.text.format.NamedTextColor;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TextColorSerializer
extends TypeAdapter<TextColor> {
    static final TypeAdapter<TextColor> INSTANCE = new TextColorSerializer(false).nullSafe();
    static final TypeAdapter<TextColor> DOWNSAMPLE_COLOR = new TextColorSerializer(true).nullSafe();
    private final boolean downsampleColor;

    private TextColorSerializer(boolean bl) {
        this.downsampleColor = bl;
    }

    public void write(JsonWriter jsonWriter, TextColor textColor) {
        if (textColor instanceof NamedTextColor) {
            jsonWriter.value(NamedTextColor.NAMES.key((NamedTextColor)textColor));
        } else if (this.downsampleColor) {
            jsonWriter.value(NamedTextColor.NAMES.key(NamedTextColor.nearestTo(textColor)));
        } else {
            jsonWriter.value(TextColorSerializer.asUpperCaseHexString(textColor));
        }
    }

    private static String asUpperCaseHexString(TextColor textColor) {
        return String.format(Locale.ROOT, "#%06X", textColor.value());
    }

    @Nullable
    public TextColor read(JsonReader jsonReader) {
        @Nullable TextColor textColor = TextColorSerializer.fromString(jsonReader.nextString());
        if (textColor == null) {
            return null;
        }
        return this.downsampleColor ? NamedTextColor.nearestTo(textColor) : textColor;
    }

    @Nullable
    static TextColor fromString(@NotNull String string) {
        if (string.startsWith("#")) {
            return TextColor.fromHexString(string);
        }
        return NamedTextColor.NAMES.value(string);
    }
}

