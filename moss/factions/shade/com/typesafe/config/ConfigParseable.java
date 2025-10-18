/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;

public interface ConfigParseable {
    public ConfigObject parse(ConfigParseOptions var1);

    public ConfigOrigin origin();

    public ConfigParseOptions options();
}

