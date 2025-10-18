/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package com.massivecraft.factions.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.LazyLocation;
import java.lang.reflect.Type;
import java.util.logging.Level;

public class MyLocationTypeAdapter
implements JsonDeserializer<LazyLocation>,
JsonSerializer<LazyLocation> {
    private static final String WORLD = "world";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String YAW = "yaw";
    private static final String PITCH = "pitch";

    public LazyLocation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String string = jsonObject.get(WORLD).getAsString();
            double d = jsonObject.get(X).getAsDouble();
            double d2 = jsonObject.get(Y).getAsDouble();
            double d3 = jsonObject.get(Z).getAsDouble();
            float f = jsonObject.get(YAW).getAsFloat();
            float f2 = jsonObject.get(PITCH).getAsFloat();
            return new LazyLocation(string, d, d2, d3, f, f2);
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Error encountered while deserializing a LazyLocation.", exception);
            return null;
        }
    }

    public JsonElement serialize(LazyLocation lazyLocation, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty(WORLD, lazyLocation.getWorldName());
            jsonObject.addProperty(X, (Number)lazyLocation.getX());
            jsonObject.addProperty(Y, (Number)lazyLocation.getY());
            jsonObject.addProperty(Z, (Number)lazyLocation.getZ());
            jsonObject.addProperty(YAW, (Number)lazyLocation.getYaw());
            jsonObject.addProperty(PITCH, (Number)lazyLocation.getPitch());
            return jsonObject;
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Error encountered while serializing a LazyLocation.", exception);
            return jsonObject;
        }
    }
}

