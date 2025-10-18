/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.struct;

import com.massivecraft.factions.util.TL;

public enum ChatMode {
    MOD(4, TL.CHAT_MOD),
    FACTION(3, TL.CHAT_FACTION),
    ALLIANCE(2, TL.CHAT_ALLIANCE),
    TRUCE(1, TL.CHAT_TRUCE),
    PUBLIC(0, TL.CHAT_PUBLIC);

    public final int value;
    public final TL nicename;

    private ChatMode(int n2, TL tL) {
        this.value = n2;
        this.nicename = tL;
    }

    public boolean isAtLeast(ChatMode chatMode) {
        return this.value >= chatMode.value;
    }

    public boolean isAtMost(ChatMode chatMode) {
        return this.value <= chatMode.value;
    }

    public String toString() {
        return this.nicename.toString();
    }

    public ChatMode getNext() {
        return switch (this.ordinal()) {
            case 4 -> TRUCE;
            case 3 -> ALLIANCE;
            case 2 -> FACTION;
            case 1 -> MOD;
            default -> PUBLIC;
        };
    }
}

