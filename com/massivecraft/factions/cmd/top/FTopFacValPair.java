/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.top;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.top.FTopValue;

public final class FTopFacValPair
implements Comparable<FTopFacValPair> {
    public final Faction faction;
    public final FTopValue value;

    public FTopFacValPair(Faction faction, FTopValue<?> fTopValue) {
        this.faction = faction;
        this.value = fTopValue;
    }

    @Override
    public int compareTo(FTopFacValPair fTopFacValPair) {
        return this.value.compareTo(fTopFacValPair.value);
    }
}

