/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.TypeAdapter
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
 *  com.google.gson.stream.JsonWriter
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import moss.factions.shade.net.kyori.adventure.key.Key;
import moss.factions.shade.net.kyori.adventure.text.BlockNBTComponent;
import moss.factions.shade.net.kyori.adventure.text.BuildableComponent;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ComponentLike;
import moss.factions.shade.net.kyori.adventure.text.EntityNBTComponent;
import moss.factions.shade.net.kyori.adventure.text.KeybindComponent;
import moss.factions.shade.net.kyori.adventure.text.NBTComponent;
import moss.factions.shade.net.kyori.adventure.text.NBTComponentBuilder;
import moss.factions.shade.net.kyori.adventure.text.ScoreComponent;
import moss.factions.shade.net.kyori.adventure.text.SelectorComponent;
import moss.factions.shade.net.kyori.adventure.text.StorageNBTComponent;
import moss.factions.shade.net.kyori.adventure.text.TextComponent;
import moss.factions.shade.net.kyori.adventure.text.TranslatableComponent;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.serializer.gson.SerializerFactory;
import moss.factions.shade.net.kyori.adventure.util.Buildable;
import org.jetbrains.annotations.Nullable;

final class ComponentSerializerImpl
extends TypeAdapter<Component> {
    static final String TEXT = "text";
    static final String TRANSLATE = "translate";
    static final String TRANSLATE_FALLBACK = "fallback";
    static final String TRANSLATE_WITH = "with";
    static final String SCORE = "score";
    static final String SCORE_NAME = "name";
    static final String SCORE_OBJECTIVE = "objective";
    static final String SCORE_VALUE = "value";
    static final String SELECTOR = "selector";
    static final String KEYBIND = "keybind";
    static final String EXTRA = "extra";
    static final String NBT = "nbt";
    static final String NBT_INTERPRET = "interpret";
    static final String NBT_BLOCK = "block";
    static final String NBT_ENTITY = "entity";
    static final String NBT_STORAGE = "storage";
    static final String SEPARATOR = "separator";
    static final Type COMPONENT_LIST_TYPE = new TypeToken<List<Component>>(){}.getType();
    private final Gson gson;

    static TypeAdapter<Component> create(Gson gson) {
        return new ComponentSerializerImpl(gson).nullSafe();
    }

    private ComponentSerializerImpl(Gson gson) {
        this.gson = gson;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public BuildableComponent<?, ?> read(JsonReader jsonReader) {
        Object object;
        JsonToken jsonToken = jsonReader.peek();
        if (jsonToken == JsonToken.STRING || jsonToken == JsonToken.NUMBER || jsonToken == JsonToken.BOOLEAN) {
            return Component.text(ComponentSerializerImpl.readString(jsonReader));
        }
        if (jsonToken == JsonToken.BEGIN_ARRAY) {
            void var3_4;
            Object var3_3 = null;
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Object object2 = this.read(jsonReader);
                if (var3_4 == null) {
                    Buildable.Builder builder = object2.toBuilder();
                    continue;
                }
                var3_4.append((Component)object2);
            }
            if (var3_4 == null) {
                throw ComponentSerializerImpl.notSureHowToDeserialize(jsonReader.getPath());
            }
            jsonReader.endArray();
            return var3_4.build();
        }
        if (jsonToken != JsonToken.BEGIN_OBJECT) {
            throw ComponentSerializerImpl.notSureHowToDeserialize(jsonReader.getPath());
        }
        JsonObject jsonObject = new JsonObject();
        List list = Collections.emptyList();
        String string = null;
        String string2 = null;
        String string3 = null;
        List list2 = null;
        String string4 = null;
        String string5 = null;
        String string6 = null;
        String string7 = null;
        String string8 = null;
        String string9 = null;
        boolean bl = false;
        BlockNBTComponent.Pos pos = null;
        String string10 = null;
        Key key = null;
        Object object3 = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            object = jsonReader.nextName();
            if (((String)object).equals(TEXT)) {
                string = ComponentSerializerImpl.readString(jsonReader);
                continue;
            }
            if (((String)object).equals(TRANSLATE)) {
                string2 = jsonReader.nextString();
                continue;
            }
            if (((String)object).equals(TRANSLATE_FALLBACK)) {
                string3 = jsonReader.nextString();
                continue;
            }
            if (((String)object).equals(TRANSLATE_WITH)) {
                list2 = (List)this.gson.fromJson(jsonReader, COMPONENT_LIST_TYPE);
                continue;
            }
            if (((String)object).equals(SCORE)) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String string11 = jsonReader.nextName();
                    if (string11.equals(SCORE_NAME)) {
                        string4 = jsonReader.nextString();
                        continue;
                    }
                    if (string11.equals(SCORE_OBJECTIVE)) {
                        string5 = jsonReader.nextString();
                        continue;
                    }
                    if (string11.equals(SCORE_VALUE)) {
                        string6 = jsonReader.nextString();
                        continue;
                    }
                    jsonReader.skipValue();
                }
                if (string4 == null || string5 == null) {
                    throw new JsonParseException("A score component requires a name and objective");
                }
                jsonReader.endObject();
                continue;
            }
            if (((String)object).equals(SELECTOR)) {
                string7 = jsonReader.nextString();
                continue;
            }
            if (((String)object).equals(KEYBIND)) {
                string8 = jsonReader.nextString();
                continue;
            }
            if (((String)object).equals(NBT)) {
                string9 = jsonReader.nextString();
                continue;
            }
            if (((String)object).equals(NBT_INTERPRET)) {
                bl = jsonReader.nextBoolean();
                continue;
            }
            if (((String)object).equals(NBT_BLOCK)) {
                pos = (BlockNBTComponent.Pos)this.gson.fromJson(jsonReader, SerializerFactory.BLOCK_NBT_POS_TYPE);
                continue;
            }
            if (((String)object).equals(NBT_ENTITY)) {
                string10 = jsonReader.nextString();
                continue;
            }
            if (((String)object).equals(NBT_STORAGE)) {
                key = (Key)this.gson.fromJson(jsonReader, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (((String)object).equals(EXTRA)) {
                list = (List)this.gson.fromJson(jsonReader, COMPONENT_LIST_TYPE);
                continue;
            }
            if (((String)object).equals(SEPARATOR)) {
                object3 = this.read(jsonReader);
                continue;
            }
            jsonObject.add((String)object, (JsonElement)this.gson.fromJson(jsonReader, JsonElement.class));
        }
        if (string != null) {
            object = Component.text().content(string);
        } else if (string2 != null) {
            object = list2 != null ? Component.translatable().key(string2).fallback(string3).args(list2) : Component.translatable().key(string2).fallback(string3);
        } else if (string4 != null && string5 != null) {
            object = string6 == null ? Component.score().name(string4).objective(string5) : Component.score().name(string4).objective(string5).value(string6);
        } else if (string7 != null) {
            object = Component.selector().pattern(string7).separator((ComponentLike)object3);
        } else if (string8 != null) {
            object = Component.keybind().keybind(string8);
        } else {
            if (string9 == null) throw ComponentSerializerImpl.notSureHowToDeserialize(jsonReader.getPath());
            if (pos != null) {
                object = ComponentSerializerImpl.nbt(Component.blockNBT(), string9, bl, object3).pos(pos);
            } else if (string10 != null) {
                object = ComponentSerializerImpl.nbt(Component.entityNBT(), string9, bl, object3).selector(string10);
            } else {
                if (key == null) throw ComponentSerializerImpl.notSureHowToDeserialize(jsonReader.getPath());
                object = ComponentSerializerImpl.nbt(Component.storageNBT(), string9, bl, object3).storage(key);
            }
        }
        object.style((Style)this.gson.fromJson((JsonElement)jsonObject, SerializerFactory.STYLE_TYPE)).append(list);
        jsonReader.endObject();
        return object.build();
    }

    private static String readString(JsonReader jsonReader) {
        JsonToken jsonToken = jsonReader.peek();
        if (jsonToken == JsonToken.STRING || jsonToken == JsonToken.NUMBER) {
            return jsonReader.nextString();
        }
        if (jsonToken == JsonToken.BOOLEAN) {
            return String.valueOf(jsonReader.nextBoolean());
        }
        throw new JsonParseException("Token of type " + jsonToken + " cannot be interpreted as a string");
    }

    private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(B b, String string, boolean bl, @Nullable Component component) {
        return b.nbtPath(string).interpret(bl).separator(component);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void write(JsonWriter jsonWriter, Component component) {
        Object object;
        jsonWriter.beginObject();
        if (component.hasStyling() && (object = this.gson.toJsonTree((Object)component.style(), SerializerFactory.STYLE_TYPE)).isJsonObject()) {
            for (Map.Entry entry : object.getAsJsonObject().entrySet()) {
                jsonWriter.name((String)entry.getKey());
                this.gson.toJson((JsonElement)entry.getValue(), jsonWriter);
            }
        }
        if (!component.children().isEmpty()) {
            jsonWriter.name(EXTRA);
            this.gson.toJson(component.children(), COMPONENT_LIST_TYPE, jsonWriter);
        }
        if (component instanceof TextComponent) {
            jsonWriter.name(TEXT);
            jsonWriter.value(((TextComponent)component).content());
        } else if (component instanceof TranslatableComponent) {
            object = (TranslatableComponent)component;
            jsonWriter.name(TRANSLATE);
            jsonWriter.value(object.key());
            String string = object.fallback();
            if (string != null) {
                jsonWriter.name(TRANSLATE_FALLBACK);
                jsonWriter.value(string);
            }
            if (!object.args().isEmpty()) {
                jsonWriter.name(TRANSLATE_WITH);
                this.gson.toJson(object.args(), COMPONENT_LIST_TYPE, jsonWriter);
            }
        } else if (component instanceof ScoreComponent) {
            object = (ScoreComponent)component;
            jsonWriter.name(SCORE);
            jsonWriter.beginObject();
            jsonWriter.name(SCORE_NAME);
            jsonWriter.value(object.name());
            jsonWriter.name(SCORE_OBJECTIVE);
            jsonWriter.value(object.objective());
            if (object.value() != null) {
                jsonWriter.name(SCORE_VALUE);
                jsonWriter.value(object.value());
            }
            jsonWriter.endObject();
        } else if (component instanceof SelectorComponent) {
            object = (SelectorComponent)component;
            jsonWriter.name(SELECTOR);
            jsonWriter.value(object.pattern());
            this.serializeSeparator(jsonWriter, object.separator());
        } else if (component instanceof KeybindComponent) {
            jsonWriter.name(KEYBIND);
            jsonWriter.value(((KeybindComponent)component).keybind());
        } else {
            if (!(component instanceof NBTComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(component);
            object = (NBTComponent)component;
            jsonWriter.name(NBT);
            jsonWriter.value(object.nbtPath());
            jsonWriter.name(NBT_INTERPRET);
            jsonWriter.value(object.interpret());
            this.serializeSeparator(jsonWriter, object.separator());
            if (component instanceof BlockNBTComponent) {
                jsonWriter.name(NBT_BLOCK);
                this.gson.toJson((Object)((BlockNBTComponent)component).pos(), SerializerFactory.BLOCK_NBT_POS_TYPE, jsonWriter);
            } else if (component instanceof EntityNBTComponent) {
                jsonWriter.name(NBT_ENTITY);
                jsonWriter.value(((EntityNBTComponent)component).selector());
            } else {
                if (!(component instanceof StorageNBTComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(component);
                jsonWriter.name(NBT_STORAGE);
                this.gson.toJson((Object)((StorageNBTComponent)component).storage(), SerializerFactory.KEY_TYPE, jsonWriter);
            }
        }
        jsonWriter.endObject();
    }

    private void serializeSeparator(JsonWriter jsonWriter, @Nullable Component component) {
        if (component != null) {
            jsonWriter.name(SEPARATOR);
            this.write(jsonWriter, component);
        }
    }

    static JsonParseException notSureHowToDeserialize(Object object) {
        return new JsonParseException("Don't know how to turn " + object + " into a Component");
    }

    private static IllegalArgumentException notSureHowToSerialize(Component component) {
        return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
    }
}

