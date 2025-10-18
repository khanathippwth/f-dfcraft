/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionEvent;

public class FactionPlayerEvent
extends FactionEvent {
    private final FPlayer fPlayer;

    public FactionPlayerEvent(Faction faction, FPlayer fPlayer) {
        super(faction);
        this.fPlayer = fPlayer;
    }

    public FPlayer getfPlayer() {
        return this.fPlayer;
    }
}

