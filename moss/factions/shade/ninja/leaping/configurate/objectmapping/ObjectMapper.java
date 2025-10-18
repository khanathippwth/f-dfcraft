/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.commented.CommentedConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.Setting;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ObjectMapper<T> {
    private final TypeToken<T> type;
    private final Class<? super T> clazz;
    private final @Nullable Invokable<T, T> constructor;
    private final Map<String, FieldData> cachedFields = new LinkedHashMap<String, FieldData>();

    public static <T> ObjectMapper<T> forClass(@NonNull Class<T> clazz) {
        return DefaultObjectMapperFactory.getInstance().getMapper(clazz);
    }

    public static <T> ObjectMapper<T> forType(@NonNull TypeToken<T> typeToken) {
        return DefaultObjectMapperFactory.getInstance().getMapper(typeToken);
    }

    public static <T> BoundInstance forObject(@NonNull T t) {
        return ObjectMapper.forClass(Objects.requireNonNull(t).getClass()).bind(t);
    }

    public static <T> BoundInstance forObject(TypeToken<T> typeToken, @NonNull T t) {
        return ObjectMapper.forType(Objects.requireNonNull(typeToken)).bind(t);
    }

    @Deprecated
    protected ObjectMapper(Class<T> clazz) {
        this(TypeToken.of(clazz));
    }

    @Deprecated
    protected boolean isLegacy() {
        if (this.getClass().getPackage() != ObjectMapper.class.getPackage()) {
            try {
                this.getClass().getDeclaredMethod("collectFields", Map.class, Class.class);
                return true;
            } catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return false;
    }

    protected ObjectMapper(TypeToken<T> typeToken) {
        this.type = typeToken;
        this.clazz = typeToken.getRawType();
        if (this.clazz.isInterface()) {
            throw new ObjectMappingException("ObjectMapper can only work with concrete types");
        }
        Invokable<T, T> invokable = null;
        try {
            invokable = typeToken.constructor(typeToken.getRawType().getDeclaredConstructor(new Class[0]));
            invokable.setAccessible(true);
        } catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        this.constructor = invokable;
        TypeToken<T> typeToken2 = typeToken;
        Class<T> clazz = typeToken.getRawType();
        boolean bl = this.isLegacy();
        while (true) {
            if (bl) {
                this.collectFields(this.cachedFields, clazz);
            } else {
                this.collectFields(this.cachedFields, typeToken2);
            }
            clazz = clazz.getSuperclass();
            if (clazz.equals(Object.class)) break;
            typeToken2 = typeToken2.getSupertype(clazz);
        }
    }

    @Deprecated
    protected void collectFields(Map<String, FieldData> map, Class<? super T> clazz) {
    }

    protected void collectFields(Map<String, FieldData> map, TypeToken<? super T> typeToken) {
        for (Field field : typeToken.getRawType().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Setting.class)) continue;
            Setting setting = field.getAnnotation(Setting.class);
            String string = setting.value();
            if (string.isEmpty()) {
                string = field.getName();
            }
            TypeToken<?> typeToken2 = typeToken.resolveType(field.getGenericType());
            FieldData fieldData = new FieldData(field, setting.comment(), typeToken2);
            field.setAccessible(true);
            if (map.containsKey(string)) continue;
            map.put(string, fieldData);
        }
    }

    protected T constructObject() {
        if (this.constructor == null) {
            throw new ObjectMappingException("No zero-arg constructor is available for class " + this.type + " but is required to construct new instances!");
        }
        try {
            return this.constructor.invoke(null, new Object[0]);
        } catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            throw new ObjectMappingException("Unable to create instance of target class " + this.type, reflectiveOperationException);
        }
    }

    public boolean canCreateInstances() {
        return this.constructor != null;
    }

    public BoundInstance bind(T t) {
        return new BoundInstance(t);
    }

    public BoundInstance bindToNew() {
        return new BoundInstance(this.constructObject());
    }

    @Deprecated
    public Class<T> getMappedType() {
        return this.clazz;
    }

    public TypeToken<T> getType() {
        return this.type;
    }

    public class BoundInstance {
        private final T boundInstance;

        protected BoundInstance(T t) {
            this.boundInstance = t;
        }

        public T populate(ConfigurationNode configurationNode) {
            for (Map.Entry entry : ObjectMapper.this.cachedFields.entrySet()) {
                ConfigurationNode configurationNode2 = configurationNode.getNode(entry.getKey());
                ((FieldData)entry.getValue()).deserializeFrom(this.boundInstance, configurationNode2);
            }
            return this.boundInstance;
        }

        public void serialize(ConfigurationNode configurationNode) {
            for (Map.Entry entry : ObjectMapper.this.cachedFields.entrySet()) {
                ConfigurationNode configurationNode2 = configurationNode.getNode(entry.getKey());
                ((FieldData)entry.getValue()).serializeTo(this.boundInstance, configurationNode2);
            }
        }

        public T getInstance() {
            return this.boundInstance;
        }
    }

    protected static class FieldData {
        private final Field field;
        private final TypeToken<?> fieldType;
        private final String comment;

        public FieldData(Field field, String string) {
            this(field, string, TypeToken.of(field.getGenericType()));
        }

        public FieldData(Field field, String string, TypeToken<?> typeToken) {
            this.field = field;
            this.comment = string;
            this.fieldType = typeToken;
        }

        public void deserializeFrom(Object object, ConfigurationNode configurationNode) {
            TypeSerializer<?> typeSerializer = configurationNode.getOptions().getSerializers().get(this.fieldType);
            if (typeSerializer == null) {
                throw new ObjectMappingException("No TypeSerializer found for field " + this.field.getName() + " of type " + this.fieldType);
            }
            Object var4_4 = configurationNode.isVirtual() ? null : typeSerializer.deserialize(this.fieldType, configurationNode);
            try {
                if (var4_4 == null) {
                    Object object2 = this.field.get(object);
                    if (object2 != null) {
                        this.serializeTo(object, configurationNode);
                    }
                } else {
                    this.field.set(object, var4_4);
                }
            } catch (IllegalAccessException illegalAccessException) {
                throw new ObjectMappingException("Unable to deserialize field " + this.field.getName(), illegalAccessException);
            }
        }

        public void serializeTo(Object object, ConfigurationNode configurationNode) {
            try {
                TypeSerializer<?> typeSerializer;
                Object object2 = this.field.get(object);
                if (object2 == null) {
                    configurationNode.setValue(null);
                } else {
                    typeSerializer = configurationNode.getOptions().getSerializers().get(this.fieldType);
                    if (typeSerializer == null) {
                        throw new ObjectMappingException("No TypeSerializer found for field " + this.field.getName() + " of type " + this.fieldType);
                    }
                    typeSerializer.serialize(this.fieldType, object2, configurationNode);
                }
                if (configurationNode instanceof CommentedConfigurationNode && this.comment != null && !this.comment.isEmpty() && !(typeSerializer = (CommentedConfigurationNode)configurationNode).getComment().isPresent()) {
                    typeSerializer.setComment(this.comment);
                }
            } catch (IllegalAccessException illegalAccessException) {
                throw new ObjectMappingException("Unable to serialize field " + this.field.getName(), illegalAccessException);
            }
        }
    }
}

