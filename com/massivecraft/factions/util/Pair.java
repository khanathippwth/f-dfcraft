/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.util;

public class Pair<Left, Right> {
    private final Left left;
    private final Right right;

    public static <Left, Right> Pair<Left, Right> of(Left Left, Right Right) {
        return new Pair<Left, Right>(Left, Right);
    }

    private Pair(Left Left, Right Right) {
        this.left = Left;
        this.right = Right;
    }

    public Left getLeft() {
        return this.left;
    }

    public Right getRight() {
        return this.right;
    }
}

