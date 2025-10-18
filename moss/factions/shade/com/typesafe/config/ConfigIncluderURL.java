/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.net.URL;
import moss.factions.shade.com.typesafe.config.ConfigIncludeContext;
import moss.factions.shade.com.typesafe.config.ConfigObject;

public interface ConfigIncluderURL {
    public ConfigObject includeURL(ConfigIncludeContext var1, URL var2);
}

