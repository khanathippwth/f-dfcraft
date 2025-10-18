/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigList;
import moss.factions.shade.com.typesafe.config.ConfigMemorySize;
import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigResolveOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.ConfigNull;
import moss.factions.shade.com.typesafe.config.impl.ConfigNumber;
import moss.factions.shade.com.typesafe.config.impl.ConfigString;
import moss.factions.shade.com.typesafe.config.impl.DefaultTransformer;
import moss.factions.shade.com.typesafe.config.impl.MergeableValue;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigList;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;

final class SimpleConfig
implements Config,
MergeableValue,
Serializable {
    private static final long serialVersionUID = 1L;
    private final AbstractConfigObject object;

    SimpleConfig(AbstractConfigObject abstractConfigObject) {
        this.object = abstractConfigObject;
    }

    @Override
    public AbstractConfigObject root() {
        return this.object;
    }

    @Override
    public ConfigOrigin origin() {
        return this.object.origin();
    }

    @Override
    public SimpleConfig resolve() {
        return this.resolve(ConfigResolveOptions.defaults());
    }

    @Override
    public SimpleConfig resolve(ConfigResolveOptions configResolveOptions) {
        return this.resolveWith(this, configResolveOptions);
    }

    @Override
    public SimpleConfig resolveWith(Config config) {
        return this.resolveWith(config, ConfigResolveOptions.defaults());
    }

    @Override
    public SimpleConfig resolveWith(Config config, ConfigResolveOptions configResolveOptions) {
        AbstractConfigValue abstractConfigValue = ResolveContext.resolve(this.object, ((SimpleConfig)config).object, configResolveOptions);
        if (abstractConfigValue == this.object) {
            return this;
        }
        return new SimpleConfig((AbstractConfigObject)abstractConfigValue);
    }

    private ConfigValue hasPathPeek(String string) {
        AbstractConfigValue abstractConfigValue;
        Path path = Path.newPath(string);
        try {
            abstractConfigValue = this.object.peekPath(path);
        } catch (ConfigException.NotResolved notResolved) {
            throw ConfigImpl.improveNotResolved(path, notResolved);
        }
        return abstractConfigValue;
    }

    @Override
    public boolean hasPath(String string) {
        ConfigValue configValue = this.hasPathPeek(string);
        return configValue != null && configValue.valueType() != ConfigValueType.NULL;
    }

    @Override
    public boolean hasPathOrNull(String string) {
        ConfigValue configValue = this.hasPathPeek(string);
        return configValue != null;
    }

    @Override
    public boolean isEmpty() {
        return this.object.isEmpty();
    }

    private static void findPaths(Set<Map.Entry<String, ConfigValue>> set, Path path, AbstractConfigObject abstractConfigObject) {
        for (Map.Entry entry : abstractConfigObject.entrySet()) {
            String string = (String)entry.getKey();
            ConfigValue configValue = (ConfigValue)entry.getValue();
            Path path2 = Path.newKey(string);
            if (path != null) {
                path2 = path2.prepend(path);
            }
            if (configValue instanceof AbstractConfigObject) {
                SimpleConfig.findPaths(set, path2, (AbstractConfigObject)configValue);
                continue;
            }
            if (configValue instanceof ConfigNull) continue;
            set.add(new AbstractMap.SimpleImmutableEntry<String, ConfigValue>(path2.render(), configValue));
        }
    }

    @Override
    public Set<Map.Entry<String, ConfigValue>> entrySet() {
        HashSet<Map.Entry<String, ConfigValue>> hashSet = new HashSet<Map.Entry<String, ConfigValue>>();
        SimpleConfig.findPaths(hashSet, null, this.object);
        return hashSet;
    }

    private static AbstractConfigValue throwIfNull(AbstractConfigValue abstractConfigValue, ConfigValueType configValueType, Path path) {
        if (abstractConfigValue.valueType() == ConfigValueType.NULL) {
            throw new ConfigException.Null((ConfigOrigin)abstractConfigValue.origin(), path.render(), configValueType != null ? configValueType.name() : null);
        }
        return abstractConfigValue;
    }

    private static AbstractConfigValue findKey(AbstractConfigObject abstractConfigObject, String string, ConfigValueType configValueType, Path path) {
        return SimpleConfig.throwIfNull(SimpleConfig.findKeyOrNull(abstractConfigObject, string, configValueType, path), configValueType, path);
    }

    private static AbstractConfigValue findKeyOrNull(AbstractConfigObject abstractConfigObject, String string, ConfigValueType configValueType, Path path) {
        AbstractConfigValue abstractConfigValue = abstractConfigObject.peekAssumingResolved(string, path);
        if (abstractConfigValue == null) {
            throw new ConfigException.Missing(abstractConfigObject.origin(), path.render());
        }
        if (configValueType != null) {
            abstractConfigValue = DefaultTransformer.transform(abstractConfigValue, configValueType);
        }
        if (configValueType != null && abstractConfigValue.valueType() != configValueType && abstractConfigValue.valueType() != ConfigValueType.NULL) {
            throw new ConfigException.WrongType(abstractConfigValue.origin(), path.render(), configValueType.name(), abstractConfigValue.valueType().name());
        }
        return abstractConfigValue;
    }

    private static AbstractConfigValue findOrNull(AbstractConfigObject abstractConfigObject, Path path, ConfigValueType configValueType, Path path2) {
        try {
            String string = path.first();
            Path path3 = path.remainder();
            if (path3 == null) {
                return SimpleConfig.findKeyOrNull(abstractConfigObject, string, configValueType, path2);
            }
            AbstractConfigObject abstractConfigObject2 = (AbstractConfigObject)SimpleConfig.findKey(abstractConfigObject, string, ConfigValueType.OBJECT, path2.subPath(0, path2.length() - path3.length()));
            assert (abstractConfigObject2 != null);
            return SimpleConfig.findOrNull(abstractConfigObject2, path3, configValueType, path2);
        } catch (ConfigException.NotResolved notResolved) {
            throw ConfigImpl.improveNotResolved(path, notResolved);
        }
    }

    AbstractConfigValue find(Path path, ConfigValueType configValueType, Path path2) {
        return SimpleConfig.throwIfNull(SimpleConfig.findOrNull(this.object, path, configValueType, path2), configValueType, path2);
    }

    AbstractConfigValue find(String string, ConfigValueType configValueType) {
        Path path = Path.newPath(string);
        return this.find(path, configValueType, path);
    }

    private AbstractConfigValue findOrNull(Path path, ConfigValueType configValueType, Path path2) {
        return SimpleConfig.findOrNull(this.object, path, configValueType, path2);
    }

    private AbstractConfigValue findOrNull(String string, ConfigValueType configValueType) {
        Path path = Path.newPath(string);
        return this.findOrNull(path, configValueType, path);
    }

    @Override
    public AbstractConfigValue getValue(String string) {
        return this.find(string, null);
    }

    @Override
    public boolean getIsNull(String string) {
        AbstractConfigValue abstractConfigValue = this.findOrNull(string, null);
        return abstractConfigValue.valueType() == ConfigValueType.NULL;
    }

    @Override
    public boolean getBoolean(String string) {
        AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.BOOLEAN);
        return (Boolean)abstractConfigValue.unwrapped();
    }

    private ConfigNumber getConfigNumber(String string) {
        AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.NUMBER);
        return (ConfigNumber)abstractConfigValue;
    }

    @Override
    public Number getNumber(String string) {
        return this.getConfigNumber(string).unwrapped();
    }

    @Override
    public int getInt(String string) {
        ConfigNumber configNumber = this.getConfigNumber(string);
        return configNumber.intValueRangeChecked(string);
    }

    @Override
    public long getLong(String string) {
        return this.getNumber(string).longValue();
    }

    @Override
    public double getDouble(String string) {
        return this.getNumber(string).doubleValue();
    }

    @Override
    public String getString(String string) {
        AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.STRING);
        return (String)abstractConfigValue.unwrapped();
    }

    @Override
    public <T extends Enum<T>> T getEnum(Class<T> clazz, String string) {
        AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.STRING);
        return this.getEnumValue(string, clazz, abstractConfigValue);
    }

    @Override
    public ConfigList getList(String string) {
        AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.LIST);
        return (ConfigList)((Object)abstractConfigValue);
    }

    @Override
    public AbstractConfigObject getObject(String string) {
        AbstractConfigObject abstractConfigObject = (AbstractConfigObject)this.find(string, ConfigValueType.OBJECT);
        return abstractConfigObject;
    }

    @Override
    public SimpleConfig getConfig(String string) {
        return this.getObject(string).toConfig();
    }

    @Override
    public Object getAnyRef(String string) {
        AbstractConfigValue abstractConfigValue = this.find(string, null);
        return abstractConfigValue.unwrapped();
    }

    @Override
    public Long getBytes(String string) {
        Long l = null;
        try {
            l = this.getLong(string);
        } catch (ConfigException.WrongType wrongType) {
            AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.STRING);
            l = SimpleConfig.parseBytes((String)abstractConfigValue.unwrapped(), abstractConfigValue.origin(), string);
        }
        return l;
    }

    @Override
    public ConfigMemorySize getMemorySize(String string) {
        return ConfigMemorySize.ofBytes(this.getBytes(string));
    }

    @Override
    @Deprecated
    public Long getMilliseconds(String string) {
        return this.getDuration(string, TimeUnit.MILLISECONDS);
    }

    @Override
    @Deprecated
    public Long getNanoseconds(String string) {
        return this.getDuration(string, TimeUnit.NANOSECONDS);
    }

    @Override
    public long getDuration(String string, TimeUnit timeUnit) {
        AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.STRING);
        long l = timeUnit.convert(SimpleConfig.parseDuration((String)abstractConfigValue.unwrapped(), abstractConfigValue.origin(), string), TimeUnit.NANOSECONDS);
        return l;
    }

    @Override
    public Duration getDuration(String string) {
        AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.STRING);
        long l = SimpleConfig.parseDuration((String)abstractConfigValue.unwrapped(), abstractConfigValue.origin(), string);
        return Duration.ofNanos(l);
    }

    @Override
    public Period getPeriod(String string) {
        AbstractConfigValue abstractConfigValue = this.find(string, ConfigValueType.STRING);
        return SimpleConfig.parsePeriod((String)abstractConfigValue.unwrapped(), abstractConfigValue.origin(), string);
    }

    @Override
    public TemporalAmount getTemporal(String string) {
        try {
            return this.getDuration(string);
        } catch (ConfigException.BadValue badValue) {
            return this.getPeriod(string);
        }
    }

    private <T> List<T> getHomogeneousUnwrappedList(String string, ConfigValueType configValueType) {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        ConfigList configList = this.getList(string);
        for (ConfigValue configValue : configList) {
            AbstractConfigValue abstractConfigValue = (AbstractConfigValue)configValue;
            if (configValueType != null) {
                abstractConfigValue = DefaultTransformer.transform(abstractConfigValue, configValueType);
            }
            if (abstractConfigValue.valueType() != configValueType) {
                throw new ConfigException.WrongType(abstractConfigValue.origin(), string, "list of " + configValueType.name(), "list of " + abstractConfigValue.valueType().name());
            }
            arrayList.add(abstractConfigValue.unwrapped());
        }
        return arrayList;
    }

    @Override
    public List<Boolean> getBooleanList(String string) {
        return this.getHomogeneousUnwrappedList(string, ConfigValueType.BOOLEAN);
    }

    @Override
    public List<Number> getNumberList(String string) {
        return this.getHomogeneousUnwrappedList(string, ConfigValueType.NUMBER);
    }

    @Override
    public List<Integer> getIntList(String string) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        List list = this.getHomogeneousWrappedList(string, ConfigValueType.NUMBER);
        for (AbstractConfigValue abstractConfigValue : list) {
            arrayList.add(((ConfigNumber)abstractConfigValue).intValueRangeChecked(string));
        }
        return arrayList;
    }

    @Override
    public List<Long> getLongList(String string) {
        ArrayList<Long> arrayList = new ArrayList<Long>();
        List<Number> list = this.getNumberList(string);
        for (Number number : list) {
            arrayList.add(number.longValue());
        }
        return arrayList;
    }

    @Override
    public List<Double> getDoubleList(String string) {
        ArrayList<Double> arrayList = new ArrayList<Double>();
        List<Number> list = this.getNumberList(string);
        for (Number number : list) {
            arrayList.add(number.doubleValue());
        }
        return arrayList;
    }

    @Override
    public List<String> getStringList(String string) {
        return this.getHomogeneousUnwrappedList(string, ConfigValueType.STRING);
    }

    @Override
    public <T extends Enum<T>> List<T> getEnumList(Class<T> clazz, String string) {
        List<T> list = this.getHomogeneousWrappedList(string, ConfigValueType.STRING);
        ArrayList<T> arrayList = new ArrayList<T>();
        for (ConfigString configString : list) {
            arrayList.add(this.getEnumValue(string, clazz, configString));
        }
        return arrayList;
    }

    private <T extends Enum<T>> T getEnumValue(String string, Class<T> clazz, ConfigValue configValue) {
        String string2 = (String)configValue.unwrapped();
        try {
            return Enum.valueOf(clazz, string2);
        } catch (IllegalArgumentException illegalArgumentException) {
            ArrayList<String> arrayList = new ArrayList<String>();
            Enum[] enumArray = (Enum[])clazz.getEnumConstants();
            if (enumArray != null) {
                for (Enum enum_ : enumArray) {
                    arrayList.add(enum_.name());
                }
            }
            throw new ConfigException.BadValue(configValue.origin(), string, String.format("The enum class %s has no constant of the name '%s' (should be one of %s.)", clazz.getSimpleName(), string2, arrayList));
        }
    }

    private <T extends ConfigValue> List<T> getHomogeneousWrappedList(String string, ConfigValueType configValueType) {
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
        ConfigList configList = this.getList(string);
        for (ConfigValue configValue : configList) {
            AbstractConfigValue abstractConfigValue = (AbstractConfigValue)configValue;
            if (configValueType != null) {
                abstractConfigValue = DefaultTransformer.transform(abstractConfigValue, configValueType);
            }
            if (abstractConfigValue.valueType() != configValueType) {
                throw new ConfigException.WrongType(abstractConfigValue.origin(), string, "list of " + configValueType.name(), "list of " + abstractConfigValue.valueType().name());
            }
            arrayList.add(abstractConfigValue);
        }
        return arrayList;
    }

    public List<ConfigObject> getObjectList(String string) {
        return this.getHomogeneousWrappedList(string, ConfigValueType.OBJECT);
    }

    @Override
    public List<? extends Config> getConfigList(String string) {
        List<ConfigObject> list = this.getObjectList(string);
        ArrayList<Config> arrayList = new ArrayList<Config>();
        for (ConfigObject configObject : list) {
            arrayList.add(configObject.toConfig());
        }
        return arrayList;
    }

    @Override
    public List<? extends Object> getAnyRefList(String string) {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        ConfigList configList = this.getList(string);
        for (ConfigValue configValue : configList) {
            arrayList.add(configValue.unwrapped());
        }
        return arrayList;
    }

    @Override
    public List<Long> getBytesList(String string) {
        ArrayList<Long> arrayList = new ArrayList<Long>();
        ConfigList configList = this.getList(string);
        for (ConfigValue configValue : configList) {
            if (configValue.valueType() == ConfigValueType.NUMBER) {
                arrayList.add(((Number)configValue.unwrapped()).longValue());
                continue;
            }
            if (configValue.valueType() == ConfigValueType.STRING) {
                String string2 = (String)configValue.unwrapped();
                Long l = SimpleConfig.parseBytes(string2, configValue.origin(), string);
                arrayList.add(l);
                continue;
            }
            throw new ConfigException.WrongType(configValue.origin(), string, "memory size string or number of bytes", configValue.valueType().name());
        }
        return arrayList;
    }

    @Override
    public List<ConfigMemorySize> getMemorySizeList(String string) {
        List<Long> list = this.getBytesList(string);
        ArrayList<ConfigMemorySize> arrayList = new ArrayList<ConfigMemorySize>();
        for (Long l : list) {
            arrayList.add(ConfigMemorySize.ofBytes(l));
        }
        return arrayList;
    }

    @Override
    public List<Long> getDurationList(String string, TimeUnit timeUnit) {
        ArrayList<Long> arrayList = new ArrayList<Long>();
        ConfigList configList = this.getList(string);
        for (ConfigValue configValue : configList) {
            Object object;
            if (configValue.valueType() == ConfigValueType.NUMBER) {
                object = timeUnit.convert(((Number)configValue.unwrapped()).longValue(), TimeUnit.MILLISECONDS);
                arrayList.add((Long)object);
                continue;
            }
            if (configValue.valueType() == ConfigValueType.STRING) {
                object = (String)configValue.unwrapped();
                Long l = timeUnit.convert(SimpleConfig.parseDuration((String)object, configValue.origin(), string), TimeUnit.NANOSECONDS);
                arrayList.add(l);
                continue;
            }
            throw new ConfigException.WrongType(configValue.origin(), string, "duration string or number of milliseconds", configValue.valueType().name());
        }
        return arrayList;
    }

    @Override
    public List<Duration> getDurationList(String string) {
        List<Long> list = this.getDurationList(string, TimeUnit.NANOSECONDS);
        ArrayList<Duration> arrayList = new ArrayList<Duration>(list.size());
        for (Long l : list) {
            arrayList.add(Duration.ofNanos(l));
        }
        return arrayList;
    }

    @Override
    @Deprecated
    public List<Long> getMillisecondsList(String string) {
        return this.getDurationList(string, TimeUnit.MILLISECONDS);
    }

    @Override
    @Deprecated
    public List<Long> getNanosecondsList(String string) {
        return this.getDurationList(string, TimeUnit.NANOSECONDS);
    }

    @Override
    public AbstractConfigObject toFallbackValue() {
        return this.object;
    }

    @Override
    public SimpleConfig withFallback(ConfigMergeable configMergeable) {
        return this.object.withFallback(configMergeable).toConfig();
    }

    public final boolean equals(Object object) {
        if (object instanceof SimpleConfig) {
            return this.object.equals(((SimpleConfig)object).object);
        }
        return false;
    }

    public final int hashCode() {
        return 41 * this.object.hashCode();
    }

    public String toString() {
        return "Config(" + this.object.toString() + ")";
    }

    private static String getUnits(String string) {
        char c;
        int n;
        for (n = string.length() - 1; n >= 0 && Character.isLetter(c = string.charAt(n)); --n) {
        }
        return string.substring(n + 1);
    }

    public static Period parsePeriod(String string, ConfigOrigin configOrigin, String string2) {
        ChronoUnit chronoUnit;
        String string3;
        String string4 = ConfigImplUtil.unicodeTrim(string);
        String string5 = string3 = SimpleConfig.getUnits(string4);
        String string6 = ConfigImplUtil.unicodeTrim(string4.substring(0, string4.length() - string5.length()));
        if (string6.length() == 0) {
            throw new ConfigException.BadValue(configOrigin, string2, "No number in period value '" + string + "'");
        }
        if (string5.length() > 2 && !string5.endsWith("s")) {
            string5 = string5 + "s";
        }
        if (string5.equals("") || string5.equals("d") || string5.equals("days")) {
            chronoUnit = ChronoUnit.DAYS;
        } else if (string5.equals("w") || string5.equals("weeks")) {
            chronoUnit = ChronoUnit.WEEKS;
        } else if (string5.equals("m") || string5.equals("mo") || string5.equals("months")) {
            chronoUnit = ChronoUnit.MONTHS;
        } else if (string5.equals("y") || string5.equals("years")) {
            chronoUnit = ChronoUnit.YEARS;
        } else {
            throw new ConfigException.BadValue(configOrigin, string2, "Could not parse time unit '" + string3 + "' (try d, w, mo, y)");
        }
        try {
            return SimpleConfig.periodOf(Integer.parseInt(string6), chronoUnit);
        } catch (NumberFormatException numberFormatException) {
            throw new ConfigException.BadValue(configOrigin, string2, "Could not parse duration number '" + string6 + "'");
        }
    }

    private static Period periodOf(int n, ChronoUnit chronoUnit) {
        if (chronoUnit.isTimeBased()) {
            throw new DateTimeException(chronoUnit + " cannot be converted to a java.time.Period");
        }
        switch (chronoUnit) {
            case DAYS: {
                return Period.ofDays(n);
            }
            case WEEKS: {
                return Period.ofWeeks(n);
            }
            case MONTHS: {
                return Period.ofMonths(n);
            }
            case YEARS: {
                return Period.ofYears(n);
            }
        }
        throw new DateTimeException(chronoUnit + " cannot be converted to a java.time.Period");
    }

    public static long parseDuration(String string, ConfigOrigin configOrigin, String string2) {
        String string3;
        String string4 = ConfigImplUtil.unicodeTrim(string);
        String string5 = string3 = SimpleConfig.getUnits(string4);
        String string6 = ConfigImplUtil.unicodeTrim(string4.substring(0, string4.length() - string5.length()));
        TimeUnit timeUnit = null;
        if (string6.length() == 0) {
            throw new ConfigException.BadValue(configOrigin, string2, "No number in duration value '" + string + "'");
        }
        if (string5.length() > 2 && !string5.endsWith("s")) {
            string5 = string5 + "s";
        }
        if (string5.equals("") || string5.equals("ms") || string5.equals("millis") || string5.equals("milliseconds")) {
            timeUnit = TimeUnit.MILLISECONDS;
        } else if (string5.equals("us") || string5.equals("micros") || string5.equals("microseconds")) {
            timeUnit = TimeUnit.MICROSECONDS;
        } else if (string5.equals("ns") || string5.equals("nanos") || string5.equals("nanoseconds")) {
            timeUnit = TimeUnit.NANOSECONDS;
        } else if (string5.equals("d") || string5.equals("days")) {
            timeUnit = TimeUnit.DAYS;
        } else if (string5.equals("h") || string5.equals("hours")) {
            timeUnit = TimeUnit.HOURS;
        } else if (string5.equals("s") || string5.equals("seconds")) {
            timeUnit = TimeUnit.SECONDS;
        } else if (string5.equals("m") || string5.equals("minutes")) {
            timeUnit = TimeUnit.MINUTES;
        } else {
            throw new ConfigException.BadValue(configOrigin, string2, "Could not parse time unit '" + string3 + "' (try ns, us, ms, s, m, h, d)");
        }
        try {
            if (string6.matches("[+-]?[0-9]+")) {
                return timeUnit.toNanos(Long.parseLong(string6));
            }
            long l = timeUnit.toNanos(1L);
            return (long)(Double.parseDouble(string6) * (double)l);
        } catch (NumberFormatException numberFormatException) {
            throw new ConfigException.BadValue(configOrigin, string2, "Could not parse duration number '" + string6 + "'");
        }
    }

    public static long parseBytes(String string, ConfigOrigin configOrigin, String string2) {
        String string3 = ConfigImplUtil.unicodeTrim(string);
        String string4 = SimpleConfig.getUnits(string3);
        String string5 = ConfigImplUtil.unicodeTrim(string3.substring(0, string3.length() - string4.length()));
        if (string5.length() == 0) {
            throw new ConfigException.BadValue(configOrigin, string2, "No number in size-in-bytes value '" + string + "'");
        }
        MemoryUnit memoryUnit = MemoryUnit.parseUnit(string4);
        if (memoryUnit == null) {
            throw new ConfigException.BadValue(configOrigin, string2, "Could not parse size-in-bytes unit '" + string4 + "' (try k, K, kB, KiB, kilobytes, kibibytes)");
        }
        try {
            BigInteger bigInteger;
            if (string5.matches("[0-9]+")) {
                bigInteger = memoryUnit.bytes.multiply(new BigInteger(string5));
            } else {
                BigDecimal bigDecimal = new BigDecimal(memoryUnit.bytes).multiply(new BigDecimal(string5));
                bigInteger = bigDecimal.toBigInteger();
            }
            if (bigInteger.bitLength() < 64) {
                return bigInteger.longValue();
            }
            throw new ConfigException.BadValue(configOrigin, string2, "size-in-bytes value is out of range for a 64-bit long: '" + string + "'");
        } catch (NumberFormatException numberFormatException) {
            throw new ConfigException.BadValue(configOrigin, string2, "Could not parse size-in-bytes number '" + string5 + "'");
        }
    }

    private AbstractConfigValue peekPath(Path path) {
        return this.root().peekPath(path);
    }

    private static void addProblem(List<ConfigException.ValidationProblem> list, Path path, ConfigOrigin configOrigin, String string) {
        list.add(new ConfigException.ValidationProblem(path.render(), configOrigin, string));
    }

    private static String getDesc(ConfigValueType configValueType) {
        return configValueType.name().toLowerCase();
    }

    private static String getDesc(ConfigValue configValue) {
        if (configValue instanceof AbstractConfigObject) {
            AbstractConfigObject abstractConfigObject = (AbstractConfigObject)configValue;
            if (!abstractConfigObject.isEmpty()) {
                return "object with keys " + abstractConfigObject.keySet();
            }
            return SimpleConfig.getDesc(configValue.valueType());
        }
        return SimpleConfig.getDesc(configValue.valueType());
    }

    private static void addMissing(List<ConfigException.ValidationProblem> list, String string, Path path, ConfigOrigin configOrigin) {
        SimpleConfig.addProblem(list, path, configOrigin, "No setting at '" + path.render() + "', expecting: " + string);
    }

    private static void addMissing(List<ConfigException.ValidationProblem> list, ConfigValue configValue, Path path, ConfigOrigin configOrigin) {
        SimpleConfig.addMissing(list, SimpleConfig.getDesc(configValue), path, configOrigin);
    }

    static void addMissing(List<ConfigException.ValidationProblem> list, ConfigValueType configValueType, Path path, ConfigOrigin configOrigin) {
        SimpleConfig.addMissing(list, SimpleConfig.getDesc(configValueType), path, configOrigin);
    }

    private static void addWrongType(List<ConfigException.ValidationProblem> list, String string, AbstractConfigValue abstractConfigValue, Path path) {
        SimpleConfig.addProblem(list, path, abstractConfigValue.origin(), "Wrong value type at '" + path.render() + "', expecting: " + string + " but got: " + SimpleConfig.getDesc(abstractConfigValue));
    }

    private static void addWrongType(List<ConfigException.ValidationProblem> list, ConfigValue configValue, AbstractConfigValue abstractConfigValue, Path path) {
        SimpleConfig.addWrongType(list, SimpleConfig.getDesc(configValue), abstractConfigValue, path);
    }

    private static void addWrongType(List<ConfigException.ValidationProblem> list, ConfigValueType configValueType, AbstractConfigValue abstractConfigValue, Path path) {
        SimpleConfig.addWrongType(list, SimpleConfig.getDesc(configValueType), abstractConfigValue, path);
    }

    private static boolean couldBeNull(AbstractConfigValue abstractConfigValue) {
        return DefaultTransformer.transform(abstractConfigValue, ConfigValueType.NULL).valueType() == ConfigValueType.NULL;
    }

    private static boolean haveCompatibleTypes(ConfigValue configValue, AbstractConfigValue abstractConfigValue) {
        if (SimpleConfig.couldBeNull((AbstractConfigValue)configValue)) {
            return true;
        }
        return SimpleConfig.haveCompatibleTypes(configValue.valueType(), abstractConfigValue);
    }

    private static boolean haveCompatibleTypes(ConfigValueType configValueType, AbstractConfigValue abstractConfigValue) {
        if (configValueType == ConfigValueType.NULL || SimpleConfig.couldBeNull(abstractConfigValue)) {
            return true;
        }
        if (configValueType == ConfigValueType.OBJECT) {
            return abstractConfigValue instanceof AbstractConfigObject;
        }
        if (configValueType == ConfigValueType.LIST) {
            return abstractConfigValue instanceof SimpleConfigList || abstractConfigValue instanceof SimpleConfigObject;
        }
        if (configValueType == ConfigValueType.STRING) {
            return true;
        }
        if (abstractConfigValue instanceof ConfigString) {
            return true;
        }
        return configValueType == abstractConfigValue.valueType();
    }

    private static void checkValidObject(Path path, AbstractConfigObject abstractConfigObject, AbstractConfigObject abstractConfigObject2, List<ConfigException.ValidationProblem> list) {
        for (Map.Entry entry : abstractConfigObject.entrySet()) {
            String string = (String)entry.getKey();
            Path path2 = path != null ? Path.newKey(string).prepend(path) : Path.newKey(string);
            AbstractConfigValue abstractConfigValue = abstractConfigObject2.get(string);
            if (abstractConfigValue == null) {
                SimpleConfig.addMissing(list, (ConfigValue)entry.getValue(), path2, (ConfigOrigin)abstractConfigObject2.origin());
                continue;
            }
            SimpleConfig.checkValid(path2, (ConfigValue)entry.getValue(), abstractConfigValue, list);
        }
    }

    private static void checkListCompatibility(Path path, SimpleConfigList simpleConfigList, SimpleConfigList simpleConfigList2, List<ConfigException.ValidationProblem> list) {
        if (!simpleConfigList.isEmpty() && !simpleConfigList2.isEmpty()) {
            AbstractConfigValue abstractConfigValue = simpleConfigList.get(0);
            for (ConfigValue configValue : simpleConfigList2) {
                AbstractConfigValue abstractConfigValue2 = (AbstractConfigValue)configValue;
                if (SimpleConfig.haveCompatibleTypes(abstractConfigValue, abstractConfigValue2)) continue;
                SimpleConfig.addProblem(list, path, abstractConfigValue2.origin(), "List at '" + path.render() + "' contains wrong value type, expecting list of " + SimpleConfig.getDesc(abstractConfigValue) + " but got element of type " + SimpleConfig.getDesc(abstractConfigValue2));
                break;
            }
        }
    }

    static void checkValid(Path path, ConfigValueType configValueType, AbstractConfigValue abstractConfigValue, List<ConfigException.ValidationProblem> list) {
        if (SimpleConfig.haveCompatibleTypes(configValueType, abstractConfigValue)) {
            AbstractConfigValue abstractConfigValue2;
            if (configValueType == ConfigValueType.LIST && abstractConfigValue instanceof SimpleConfigObject && !((abstractConfigValue2 = DefaultTransformer.transform(abstractConfigValue, ConfigValueType.LIST)) instanceof SimpleConfigList)) {
                SimpleConfig.addWrongType(list, configValueType, abstractConfigValue, path);
            }
        } else {
            SimpleConfig.addWrongType(list, configValueType, abstractConfigValue, path);
        }
    }

    private static void checkValid(Path path, ConfigValue configValue, AbstractConfigValue abstractConfigValue, List<ConfigException.ValidationProblem> list) {
        if (SimpleConfig.haveCompatibleTypes(configValue, abstractConfigValue)) {
            if (configValue instanceof AbstractConfigObject && abstractConfigValue instanceof AbstractConfigObject) {
                SimpleConfig.checkValidObject(path, (AbstractConfigObject)configValue, (AbstractConfigObject)abstractConfigValue, list);
            } else if (configValue instanceof SimpleConfigList && abstractConfigValue instanceof SimpleConfigList) {
                SimpleConfigList simpleConfigList = (SimpleConfigList)configValue;
                SimpleConfigList simpleConfigList2 = (SimpleConfigList)abstractConfigValue;
                SimpleConfig.checkListCompatibility(path, simpleConfigList, simpleConfigList2, list);
            } else if (configValue instanceof SimpleConfigList && abstractConfigValue instanceof SimpleConfigObject) {
                SimpleConfigList simpleConfigList = (SimpleConfigList)configValue;
                AbstractConfigValue abstractConfigValue2 = DefaultTransformer.transform(abstractConfigValue, ConfigValueType.LIST);
                if (abstractConfigValue2 instanceof SimpleConfigList) {
                    SimpleConfig.checkListCompatibility(path, simpleConfigList, (SimpleConfigList)abstractConfigValue2, list);
                } else {
                    SimpleConfig.addWrongType(list, configValue, abstractConfigValue, path);
                }
            }
        } else {
            SimpleConfig.addWrongType(list, configValue, abstractConfigValue, path);
        }
    }

    @Override
    public boolean isResolved() {
        return this.root().resolveStatus() == ResolveStatus.RESOLVED;
    }

    @Override
    public void checkValid(Config config, String ... stringArray) {
        SimpleConfig simpleConfig = (SimpleConfig)config;
        if (simpleConfig.root().resolveStatus() != ResolveStatus.RESOLVED) {
            throw new ConfigException.BugOrBroken("do not call checkValid() with an unresolved reference config, call Config#resolve(), see Config#resolve() API docs");
        }
        if (this.root().resolveStatus() != ResolveStatus.RESOLVED) {
            throw new ConfigException.NotResolved("need to Config#resolve() each config before using it, see the API docs for Config#resolve()");
        }
        ArrayList<ConfigException.ValidationProblem> arrayList = new ArrayList<ConfigException.ValidationProblem>();
        if (stringArray.length == 0) {
            SimpleConfig.checkValidObject(null, simpleConfig.root(), this.root(), arrayList);
        } else {
            for (String string : stringArray) {
                Path path = Path.newPath(string);
                AbstractConfigValue abstractConfigValue = simpleConfig.peekPath(path);
                if (abstractConfigValue == null) continue;
                AbstractConfigValue abstractConfigValue2 = this.peekPath(path);
                if (abstractConfigValue2 != null) {
                    SimpleConfig.checkValid(path, abstractConfigValue, abstractConfigValue2, arrayList);
                    continue;
                }
                SimpleConfig.addMissing(arrayList, abstractConfigValue, path, this.origin());
            }
        }
        if (!arrayList.isEmpty()) {
            throw new ConfigException.ValidationFailed(arrayList);
        }
    }

    @Override
    public SimpleConfig withOnlyPath(String string) {
        Path path = Path.newPath(string);
        return new SimpleConfig(this.root().withOnlyPath(path));
    }

    @Override
    public SimpleConfig withoutPath(String string) {
        Path path = Path.newPath(string);
        return new SimpleConfig(this.root().withoutPath(path));
    }

    @Override
    public SimpleConfig withValue(String string, ConfigValue configValue) {
        Path path = Path.newPath(string);
        return new SimpleConfig(this.root().withValue(path, configValue));
    }

    SimpleConfig atKey(ConfigOrigin configOrigin, String string) {
        return this.root().atKey(configOrigin, string);
    }

    @Override
    public SimpleConfig atKey(String string) {
        return this.root().atKey(string);
    }

    @Override
    public Config atPath(String string) {
        return this.root().atPath(string);
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }

    private static enum MemoryUnit {
        BYTES("", 1024, 0),
        KILOBYTES("kilo", 1000, 1),
        MEGABYTES("mega", 1000, 2),
        GIGABYTES("giga", 1000, 3),
        TERABYTES("tera", 1000, 4),
        PETABYTES("peta", 1000, 5),
        EXABYTES("exa", 1000, 6),
        ZETTABYTES("zetta", 1000, 7),
        YOTTABYTES("yotta", 1000, 8),
        KIBIBYTES("kibi", 1024, 1),
        MEBIBYTES("mebi", 1024, 2),
        GIBIBYTES("gibi", 1024, 3),
        TEBIBYTES("tebi", 1024, 4),
        PEBIBYTES("pebi", 1024, 5),
        EXBIBYTES("exbi", 1024, 6),
        ZEBIBYTES("zebi", 1024, 7),
        YOBIBYTES("yobi", 1024, 8);

        final String prefix;
        final int powerOf;
        final int power;
        final BigInteger bytes;
        private static Map<String, MemoryUnit> unitsMap;

        private MemoryUnit(String string2, int n2, int n3) {
            this.prefix = string2;
            this.powerOf = n2;
            this.power = n3;
            this.bytes = BigInteger.valueOf(n2).pow(n3);
        }

        private static Map<String, MemoryUnit> makeUnitsMap() {
            HashMap<String, MemoryUnit> hashMap = new HashMap<String, MemoryUnit>();
            for (MemoryUnit memoryUnit : MemoryUnit.values()) {
                hashMap.put(memoryUnit.prefix + "byte", memoryUnit);
                hashMap.put(memoryUnit.prefix + "bytes", memoryUnit);
                if (memoryUnit.prefix.length() == 0) {
                    hashMap.put("b", memoryUnit);
                    hashMap.put("B", memoryUnit);
                    hashMap.put("", memoryUnit);
                    continue;
                }
                String string = memoryUnit.prefix.substring(0, 1);
                String string2 = string.toUpperCase();
                if (memoryUnit.powerOf == 1024) {
                    hashMap.put(string, memoryUnit);
                    hashMap.put(string2, memoryUnit);
                    hashMap.put(string2 + "i", memoryUnit);
                    hashMap.put(string2 + "iB", memoryUnit);
                    continue;
                }
                if (memoryUnit.powerOf == 1000) {
                    if (memoryUnit.power == 1) {
                        hashMap.put(string + "B", memoryUnit);
                        continue;
                    }
                    hashMap.put(string2 + "B", memoryUnit);
                    continue;
                }
                throw new RuntimeException("broken MemoryUnit enum");
            }
            return hashMap;
        }

        static MemoryUnit parseUnit(String string) {
            return unitsMap.get(string);
        }

        static {
            unitsMap = MemoryUnit.makeUnitsMap();
        }
    }
}

