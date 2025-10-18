/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.data;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;

public abstract class MemoryFactions
extends Factions {
    public final Map<Integer, Faction> factions = new ConcurrentHashMap<Integer, Faction>();
    public int nextId = 1;
    private static final int ID_WILDERNESS = 0;
    private static final int ID_SAFEZONE = -1;
    private static final int ID_WARZONE = -2;

    @Override
    public int load() {
        Faction faction;
        if (!this.factions.containsKey(0)) {
            faction = this.generateFactionObject(0);
            this.factions.put(0, faction);
            faction.setTag(TL.WILDERNESS.toString());
            faction.setDescription(TL.WILDERNESS_DESCRIPTION.toString());
        } else {
            faction = this.factions.get(0);
            if (!faction.getTag().equalsIgnoreCase(TL.WILDERNESS.toString())) {
                faction.setTag(TL.WILDERNESS.toString());
            }
            if (!faction.getDescription().equalsIgnoreCase(TL.WILDERNESS_DESCRIPTION.toString())) {
                faction.setDescription(TL.WILDERNESS_DESCRIPTION.toString());
            }
        }
        if (!this.factions.containsKey(-1)) {
            faction = this.generateFactionObject(-1);
            this.factions.put(-1, faction);
            faction.setTag(TL.SAFEZONE.toString());
            faction.setDescription(TL.SAFEZONE_DESCRIPTION.toString());
        } else {
            faction = this.factions.get(-1);
            if (!faction.getTag().equalsIgnoreCase(TL.SAFEZONE.toString())) {
                faction.setTag(TL.SAFEZONE.toString());
            }
            if (!faction.getDescription().equalsIgnoreCase(TL.SAFEZONE_DESCRIPTION.toString())) {
                faction.setDescription(TL.SAFEZONE_DESCRIPTION.toString());
            }
            if (faction.getTag().contains(" ")) {
                faction.setTag(TL.SAFEZONE.toString());
            }
        }
        if (!this.factions.containsKey(-2)) {
            faction = this.generateFactionObject(-2);
            this.factions.put(-2, faction);
            faction.setTag(TL.WARZONE.toString());
            faction.setDescription(TL.WARZONE_DESCRIPTION.toString());
        } else {
            faction = this.factions.get(-2);
            if (!faction.getTag().equalsIgnoreCase(TL.WARZONE.toString())) {
                faction.setTag(TL.WARZONE.toString());
            }
            if (!faction.getDescription().equalsIgnoreCase(TL.WARZONE_DESCRIPTION.toString())) {
                faction.setDescription(TL.WARZONE_DESCRIPTION.toString());
            }
            if (faction.getTag().contains(" ")) {
                faction.setTag(TL.WARZONE.toString());
            }
        }
        return 0;
    }

    @Override
    @Deprecated
    public Faction getFactionById(String string) {
        return this.factions.get(Integer.parseInt(string));
    }

    @Override
    public Faction getFactionById(int n) {
        return this.factions.get(n);
    }

    public abstract Faction generateFactionObject(int var1);

    @Override
    public Faction getByTag(String string) {
        String string2 = MiscUtil.getComparisonString(string);
        for (Faction faction : this.factions.values()) {
            if (!faction.getComparisonTag().equals(string2)) continue;
            return faction;
        }
        return null;
    }

    @Override
    public Faction getBestTagMatch(String string) {
        int n = 0;
        string = string.toLowerCase();
        int n2 = string.length();
        Faction faction = null;
        for (Faction faction2 : this.factions.values()) {
            String string2 = faction2.getTag();
            if ((string2 = ChatColor.stripColor((String)string2)).length() < n2 || !string2.toLowerCase().startsWith(string)) continue;
            int n3 = string2.length() - n2;
            if (n3 == 0) {
                return faction2;
            }
            if (n3 >= n && n != 0) continue;
            n = n3;
            faction = faction2;
        }
        return faction;
    }

    @Override
    public boolean isTagTaken(String string) {
        return this.getByTag(string) != null;
    }

    @Override
    public boolean isValidFactionId(String string) {
        return this.isValidFactionId(Integer.parseInt(string));
    }

    @Override
    public boolean isValidFactionId(int n) {
        return this.factions.containsKey(n);
    }

    @Override
    public Faction createFaction() {
        Faction faction = this.generateFactionObject();
        this.factions.put(faction.getIntId(), faction);
        return faction;
    }

    @Override
    public Set<String> getFactionTags() {
        HashSet<String> hashSet = new HashSet<String>();
        for (Faction faction : this.factions.values()) {
            hashSet.add(faction.getTag());
        }
        return hashSet;
    }

    public abstract Faction generateFactionObject();

    @Override
    public void removeFaction(String string) {
        this.factions.remove(Integer.parseInt(string)).remove();
    }

    @Override
    public void removeFaction(Faction faction) {
        this.factions.remove(faction.getIntId()).remove();
    }

    @Override
    public ArrayList<Faction> getAllFactions() {
        return new ArrayList<Faction>(this.factions.values());
    }

    @Override
    public Faction getWilderness() {
        return this.factions.get(0);
    }

    @Override
    public Faction getSafeZone() {
        return this.factions.get(-1);
    }

    @Override
    public Faction getWarZone() {
        return this.factions.get(-2);
    }

    public abstract void convertFrom(MemoryFactions var1);
}

