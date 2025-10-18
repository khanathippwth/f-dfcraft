/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionAttemptCreateEvent
extends Event
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final String factionTag;
    private final Player sender;
    private boolean cancelled;

    public FactionAttemptCreateEvent(Player player, String string) {
        this.factionTag = string;
        this.sender = player;
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getByPlayer(this.sender);
    }

    @Deprecated
    public String getFactionTag() {
        return this.factionTag;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }
}

