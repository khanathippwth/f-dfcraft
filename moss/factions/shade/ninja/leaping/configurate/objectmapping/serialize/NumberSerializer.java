/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.util.function.Predicate;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class NumberSerializer
implements TypeSerializer<Number> {
    NumberSerializer() {
    }

    public static Predicate<TypeToken<Number>> getPredicate() {
        return typeToken -> {
            Class clazz = (typeToken = typeToken.wrap()).getRawType();
            return Integer.class.equals(clazz) || Long.class.equals(clazz) || Short.class.equals(clazz) || Byte.class.equals(clazz) || Float.class.equals(clazz) || Double.class.equals(clazz);
        };
    }

    @Override
    public Number deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        Class<?> clazz = (typeToken = typeToken.wrap()).getRawType();
        if (Integer.class.equals(clazz)) {
            return configurationNode.getInt();
        }
        if (Long.class.equals(clazz)) {
            return configurationNode.getLong();
        }
        if (Short.class.equals(clazz)) {
            return (short)configurationNode.getInt();
        }
        if (Byte.class.equals(clazz)) {
            return (byte)configurationNode.getInt();
        }
        if (Float.class.equals(clazz)) {
            return Float.valueOf(configurationNode.getFloat());
        }
        if (Double.class.equals(clazz)) {
            return configurationNode.getDouble();
        }
        return null;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable Number number, @NonNull ConfigurationNode configurationNode) {
        if (number == null) {
            configurationNode.setValue(null);
            return;
        }
        if (configurationNode.getOptions().acceptsType(number.getClass())) {
            configurationNode.setValue(number);
            return;
        }
        if (number instanceof Float && configurationNode.getOptions().acceptsType(Double.class)) {
            configurationNode.setValue(number.doubleValue());
        } else if (number instanceof Byte) {
            if (configurationNode.getOptions().acceptsType(Short.class)) {
                configurationNode.setValue(number.shortValue());
            } else if (configurationNode.getOptions().acceptsType(Integer.class)) {
                configurationNode.setValue(number.intValue());
            } else if (configurationNode.getOptions().acceptsType(Long.class)) {
                configurationNode.setValue(number.longValue());
            }
        } else if (number instanceof Short) {
            if (configurationNode.getOptions().acceptsType(Integer.class)) {
                configurationNode.setValue(number.intValue());
            } else if (configurationNode.getOptions().acceptsType(Long.class)) {
                configurationNode.setValue(number.longValue());
            }
        } else if (number instanceof Integer) {
            if (configurationNode.getOptions().acceptsType(Long.class)) {
                configurationNode.setValue(number.longValue());
            }
        } else {
            throw new ObjectMappingException("Unable to coerce value of type " + number.getClass() + " to one accepted by node " + configurationNode);
        }
    }
}

