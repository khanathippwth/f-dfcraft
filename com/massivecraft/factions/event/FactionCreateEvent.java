/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionCreateEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String factionTag;
    private final Player sender;
    private final Faction faction;

    public FactionCreateEvent(Player player, String string, Faction faction) {
        this.factionTag = string;
        this.sender = player;
        this.faction = faction;
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getByPlayer(this.sender);
    }

    @Deprecated
    public String getFactionTag() {
        return this.factionTag;
    }

    public Faction getFaction() {
        return this.faction;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

