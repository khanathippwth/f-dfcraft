/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.data.json.JSONFPlayers;
import java.util.Collection;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class FPlayers {
    protected static FPlayers instance = FPlayers.getFPlayersImpl();

    public abstract void clean();

    public static FPlayers getInstance() {
        return instance;
    }

    private static FPlayers getFPlayersImpl() {
        return new JSONFPlayers();
    }

    public abstract Collection<FPlayer> getOnlinePlayers();

    public abstract FPlayer getByPlayer(Player var1);

    public abstract Collection<FPlayer> getAllFPlayers();

    public abstract void forceSave();

    public abstract void forceSave(boolean var1);

    public abstract FPlayer getByOfflinePlayer(OfflinePlayer var1);

    public abstract FPlayer getById(String var1);

    public abstract int load();
}

