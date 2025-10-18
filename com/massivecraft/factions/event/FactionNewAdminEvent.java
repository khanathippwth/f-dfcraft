/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionPlayerEvent;

public class FactionNewAdminEvent
extends FactionPlayerEvent {
    public FactionNewAdminEvent(FPlayer fPlayer, Faction faction) {
        super(faction, fPlayer);
    }
}

