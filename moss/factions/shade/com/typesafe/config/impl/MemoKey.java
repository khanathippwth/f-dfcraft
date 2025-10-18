/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.Path;

final class MemoKey {
    private final AbstractConfigValue value;
    private final Path restrictToChildOrNull;

    MemoKey(AbstractConfigValue abstractConfigValue, Path path) {
        this.value = abstractConfigValue;
        this.restrictToChildOrNull = path;
    }

    public final int hashCode() {
        int n = System.identityHashCode(this.value);
        if (this.restrictToChildOrNull != null) {
            return n + 41 * (41 + this.restrictToChildOrNull.hashCode());
        }
        return n;
    }

    public final boolean equals(Object object) {
        if (object instanceof MemoKey) {
            MemoKey memoKey = (MemoKey)object;
            if (memoKey.value != this.value) {
                return false;
            }
            if (memoKey.restrictToChildOrNull == this.restrictToChildOrNull) {
                return true;
            }
            if (memoKey.restrictToChildOrNull == null || this.restrictToChildOrNull == null) {
                return false;
            }
            return memoKey.restrictToChildOrNull.equals(this.restrictToChildOrNull);
        }
        return false;
    }

    public final String toString() {
        return "MemoKey(" + this.value + "@" + System.identityHashCode(this.value) + "," + this.restrictToChildOrNull + ")";
    }
}

