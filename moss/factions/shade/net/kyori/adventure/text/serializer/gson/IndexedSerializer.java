/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParseException
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 */
package moss.factions.shade.net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import moss.factions.shade.net.kyori.adventure.util.Index;

final class IndexedSerializer<E>
extends TypeAdapter<E> {
    private final String name;
    private final Index<String, E> map;
    private final boolean throwOnUnknownKey;

    public static <E> TypeAdapter<E> strict(String string, Index<String, E> index) {
        return new IndexedSerializer<E>(string, index, true).nullSafe();
    }

    public static <E> TypeAdapter<E> lenient(String string, Index<String, E> index) {
        return new IndexedSerializer<E>(string, index, false).nullSafe();
    }

    private IndexedSerializer(String string, Index<String, E> index, boolean bl) {
        this.name = string;
        this.map = index;
        this.throwOnUnknownKey = bl;
    }

    public void write(JsonWriter jsonWriter, E e) {
        jsonWriter.value(this.map.key(e));
    }

    public E read(JsonReader jsonReader) {
        String string = jsonReader.nextString();
        E e = this.map.value(string);
        if (e != null) {
            return e;
        }
        if (this.throwOnUnknownKey) {
            throw new JsonParseException("invalid " + this.name + ":  " + string);
        }
        return null;
    }
}

