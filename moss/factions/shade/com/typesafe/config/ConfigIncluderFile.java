/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.io.File;
import moss.factions.shade.com.typesafe.config.ConfigIncludeContext;
import moss.factions.shade.com.typesafe.config.ConfigObject;

public interface ConfigIncluderFile {
    public ConfigObject includeFile(ConfigIncludeContext var1, File var2);
}

