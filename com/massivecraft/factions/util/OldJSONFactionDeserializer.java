/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 */
package com.massivecraft.factions.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.json.JSONFaction;
import java.lang.reflect.Type;

public class OldJSONFactionDeserializer
implements JsonDeserializer<JSONFaction> {
    public JSONFaction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        jsonElement.getAsJsonObject().remove("id");
        return (JSONFaction)FactionsPlugin.getInstance().getGson().fromJson(jsonElement, JSONFaction.class);
    }
}

