/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashSet;
import java.util.Set;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.AbstractListChildSerializer;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedConsumer;

class SetSerializer
extends AbstractListChildSerializer<Set<?>> {
    SetSerializer() {
    }

    @Override
    TypeToken<?> getElementType(TypeToken<?> typeToken) {
        if (!(typeToken.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }
        return typeToken.resolveType(Set.class.getTypeParameters()[0]);
    }

    @Override
    Set<?> createNew(int n, TypeToken<?> typeToken) {
        return new LinkedHashSet(n);
    }

    @Override
    void forEachElement(Set<?> set, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
        for (Object obj : set) {
            checkedConsumer.accept(obj);
        }
    }

    @Override
    void deserializeSingle(int n, Set<?> set, Object object) {
        set.add(object);
    }
}

