/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.data.json.JSONFactions;
import java.util.ArrayList;
import java.util.Set;

public abstract class Factions {
    protected static Factions instance = Factions.getFactionsImpl();

    @Deprecated
    public abstract Faction getFactionById(String var1);

    public abstract Faction getFactionById(int var1);

    public abstract Faction getByTag(String var1);

    public abstract Faction getBestTagMatch(String var1);

    public abstract boolean isTagTaken(String var1);

    @Deprecated
    public abstract boolean isValidFactionId(String var1);

    public abstract boolean isValidFactionId(int var1);

    public abstract Faction createFaction();

    @Deprecated
    public abstract void removeFaction(String var1);

    public abstract void removeFaction(Faction var1);

    public abstract Set<String> getFactionTags();

    public abstract ArrayList<Faction> getAllFactions();

    @Deprecated
    public Faction getNone() {
        return this.getWilderness();
    }

    public abstract Faction getWilderness();

    public abstract Faction getSafeZone();

    public abstract Faction getWarZone();

    public abstract void forceSave();

    public abstract void forceSave(boolean var1);

    public static Factions getInstance() {
        return instance;
    }

    private static Factions getFactionsImpl() {
        return new JSONFactions();
    }

    public abstract int load();
}

