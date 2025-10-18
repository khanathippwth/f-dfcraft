/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 */
package com.massivecraft.factions.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MapFLocToStringSetTypeAdapter
implements JsonDeserializer<Map<FLocation, Set<String>>>,
JsonSerializer<Map<FLocation, Set<String>>> {
    public Map<FLocation, Set<String>> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null) {
                return null;
            }
            ConcurrentHashMap<FLocation, Set<String>> concurrentHashMap = new ConcurrentHashMap<FLocation, Set<String>>();
            for (Map.Entry entry : jsonObject.entrySet()) {
                String string = (String)entry.getKey();
                for (Map.Entry entry2 : ((JsonElement)entry.getValue()).getAsJsonObject().entrySet()) {
                    String[] stringArray = ((String)entry2.getKey()).trim().split("[,\\s]+");
                    int n = Integer.parseInt(stringArray[0]);
                    int n2 = Integer.parseInt(stringArray[1]);
                    HashSet<String> hashSet = new HashSet<String>();
                    Iterator iterator = ((JsonElement)entry2.getValue()).getAsJsonArray().iterator();
                    while (iterator.hasNext()) {
                        hashSet.add(((JsonElement)iterator.next()).getAsString());
                    }
                    concurrentHashMap.put(new FLocation(string, n, n2), hashSet);
                }
            }
            return concurrentHashMap;
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Error encountered while deserializing a Map of FLocations to String Sets.", exception);
            return null;
        }
    }

    public JsonElement serialize(Map<FLocation, Set<String>> map, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        try {
            if (map != null) {
                for (Map.Entry<FLocation, Set<String>> entry : map.entrySet()) {
                    FLocation fLocation = entry.getKey();
                    String string = fLocation.getWorldName();
                    Set<String> set = entry.getValue();
                    if (set == null || set.isEmpty()) continue;
                    JsonArray jsonArray = new JsonArray();
                    Iterator<String> iterator = set.iterator();
                    while (iterator.hasNext()) {
                        JsonPrimitive jsonPrimitive = new JsonPrimitive(iterator.next());
                        jsonArray.add((JsonElement)jsonPrimitive);
                    }
                    if (!jsonObject.has(string)) {
                        jsonObject.add(string, (JsonElement)new JsonObject());
                    }
                    jsonObject.get(string).getAsJsonObject().add(fLocation.getCoordString(), (JsonElement)jsonArray);
                }
            }
            return jsonObject;
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Error encountered while serializing a Map of FLocations to String Sets.", exception);
            return jsonObject;
        }
    }
}

