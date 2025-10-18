/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigDouble;
import moss.factions.shade.com.typesafe.config.impl.ConfigInt;
import moss.factions.shade.com.typesafe.config.impl.ConfigLong;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;

abstract class ConfigNumber
extends AbstractConfigValue
implements Serializable {
    private static final long serialVersionUID = 2L;
    protected final String originalText;

    protected ConfigNumber(ConfigOrigin configOrigin, String string) {
        super(configOrigin);
        this.originalText = string;
    }

    @Override
    public abstract Number unwrapped();

    @Override
    String transformToString() {
        return this.originalText;
    }

    int intValueRangeChecked(String string) {
        long l = this.longValue();
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new ConfigException.WrongType(this.origin(), string, "32-bit integer", "out-of-range value " + l);
        }
        return (int)l;
    }

    protected abstract long longValue();

    protected abstract double doubleValue();

    private boolean isWhole() {
        long l = this.longValue();
        return (double)l == this.doubleValue();
    }

    @Override
    protected boolean canEqual(Object object) {
        return object instanceof ConfigNumber;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConfigNumber && this.canEqual(object)) {
            ConfigNumber configNumber = (ConfigNumber)object;
            if (this.isWhole()) {
                return configNumber.isWhole() && this.longValue() == configNumber.longValue();
            }
            return !configNumber.isWhole() && this.doubleValue() == configNumber.doubleValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        long l = this.isWhole() ? this.longValue() : Double.doubleToLongBits(this.doubleValue());
        return (int)(l ^ l >>> 32);
    }

    static ConfigNumber newNumber(ConfigOrigin configOrigin, long l, String string) {
        if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) {
            return new ConfigInt(configOrigin, (int)l, string);
        }
        return new ConfigLong(configOrigin, l, string);
    }

    static ConfigNumber newNumber(ConfigOrigin configOrigin, double d, String string) {
        long l = (long)d;
        if ((double)l == d) {
            return ConfigNumber.newNumber(configOrigin, l, string);
        }
        return new ConfigDouble(configOrigin, d, string);
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }
}

