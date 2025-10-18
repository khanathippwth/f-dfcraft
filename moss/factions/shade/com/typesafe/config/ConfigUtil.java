/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.util.List;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;

public final class ConfigUtil {
    private ConfigUtil() {
    }

    public static String quoteString(String string) {
        return ConfigImplUtil.renderJsonString(string);
    }

    public static String joinPath(String ... stringArray) {
        return ConfigImplUtil.joinPath(stringArray);
    }

    public static String joinPath(List<String> list) {
        return ConfigImplUtil.joinPath(list);
    }

    public static List<String> splitPath(String string) {
        return ConfigImplUtil.splitPath(string);
    }
}

