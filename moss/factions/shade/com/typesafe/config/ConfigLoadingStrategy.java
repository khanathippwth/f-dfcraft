/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;

public interface ConfigLoadingStrategy {
    public Config parseApplicationConfig(ConfigParseOptions var1);
}

