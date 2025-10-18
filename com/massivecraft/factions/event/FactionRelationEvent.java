/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionRelationEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Faction fsender;
    private final Faction ftarget;
    private final Relation foldrel;
    private final Relation frel;

    public FactionRelationEvent(Faction faction, Faction faction2, Relation relation, Relation relation2) {
        this.fsender = faction;
        this.ftarget = faction2;
        this.foldrel = relation;
        this.frel = relation2;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Relation getOldRelation() {
        return this.foldrel;
    }

    public Relation getRelation() {
        return this.frel;
    }

    public Faction getFaction() {
        return this.fsender;
    }

    public Faction getTargetFaction() {
        return this.ftarget;
    }
}

