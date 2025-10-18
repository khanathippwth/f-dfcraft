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
import org.bukkit.event.Cancellable;

public class FPlayerJoinEvent
extends FactionPlayerEvent
implements Cancellable {
    private final PlayerJoinReason reason;
    private boolean cancelled = false;

    public FPlayerJoinEvent(FPlayer fPlayer, Faction faction, PlayerJoinReason playerJoinReason) {
        super(faction, fPlayer);
        this.reason = playerJoinReason;
    }

    public PlayerJoinReason getReason() {
        return this.reason;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }

    public static enum PlayerJoinReason {
        CREATE,
        LEADER,
        COMMAND;

    }
}

