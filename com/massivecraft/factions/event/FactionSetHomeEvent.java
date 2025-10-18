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

public class FactionSetHomeEvent
extends FactionPlayerEvent
implements Cancellable {
    private final Location location;
    private boolean cancelled;

    public FactionSetHomeEvent(FPlayer fPlayer, Location location) {
        super(fPlayer.getFaction(), fPlayer);
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }
}

