/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.massivecraft.factions;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.data.json.JSONBoard;
import java.util.List;
import java.util.Set;
import moss.factions.shade.net.kyori.adventure.text.Component;
import org.bukkit.World;

public abstract class Board {
    protected static final Board instance = Board.getBoardImpl();

    public abstract String getIdAt(FLocation var1);

    public abstract int getIntIdAt(FLocation var1);

    private static Board getBoardImpl() {
        return new JSONBoard();
    }

    public static Board getInstance() {
        return instance;
    }

    public abstract Faction getFactionAt(FLocation var1);

    @Deprecated
    public abstract void setIdAt(String var1, FLocation var2);

    public abstract void setFactionAt(Faction var1, FLocation var2);

    public abstract void removeAt(FLocation var1);

    @Deprecated
    public abstract Set<FLocation> getAllClaims(String var1);

    public abstract Set<FLocation> getAllClaims(Faction var1);

    public abstract void clearOwnershipAt(FLocation var1);

    @Deprecated
    public abstract void unclaimAll(String var1);

    public abstract void unclaimAll(Faction var1);

    @Deprecated
    public abstract void unclaimAllInWorld(String var1, World var2);

    public abstract void unclaimAllInWorld(Faction var1, World var2);

    public abstract boolean isBorderLocation(FLocation var1);

    public abstract boolean isConnectedLocation(FLocation var1, Faction var2);

    public abstract boolean hasFactionWithin(FLocation var1, Faction var2, int var3);

    public abstract void clean();

    @Deprecated
    public abstract int getFactionCoordCount(String var1);

    public abstract int getFactionCoordCount(Faction var1);

    public abstract int getFactionCoordCountInWorld(Faction var1, String var2);

    public abstract List<Component> getMap(FPlayer var1, FLocation var2, double var3);

    public abstract void forceSave();

    public abstract void forceSave(boolean var1);

    public abstract int load();
}

