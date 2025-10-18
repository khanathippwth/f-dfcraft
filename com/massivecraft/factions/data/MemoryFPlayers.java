/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.data;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class MemoryFPlayers
extends FPlayers {
    public Map<String, FPlayer> fPlayers = new ConcurrentSkipListMap<String, FPlayer>(String.CASE_INSENSITIVE_ORDER);

    @Override
    public void clean() {
        for (FPlayer fPlayer : this.fPlayers.values()) {
            if (Factions.getInstance().isValidFactionId(fPlayer.getFactionIntId())) continue;
            FactionsPlugin.getInstance().log("Reset faction data (invalid faction:" + fPlayer.getFactionIntId() + ") for player " + fPlayer.getName());
            fPlayer.resetFactionData(false);
        }
    }

    @Override
    public Collection<FPlayer> getOnlinePlayers() {
        HashSet<FPlayer> hashSet = new HashSet<FPlayer>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            hashSet.add(this.getByPlayer(player));
        }
        return hashSet;
    }

    @Override
    public FPlayer getByPlayer(Player player) {
        return this.getById(player.getUniqueId().toString());
    }

    public List<FPlayer> getAllFPlayers() {
        return new ArrayList<FPlayer>(this.fPlayers.values());
    }

    @Override
    public FPlayer getByOfflinePlayer(OfflinePlayer offlinePlayer) {
        return this.getById(offlinePlayer.getUniqueId().toString());
    }

    @Override
    public FPlayer getById(String string) {
        FPlayer fPlayer = this.fPlayers.get(string);
        if (fPlayer == null) {
            fPlayer = this.generateFPlayer(string);
        }
        return fPlayer;
    }

    protected abstract FPlayer generateFPlayer(String var1);

    public abstract void convertFrom(MemoryFPlayers var1);
}

