/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class FactionDisbandEvent
extends FactionEvent
implements Cancellable {
    private boolean cancelled = false;
    private final Player sender;

    public FactionDisbandEvent(Player player, Faction faction) {
        super(faction);
        this.sender = player;
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getByPlayer(this.sender);
    }

    public Player getPlayer() {
        return this.sender;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }
}

