/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;

final class ConfigBoolean
extends AbstractConfigValue
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final boolean value;

    ConfigBoolean(ConfigOrigin configOrigin, boolean bl) {
        super(configOrigin);
        this.value = bl;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.BOOLEAN;
    }

    @Override
    public Boolean unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        return this.value ? "true" : "false";
    }

    @Override
    protected ConfigBoolean newCopy(ConfigOrigin configOrigin) {
        return new ConfigBoolean(configOrigin, this.value);
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }
}

