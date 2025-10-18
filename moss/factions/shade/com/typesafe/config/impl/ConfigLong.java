/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.ConfigNumber;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;

final class ConfigLong
extends ConfigNumber
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final long value;

    ConfigLong(ConfigOrigin configOrigin, long l, String string) {
        super(configOrigin, string);
        this.value = l;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.NUMBER;
    }

    @Override
    public Long unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        String string = super.transformToString();
        if (string == null) {
            return Long.toString(this.value);
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
    protected ConfigLong newCopy(ConfigOrigin configOrigin) {
        return new ConfigLong(configOrigin, this.value, this.originalText);
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }
}

