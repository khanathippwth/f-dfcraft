/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.top;

import com.massivecraft.factions.cmd.top.FTopValue;

public abstract class FTopGTNumberValue<T extends FTopGTNumberValue<T, N>, N extends Comparable<N>>
implements FTopValue<T> {
    protected final N value;

    public FTopGTNumberValue(N n) {
        this.value = n;
    }

    @Override
    public int compareTo(T t) {
        return -this.value.compareTo(((FTopGTNumberValue)t).value);
    }
}

