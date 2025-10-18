/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Collection;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;

enum ResolveStatus {
    UNRESOLVED,
    RESOLVED;


    static final ResolveStatus fromValues(Collection<? extends AbstractConfigValue> collection) {
        for (AbstractConfigValue abstractConfigValue : collection) {
            if (abstractConfigValue.resolveStatus() != UNRESOLVED) continue;
            return UNRESOLVED;
        }
        return RESOLVED;
    }

    static final ResolveStatus fromBoolean(boolean bl) {
        return bl ? RESOLVED : UNRESOLVED;
    }
}

