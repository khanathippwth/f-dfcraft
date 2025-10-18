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
import com.massivecraft.factions.data.json.JSONFPlayer;
import java.lang.reflect.Type;

public class OldJSONFPlayerDeserializer
implements JsonDeserializer<JSONFPlayer> {
    public JSONFPlayer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        int n;
        JsonElement jsonElement2 = jsonElement.getAsJsonObject().remove("factionId");
        String string = jsonElement2.getAsString();
        try {
            n = Integer.parseInt(string);
        } catch (NumberFormatException numberFormatException) {
            n = 0;
        }
        jsonElement.getAsJsonObject().addProperty("factionId", (Number)n);
        return (JSONFPlayer)FactionsPlugin.getInstance().getGson().fromJson(jsonElement, JSONFPlayer.class);
    }
}

