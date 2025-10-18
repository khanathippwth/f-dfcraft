/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.event.FactionPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class FactionRenameEvent
extends FactionPlayerEvent
implements Cancellable {
    private boolean cancelled = false;
    private final String tag;

    public FactionRenameEvent(FPlayer fPlayer, String string) {
        super(fPlayer.getFaction(), fPlayer);
        this.tag = string;
    }

    @Deprecated
    public Player getPlayer() {
        return this.getfPlayer().getPlayer();
    }

    @Deprecated
    public String getOldFactionTag() {
        return this.getFaction().getTag();
    }

    public String getFactionTag() {
        return this.tag;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }
}

