/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.util.function.Predicate;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ScalarSerializer<T>
implements TypeSerializer<T> {
    private final TypeToken<T> type;

    protected ScalarSerializer(TypeToken<T> typeToken) {
        this.type = typeToken.wrap();
    }

    protected ScalarSerializer(Class<T> clazz) {
        if (clazz.getTypeParameters().length > 0) {
            throw new IllegalArgumentException("Provided type " + clazz + " has type parameters but was not provided as a TypeToken!");
        }
        this.type = TypeToken.of(clazz);
    }

    public final TypeToken<T> type() {
        return this.type;
    }

    @Override
    public final @Nullable T deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) {
        Object object;
        ConfigurationNode configurationNode2 = configurationNode;
        if (configurationNode.isList() && (object = configurationNode.getChildrenList()).size() == 1) {
            configurationNode2 = object.get(0);
        }
        if (configurationNode2.isList() || configurationNode2.isMap()) {
            throw new ObjectMappingException("Value must be provided as a scalar!");
        }
        object = configurationNode2.getValue();
        if (object == null) {
            return null;
        }
        typeToken = typeToken.wrap();
        @Nullable T t = this.cast(object);
        if (t != null) {
            return t;
        }
        return this.deserialize(typeToken, object);
    }

    @Override
    public final void serialize(TypeToken<?> typeToken, @Nullable T t, ConfigurationNode configurationNode) {
        if (t == null) {
            configurationNode.setValue(null);
            return;
        }
        if (configurationNode.getOptions().acceptsType(t.getClass())) {
            configurationNode.setValue(t);
            return;
        }
        configurationNode.setValue(this.serialize(t, configurationNode.getOptions()::acceptsType));
    }

    public abstract T deserialize(TypeToken<?> var1, Object var2);

    public abstract Object serialize(T var1, Predicate<Class<?>> var2);

    public final T deserialize(Object object) {
        @Nullable T t = this.cast(object);
        if (t != null) {
            return t;
        }
        return this.deserialize(this.type(), object);
    }

    private @Nullable T cast(Object object) {
        Class<T> clazz = this.type().getRawType();
        if (clazz.isInstance(object)) {
            return (T)object;
        }
        return null;
    }

    public final @Nullable T tryDeserialize(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        try {
            return this.deserialize(object);
        } catch (ObjectMappingException objectMappingException) {
            return null;
        }
    }

    public final String serializeToString(T t) {
        if (t instanceof CharSequence) {
            return t.toString();
        }
        return (String)this.serialize(t, clazz -> clazz.isAssignableFrom(String.class));
    }
}

