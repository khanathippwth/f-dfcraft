/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigParseable;

public interface ConfigIncludeContext {
    public ConfigParseable relativeTo(String var1);

    public ConfigParseOptions parseOptions();

    public ConfigIncludeContext setParseOptions(ConfigParseOptions var1);
}

