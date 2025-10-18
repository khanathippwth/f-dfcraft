/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;

interface Container
extends ConfigValue {
    public AbstractConfigValue replaceChild(AbstractConfigValue var1, AbstractConfigValue var2);

    public boolean hasDescendant(AbstractConfigValue var1);
}

