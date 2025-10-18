/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.impl.ConfigBeanImpl;

public class ConfigBeanFactory {
    public static <T> T create(Config config, Class<T> clazz) {
        return ConfigBeanImpl.createInternal(config, clazz);
    }
}

