/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.ConfigValue;

public interface ConfigResolver {
    public ConfigValue lookup(String var1);

    public ConfigResolver withFallback(ConfigResolver var1);
}

