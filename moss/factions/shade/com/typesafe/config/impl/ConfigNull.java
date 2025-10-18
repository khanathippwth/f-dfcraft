/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;

final class ConfigNull
extends AbstractConfigValue
implements Serializable {
    private static final long serialVersionUID = 2L;

    ConfigNull(ConfigOrigin configOrigin) {
        super(configOrigin);
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.NULL;
    }

    @Override
    public Object unwrapped() {
        return null;
    }

    @Override
    String transformToString() {
        return "null";
    }

    @Override
    protected void render(StringBuilder stringBuilder, int n, boolean bl, ConfigRenderOptions configRenderOptions) {
        stringBuilder.append("null");
    }

    @Override
    protected ConfigNull newCopy(ConfigOrigin configOrigin) {
        return new ConfigNull(configOrigin);
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }
}

