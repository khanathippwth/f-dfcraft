/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;

abstract class ConfigString
extends AbstractConfigValue
implements Serializable {
    private static final long serialVersionUID = 2L;
    protected final String value;

    protected ConfigString(ConfigOrigin configOrigin, String string) {
        super(configOrigin);
        this.value = string;
    }

    boolean wasQuoted() {
        return this instanceof Quoted;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.STRING;
    }

    @Override
    public String unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        return this.value;
    }

    @Override
    protected void render(StringBuilder stringBuilder, int n, boolean bl, ConfigRenderOptions configRenderOptions) {
        String string = configRenderOptions.getJson() ? ConfigImplUtil.renderJsonString(this.value) : ConfigImplUtil.renderStringUnquotedIfPossible(this.value);
        stringBuilder.append(string);
    }

    static final class Unquoted
    extends ConfigString {
        Unquoted(ConfigOrigin configOrigin, String string) {
            super(configOrigin, string);
        }

        @Override
        protected Unquoted newCopy(ConfigOrigin configOrigin) {
            return new Unquoted(configOrigin, this.value);
        }

        private Object writeReplace() {
            return new SerializedConfigValue(this);
        }
    }

    static final class Quoted
    extends ConfigString {
        Quoted(ConfigOrigin configOrigin, String string) {
            super(configOrigin, string);
        }

        @Override
        protected Quoted newCopy(ConfigOrigin configOrigin) {
            return new Quoted(configOrigin, this.value);
        }

        private Object writeReplace() {
            return new SerializedConfigValue(this);
        }
    }
}

