/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Collection;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;

interface Unmergeable {
    public Collection<? extends AbstractConfigValue> unmergedValues();
}

