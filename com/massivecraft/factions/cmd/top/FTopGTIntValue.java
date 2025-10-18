/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.top;

import com.massivecraft.factions.cmd.top.FTopGTNumberValue;

public class FTopGTIntValue
extends FTopGTNumberValue<FTopGTIntValue, Integer> {
    public FTopGTIntValue(Integer n) {
        super(n);
    }

    @Override
    public String getDisplayString() {
        return String.valueOf(this.value);
    }
}

