/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.reflect.TypeToken
 *  org.bukkit.Material
 */
package com.massivecraft.factions.util.material;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FactionsPlugin;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;

public class MaterialDb {
    private static Map<String, Material> map;

    private MaterialDb() {
    }

    public static Material get(String string) {
        return MaterialDb.get(string, Material.AIR);
    }

    public static Material get(String string, Material material) {
        if (string == null) {
            FactionsPlugin.getInstance().log("Null material name found");
            return material;
        }
        Material material2 = Material.getMaterial((String)string);
        if (material2 == null) {
            material2 = map.get(string.toUpperCase());
        }
        if (material2 == null) {
            FactionsPlugin.getInstance().log(Level.INFO, "Material does not exist: " + string.toUpperCase());
            return material;
        }
        return material2;
    }

    public static void load() {
        InputStreamReader inputStreamReader = new InputStreamReader(FactionsPlugin.getInstance().getResource("materials.json"));
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap hashMap = (HashMap)FactionsPlugin.getInstance().getGson().fromJson((Reader)inputStreamReader, type);
        map = new HashMap<String, Material>();
        for (Material material : Material.values()) {
            map.put(material.name(), material);
        }
        hashMap.forEach((string, string2) -> {
            Material material;
            Material material2 = Material.getMaterial((String)string);
            boolean bl = material2 == null;
            if (bl == ((material = Material.getMaterial((String)string2)) == null)) {
                return;
            }
            map.put(bl ? string : string2, bl ? material : material2);
        });
        FactionsPlugin.getInstance().getLogger().info(String.format("Loaded %s material mappings.", map.size()));
    }
}

