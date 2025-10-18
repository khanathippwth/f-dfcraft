/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.data.json;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.data.MemoryFPlayer;
import com.massivecraft.factions.data.json.JSONFPlayers;

public class JSONFPlayer
extends MemoryFPlayer {
    @Deprecated
    public JSONFPlayer(MemoryFPlayer memoryFPlayer) {
        super(memoryFPlayer);
    }

    public JSONFPlayer(String string) {
        super(string);
    }

    @Override
    public void remove() {
        ((JSONFPlayers)FPlayers.getInstance()).fPlayers.remove(this.getId());
    }
}

