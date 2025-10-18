/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigResolver;
import moss.factions.shade.com.typesafe.config.ConfigValue;

public final class ConfigResolveOptions {
    private final boolean useSystemEnvironment;
    private final boolean allowUnresolved;
    private final ConfigResolver resolver;
    private static final ConfigResolver NULL_RESOLVER = new ConfigResolver(){

        @Override
        public ConfigValue lookup(String string) {
            return null;
        }

        @Override
        public ConfigResolver withFallback(ConfigResolver configResolver) {
            return configResolver;
        }
    };

    private ConfigResolveOptions(boolean bl, boolean bl2, ConfigResolver configResolver) {
        this.useSystemEnvironment = bl;
        this.allowUnresolved = bl2;
        this.resolver = configResolver;
    }

    public static ConfigResolveOptions defaults() {
        return new ConfigResolveOptions(true, false, NULL_RESOLVER);
    }

    public static ConfigResolveOptions noSystem() {
        return ConfigResolveOptions.defaults().setUseSystemEnvironment(false);
    }

    public ConfigResolveOptions setUseSystemEnvironment(boolean bl) {
        return new ConfigResolveOptions(bl, this.allowUnresolved, this.resolver);
    }

    public boolean getUseSystemEnvironment() {
        return this.useSystemEnvironment;
    }

    public ConfigResolveOptions setAllowUnresolved(boolean bl) {
        return new ConfigResolveOptions(this.useSystemEnvironment, bl, this.resolver);
    }

    public ConfigResolveOptions appendResolver(ConfigResolver configResolver) {
        if (configResolver == null) {
            throw new ConfigException.BugOrBroken("null resolver passed to appendResolver");
        }
        if (configResolver == this.resolver) {
            return this;
        }
        return new ConfigResolveOptions(this.useSystemEnvironment, this.allowUnresolved, this.resolver.withFallback(configResolver));
    }

    public ConfigResolver getResolver() {
        return this.resolver;
    }

    public boolean getAllowUnresolved() {
        return this.allowUnresolved;
    }
}

