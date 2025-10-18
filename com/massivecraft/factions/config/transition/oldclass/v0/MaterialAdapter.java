/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  org.bukkit.Material
 */
package com.massivecraft.factions.config.transition.oldclass.v0;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.massivecraft.factions.util.material.MaterialDb;
import org.bukkit.Material;

public class MaterialAdapter
extends TypeAdapter<Material> {
    public void write(JsonWriter jsonWriter, Material material) {
        jsonWriter.value(material.name());
    }

    public Material read(JsonReader jsonReader) {
        return MaterialDb.get(jsonReader.nextString());
    }
}

