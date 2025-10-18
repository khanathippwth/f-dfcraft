/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigList;
import moss.factions.shade.com.typesafe.config.ConfigMemorySize;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.Optional;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfig;

public class ConfigBeanImpl {
    public static <T> T createInternal(Config config, Class<T> clazz) {
        Object object;
        if (((SimpleConfig)config).root().resolveStatus() != ResolveStatus.RESOLVED) {
            throw new ConfigException.NotResolved("need to Config#resolve() a config before using it to initialize a bean, see the API docs for Config#resolve()");
        }
        HashMap<String, AbstractConfigValue> hashMap = new HashMap<String, AbstractConfigValue>();
        HashMap<String, PropertyDescriptor[]> hashMap2 = new HashMap<String, PropertyDescriptor[]>();
        for (Map.Entry entry : config.root().entrySet()) {
            object = (PropertyDescriptor[])entry.getKey();
            String string = ConfigImplUtil.toCamelCase((String)object);
            if (hashMap2.containsKey(string) && !((String)object).equals(string)) continue;
            hashMap.put(string, (AbstractConfigValue)entry.getValue());
            hashMap2.put(string, (PropertyDescriptor[])object);
        }
        Object object2 = null;
        try {
            object2 = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException introspectionException) {
            throw new ConfigException.BadBean("Could not get bean information for class " + clazz.getName(), introspectionException);
        }
        try {
            Object object3;
            Object object4;
            Object object5;
            Object object6;
            GenericDeclaration genericDeclaration;
            Map.Entry entry;
            entry = new ArrayList();
            for (PropertyDescriptor object72 : object2.getPropertyDescriptors()) {
                if (object72.getReadMethod() == null || object72.getWriteMethod() == null) continue;
                entry.add(object72);
            }
            object = new ArrayList();
            Iterator<Object> iterator = entry.iterator();
            while (iterator.hasNext()) {
                PropertyDescriptor propertyDescriptor = (PropertyDescriptor)iterator.next();
                Method method = propertyDescriptor.getWriteMethod();
                genericDeclaration = method.getParameterTypes()[0];
                object6 = ConfigBeanImpl.getValueTypeOrNull(genericDeclaration);
                if (object6 == null) continue;
                object5 = (String)hashMap2.get(propertyDescriptor.getName());
                if (object5 == null) {
                    object5 = propertyDescriptor.getName();
                }
                object4 = Path.newKey((String)object5);
                object3 = (AbstractConfigValue)hashMap.get(propertyDescriptor.getName());
                if (object3 != null) {
                    SimpleConfig.checkValid((Path)object4, object6, (AbstractConfigValue)object3, (List<ConfigException.ValidationProblem>)object);
                    continue;
                }
                if (ConfigBeanImpl.isOptionalProperty(clazz, propertyDescriptor)) continue;
                SimpleConfig.addMissing((List<ConfigException.ValidationProblem>)object, object6, (Path)object4, config.origin());
            }
            if (!object.isEmpty()) {
                throw new ConfigException.ValidationFailed((Iterable<ConfigException.ValidationProblem>)object);
            }
            iterator = clazz.newInstance();
            Iterator iterator2 = entry.iterator();
            while (iterator2.hasNext()) {
                PropertyDescriptor propertyDescriptor = (PropertyDescriptor)iterator2.next();
                genericDeclaration = propertyDescriptor.getWriteMethod();
                object6 = ((Method)genericDeclaration).getGenericParameterTypes()[0];
                object5 = ((Method)genericDeclaration).getParameterTypes()[0];
                object4 = (String)hashMap2.get(propertyDescriptor.getName());
                if (object4 == null) {
                    if (ConfigBeanImpl.isOptionalProperty(clazz, propertyDescriptor)) continue;
                    throw new ConfigException.Missing(propertyDescriptor.getName());
                }
                object3 = ConfigBeanImpl.getValue(clazz, (Type)object6, object5, config, (String)object4);
                ((Method)genericDeclaration).invoke(iterator, object3);
            }
            return (T)iterator;
        } catch (InstantiationException instantiationException) {
            throw new ConfigException.BadBean(clazz.getName() + " needs a public no-args constructor to be used as a bean", instantiationException);
        } catch (IllegalAccessException illegalAccessException) {
            throw new ConfigException.BadBean(clazz.getName() + " getters and setters are not accessible, they must be for use as a bean", illegalAccessException);
        } catch (InvocationTargetException invocationTargetException) {
            throw new ConfigException.BadBean("Calling bean method on " + clazz.getName() + " caused an exception", invocationTargetException);
        }
    }

    private static Object getValue(Class<?> clazz, Type type, Class<?> clazz2, Config config, String string) {
        if (clazz2 == Boolean.class || clazz2 == Boolean.TYPE) {
            return config.getBoolean(string);
        }
        if (clazz2 == Integer.class || clazz2 == Integer.TYPE) {
            return config.getInt(string);
        }
        if (clazz2 == Double.class || clazz2 == Double.TYPE) {
            return config.getDouble(string);
        }
        if (clazz2 == Long.class || clazz2 == Long.TYPE) {
            return config.getLong(string);
        }
        if (clazz2 == String.class) {
            return config.getString(string);
        }
        if (clazz2 == Duration.class) {
            return config.getDuration(string);
        }
        if (clazz2 == ConfigMemorySize.class) {
            return config.getMemorySize(string);
        }
        if (clazz2 == Object.class) {
            return config.getAnyRef(string);
        }
        if (clazz2 == List.class) {
            return ConfigBeanImpl.getListValue(clazz, type, clazz2, config, string);
        }
        if (clazz2 == Set.class) {
            return ConfigBeanImpl.getSetValue(clazz, type, clazz2, config, string);
        }
        if (clazz2 == Map.class) {
            Type[] typeArray = ((ParameterizedType)type).getActualTypeArguments();
            if (typeArray[0] != String.class || typeArray[1] != Object.class) {
                throw new ConfigException.BadBean("Bean property '" + string + "' of class " + clazz.getName() + " has unsupported Map<" + typeArray[0] + "," + typeArray[1] + ">, only Map<String,Object> is supported right now");
            }
            return config.getObject(string).unwrapped();
        }
        if (clazz2 == Config.class) {
            return config.getConfig(string);
        }
        if (clazz2 == ConfigObject.class) {
            return config.getObject(string);
        }
        if (clazz2 == ConfigValue.class) {
            return config.getValue(string);
        }
        if (clazz2 == ConfigList.class) {
            return config.getList(string);
        }
        if (clazz2.isEnum()) {
            Object obj = config.getEnum(clazz2, string);
            return obj;
        }
        if (ConfigBeanImpl.hasAtLeastOneBeanProperty(clazz2)) {
            return ConfigBeanImpl.createInternal(config.getConfig(string), clazz2);
        }
        throw new ConfigException.BadBean("Bean property " + string + " of class " + clazz.getName() + " has unsupported type " + type);
    }

    private static Object getSetValue(Class<?> clazz, Type type, Class<?> clazz2, Config config, String string) {
        return new HashSet((List)ConfigBeanImpl.getListValue(clazz, type, clazz2, config, string));
    }

    private static Object getListValue(Class<?> clazz, Type type, Class<?> clazz2, Config config, String string) {
        Type type2 = ((ParameterizedType)type).getActualTypeArguments()[0];
        if (type2 == Boolean.class) {
            return config.getBooleanList(string);
        }
        if (type2 == Integer.class) {
            return config.getIntList(string);
        }
        if (type2 == Double.class) {
            return config.getDoubleList(string);
        }
        if (type2 == Long.class) {
            return config.getLongList(string);
        }
        if (type2 == String.class) {
            return config.getStringList(string);
        }
        if (type2 == Duration.class) {
            return config.getDurationList(string);
        }
        if (type2 == ConfigMemorySize.class) {
            return config.getMemorySizeList(string);
        }
        if (type2 == Object.class) {
            return config.getAnyRefList(string);
        }
        if (type2 == Config.class) {
            return config.getConfigList(string);
        }
        if (type2 == ConfigObject.class) {
            return config.getObjectList(string);
        }
        if (type2 == ConfigValue.class) {
            return config.getList(string);
        }
        if (((Class)type2).isEnum()) {
            List list = config.getEnumList((Class)type2, string);
            return list;
        }
        if (ConfigBeanImpl.hasAtLeastOneBeanProperty((Class)type2)) {
            ArrayList arrayList = new ArrayList();
            List<? extends Config> list = config.getConfigList(string);
            for (Config config2 : list) {
                arrayList.add(ConfigBeanImpl.createInternal(config2, (Class)type2));
            }
            return arrayList;
        }
        throw new ConfigException.BadBean("Bean property '" + string + "' of class " + clazz.getName() + " has unsupported list element type " + type2);
    }

    private static ConfigValueType getValueTypeOrNull(Class<?> clazz) {
        if (clazz == Boolean.class || clazz == Boolean.TYPE) {
            return ConfigValueType.BOOLEAN;
        }
        if (clazz == Integer.class || clazz == Integer.TYPE) {
            return ConfigValueType.NUMBER;
        }
        if (clazz == Double.class || clazz == Double.TYPE) {
            return ConfigValueType.NUMBER;
        }
        if (clazz == Long.class || clazz == Long.TYPE) {
            return ConfigValueType.NUMBER;
        }
        if (clazz == String.class) {
            return ConfigValueType.STRING;
        }
        if (clazz == Duration.class) {
            return null;
        }
        if (clazz == ConfigMemorySize.class) {
            return null;
        }
        if (clazz == List.class) {
            return ConfigValueType.LIST;
        }
        if (clazz == Map.class) {
            return ConfigValueType.OBJECT;
        }
        if (clazz == Config.class) {
            return ConfigValueType.OBJECT;
        }
        if (clazz == ConfigObject.class) {
            return ConfigValueType.OBJECT;
        }
        if (clazz == ConfigList.class) {
            return ConfigValueType.LIST;
        }
        return null;
    }

    private static boolean hasAtLeastOneBeanProperty(Class<?> clazz) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException introspectionException) {
            return false;
        }
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            if (propertyDescriptor.getReadMethod() == null || propertyDescriptor.getWriteMethod() == null) continue;
            return true;
        }
        return false;
    }

    private static boolean isOptionalProperty(Class clazz, PropertyDescriptor propertyDescriptor) {
        Field field = ConfigBeanImpl.getField(clazz, propertyDescriptor.getName());
        return field != null ? ((Optional[])field.getAnnotationsByType(Optional.class)).length > 0 : ((Optional[])propertyDescriptor.getReadMethod().getAnnotationsByType(Optional.class)).length > 0;
    }

    private static Field getField(Class clazz, String string) {
        try {
            Field field = clazz.getDeclaredField(string);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException noSuchFieldException) {
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                return null;
            }
            return ConfigBeanImpl.getField(clazz, string);
        }
    }
}

