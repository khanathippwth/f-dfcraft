/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.ConfigNumber;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;

final class ConfigDouble
extends ConfigNumber
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final double value;

    ConfigDouble(ConfigOrigin configOrigin, double d, String string) {
        super(configOrigin, string);
        this.value = d;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.NUMBER;
    }

    @Override
    public Double unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        String string = super.transformToString();
        if (string == null) {
            return Double.toString(this.value);
        }
        return string;
    }

    @Override
    protected long longValue() {
        return (long)this.value;
    }

    @Override
    protected double doubleValue() {
        return this.value;
    }

    @Override
    protected ConfigDouble newCopy(ConfigOrigin configOrigin) {
        return new ConfigDouble(configOrigin, this.value, this.originalText);
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }
}

