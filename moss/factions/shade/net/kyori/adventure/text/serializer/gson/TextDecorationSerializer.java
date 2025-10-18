/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 */
package moss.factions.shade.net.kyori.adventure.text.serializer.gson;

import com.google.gson.TypeAdapter;
import moss.factions.shade.net.kyori.adventure.text.format.TextDecoration;
import moss.factions.shade.net.kyori.adventure.text.serializer.gson.IndexedSerializer;

final class TextDecorationSerializer {
    static final TypeAdapter<TextDecoration> INSTANCE = IndexedSerializer.strict("text decoration", TextDecoration.NAMES);

    private TextDecorationSerializer() {
    }
}

