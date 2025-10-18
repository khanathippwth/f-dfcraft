/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigFactory;
import moss.factions.shade.com.typesafe.config.ConfigIncludeContext;
import moss.factions.shade.com.typesafe.config.ConfigIncluder;
import moss.factions.shade.com.typesafe.config.ConfigIncluderClasspath;
import moss.factions.shade.com.typesafe.config.ConfigIncluderFile;
import moss.factions.shade.com.typesafe.config.ConfigIncluderURL;
import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigParseable;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.FullIncluder;
import moss.factions.shade.com.typesafe.config.impl.Parseable;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;

class SimpleIncluder
implements FullIncluder {
    private ConfigIncluder fallback;

    SimpleIncluder(ConfigIncluder configIncluder) {
        this.fallback = configIncluder;
    }

    static ConfigParseOptions clearForInclude(ConfigParseOptions configParseOptions) {
        return configParseOptions.setSyntax(null).setOriginDescription(null).setAllowMissing(true);
    }

    @Override
    public ConfigObject include(ConfigIncludeContext configIncludeContext, String string) {
        ConfigObject configObject = SimpleIncluder.includeWithoutFallback(configIncludeContext, string);
        if (this.fallback != null) {
            return configObject.withFallback(this.fallback.include(configIncludeContext, string));
        }
        return configObject;
    }

    static ConfigObject includeWithoutFallback(ConfigIncludeContext configIncludeContext, String string) {
        URL uRL;
        try {
            uRL = new URL(string);
        } catch (MalformedURLException malformedURLException) {
            uRL = null;
        }
        if (uRL != null) {
            return SimpleIncluder.includeURLWithoutFallback(configIncludeContext, uRL);
        }
        RelativeNameSource relativeNameSource = new RelativeNameSource(configIncludeContext);
        return SimpleIncluder.fromBasename(relativeNameSource, string, configIncludeContext.parseOptions());
    }

    @Override
    public ConfigObject includeURL(ConfigIncludeContext configIncludeContext, URL uRL) {
        ConfigObject configObject = SimpleIncluder.includeURLWithoutFallback(configIncludeContext, uRL);
        if (this.fallback != null && this.fallback instanceof ConfigIncluderURL) {
            return configObject.withFallback(((ConfigIncluderURL)((Object)this.fallback)).includeURL(configIncludeContext, uRL));
        }
        return configObject;
    }

    static ConfigObject includeURLWithoutFallback(ConfigIncludeContext configIncludeContext, URL uRL) {
        return ConfigFactory.parseURL(uRL, configIncludeContext.parseOptions()).root();
    }

    @Override
    public ConfigObject includeFile(ConfigIncludeContext configIncludeContext, File file) {
        ConfigObject configObject = SimpleIncluder.includeFileWithoutFallback(configIncludeContext, file);
        if (this.fallback != null && this.fallback instanceof ConfigIncluderFile) {
            return configObject.withFallback(((ConfigIncluderFile)((Object)this.fallback)).includeFile(configIncludeContext, file));
        }
        return configObject;
    }

    static ConfigObject includeFileWithoutFallback(ConfigIncludeContext configIncludeContext, File file) {
        return ConfigFactory.parseFileAnySyntax(file, configIncludeContext.parseOptions()).root();
    }

    @Override
    public ConfigObject includeResources(ConfigIncludeContext configIncludeContext, String string) {
        ConfigObject configObject = SimpleIncluder.includeResourceWithoutFallback(configIncludeContext, string);
        if (this.fallback != null && this.fallback instanceof ConfigIncluderClasspath) {
            return configObject.withFallback(((ConfigIncluderClasspath)((Object)this.fallback)).includeResources(configIncludeContext, string));
        }
        return configObject;
    }

    static ConfigObject includeResourceWithoutFallback(ConfigIncludeContext configIncludeContext, String string) {
        return ConfigFactory.parseResourcesAnySyntax(string, configIncludeContext.parseOptions()).root();
    }

    @Override
    public ConfigIncluder withFallback(ConfigIncluder configIncluder) {
        if (this == configIncluder) {
            throw new ConfigException.BugOrBroken("trying to create includer cycle");
        }
        if (this.fallback == configIncluder) {
            return this;
        }
        if (this.fallback != null) {
            return new SimpleIncluder(this.fallback.withFallback(configIncluder));
        }
        return new SimpleIncluder(configIncluder);
    }

    static ConfigObject fromBasename(NameSource nameSource, String string, ConfigParseOptions configParseOptions) {
        ConfigObject configObject;
        if (string.endsWith(".conf") || string.endsWith(".json") || string.endsWith(".properties")) {
            ConfigParseable configParseable = nameSource.nameToParseable(string, configParseOptions);
            configObject = configParseable.parse(configParseable.options().setAllowMissing(configParseOptions.getAllowMissing()));
        } else {
            Object object;
            ConfigParseable configParseable = nameSource.nameToParseable(string + ".conf", configParseOptions);
            ConfigParseable configParseable2 = nameSource.nameToParseable(string + ".json", configParseOptions);
            ConfigParseable configParseable3 = nameSource.nameToParseable(string + ".properties", configParseOptions);
            boolean bl = false;
            ArrayList<ConfigException.IO> arrayList = new ArrayList<ConfigException.IO>();
            ConfigSyntax configSyntax = configParseOptions.getSyntax();
            configObject = SimpleConfigObject.empty(SimpleConfigOrigin.newSimple(string));
            if (configSyntax == null || configSyntax == ConfigSyntax.CONF) {
                try {
                    configObject = configParseable.parse(configParseable.options().setAllowMissing(false).setSyntax(ConfigSyntax.CONF));
                    bl = true;
                } catch (ConfigException.IO iO) {
                    arrayList.add(iO);
                }
            }
            if (configSyntax == null || configSyntax == ConfigSyntax.JSON) {
                try {
                    object = configParseable2.parse(configParseable2.options().setAllowMissing(false).setSyntax(ConfigSyntax.JSON));
                    configObject = configObject.withFallback((ConfigMergeable)object);
                    bl = true;
                } catch (ConfigException.IO iO) {
                    arrayList.add(iO);
                }
            }
            if (configSyntax == null || configSyntax == ConfigSyntax.PROPERTIES) {
                try {
                    object = configParseable3.parse(configParseable3.options().setAllowMissing(false).setSyntax(ConfigSyntax.PROPERTIES));
                    configObject = configObject.withFallback((ConfigMergeable)object);
                    bl = true;
                } catch (ConfigException.IO iO) {
                    arrayList.add(iO);
                }
            }
            if (!configParseOptions.getAllowMissing() && !bl) {
                if (ConfigImpl.traceLoadsEnabled()) {
                    ConfigImpl.trace("Did not find '" + string + "' with any extension (.conf, .json, .properties); exceptions should have been logged above.");
                }
                if (arrayList.isEmpty()) {
                    throw new ConfigException.BugOrBroken("should not be reached: nothing found but no exceptions thrown");
                }
                object = new StringBuilder();
                for (Throwable throwable : arrayList) {
                    ((StringBuilder)object).append(throwable.getMessage());
                    ((StringBuilder)object).append(", ");
                }
                ((StringBuilder)object).setLength(((StringBuilder)object).length() - 2);
                throw new ConfigException.IO(SimpleConfigOrigin.newSimple(string), ((StringBuilder)object).toString(), (Throwable)arrayList.get(0));
            }
            if (!bl && ConfigImpl.traceLoadsEnabled()) {
                ConfigImpl.trace("Did not find '" + string + "' with any extension (.conf, .json, .properties); but '" + string + "' is allowed to be missing. Exceptions from load attempts should have been logged above.");
            }
        }
        return configObject;
    }

    static FullIncluder makeFull(ConfigIncluder configIncluder) {
        if (configIncluder instanceof FullIncluder) {
            return (FullIncluder)configIncluder;
        }
        return new Proxy(configIncluder);
    }

    private static class Proxy
    implements FullIncluder {
        final ConfigIncluder delegate;

        Proxy(ConfigIncluder configIncluder) {
            this.delegate = configIncluder;
        }

        @Override
        public ConfigIncluder withFallback(ConfigIncluder configIncluder) {
            return this;
        }

        @Override
        public ConfigObject include(ConfigIncludeContext configIncludeContext, String string) {
            return this.delegate.include(configIncludeContext, string);
        }

        @Override
        public ConfigObject includeResources(ConfigIncludeContext configIncludeContext, String string) {
            if (this.delegate instanceof ConfigIncluderClasspath) {
                return ((ConfigIncluderClasspath)((Object)this.delegate)).includeResources(configIncludeContext, string);
            }
            return SimpleIncluder.includeResourceWithoutFallback(configIncludeContext, string);
        }

        @Override
        public ConfigObject includeURL(ConfigIncludeContext configIncludeContext, URL uRL) {
            if (this.delegate instanceof ConfigIncluderURL) {
                return ((ConfigIncluderURL)((Object)this.delegate)).includeURL(configIncludeContext, uRL);
            }
            return SimpleIncluder.includeURLWithoutFallback(configIncludeContext, uRL);
        }

        @Override
        public ConfigObject includeFile(ConfigIncludeContext configIncludeContext, File file) {
            if (this.delegate instanceof ConfigIncluderFile) {
                return ((ConfigIncluderFile)((Object)this.delegate)).includeFile(configIncludeContext, file);
            }
            return SimpleIncluder.includeFileWithoutFallback(configIncludeContext, file);
        }
    }

    private static class RelativeNameSource
    implements NameSource {
        private final ConfigIncludeContext context;

        RelativeNameSource(ConfigIncludeContext configIncludeContext) {
            this.context = configIncludeContext;
        }

        @Override
        public ConfigParseable nameToParseable(String string, ConfigParseOptions configParseOptions) {
            ConfigParseable configParseable = this.context.relativeTo(string);
            if (configParseable == null) {
                return Parseable.newNotFound(string, "include was not found: '" + string + "'", configParseOptions);
            }
            return configParseable;
        }
    }

    static interface NameSource {
        public ConfigParseable nameToParseable(String var1, ConfigParseOptions var2);
    }
}

