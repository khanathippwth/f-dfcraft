/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.struct;

public class BanInfo {
    private final String banner;
    private final String banned;
    private final long time;

    public BanInfo(String string, String string2, long l) {
        this.banner = string;
        this.banned = string2;
        this.time = l;
    }

    public String getBanner() {
        return this.banner;
    }

    public String getBanned() {
        return this.banned;
    }

    public long getTime() {
        return this.time;
    }
}

