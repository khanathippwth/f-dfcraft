/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.util.Map;
import moss.factions.shade.com.typesafe.config.ConfigList;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;

public final class ConfigValueFactory {
    private ConfigValueFactory() {
    }

    public static ConfigValue fromAnyRef(Object object, String string) {
        return ConfigImpl.fromAnyRef(object, string);
    }

    public static ConfigObject fromMap(Map<String, ? extends Object> map, String string) {
        return (ConfigObject)ConfigValueFactory.fromAnyRef(map, string);
    }

    public static ConfigList fromIterable(Iterable<? extends Object> iterable, String string) {
        return (ConfigList)ConfigValueFactory.fromAnyRef(iterable, string);
    }

    public static ConfigValue fromAnyRef(Object object) {
        return ConfigValueFactory.fromAnyRef(object, null);
    }

    public static ConfigObject fromMap(Map<String, ? extends Object> map) {
        return ConfigValueFactory.fromMap(map, null);
    }

    public static ConfigList fromIterable(Iterable<? extends Object> iterable) {
        return ConfigValueFactory.fromIterable(iterable, null);
    }
}

