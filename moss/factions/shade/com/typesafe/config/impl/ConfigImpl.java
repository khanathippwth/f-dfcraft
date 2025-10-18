/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigIncluder;
import moss.factions.shade.com.typesafe.config.ConfigMemorySize;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigParseable;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigBoolean;
import moss.factions.shade.com.typesafe.config.impl.ConfigDouble;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.ConfigInt;
import moss.factions.shade.com.typesafe.config.impl.ConfigLong;
import moss.factions.shade.com.typesafe.config.impl.ConfigNull;
import moss.factions.shade.com.typesafe.config.impl.ConfigNumber;
import moss.factions.shade.com.typesafe.config.impl.ConfigString;
import moss.factions.shade.com.typesafe.config.impl.FromMapMode;
import moss.factions.shade.com.typesafe.config.impl.Parseable;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.PropertiesParser;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigList;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.SimpleIncluder;

public class ConfigImpl {
    private static final String ENV_VAR_OVERRIDE_PREFIX = "CONFIG_FORCE_";
    private static final ConfigOrigin defaultValueOrigin = SimpleConfigOrigin.newSimple("hardcoded value");
    private static final ConfigBoolean defaultTrueValue = new ConfigBoolean(defaultValueOrigin, true);
    private static final ConfigBoolean defaultFalseValue = new ConfigBoolean(defaultValueOrigin, false);
    private static final ConfigNull defaultNullValue = new ConfigNull(defaultValueOrigin);
    private static final SimpleConfigList defaultEmptyList = new SimpleConfigList(defaultValueOrigin, Collections.emptyList());
    private static final SimpleConfigObject defaultEmptyObject = SimpleConfigObject.empty(defaultValueOrigin);

    public static Config computeCachedConfig(ClassLoader classLoader, String string, Callable<Config> callable) {
        LoaderCache loaderCache;
        try {
            loaderCache = LoaderCacheHolder.cache;
        } catch (ExceptionInInitializerError exceptionInInitializerError) {
            throw ConfigImplUtil.extractInitializerError(exceptionInInitializerError);
        }
        return loaderCache.getOrElseUpdate(classLoader, string, callable);
    }

    public static ConfigObject parseResourcesAnySyntax(Class<?> clazz, String string, ConfigParseOptions configParseOptions) {
        ClasspathNameSourceWithClass classpathNameSourceWithClass = new ClasspathNameSourceWithClass(clazz);
        return SimpleIncluder.fromBasename(classpathNameSourceWithClass, string, configParseOptions);
    }

    public static ConfigObject parseResourcesAnySyntax(String string, ConfigParseOptions configParseOptions) {
        ClasspathNameSource classpathNameSource = new ClasspathNameSource();
        return SimpleIncluder.fromBasename(classpathNameSource, string, configParseOptions);
    }

    public static ConfigObject parseFileAnySyntax(File file, ConfigParseOptions configParseOptions) {
        FileNameSource fileNameSource = new FileNameSource();
        return SimpleIncluder.fromBasename(fileNameSource, file.getPath(), configParseOptions);
    }

    static AbstractConfigObject emptyObject(String string) {
        SimpleConfigOrigin simpleConfigOrigin = string != null ? SimpleConfigOrigin.newSimple(string) : null;
        return ConfigImpl.emptyObject(simpleConfigOrigin);
    }

    public static Config emptyConfig(String string) {
        return ConfigImpl.emptyObject(string).toConfig();
    }

    static AbstractConfigObject empty(ConfigOrigin configOrigin) {
        return ConfigImpl.emptyObject(configOrigin);
    }

    private static SimpleConfigList emptyList(ConfigOrigin configOrigin) {
        if (configOrigin == null || configOrigin == defaultValueOrigin) {
            return defaultEmptyList;
        }
        return new SimpleConfigList(configOrigin, Collections.emptyList());
    }

    private static AbstractConfigObject emptyObject(ConfigOrigin configOrigin) {
        if (configOrigin == defaultValueOrigin) {
            return defaultEmptyObject;
        }
        return SimpleConfigObject.empty(configOrigin);
    }

    private static ConfigOrigin valueOrigin(String string) {
        if (string == null) {
            return defaultValueOrigin;
        }
        return SimpleConfigOrigin.newSimple(string);
    }

    public static ConfigValue fromAnyRef(Object object, String string) {
        ConfigOrigin configOrigin = ConfigImpl.valueOrigin(string);
        return ConfigImpl.fromAnyRef(object, configOrigin, FromMapMode.KEYS_ARE_KEYS);
    }

    public static ConfigObject fromPathMap(Map<String, ? extends Object> map, String string) {
        ConfigOrigin configOrigin = ConfigImpl.valueOrigin(string);
        return (ConfigObject)((Object)ConfigImpl.fromAnyRef(map, configOrigin, FromMapMode.KEYS_ARE_PATHS));
    }

    static AbstractConfigValue fromAnyRef(Object object, ConfigOrigin configOrigin, FromMapMode fromMapMode) {
        if (configOrigin == null) {
            throw new ConfigException.BugOrBroken("origin not supposed to be null");
        }
        if (object == null) {
            if (configOrigin != defaultValueOrigin) {
                return new ConfigNull(configOrigin);
            }
            return defaultNullValue;
        }
        if (object instanceof AbstractConfigValue) {
            return (AbstractConfigValue)object;
        }
        if (object instanceof Boolean) {
            if (configOrigin != defaultValueOrigin) {
                return new ConfigBoolean(configOrigin, (Boolean)object);
            }
            if (((Boolean)object).booleanValue()) {
                return defaultTrueValue;
            }
            return defaultFalseValue;
        }
        if (object instanceof String) {
            return new ConfigString.Quoted(configOrigin, (String)object);
        }
        if (object instanceof Number) {
            if (object instanceof Double) {
                return new ConfigDouble(configOrigin, (Double)object, null);
            }
            if (object instanceof Integer) {
                return new ConfigInt(configOrigin, (Integer)object, null);
            }
            if (object instanceof Long) {
                return new ConfigLong(configOrigin, (Long)object, null);
            }
            return ConfigNumber.newNumber(configOrigin, ((Number)object).doubleValue(), null);
        }
        if (object instanceof Duration) {
            return new ConfigLong(configOrigin, ((Duration)object).toMillis(), null);
        }
        if (object instanceof Map) {
            if (((Map)object).isEmpty()) {
                return ConfigImpl.emptyObject(configOrigin);
            }
            if (fromMapMode == FromMapMode.KEYS_ARE_KEYS) {
                HashMap<String, AbstractConfigValue> hashMap = new HashMap<String, AbstractConfigValue>();
                for (Map.Entry entry : ((Map)object).entrySet()) {
                    Object k = entry.getKey();
                    if (!(k instanceof String)) {
                        throw new ConfigException.BugOrBroken("bug in method caller: not valid to create ConfigObject from map with non-String key: " + k);
                    }
                    AbstractConfigValue abstractConfigValue = ConfigImpl.fromAnyRef(entry.getValue(), configOrigin, fromMapMode);
                    hashMap.put((String)k, abstractConfigValue);
                }
                return new SimpleConfigObject(configOrigin, hashMap);
            }
            return PropertiesParser.fromPathMap(configOrigin, (Map)object);
        }
        if (object instanceof Iterable) {
            Iterator iterator = ((Iterable)object).iterator();
            if (!iterator.hasNext()) {
                return ConfigImpl.emptyList(configOrigin);
            }
            ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
            while (iterator.hasNext()) {
                AbstractConfigValue abstractConfigValue = ConfigImpl.fromAnyRef(iterator.next(), configOrigin, fromMapMode);
                arrayList.add(abstractConfigValue);
            }
            return new SimpleConfigList(configOrigin, arrayList);
        }
        if (object instanceof ConfigMemorySize) {
            return new ConfigLong(configOrigin, ((ConfigMemorySize)object).toBytes(), null);
        }
        throw new ConfigException.BugOrBroken("bug in method caller: not valid to create ConfigValue from: " + object);
    }

    static ConfigIncluder defaultIncluder() {
        try {
            return DefaultIncluderHolder.defaultIncluder;
        } catch (ExceptionInInitializerError exceptionInInitializerError) {
            throw ConfigImplUtil.extractInitializerError(exceptionInInitializerError);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Properties getSystemProperties() {
        Properties properties = System.getProperties();
        Properties properties2 = new Properties();
        Properties properties3 = properties;
        synchronized (properties3) {
            for (Map.Entry entry : properties.entrySet()) {
                if (entry.getKey().toString().startsWith("java.version.")) continue;
                properties2.put(entry.getKey(), entry.getValue());
            }
        }
        return properties2;
    }

    private static AbstractConfigObject loadSystemProperties() {
        return (AbstractConfigObject)Parseable.newProperties(ConfigImpl.getSystemProperties(), ConfigParseOptions.defaults().setOriginDescription("system properties")).parse();
    }

    static AbstractConfigObject systemPropertiesAsConfigObject() {
        try {
            return SystemPropertiesHolder.systemProperties;
        } catch (ExceptionInInitializerError exceptionInInitializerError) {
            throw ConfigImplUtil.extractInitializerError(exceptionInInitializerError);
        }
    }

    public static Config systemPropertiesAsConfig() {
        return ConfigImpl.systemPropertiesAsConfigObject().toConfig();
    }

    public static void reloadSystemPropertiesConfig() {
        SystemPropertiesHolder.systemProperties = ConfigImpl.loadSystemProperties();
    }

    private static AbstractConfigObject loadEnvVariables() {
        return PropertiesParser.fromStringMap(ConfigImpl.newSimpleOrigin("env variables"), System.getenv());
    }

    static AbstractConfigObject envVariablesAsConfigObject() {
        try {
            return EnvVariablesHolder.envVariables;
        } catch (ExceptionInInitializerError exceptionInInitializerError) {
            throw ConfigImplUtil.extractInitializerError(exceptionInInitializerError);
        }
    }

    public static Config envVariablesAsConfig() {
        return ConfigImpl.envVariablesAsConfigObject().toConfig();
    }

    public static void reloadEnvVariablesConfig() {
        EnvVariablesHolder.envVariables = ConfigImpl.loadEnvVariables();
    }

    private static AbstractConfigObject loadEnvVariablesOverrides() {
        HashMap<String, String> hashMap = new HashMap<String, String>(System.getenv());
        HashMap<String, String> hashMap2 = new HashMap<String, String>(System.getenv());
        for (String string : hashMap.keySet()) {
            if (!string.startsWith(ENV_VAR_OVERRIDE_PREFIX)) continue;
            hashMap2.put(ConfigImplUtil.envVariableAsProperty(string, ENV_VAR_OVERRIDE_PREFIX), (String)hashMap.get(string));
        }
        return PropertiesParser.fromStringMap(ConfigImpl.newSimpleOrigin("env variables overrides"), hashMap2);
    }

    static AbstractConfigObject envVariablesOverridesAsConfigObject() {
        try {
            return EnvVariablesOverridesHolder.envVariables;
        } catch (ExceptionInInitializerError exceptionInInitializerError) {
            throw ConfigImplUtil.extractInitializerError(exceptionInInitializerError);
        }
    }

    public static Config envVariablesOverridesAsConfig() {
        return ConfigImpl.envVariablesOverridesAsConfigObject().toConfig();
    }

    public static void reloadEnvVariablesOverridesConfig() {
        EnvVariablesOverridesHolder.envVariables = ConfigImpl.loadEnvVariablesOverrides();
    }

    public static Config defaultReference(final ClassLoader classLoader) {
        return ConfigImpl.computeCachedConfig(classLoader, "defaultReference", new Callable<Config>(){

            @Override
            public Config call() {
                Config config = ConfigImpl.unresolvedReference(classLoader);
                return ConfigImpl.systemPropertiesAsConfig().withFallback(config).resolve();
            }
        });
    }

    private static Config unresolvedReference(final ClassLoader classLoader) {
        return ConfigImpl.computeCachedConfig(classLoader, "unresolvedReference", new Callable<Config>(){

            @Override
            public Config call() {
                return Parseable.newResources("reference.conf", ConfigParseOptions.defaults().setClassLoader(classLoader)).parse().toConfig();
            }
        });
    }

    public static Config defaultReferenceUnresolved(ClassLoader classLoader) {
        try {
            ConfigImpl.defaultReference(classLoader);
        } catch (ConfigException.UnresolvedSubstitution unresolvedSubstitution) {
            throw unresolvedSubstitution.addExtraDetail("Could not resolve substitution in reference.conf to a value: %s. All reference.conf files are required to be fully, independently resolvable, and should not require the presence of values for substitutions from further up the hierarchy.");
        }
        return ConfigImpl.unresolvedReference(classLoader);
    }

    public static boolean traceLoadsEnabled() {
        try {
            return DebugHolder.traceLoadsEnabled();
        } catch (ExceptionInInitializerError exceptionInInitializerError) {
            throw ConfigImplUtil.extractInitializerError(exceptionInInitializerError);
        }
    }

    public static boolean traceSubstitutionsEnabled() {
        try {
            return DebugHolder.traceSubstitutionsEnabled();
        } catch (ExceptionInInitializerError exceptionInInitializerError) {
            throw ConfigImplUtil.extractInitializerError(exceptionInInitializerError);
        }
    }

    public static void trace(String string) {
        System.err.println(string);
    }

    public static void trace(int n, String string) {
        while (n > 0) {
            System.err.print("  ");
            --n;
        }
        System.err.println(string);
    }

    static ConfigException.NotResolved improveNotResolved(Path path, ConfigException.NotResolved notResolved) {
        String string = path.render() + " has not been resolved, you need to call Config#resolve(), see API docs for Config#resolve()";
        if (string.equals(notResolved.getMessage())) {
            return notResolved;
        }
        return new ConfigException.NotResolved(string, notResolved);
    }

    public static ConfigOrigin newSimpleOrigin(String string) {
        if (string == null) {
            return defaultValueOrigin;
        }
        return SimpleConfigOrigin.newSimple(string);
    }

    public static ConfigOrigin newFileOrigin(String string) {
        return SimpleConfigOrigin.newFile(string);
    }

    public static ConfigOrigin newURLOrigin(URL uRL) {
        return SimpleConfigOrigin.newURL(uRL);
    }

    static /* synthetic */ AbstractConfigObject access$000() {
        return ConfigImpl.loadSystemProperties();
    }

    static /* synthetic */ AbstractConfigObject access$100() {
        return ConfigImpl.loadEnvVariables();
    }

    static /* synthetic */ AbstractConfigObject access$200() {
        return ConfigImpl.loadEnvVariablesOverrides();
    }

    private static class DebugHolder {
        private static String LOADS = "loads";
        private static String SUBSTITUTIONS = "substitutions";
        private static final Map<String, Boolean> diagnostics = DebugHolder.loadDiagnostics();
        private static final boolean traceLoadsEnabled = diagnostics.get(LOADS);
        private static final boolean traceSubstitutionsEnabled = diagnostics.get(SUBSTITUTIONS);

        private DebugHolder() {
        }

        private static Map<String, Boolean> loadDiagnostics() {
            String[] stringArray;
            HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
            hashMap.put(LOADS, false);
            hashMap.put(SUBSTITUTIONS, false);
            String string = System.getProperty("config.trace");
            if (string == null) {
                return hashMap;
            }
            for (String string2 : stringArray = string.split(",")) {
                if (string2.equals(LOADS)) {
                    hashMap.put(LOADS, true);
                    continue;
                }
                if (string2.equals(SUBSTITUTIONS)) {
                    hashMap.put(SUBSTITUTIONS, true);
                    continue;
                }
                System.err.println("config.trace property contains unknown trace topic '" + string2 + "'");
            }
            return hashMap;
        }

        static boolean traceLoadsEnabled() {
            return traceLoadsEnabled;
        }

        static boolean traceSubstitutionsEnabled() {
            return traceSubstitutionsEnabled;
        }
    }

    private static class EnvVariablesOverridesHolder {
        static volatile AbstractConfigObject envVariables = ConfigImpl.access$200();

        private EnvVariablesOverridesHolder() {
        }
    }

    private static class EnvVariablesHolder {
        static volatile AbstractConfigObject envVariables = ConfigImpl.access$100();

        private EnvVariablesHolder() {
        }
    }

    private static class SystemPropertiesHolder {
        static volatile AbstractConfigObject systemProperties = ConfigImpl.access$000();

        private SystemPropertiesHolder() {
        }
    }

    private static class DefaultIncluderHolder {
        static final ConfigIncluder defaultIncluder = new SimpleIncluder(null);

        private DefaultIncluderHolder() {
        }
    }

    static class ClasspathNameSourceWithClass
    implements SimpleIncluder.NameSource {
        private final Class<?> klass;

        public ClasspathNameSourceWithClass(Class<?> clazz) {
            this.klass = clazz;
        }

        @Override
        public ConfigParseable nameToParseable(String string, ConfigParseOptions configParseOptions) {
            return Parseable.newResources(this.klass, string, configParseOptions);
        }
    }

    static class ClasspathNameSource
    implements SimpleIncluder.NameSource {
        ClasspathNameSource() {
        }

        @Override
        public ConfigParseable nameToParseable(String string, ConfigParseOptions configParseOptions) {
            return Parseable.newResources(string, configParseOptions);
        }
    }

    static class FileNameSource
    implements SimpleIncluder.NameSource {
        FileNameSource() {
        }

        @Override
        public ConfigParseable nameToParseable(String string, ConfigParseOptions configParseOptions) {
            return Parseable.newFile(new File(string), configParseOptions);
        }
    }

    private static class LoaderCacheHolder {
        static final LoaderCache cache = new LoaderCache();

        private LoaderCacheHolder() {
        }
    }

    private static class LoaderCache {
        private Config currentSystemProperties = null;
        private WeakReference<ClassLoader> currentLoader = new WeakReference<Object>(null);
        private Map<String, Config> cache = new HashMap<String, Config>();

        LoaderCache() {
        }

        synchronized Config getOrElseUpdate(ClassLoader classLoader, String string, Callable<Config> callable) {
            Config config;
            Config config2;
            if (classLoader != this.currentLoader.get()) {
                this.cache.clear();
                this.currentLoader = new WeakReference<ClassLoader>(classLoader);
            }
            if ((config2 = ConfigImpl.systemPropertiesAsConfig()) != this.currentSystemProperties) {
                this.cache.clear();
                this.currentSystemProperties = config2;
            }
            if ((config = this.cache.get(string)) == null) {
                try {
                    config = callable.call();
                } catch (RuntimeException runtimeException) {
                    throw runtimeException;
                } catch (Exception exception) {
                    throw new ConfigException.Generic(exception.getMessage(), exception);
                }
                if (config == null) {
                    throw new ConfigException.BugOrBroken("null config from cache updater");
                }
                this.cache.put(string, config);
            }
            return config;
        }
    }
}

