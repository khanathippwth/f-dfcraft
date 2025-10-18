/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.util.Map;
import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigValue;

public interface ConfigObject
extends ConfigValue,
Map<String, ConfigValue> {
    public Config toConfig();

    @Override
    public Map<String, Object> unwrapped();

    @Override
    public ConfigObject withFallback(ConfigMergeable var1);

    @Override
    public ConfigValue get(Object var1);

    public ConfigObject withOnlyKey(String var1);

    public ConfigObject withoutKey(String var1);

    public ConfigObject withValue(String var1, ConfigValue var2);

    @Override
    public ConfigObject withOrigin(ConfigOrigin var1);
}

