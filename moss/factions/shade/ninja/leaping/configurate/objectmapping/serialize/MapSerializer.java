/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class MapSerializer
implements TypeSerializer<Map<?, ?>> {
    MapSerializer() {
    }

    @Override
    public Map<?, ?> deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (configurationNode.isMap()) {
            if (!(typeToken.getType() instanceof ParameterizedType)) {
                throw new ObjectMappingException("Raw types are not supported for collections");
            }
            TypeToken<?> typeToken2 = typeToken.resolveType(Map.class.getTypeParameters()[0]);
            TypeToken<?> typeToken3 = typeToken.resolveType(Map.class.getTypeParameters()[1]);
            @Nullable TypeSerializer<?> typeSerializer = configurationNode.getOptions().getSerializers().get(typeToken2);
            @Nullable TypeSerializer<?> typeSerializer2 = configurationNode.getOptions().getSerializers().get(typeToken3);
            if (typeSerializer == null) {
                throw new ObjectMappingException("No type serializer available for type " + typeToken2);
            }
            if (typeSerializer2 == null) {
                throw new ObjectMappingException("No type serializer available for type " + typeToken3);
            }
            ConfigurationNode configurationNode2 = ConfigurationNode.root(configurationNode.getOptions());
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : configurationNode.getChildrenMap().entrySet()) {
                @Nullable ? obj = typeSerializer.deserialize(typeToken2, configurationNode2.setValue(entry.getKey()));
                @Nullable ? obj2 = typeSerializer2.deserialize(typeToken3, entry.getValue());
                if (obj == null || obj2 == null) continue;
                linkedHashMap.put(obj, obj2);
            }
        }
        return linkedHashMap;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable Map<?, ?> map, @NonNull ConfigurationNode configurationNode) {
        if (!(typeToken.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }
        TypeToken<?> typeToken2 = typeToken.resolveType(Map.class.getTypeParameters()[0]);
        TypeToken<?> typeToken3 = typeToken.resolveType(Map.class.getTypeParameters()[1]);
        TypeSerializer<?> typeSerializer = configurationNode.getOptions().getSerializers().get(typeToken2);
        TypeSerializer<?> typeSerializer2 = configurationNode.getOptions().getSerializers().get(typeToken3);
        if (typeSerializer == null) {
            throw new ObjectMappingException("No type serializer available for type " + typeToken2);
        }
        if (typeSerializer2 == null) {
            throw new ObjectMappingException("No type serializer available for type " + typeToken3);
        }
        if (map == null || map.isEmpty()) {
            configurationNode.setValue(ImmutableMap.of());
        } else {
            HashSet<Object> hashSet = new HashSet<Object>(configurationNode.getChildrenMap().keySet());
            ConfigurationNode configurationNode2 = ConfigurationNode.root(configurationNode.getOptions());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                typeSerializer.serialize(typeToken2, entry.getKey(), configurationNode2);
                Object object = Objects.requireNonNull(configurationNode2.getValue(), "Key must not be null!");
                typeSerializer2.serialize(typeToken3, entry.getValue(), configurationNode.getNode(object));
                hashSet.remove(object);
            }
            for (Map.Entry<Object, Object> entry : hashSet) {
                configurationNode.removeChild(entry);
            }
        }
    }
}

