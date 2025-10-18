/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.AnnotatedObjectSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.ArraySerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.BooleanSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.CharSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.ConfigurationNodeSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.EnumValueSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.ListSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.MapSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.NumberSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.PatternSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.SetSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.StringSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.URISerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.URLSerializer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.UUIDSerializer;

public class TypeSerializers {
    static final TypeSerializerCollection DEFAULT_SERIALIZERS = new TypeSerializerCollection(null);

    @Deprecated
    public static TypeSerializerCollection getDefaultSerializers() {
        return TypeSerializerCollection.defaults();
    }

    @Deprecated
    public static TypeSerializerCollection newCollection() {
        return TypeSerializerCollection.create();
    }

    static {
        DEFAULT_SERIALIZERS.register(TypeToken.of(URI.class), new URISerializer());
        DEFAULT_SERIALIZERS.register(TypeToken.of(URL.class), new URLSerializer());
        DEFAULT_SERIALIZERS.register(TypeToken.of(UUID.class), new UUIDSerializer());
        DEFAULT_SERIALIZERS.register(typeToken -> typeToken.getRawType().isAnnotationPresent(ConfigSerializable.class), new AnnotatedObjectSerializer());
        DEFAULT_SERIALIZERS.register(NumberSerializer.getPredicate(), new NumberSerializer());
        DEFAULT_SERIALIZERS.register(TypeToken.of(Character.class), new CharSerializer());
        DEFAULT_SERIALIZERS.register(TypeToken.of(String.class), new StringSerializer());
        DEFAULT_SERIALIZERS.register(TypeToken.of(Boolean.class), new BooleanSerializer());
        DEFAULT_SERIALIZERS.register(new TypeToken<Map<?, ?>>(){}, new MapSerializer());
        DEFAULT_SERIALIZERS.register(new TypeToken<List<?>>(){}, new ListSerializer());
        DEFAULT_SERIALIZERS.register(new TypeToken<Enum<?>>(){}, new EnumValueSerializer());
        DEFAULT_SERIALIZERS.register(TypeToken.of(Pattern.class), new PatternSerializer());
        DEFAULT_SERIALIZERS.register(ArraySerializer.Objects.predicate(), new ArraySerializer.Objects());
        DEFAULT_SERIALIZERS.register(TypeToken.of(boolean[].class), new ArraySerializer.Booleans());
        DEFAULT_SERIALIZERS.register(TypeToken.of(byte[].class), new ArraySerializer.Bytes());
        DEFAULT_SERIALIZERS.register(TypeToken.of(char[].class), new ArraySerializer.Chars());
        DEFAULT_SERIALIZERS.register(TypeToken.of(short[].class), new ArraySerializer.Shorts());
        DEFAULT_SERIALIZERS.register(TypeToken.of(int[].class), new ArraySerializer.Ints());
        DEFAULT_SERIALIZERS.register(TypeToken.of(long[].class), new ArraySerializer.Longs());
        DEFAULT_SERIALIZERS.register(TypeToken.of(float[].class), new ArraySerializer.Floats());
        DEFAULT_SERIALIZERS.register(TypeToken.of(double[].class), new ArraySerializer.Doubles());
        DEFAULT_SERIALIZERS.register(new TypeToken<Set<?>>(){}, new SetSerializer());
        DEFAULT_SERIALIZERS.register(ConfigurationNodeSerializer.TYPE, new ConfigurationNodeSerializer());
    }
}

