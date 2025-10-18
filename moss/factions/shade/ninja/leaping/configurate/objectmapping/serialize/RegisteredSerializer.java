/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.util.function.Predicate;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.SuperTypePredicate;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

final class RegisteredSerializer {
    final Predicate<TypeToken<?>> predicate;
    final TypeSerializer<?> serializer;

    RegisteredSerializer(TypeToken<?> typeToken, TypeSerializer<?> typeSerializer) {
        this(new SuperTypePredicate(typeToken), typeSerializer);
    }

    RegisteredSerializer(Predicate<TypeToken<?>> predicate, TypeSerializer<?> typeSerializer) {
        this.predicate = predicate;
        this.serializer = typeSerializer;
    }
}

