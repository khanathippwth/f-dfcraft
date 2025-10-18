/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigValue;

public interface ConfigList
extends List<ConfigValue>,
ConfigValue {
    @Override
    public List<Object> unwrapped();

    @Override
    public ConfigList withOrigin(ConfigOrigin var1);
}

