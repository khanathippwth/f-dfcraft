/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.data.json;

import com.massivecraft.factions.data.MemoryFaction;

public class JSONFaction
extends MemoryFaction {
    @Deprecated
    public JSONFaction(MemoryFaction memoryFaction) {
        super(memoryFaction);
    }

    private JSONFaction() {
    }

    public JSONFaction(String string) {
        this(Integer.parseInt(string));
    }

    public JSONFaction(int n) {
        super(n);
    }
}

