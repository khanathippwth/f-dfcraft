/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonParseException
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
 *  com.google.gson.stream.JsonWriter
 */
package moss.factions.shade.net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import moss.factions.shade.net.kyori.adventure.key.Key;
import moss.factions.shade.net.kyori.adventure.nbt.api.BinaryTagHolder;
import moss.factions.shade.net.kyori.adventure.text.event.HoverEvent;
import moss.factions.shade.net.kyori.adventure.text.serializer.gson.SerializerFactory;

final class ShowItemSerializer
extends TypeAdapter<HoverEvent.ShowItem> {
    static final String ID = "id";
    static final String COUNT = "count";
    static final String TAG = "tag";
    private final Gson gson;

    static TypeAdapter<HoverEvent.ShowItem> create(Gson gson) {
        return new ShowItemSerializer(gson).nullSafe();
    }

    private ShowItemSerializer(Gson gson) {
        this.gson = gson;
    }

    public HoverEvent.ShowItem read(JsonReader jsonReader) {
        jsonReader.beginObject();
        Key key = null;
        int n = 1;
        BinaryTagHolder binaryTagHolder = null;
        while (jsonReader.hasNext()) {
            String string = jsonReader.nextName();
            if (string.equals(ID)) {
                key = (Key)this.gson.fromJson(jsonReader, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (string.equals(COUNT)) {
                n = jsonReader.nextInt();
                continue;
            }
            if (string.equals(TAG)) {
                JsonToken jsonToken = jsonReader.peek();
                if (jsonToken == JsonToken.STRING || jsonToken == JsonToken.NUMBER) {
                    binaryTagHolder = BinaryTagHolder.binaryTagHolder(jsonReader.nextString());
                    continue;
                }
                if (jsonToken == JsonToken.BOOLEAN) {
                    binaryTagHolder = BinaryTagHolder.binaryTagHolder(String.valueOf(jsonReader.nextBoolean()));
                    continue;
                }
                if (jsonToken == JsonToken.NULL) {
                    jsonReader.nextNull();
                    continue;
                }
                throw new JsonParseException("Expected tag to be a string");
            }
            jsonReader.skipValue();
        }
        if (key == null) {
            throw new JsonParseException("Not sure how to deserialize show_item hover event");
        }
        jsonReader.endObject();
        return HoverEvent.ShowItem.of(key, n, binaryTagHolder);
    }

    public void write(JsonWriter jsonWriter, HoverEvent.ShowItem showItem) {
        BinaryTagHolder binaryTagHolder;
        jsonWriter.beginObject();
        jsonWriter.name(ID);
        this.gson.toJson((Object)showItem.item(), SerializerFactory.KEY_TYPE, jsonWriter);
        int n = showItem.count();
        if (n != 1) {
            jsonWriter.name(COUNT);
            jsonWriter.value((long)n);
        }
        if ((binaryTagHolder = showItem.nbt()) != null) {
            jsonWriter.name(TAG);
            jsonWriter.value(binaryTagHolder.string());
        }
        jsonWriter.endObject();
    }
}

