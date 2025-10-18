/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonParseException
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.util.UUID;
import moss.factions.shade.net.kyori.adventure.key.Key;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.event.HoverEvent;
import moss.factions.shade.net.kyori.adventure.text.serializer.gson.SerializerFactory;
import org.jetbrains.annotations.Nullable;

final class ShowEntitySerializer
extends TypeAdapter<HoverEvent.ShowEntity> {
    static final String TYPE = "type";
    static final String ID = "id";
    static final String NAME = "name";
    private final Gson gson;

    static TypeAdapter<HoverEvent.ShowEntity> create(Gson gson) {
        return new ShowEntitySerializer(gson).nullSafe();
    }

    private ShowEntitySerializer(Gson gson) {
        this.gson = gson;
    }

    public HoverEvent.ShowEntity read(JsonReader jsonReader) {
        jsonReader.beginObject();
        Key key = null;
        UUID uUID = null;
        Component component = null;
        while (jsonReader.hasNext()) {
            String string = jsonReader.nextName();
            if (string.equals(TYPE)) {
                key = (Key)this.gson.fromJson(jsonReader, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (string.equals(ID)) {
                uUID = UUID.fromString(jsonReader.nextString());
                continue;
            }
            if (string.equals(NAME)) {
                component = (Component)this.gson.fromJson(jsonReader, SerializerFactory.COMPONENT_TYPE);
                continue;
            }
            jsonReader.skipValue();
        }
        if (key == null || uUID == null) {
            throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
        }
        jsonReader.endObject();
        return HoverEvent.ShowEntity.of(key, uUID, component);
    }

    public void write(JsonWriter jsonWriter, HoverEvent.ShowEntity showEntity) {
        jsonWriter.beginObject();
        jsonWriter.name(TYPE);
        this.gson.toJson((Object)showEntity.type(), SerializerFactory.KEY_TYPE, jsonWriter);
        jsonWriter.name(ID);
        jsonWriter.value(showEntity.id().toString());
        @Nullable Component component = showEntity.name();
        if (component != null) {
            jsonWriter.name(NAME);
            this.gson.toJson((Object)component, SerializerFactory.COMPONENT_TYPE, jsonWriter);
        }
        jsonWriter.endObject();
    }
}

