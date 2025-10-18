/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.Modifier;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMapper;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class AnnotatedObjectSerializer
implements TypeSerializer<Object> {
    public static final String CLASS_KEY = "__class__";

    AnnotatedObjectSerializer() {
    }

    @Override
    public Object deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        TypeToken<?> typeToken2 = this.getInstantiableType(typeToken, configurationNode.getNode(CLASS_KEY).getString());
        return configurationNode.getOptions().getObjectMapperFactory().getMapper(typeToken2).bindToNew().populate(configurationNode);
    }

    private TypeToken<?> getInstantiableType(TypeToken<?> typeToken, String string) {
        TypeToken<?> typeToken2;
        Class<?> clazz = typeToken.getRawType();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            if (string == null) {
                throw new ObjectMappingException("No available configured type for instances of " + typeToken);
            }
            try {
                typeToken2 = TypeToken.of(Class.forName(string));
            } catch (ClassNotFoundException classNotFoundException) {
                throw new ObjectMappingException("Unknown class of object " + string, classNotFoundException);
            }
            if (!typeToken2.isSubtypeOf(typeToken)) {
                throw new ObjectMappingException("Configured type " + string + " does not extend " + clazz.getCanonicalName());
            }
        } else {
            typeToken2 = typeToken;
        }
        return typeToken2;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable Object object, @NonNull ConfigurationNode configurationNode) {
        ObjectMapper<?> objectMapper;
        if (object == null) {
            ConfigurationNode configurationNode2 = configurationNode.getNode(CLASS_KEY);
            configurationNode.setValue(null);
            if (!configurationNode2.isVirtual()) {
                configurationNode.getNode(CLASS_KEY).setValue(configurationNode2);
            }
            return;
        }
        Class<?> clazz = typeToken.getRawType();
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            configurationNode.getNode(CLASS_KEY).setValue(object.getClass().getName());
            objectMapper = configurationNode.getOptions().getObjectMapperFactory().getMapper(object.getClass());
        } else {
            objectMapper = configurationNode.getOptions().getObjectMapperFactory().getMapper(typeToken);
        }
        objectMapper.bind(object).serialize(configurationNode);
    }
}

