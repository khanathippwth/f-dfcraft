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

public class FPlayerLeaveEvent
extends FactionPlayerEvent
implements Cancellable {
    private final PlayerLeaveReason reason;
    boolean cancelled = false;

    public FPlayerLeaveEvent(FPlayer fPlayer, Faction faction, PlayerLeaveReason playerLeaveReason) {
        super(faction, fPlayer);
        this.reason = playerLeaveReason;
    }

    public PlayerLeaveReason getReason() {
        return this.reason;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = this.reason != PlayerLeaveReason.DISBAND && this.reason != PlayerLeaveReason.RESET && bl;
    }

    public static enum PlayerLeaveReason {
        KICKED,
        DISBAND,
        RESET,
        JOINOTHER,
        LEAVE,
        BANNED;

    }
}

