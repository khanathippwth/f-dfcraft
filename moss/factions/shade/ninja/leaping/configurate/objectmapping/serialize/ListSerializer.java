/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.AbstractListChildSerializer;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedConsumer;

class ListSerializer
extends AbstractListChildSerializer<List<?>> {
    ListSerializer() {
    }

    @Override
    TypeToken<?> getElementType(TypeToken<?> typeToken) {
        if (!(typeToken.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }
        return typeToken.resolveType(List.class.getTypeParameters()[0]);
    }

    @Override
    List<?> createNew(int n, TypeToken<?> typeToken) {
        return new ArrayList(n);
    }

    @Override
    void forEachElement(List<?> list, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
        for (Object obj : list) {
            checkedConsumer.accept(obj);
        }
    }

    @Override
    void deserializeSingle(int n, List<?> list, Object object) {
        list.add(object);
    }
}

