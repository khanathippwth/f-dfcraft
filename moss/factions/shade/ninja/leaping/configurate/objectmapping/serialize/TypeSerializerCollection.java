/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.RegisteredSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.ScalarSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

public class TypeSerializerCollection {
    private final TypeSerializerCollection parent;
    private final SerializerList serializers = new SerializerList();
    private final Map<TypeToken<?>, TypeSerializer<?>> typeMatches = new ConcurrentHashMap();

    TypeSerializerCollection(TypeSerializerCollection typeSerializerCollection) {
        this.parent = typeSerializerCollection;
    }

    public static TypeSerializerCollection defaults() {
        return TypeSerializers.DEFAULT_SERIALIZERS;
    }

    public static TypeSerializerCollection create() {
        return TypeSerializerCollection.defaults().newChild();
    }

    public <T> TypeSerializer<T> get(TypeToken<T> typeToken) {
        TypeSerializer<Object> typeSerializer = this.typeMatches.computeIfAbsent(typeToken = Objects.requireNonNull(typeToken).wrap(), this.serializers);
        if (typeSerializer == null && this.parent != null) {
            typeSerializer = this.parent.get(typeToken);
        }
        return typeSerializer;
    }

    @Deprecated
    public <T> TypeSerializerCollection registerType(TypeToken<T> typeToken, TypeSerializer<? super T> typeSerializer) {
        return this.register(typeToken, typeSerializer);
    }

    public <T> TypeSerializerCollection register(TypeToken<T> typeToken, TypeSerializer<? super T> typeSerializer) {
        this.serializers.add(new RegisteredSerializer(Objects.requireNonNull(typeToken, "type"), Objects.requireNonNull(typeSerializer)));
        this.typeMatches.clear();
        return this;
    }

    @Deprecated
    public <T> TypeSerializerCollection registerPredicate(Predicate<TypeToken<T>> predicate, TypeSerializer<? super T> typeSerializer) {
        return this.register(predicate, typeSerializer);
    }

    public <T> TypeSerializerCollection register(Predicate<TypeToken<T>> predicate, TypeSerializer<? super T> typeSerializer) {
        this.serializers.add(new RegisteredSerializer(Objects.requireNonNull(predicate, "test"), Objects.requireNonNull(typeSerializer, "serializer")));
        this.typeMatches.clear();
        return this;
    }

    public <T> TypeSerializerCollection register(ScalarSerializer<T> scalarSerializer) {
        return this.register(scalarSerializer.type(), scalarSerializer);
    }

    public TypeSerializerCollection newChild() {
        return new TypeSerializerCollection(this);
    }

    private static final class SerializerList
    extends CopyOnWriteArrayList<RegisteredSerializer>
    implements Function<TypeToken<?>, TypeSerializer<?>> {
        private SerializerList() {
        }

        @Override
        public TypeSerializer<?> apply(TypeToken<?> typeToken) {
            for (RegisteredSerializer registeredSerializer : this) {
                if (!registeredSerializer.predicate.test(typeToken)) continue;
                return registeredSerializer.serializer;
            }
            return null;
        }
    }
}

