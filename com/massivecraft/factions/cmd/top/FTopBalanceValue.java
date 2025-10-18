/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.top;

import com.massivecraft.factions.cmd.top.FTopGTNumberValue;
import com.massivecraft.factions.integration.Econ;

public class FTopBalanceValue
extends FTopGTNumberValue<FTopBalanceValue, Double> {
    public FTopBalanceValue(Double d) {
        super(d);
    }

    @Override
    public String getDisplayString() {
        return Econ.moneyString((Double)this.value);
    }
}

