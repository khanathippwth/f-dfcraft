/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.top;

import com.massivecraft.factions.cmd.top.FTopGTNumberValue;
import com.massivecraft.factions.util.TL;

public class FTopFoundedValue
extends FTopGTNumberValue<FTopFoundedValue, Long> {
    public FTopFoundedValue(Long l) {
        super(l);
    }

    @Override
    public String getDisplayString() {
        return TL.sdf.format(this.value);
    }
}

