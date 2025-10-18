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
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.MemoryFPlayer;
import com.massivecraft.factions.data.MemoryFPlayers;
import com.massivecraft.factions.data.json.JSONFPlayer;
import com.massivecraft.factions.data.json.JSONFaction;
import com.massivecraft.factions.util.DiscUtil;
import com.massivecraft.factions.util.OldJSONFPlayerDeserializer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONFPlayers
extends MemoryFPlayers {
    private final File file;

    public Gson getGson() {
        return FactionsPlugin.getInstance().getGson();
    }

    @Deprecated
    public void setGson(Gson gson) {
    }

    public JSONFPlayers() {
        if (FactionsPlugin.getInstance().getServerUUID() == null) {
            FactionsPlugin.getInstance().grumpException(new RuntimeException());
        }
        this.file = new File(FactionsPlugin.getInstance().getDataFolder(), "data/players.json");
    }

    @Override
    @Deprecated
    public void convertFrom(MemoryFPlayers memoryFPlayers) {
        memoryFPlayers.fPlayers.forEach((string, fPlayer) -> this.fPlayers.put(string, new JSONFPlayer((MemoryFPlayer)fPlayer)));
        this.forceSave();
        FPlayers.instance = this;
    }

    @Override
    public void forceSave() {
        this.forceSave(true);
    }

    @Override
    public void forceSave(boolean bl) {
        ArrayList<JSONFPlayer> arrayList = new ArrayList<JSONFPlayer>();
        boolean bl2 = FactionsPlugin.getInstance().conf().data().json().isSaveAllPlayers();
        for (FPlayer fPlayer : this.fPlayers.values()) {
            if (!bl2 && !((MemoryFPlayer)fPlayer).shouldBeSaved()) continue;
            arrayList.add((JSONFPlayer)fPlayer);
        }
        this.saveCore(this.file, arrayList, bl);
    }

    private void saveCore(File file, List<JSONFPlayer> list, boolean bl) {
        DiscUtil.writeCatch(file, FactionsPlugin.getInstance().getGson().toJson(list), bl);
    }

    @Override
    public int load() {
        List<JSONFPlayer> list = this.loadCore();
        if (list == null) {
            return 0;
        }
        this.fPlayers.clear();
        list.forEach(jSONFPlayer -> this.fPlayers.put(jSONFPlayer.getId(), jSONFPlayer));
        return this.fPlayers.size();
    }

    private List<JSONFPlayer> loadCore() {
        if (!this.file.exists()) {
            return null;
        }
        String string = DiscUtil.readCatch(this.file);
        if (string == null || string.trim().isEmpty()) {
            return null;
        }
        if (string.startsWith("{")) {
            Gson gson = FactionsPlugin.getInstance().getGsonBuilder(false).registerTypeAdapter(JSONFaction.class, (Object)new OldJSONFPlayerDeserializer()).create();
            Map map = (Map)gson.fromJson(string, new TypeToken<Map<String, JSONFPlayer>>(this){}.getType());
            return new ArrayList<JSONFPlayer>(map.values());
        }
        return (List)FactionsPlugin.getInstance().getGson().fromJson(string, new TypeToken<List<JSONFPlayer>>(this){}.getType());
    }

    private boolean doesKeyNeedMigration(String string) {
        if (!string.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            return string.matches("[a-zA-Z0-9_]{2,16}");
        }
        return false;
    }

    private boolean isKeyInvalid(String string) {
        return !string.matches("[a-zA-Z0-9_]{2,16}");
    }

    @Override
    public FPlayer generateFPlayer(String string) {
        JSONFPlayer jSONFPlayer = new JSONFPlayer(string);
        this.fPlayers.put(jSONFPlayer.getId(), jSONFPlayer);
        return jSONFPlayer;
    }
}

