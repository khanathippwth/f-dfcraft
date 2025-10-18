/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Cancellable
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionPlayerEvent;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.event.Cancellable;

public class FactionRelationWishEvent
extends FactionPlayerEvent
implements Cancellable {
    private final Faction targetFaction;
    private final Relation currentRelation;
    private final Relation targetRelation;
    private boolean cancelled;

    public FactionRelationWishEvent(FPlayer fPlayer, Faction faction, Faction faction2, Relation relation, Relation relation2) {
        super(faction, fPlayer);
        this.targetFaction = faction2;
        this.currentRelation = relation;
        this.targetRelation = relation2;
    }

    public Faction getTargetFaction() {
        return this.targetFaction;
    }

    public Relation getCurrentRelation() {
        return this.currentRelation;
    }

    public Relation getTargetRelation() {
        return this.targetRelation;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }
}

