/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import java.util.List;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class AbstractListChildSerializer<T>
implements TypeSerializer<T> {
    AbstractListChildSerializer() {
    }

    @Override
    public final @Nullable T deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        TypeToken<?> typeToken2 = this.getElementType(typeToken);
        TypeSerializer<?> typeSerializer = configurationNode.getOptions().getSerializers().get(typeToken2);
        if (typeSerializer == null) {
            throw new ObjectMappingException("No applicable type serializer for type " + typeToken2);
        }
        if (configurationNode.isList()) {
            List<? extends ConfigurationNode> list = configurationNode.getChildrenList();
            T t = this.createNew(list.size(), typeToken2);
            for (int i = 0; i < list.size(); ++i) {
                this.deserializeSingle(i, t, typeSerializer.deserialize(typeToken2, list.get(i)));
            }
            return t;
        }
        Object object = configurationNode.getValue();
        if (object != null) {
            T t = this.createNew(1, typeToken2);
            this.deserializeSingle(0, t, typeSerializer.deserialize(typeToken2, configurationNode));
            return t;
        }
        return this.createNew(0, typeToken2);
    }

    @Override
    public final void serialize(@NonNull TypeToken<?> typeToken, @Nullable T t, @NonNull ConfigurationNode configurationNode) {
        TypeToken<?> typeToken2 = this.getElementType(typeToken);
        TypeSerializer<?> typeSerializer = configurationNode.getOptions().getSerializers().get(typeToken2);
        if (typeSerializer == null) {
            throw new ObjectMappingException("No applicable type serializer for type " + typeToken2);
        }
        configurationNode.setValue(ImmutableList.of());
        if (t != null) {
            this.forEachElement(t, object -> typeSerializer.serialize(typeToken2, object, configurationNode.appendListNode()));
        }
    }

    abstract TypeToken<?> getElementType(TypeToken<?> var1);

    abstract T createNew(int var1, TypeToken<?> var2);

    abstract void forEachElement(T var1, CheckedConsumer<Object, ObjectMappingException> var2);

    abstract void deserializeSingle(int var1, T var2, Object var3);
}

