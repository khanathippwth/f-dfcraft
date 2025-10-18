/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.ConfigIncludeContext;
import moss.factions.shade.com.typesafe.config.ConfigObject;

public interface ConfigIncluderClasspath {
    public ConfigObject includeResources(ConfigIncludeContext var1, String var2);
}

