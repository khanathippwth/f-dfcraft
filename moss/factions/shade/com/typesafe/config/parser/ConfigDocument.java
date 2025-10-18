/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.parser;

import moss.factions.shade.com.typesafe.config.ConfigValue;

public interface ConfigDocument {
    public ConfigDocument withValueText(String var1, String var2);

    public ConfigDocument withValue(String var1, ConfigValue var2);

    public ConfigDocument withoutPath(String var1);

    public boolean hasPath(String var1);

    public String render();
}

