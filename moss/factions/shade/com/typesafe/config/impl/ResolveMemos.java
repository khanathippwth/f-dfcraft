/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.BadMap;
import moss.factions.shade.com.typesafe.config.impl.MemoKey;

final class ResolveMemos {
    private final BadMap<MemoKey, AbstractConfigValue> memos;

    private ResolveMemos(BadMap<MemoKey, AbstractConfigValue> badMap) {
        this.memos = badMap;
    }

    ResolveMemos() {
        this(new BadMap<MemoKey, AbstractConfigValue>());
    }

    AbstractConfigValue get(MemoKey memoKey) {
        return this.memos.get(memoKey);
    }

    ResolveMemos put(MemoKey memoKey, AbstractConfigValue abstractConfigValue) {
        return new ResolveMemos(this.memos.copyingPut(memoKey, abstractConfigValue));
    }
}

