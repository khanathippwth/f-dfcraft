/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.event.Cancellable
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.event.FactionPlayerEvent;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

public class FPlayerTeleportEvent
extends FactionPlayerEvent
implements Cancellable {
    private final PlayerTeleportReason reason;
    private boolean cancelled = false;
    private final Location location;

    public FPlayerTeleportEvent(FPlayer fPlayer, Location location, PlayerTeleportReason playerTeleportReason) {
        super(fPlayer.getFaction(), fPlayer);
        this.reason = playerTeleportReason;
        this.location = location;
    }

    public PlayerTeleportReason getReason() {
        return this.reason;
    }

    public Location getDestination() {
        return this.location;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }

    public static enum PlayerTeleportReason {
        HOME,
        AHOME,
        WARP,
        STUCK;

    }
}

