/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.ConfigNumber;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;

final class ConfigInt
extends ConfigNumber
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final int value;

    ConfigInt(ConfigOrigin configOrigin, int n, String string) {
        super(configOrigin, string);
        this.value = n;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.NUMBER;
    }

    @Override
    public Integer unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        String string = super.transformToString();
        if (string == null) {
            return Integer.toString(this.value);
        }
        return string;
    }

    @Override
    protected long longValue() {
        return this.value;
    }

    @Override
    protected double doubleValue() {
        return this.value;
    }

    @Override
    protected ConfigInt newCopy(ConfigOrigin configOrigin) {
        return new ConfigInt(configOrigin, this.value, this.originalText);
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }
}

