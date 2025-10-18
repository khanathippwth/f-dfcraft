/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValueType;

public interface ConfigValue
extends ConfigMergeable {
    public ConfigOrigin origin();

    public ConfigValueType valueType();

    public Object unwrapped();

    public String render();

    public String render(ConfigRenderOptions var1);

    @Override
    public ConfigValue withFallback(ConfigMergeable var1);

    public Config atPath(String var1);

    public Config atKey(String var1);

    public ConfigValue withOrigin(ConfigOrigin var1);
}

