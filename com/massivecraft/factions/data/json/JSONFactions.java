/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 */
package com.massivecraft.factions.data.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.MemoryFaction;
import com.massivecraft.factions.data.MemoryFactions;
import com.massivecraft.factions.data.json.JSONFaction;
import com.massivecraft.factions.util.DiscUtil;
import com.massivecraft.factions.util.OldJSONFactionDeserializer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONFactions
extends MemoryFactions {
    private final File file;
    private final File nextIdFile;

    public Gson getGson() {
        return FactionsPlugin.getInstance().getGson();
    }

    public File getFile() {
        return this.file;
    }

    public JSONFactions() {
        if (FactionsPlugin.getInstance().getServerUUID() == null) {
            FactionsPlugin.getInstance().grumpException(new RuntimeException());
        }
        this.file = new File(FactionsPlugin.getInstance().getDataFolder(), "data/factions.json");
        this.nextIdFile = new File(FactionsPlugin.getInstance().getDataFolder(), "data/nextFactionId.json");
        this.nextId = 1;
    }

    @Override
    public void forceSave() {
        this.forceSave(true);
    }

    @Override
    public void forceSave(boolean bl) {
        ArrayList<Faction> arrayList = new ArrayList<Faction>(this.factions.values());
        this.saveCore(this.file, arrayList, bl);
        DiscUtil.writeCatch(this.nextIdFile, FactionsPlugin.getInstance().getGson().toJson((Object)new NextId(this.nextId)), bl);
    }

    private boolean saveCore(File file, List<Faction> list, boolean bl) {
        return DiscUtil.writeCatch(file, FactionsPlugin.getInstance().getGson().toJson(list), bl);
    }

    @Override
    public int load() {
        List<JSONFaction> list = this.loadCore();
        if (list != null) {
            list.forEach(jSONFaction -> {
                this.factions.put(jSONFaction.getIntId(), jSONFaction);
                this.updateNextIdForId(jSONFaction.getIntId());
            });
        }
        super.load();
        return this.factions.size();
    }

    private List<JSONFaction> loadCore() {
        if (!this.file.exists()) {
            return null;
        }
        String string = DiscUtil.readCatch(this.file);
        if (string == null || string.trim().isEmpty()) {
            return null;
        }
        this.nextId = 1;
        if (string.startsWith("{")) {
            Gson gson = FactionsPlugin.getInstance().getGsonBuilder(false).registerTypeAdapter(JSONFaction.class, (Object)new OldJSONFactionDeserializer()).create();
            Map map = (Map)gson.fromJson(string, new TypeToken<Map<String, JSONFaction>>(this){}.getType());
            Faction faction = (Faction)map.remove("```storage``");
            if (faction != null) {
                this.nextId = Math.max(this.nextId, faction.getMaxVaults());
            }
            for (Map.Entry entry : map.entrySet()) {
                String string2 = (String)entry.getKey();
                MemoryFaction memoryFaction = (MemoryFaction)entry.getValue();
                memoryFaction.checkPerms();
                memoryFaction.setId(string2);
                this.updateNextIdForId(string2);
            }
            return new ArrayList<JSONFaction>(map.values());
        }
        String string3 = DiscUtil.readCatch(this.nextIdFile);
        NextId nextId = (NextId)((Object)FactionsPlugin.getInstance().getGson().fromJson(string3, NextId.class));
        if (nextId != null) {
            this.nextId = nextId.next();
        }
        return (List)FactionsPlugin.getInstance().getGson().fromJson(string, new TypeToken<List<JSONFaction>>(this){}.getType());
    }

    public String getNextId() {
        while (!this.isIdFree(this.nextId)) {
            ++this.nextId;
        }
        return Integer.toString(this.nextId++);
    }

    public boolean isIdFree(String string) {
        return !this.factions.containsKey(Integer.parseInt(string));
    }

    public boolean isIdFree(int n) {
        return this.isIdFree(Integer.toString(n));
    }

    protected synchronized void updateNextIdForId(int n) {
        if (this.nextId < n) {
            this.nextId = n + 1;
        }
    }

    protected void updateNextIdForId(String string) {
        try {
            int n = Integer.parseInt(string);
            this.updateNextIdForId(n);
        } catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public Faction generateFactionObject() {
        String string = this.getNextId();
        JSONFaction jSONFaction = new JSONFaction(string);
        this.updateNextIdForId(string);
        return jSONFaction;
    }

    @Override
    public Faction generateFactionObject(int n) {
        return new JSONFaction(n);
    }

    @Override
    @Deprecated
    public void convertFrom(MemoryFactions memoryFactions) {
        memoryFactions.factions.forEach((n, faction) -> this.factions.put(n, new JSONFaction((MemoryFaction)faction)));
        this.nextId = memoryFactions.nextId;
        this.forceSave();
        Factions.instance = this;
    }

    private record NextId(int next, String BIG_WARNING) {
        NextId(int n) {
            this(n, "DO NOT DELETE OR EDIT THIS FILE UNLESS DELETING ALL FACTIONS AS WELL.");
        }
    }
}

