/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Faction faction;

    public FactionEvent(Faction faction) {
        this.faction = faction;
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

