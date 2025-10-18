/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.net.URL;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;

public final class ConfigOriginFactory {
    private ConfigOriginFactory() {
    }

    public static ConfigOrigin newSimple() {
        return ConfigOriginFactory.newSimple(null);
    }

    public static ConfigOrigin newSimple(String string) {
        return ConfigImpl.newSimpleOrigin(string);
    }

    public static ConfigOrigin newFile(String string) {
        return ConfigImpl.newFileOrigin(string);
    }

    public static ConfigOrigin newURL(URL uRL) {
        return ConfigImpl.newURLOrigin(uRL);
    }
}

