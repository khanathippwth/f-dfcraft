/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigValue;

interface MergeableValue
extends ConfigMergeable {
    public ConfigValue toFallbackValue();
}

