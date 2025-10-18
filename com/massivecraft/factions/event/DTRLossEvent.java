/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class DTRLossEvent
extends FactionPlayerEvent
implements Cancellable {
    private boolean cancelled = false;

    public DTRLossEvent(Faction faction, FPlayer fPlayer) {
        super(faction, fPlayer);
    }

    @Deprecated
    public String getFactionId() {
        return this.getFaction().getId();
    }

    @Deprecated
    public String getFactionTag() {
        return this.getFaction().getTag();
    }

    @Deprecated
    public Player getPlayer() {
        return this.getfPlayer().getPlayer();
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }
}

