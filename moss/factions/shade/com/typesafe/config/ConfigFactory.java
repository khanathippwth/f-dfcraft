/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.io.File;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigLoadingStrategy;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigResolveOptions;
import moss.factions.shade.com.typesafe.config.DefaultConfigLoadingStrategy;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.Parseable;

public final class ConfigFactory {
    private static final String STRATEGY_PROPERTY_NAME = "config.strategy";
    private static final String OVERRIDE_WITH_ENV_PROPERTY_NAME = "config.override_with_env_vars";

    private ConfigFactory() {
    }

    public static Config load(String string) {
        return ConfigFactory.load(string, ConfigParseOptions.defaults(), ConfigResolveOptions.defaults());
    }

    public static Config load(ClassLoader classLoader, String string) {
        return ConfigFactory.load(string, ConfigParseOptions.defaults().setClassLoader(classLoader), ConfigResolveOptions.defaults());
    }

    public static Config load(String string, ConfigParseOptions configParseOptions, ConfigResolveOptions configResolveOptions) {
        ConfigParseOptions configParseOptions2 = ConfigFactory.ensureClassLoader(configParseOptions, "load");
        Config config = ConfigFactory.parseResourcesAnySyntax(string, configParseOptions2);
        return ConfigFactory.load(configParseOptions2.getClassLoader(), config, configResolveOptions);
    }

    public static Config load(ClassLoader classLoader, String string, ConfigParseOptions configParseOptions, ConfigResolveOptions configResolveOptions) {
        return ConfigFactory.load(string, configParseOptions.setClassLoader(classLoader), configResolveOptions);
    }

    private static ClassLoader checkedContextClassLoader(String string) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            throw new ConfigException.BugOrBroken("Context class loader is not set for the current thread; if Thread.currentThread().getContextClassLoader() returns null, you must pass a ClassLoader explicitly to ConfigFactory." + string);
        }
        return classLoader;
    }

    private static ConfigParseOptions ensureClassLoader(ConfigParseOptions configParseOptions, String string) {
        if (configParseOptions.getClassLoader() == null) {
            return configParseOptions.setClassLoader(ConfigFactory.checkedContextClassLoader(string));
        }
        return configParseOptions;
    }

    public static Config load(Config config) {
        return ConfigFactory.load(ConfigFactory.checkedContextClassLoader("load"), config);
    }

    public static Config load(ClassLoader classLoader, Config config) {
        return ConfigFactory.load(classLoader, config, ConfigResolveOptions.defaults());
    }

    public static Config load(Config config, ConfigResolveOptions configResolveOptions) {
        return ConfigFactory.load(ConfigFactory.checkedContextClassLoader("load"), config, configResolveOptions);
    }

    public static Config load(ClassLoader classLoader, Config config, ConfigResolveOptions configResolveOptions) {
        return ConfigFactory.defaultOverrides(classLoader).withFallback(config).withFallback(ConfigImpl.defaultReferenceUnresolved(classLoader)).resolve(configResolveOptions);
    }

    public static Config load() {
        ClassLoader classLoader = ConfigFactory.checkedContextClassLoader("load");
        return ConfigFactory.load(classLoader);
    }

    public static Config load(ConfigParseOptions configParseOptions) {
        return ConfigFactory.load(configParseOptions, ConfigResolveOptions.defaults());
    }

    public static Config load(final ClassLoader classLoader) {
        final ConfigParseOptions configParseOptions = ConfigParseOptions.defaults().setClassLoader(classLoader);
        return ConfigImpl.computeCachedConfig(classLoader, "load", new Callable<Config>(){

            @Override
            public Config call() {
                return ConfigFactory.load(classLoader, ConfigFactory.defaultApplication(configParseOptions));
            }
        });
    }

    public static Config load(ClassLoader classLoader, ConfigParseOptions configParseOptions) {
        return ConfigFactory.load(configParseOptions.setClassLoader(classLoader));
    }

    public static Config load(ClassLoader classLoader, ConfigResolveOptions configResolveOptions) {
        return ConfigFactory.load(classLoader, ConfigParseOptions.defaults(), configResolveOptions);
    }

    public static Config load(ClassLoader classLoader, ConfigParseOptions configParseOptions, ConfigResolveOptions configResolveOptions) {
        ConfigParseOptions configParseOptions2 = ConfigFactory.ensureClassLoader(configParseOptions, "load");
        return ConfigFactory.load(classLoader, ConfigFactory.defaultApplication(configParseOptions2), configResolveOptions);
    }

    public static Config load(ConfigParseOptions configParseOptions, ConfigResolveOptions configResolveOptions) {
        ConfigParseOptions configParseOptions2 = ConfigFactory.ensureClassLoader(configParseOptions, "load");
        return ConfigFactory.load(ConfigFactory.defaultApplication(configParseOptions2), configResolveOptions);
    }

    public static Config defaultReference() {
        return ConfigFactory.defaultReference(ConfigFactory.checkedContextClassLoader("defaultReference"));
    }

    public static Config defaultReference(ClassLoader classLoader) {
        return ConfigImpl.defaultReference(classLoader);
    }

    public static Config defaultReferenceUnresolved() {
        return ConfigFactory.defaultReferenceUnresolved(ConfigFactory.checkedContextClassLoader("defaultReferenceUnresolved"));
    }

    public static Config defaultReferenceUnresolved(ClassLoader classLoader) {
        return ConfigImpl.defaultReferenceUnresolved(classLoader);
    }

    public static Config defaultOverrides() {
        if (ConfigFactory.getOverrideWithEnv().booleanValue()) {
            return ConfigFactory.systemEnvironmentOverrides().withFallback(ConfigFactory.systemProperties());
        }
        return ConfigFactory.systemProperties();
    }

    public static Config defaultOverrides(ClassLoader classLoader) {
        return ConfigFactory.defaultOverrides();
    }

    public static Config defaultApplication() {
        return ConfigFactory.defaultApplication(ConfigParseOptions.defaults());
    }

    public static Config defaultApplication(ClassLoader classLoader) {
        return ConfigFactory.defaultApplication(ConfigParseOptions.defaults().setClassLoader(classLoader));
    }

    public static Config defaultApplication(ConfigParseOptions configParseOptions) {
        return ConfigFactory.getConfigLoadingStrategy().parseApplicationConfig(ConfigFactory.ensureClassLoader(configParseOptions, "defaultApplication"));
    }

    public static void invalidateCaches() {
        ConfigImpl.reloadSystemPropertiesConfig();
        ConfigImpl.reloadEnvVariablesConfig();
        ConfigImpl.reloadEnvVariablesOverridesConfig();
    }

    public static Config empty() {
        return ConfigFactory.empty(null);
    }

    public static Config empty(String string) {
        return ConfigImpl.emptyConfig(string);
    }

    public static Config systemProperties() {
        return ConfigImpl.systemPropertiesAsConfig();
    }

    public static Config systemEnvironmentOverrides() {
        return ConfigImpl.envVariablesOverridesAsConfig();
    }

    public static Config systemEnvironment() {
        return ConfigImpl.envVariablesAsConfig();
    }

    public static Config parseProperties(Properties properties, ConfigParseOptions configParseOptions) {
        return Parseable.newProperties(properties, configParseOptions).parse().toConfig();
    }

    public static Config parseProperties(Properties properties) {
        return ConfigFactory.parseProperties(properties, ConfigParseOptions.defaults());
    }

    public static Config parseReader(Reader reader, ConfigParseOptions configParseOptions) {
        return Parseable.newReader(reader, configParseOptions).parse().toConfig();
    }

    public static Config parseReader(Reader reader) {
        return ConfigFactory.parseReader(reader, ConfigParseOptions.defaults());
    }

    public static Config parseURL(URL uRL, ConfigParseOptions configParseOptions) {
        return Parseable.newURL(uRL, configParseOptions).parse().toConfig();
    }

    public static Config parseURL(URL uRL) {
        return ConfigFactory.parseURL(uRL, ConfigParseOptions.defaults());
    }

    public static Config parseFile(File file, ConfigParseOptions configParseOptions) {
        return Parseable.newFile(file, configParseOptions).parse().toConfig();
    }

    public static Config parseFile(File file) {
        return ConfigFactory.parseFile(file, ConfigParseOptions.defaults());
    }

    public static Config parseFileAnySyntax(File file, ConfigParseOptions configParseOptions) {
        return ConfigImpl.parseFileAnySyntax(file, configParseOptions).toConfig();
    }

    public static Config parseFileAnySyntax(File file) {
        return ConfigFactory.parseFileAnySyntax(file, ConfigParseOptions.defaults());
    }

    public static Config parseResources(Class<?> clazz, String string, ConfigParseOptions configParseOptions) {
        return Parseable.newResources(clazz, string, configParseOptions).parse().toConfig();
    }

    public static Config parseResources(Class<?> clazz, String string) {
        return ConfigFactory.parseResources(clazz, string, ConfigParseOptions.defaults());
    }

    public static Config parseResourcesAnySyntax(Class<?> clazz, String string, ConfigParseOptions configParseOptions) {
        return ConfigImpl.parseResourcesAnySyntax(clazz, string, configParseOptions).toConfig();
    }

    public static Config parseResourcesAnySyntax(Class<?> clazz, String string) {
        return ConfigFactory.parseResourcesAnySyntax(clazz, string, ConfigParseOptions.defaults());
    }

    public static Config parseResources(ClassLoader classLoader, String string, ConfigParseOptions configParseOptions) {
        return ConfigFactory.parseResources(string, configParseOptions.setClassLoader(classLoader));
    }

    public static Config parseResources(ClassLoader classLoader, String string) {
        return ConfigFactory.parseResources(classLoader, string, ConfigParseOptions.defaults());
    }

    public static Config parseResourcesAnySyntax(ClassLoader classLoader, String string, ConfigParseOptions configParseOptions) {
        return ConfigImpl.parseResourcesAnySyntax(string, configParseOptions.setClassLoader(classLoader)).toConfig();
    }

    public static Config parseResourcesAnySyntax(ClassLoader classLoader, String string) {
        return ConfigFactory.parseResourcesAnySyntax(classLoader, string, ConfigParseOptions.defaults());
    }

    public static Config parseResources(String string, ConfigParseOptions configParseOptions) {
        ConfigParseOptions configParseOptions2 = ConfigFactory.ensureClassLoader(configParseOptions, "parseResources");
        return Parseable.newResources(string, configParseOptions2).parse().toConfig();
    }

    public static Config parseResources(String string) {
        return ConfigFactory.parseResources(string, ConfigParseOptions.defaults());
    }

    public static Config parseResourcesAnySyntax(String string, ConfigParseOptions configParseOptions) {
        return ConfigImpl.parseResourcesAnySyntax(string, configParseOptions).toConfig();
    }

    public static Config parseResourcesAnySyntax(String string) {
        return ConfigFactory.parseResourcesAnySyntax(string, ConfigParseOptions.defaults());
    }

    public static Config parseString(String string, ConfigParseOptions configParseOptions) {
        return Parseable.newString(string, configParseOptions).parse().toConfig();
    }

    public static Config parseString(String string) {
        return ConfigFactory.parseString(string, ConfigParseOptions.defaults());
    }

    public static Config parseMap(Map<String, ? extends Object> map, String string) {
        return ConfigImpl.fromPathMap(map, string).toConfig();
    }

    public static Config parseMap(Map<String, ? extends Object> map) {
        return ConfigFactory.parseMap(map, null);
    }

    private static ConfigLoadingStrategy getConfigLoadingStrategy() {
        String string = System.getProperties().getProperty(STRATEGY_PROPERTY_NAME);
        if (string != null) {
            try {
                return (ConfigLoadingStrategy)ConfigLoadingStrategy.class.cast(Class.forName(string).newInstance());
            } catch (Throwable throwable) {
                throw new ConfigException.BugOrBroken("Failed to load strategy: " + string, throwable);
            }
        }
        return new DefaultConfigLoadingStrategy();
    }

    private static Boolean getOverrideWithEnv() {
        String string = System.getProperties().getProperty(OVERRIDE_WITH_ENV_PROPERTY_NAME);
        return Boolean.parseBoolean(string);
    }
}

