/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParseException
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
 *  com.google.gson.stream.JsonWriter
 */
package com.massivecraft.factions.perms;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.PermSelectorRegistry;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.perms.selector.PlayerSelector;
import com.massivecraft.factions.perms.selector.RelationSingleSelector;
import com.massivecraft.factions.perms.selector.RoleSingleSelector;
import com.massivecraft.factions.perms.selector.UnknownSelector;
import java.util.UUID;

public class PermSelectorTypeAdapter
extends TypeAdapter<PermSelector> {
    private static boolean legacy = false;

    public static boolean isLegacy() {
        return legacy;
    }

    public void write(JsonWriter jsonWriter, PermSelector permSelector) {
        jsonWriter.value(permSelector.serialize());
    }

    public PermSelector read(JsonReader jsonReader) {
        PermSelector permSelector;
        JsonToken jsonToken = jsonReader.peek();
        if (jsonToken != JsonToken.STRING) {
            throw new JsonParseException("Non-string token found for PermSelector! Found " + jsonToken.name());
        }
        String string = jsonReader.nextString();
        PermSelector permSelector2 = null;
        if (legacy && !string.contains(":")) {
            permSelector2 = this.getLegacy(string);
        }
        if (permSelector2 == null) {
            permSelector2 = PermSelectorRegistry.create(string, true);
        }
        if (permSelector2 instanceof UnknownSelector && (permSelector = this.getLegacy(string)) != null) {
            permSelector2 = permSelector;
            PermSelectorTypeAdapter.setLegacy();
        }
        return permSelector2;
    }

    public static void setLegacy() {
        legacy = true;
    }

    private PermSelector getLegacy(String string) {
        Object object;
        Role role = Role.fromString(string.toUpperCase());
        if (role != null) {
            return new RoleSingleSelector(role);
        }
        try {
            object = Relation.valueOf(string.toUpperCase());
            if (object != Relation.MEMBER) {
                return new RelationSingleSelector((Relation)object);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        try {
            object = UUID.fromString(string);
            return new PlayerSelector((UUID)object);
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }
}

