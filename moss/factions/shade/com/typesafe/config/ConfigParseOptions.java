/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.ConfigIncluder;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;

public final class ConfigParseOptions {
    final ConfigSyntax syntax;
    final String originDescription;
    final boolean allowMissing;
    final ConfigIncluder includer;
    final ClassLoader classLoader;

    private ConfigParseOptions(ConfigSyntax configSyntax, String string, boolean bl, ConfigIncluder configIncluder, ClassLoader classLoader) {
        this.syntax = configSyntax;
        this.originDescription = string;
        this.allowMissing = bl;
        this.includer = configIncluder;
        this.classLoader = classLoader;
    }

    public static ConfigParseOptions defaults() {
        return new ConfigParseOptions(null, null, true, null, null);
    }

    public ConfigParseOptions setSyntax(ConfigSyntax configSyntax) {
        if (this.syntax == configSyntax) {
            return this;
        }
        return new ConfigParseOptions(configSyntax, this.originDescription, this.allowMissing, this.includer, this.classLoader);
    }

    public ConfigParseOptions setSyntaxFromFilename(String string) {
        ConfigSyntax configSyntax = ConfigImplUtil.syntaxFromExtension(string);
        return this.setSyntax(configSyntax);
    }

    public ConfigSyntax getSyntax() {
        return this.syntax;
    }

    public ConfigParseOptions setOriginDescription(String string) {
        if (this.originDescription == string) {
            return this;
        }
        if (this.originDescription != null && string != null && this.originDescription.equals(string)) {
            return this;
        }
        return new ConfigParseOptions(this.syntax, string, this.allowMissing, this.includer, this.classLoader);
    }

    public String getOriginDescription() {
        return this.originDescription;
    }

    ConfigParseOptions withFallbackOriginDescription(String string) {
        if (this.originDescription == null) {
            return this.setOriginDescription(string);
        }
        return this;
    }

    public ConfigParseOptions setAllowMissing(boolean bl) {
        if (this.allowMissing == bl) {
            return this;
        }
        return new ConfigParseOptions(this.syntax, this.originDescription, bl, this.includer, this.classLoader);
    }

    public boolean getAllowMissing() {
        return this.allowMissing;
    }

    public ConfigParseOptions setIncluder(ConfigIncluder configIncluder) {
        if (this.includer == configIncluder) {
            return this;
        }
        return new ConfigParseOptions(this.syntax, this.originDescription, this.allowMissing, configIncluder, this.classLoader);
    }

    public ConfigParseOptions prependIncluder(ConfigIncluder configIncluder) {
        if (configIncluder == null) {
            throw new NullPointerException("null includer passed to prependIncluder");
        }
        if (this.includer == configIncluder) {
            return this;
        }
        if (this.includer != null) {
            return this.setIncluder(configIncluder.withFallback(this.includer));
        }
        return this.setIncluder(configIncluder);
    }

    public ConfigParseOptions appendIncluder(ConfigIncluder configIncluder) {
        if (configIncluder == null) {
            throw new NullPointerException("null includer passed to appendIncluder");
        }
        if (this.includer == configIncluder) {
            return this;
        }
        if (this.includer != null) {
            return this.setIncluder(this.includer.withFallback(configIncluder));
        }
        return this.setIncluder(configIncluder);
    }

    public ConfigIncluder getIncluder() {
        return this.includer;
    }

    public ConfigParseOptions setClassLoader(ClassLoader classLoader) {
        if (this.classLoader == classLoader) {
            return this;
        }
        return new ConfigParseOptions(this.syntax, this.originDescription, this.allowMissing, this.includer, classLoader);
    }

    public ClassLoader getClassLoader() {
        if (this.classLoader == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return this.classLoader;
    }
}

