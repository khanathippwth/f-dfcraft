/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.reflect.TypeToken
 */
package com.massivecraft.factions.data.json;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.MemoryBoard;
import com.massivecraft.factions.util.DiscUtil;
import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

public class JSONBoard
extends MemoryBoard {
    private static final transient File file = new File(FactionsPlugin.getInstance().getDataFolder(), "data/board.json");

    public Map<String, Map<String, String>> dumpAsSaveFormat() {
        HashMap<String, Map<String, String>> hashMap = new HashMap<String, Map<String, String>>();
        this.worldTrackers.forEach((string, worldTracker) -> {
            TreeMap treeMap = new TreeMap();
            hashMap.put((String)string, treeMap);
            worldTracker.getChunkToFactionForSave().forEach((l, n) -> {
                int n2 = MemoryBoard.Morton.getX(l);
                int n3 = MemoryBoard.Morton.getZ(l);
                treeMap.put(n2 + "," + n3, String.valueOf(n));
            });
        });
        return hashMap;
    }

    public void loadFromSaveFormat(Map<String, Map<String, String>> map) {
        this.worldTrackers.clear();
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            String string = entry.getKey();
            MemoryBoard.WorldTracker worldTracker = this.getAndCreate(string);
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                int n;
                String[] stringArray = entry2.getKey().trim().split("[,\\s]+");
                int n2 = Integer.parseInt(stringArray[0]);
                int n3 = Integer.parseInt(stringArray[1]);
                try {
                    n = Integer.parseInt(entry2.getValue().trim());
                } catch (NumberFormatException numberFormatException) {
                    FactionsPlugin.getInstance().getLogger().warning("Found invalid faction ID '" + entry2.getValue() + "' in " + string + " at " + entry2.getKey());
                    continue;
                }
                worldTracker.addClaimOnLoad(n, n2, n3);
            }
        }
    }

    @Override
    public void forceSave() {
        this.forceSave(true);
    }

    @Override
    public void forceSave(boolean bl) {
        Map<String, Map<String, String>> map = this.dumpAsSaveFormat();
        DiscUtil.write(file, () -> FactionsPlugin.getInstance().getGson().toJson((Object)map), bl);
    }

    @Override
    public int load() {
        if (!file.exists()) {
            FactionsPlugin.getInstance().getLogger().info("No board to load from disk. Creating new file.");
            this.forceSave();
            return 0;
        }
        try {
            Type type = new TypeToken<Map<String, Map<String, String>>>(this){}.getType();
            Map map = (Map)FactionsPlugin.getInstance().getGson().fromJson(DiscUtil.read(file), type);
            this.loadFromSaveFormat(map);
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to load the board from disk.", exception);
            return 0;
        }
        return this.getTotalCount();
    }

    @Override
    public void convertFrom(MemoryBoard memoryBoard) {
        throw new UnsupportedOperationException();
    }
}

