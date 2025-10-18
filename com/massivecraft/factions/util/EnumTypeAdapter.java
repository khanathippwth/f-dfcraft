/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.TypeAdapter
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.annotations.SerializedName
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
 *  com.google.gson.stream.JsonWriter
 */
package com.massivecraft.factions.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.util.HashMap;
import java.util.Map;

public final class EnumTypeAdapter<T extends Enum<T>>
extends TypeAdapter<T> {
    private final Map<String, T> nameToConstant = new HashMap<String, T>();
    private final Map<T, String> constantToName = new HashMap<T, String>();
    public static final TypeAdapterFactory ENUM_FACTORY = EnumTypeAdapter.newEnumTypeHierarchyFactory();

    public EnumTypeAdapter(Class<T> clazz) {
        try {
            for (Enum enum_ : (Enum[])clazz.getEnumConstants()) {
                String string = enum_.name();
                SerializedName serializedName = clazz.getField(string).getAnnotation(SerializedName.class);
                if (serializedName != null) {
                    string = serializedName.value();
                }
                this.nameToConstant.put(string, enum_);
                this.constantToName.put(enum_, string);
            }
        } catch (NoSuchFieldException noSuchFieldException) {
            // empty catch block
        }
    }

    public T read(JsonReader jsonReader) {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return (T)((Enum)this.nameToConstant.get(jsonReader.nextString()));
    }

    public void write(JsonWriter jsonWriter, T t) {
        jsonWriter.value(t == null ? null : this.constantToName.get(t));
    }

    public static <TT> TypeAdapterFactory newEnumTypeHierarchyFactory() {
        return new TypeAdapterFactory(){

            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                Class clazz = typeToken.getRawType();
                if (!Enum.class.isAssignableFrom(clazz) || clazz == Enum.class) {
                    return null;
                }
                if (!clazz.isEnum()) {
                    clazz = clazz.getSuperclass();
                }
                return new EnumTypeAdapter(clazz);
            }
        };
    }
}

