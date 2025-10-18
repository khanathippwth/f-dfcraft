/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Primitives;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import moss.factions.shade.ninja.leaping.configurate.util.MapFactories;
import moss.factions.shade.ninja.leaping.configurate.util.MapFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfigurationOptions {
    private static final ConfigurationOptions DEFAULTS = new ConfigurationOptions(MapFactories.insertionOrdered(), null, TypeSerializerCollection.defaults(), null, DefaultObjectMapperFactory.getInstance(), false);
    private final @NonNull MapFactory mapFactory;
    private final @Nullable String header;
    private final @NonNull TypeSerializerCollection serializers;
    private final @Nullable ImmutableSet<Class<?>> acceptedTypes;
    private final @NonNull ObjectMapperFactory objectMapperFactory;
    private final boolean shouldCopyDefaults;

    private ConfigurationOptions(@NonNull MapFactory mapFactory, @Nullable String string, @NonNull TypeSerializerCollection typeSerializerCollection, @Nullable Set<Class<?>> set, @NonNull ObjectMapperFactory objectMapperFactory, boolean bl) {
        this.mapFactory = mapFactory;
        this.header = string;
        this.serializers = typeSerializerCollection;
        this.acceptedTypes = set == null ? null : ImmutableSet.copyOf(set);
        this.objectMapperFactory = objectMapperFactory;
        this.shouldCopyDefaults = bl;
    }

    public static @NonNull ConfigurationOptions defaults() {
        return DEFAULTS;
    }

    public @NonNull MapFactory getMapFactory() {
        return this.mapFactory;
    }

    @Deprecated
    public @NonNull ConfigurationOptions setMapFactory(@NonNull MapFactory mapFactory) {
        return this.withMapFactory(mapFactory);
    }

    public @NonNull ConfigurationOptions withMapFactory(@NonNull MapFactory mapFactory) {
        Objects.requireNonNull(mapFactory, "mapFactory");
        if (this.mapFactory == mapFactory) {
            return this;
        }
        return new ConfigurationOptions(mapFactory, this.header, this.serializers, this.acceptedTypes, this.objectMapperFactory, this.shouldCopyDefaults);
    }

    public @Nullable String getHeader() {
        return this.header;
    }

    public @NonNull ConfigurationOptions setHeader(@Nullable String string) {
        return this.withHeader(string);
    }

    public @NonNull ConfigurationOptions withHeader(@Nullable String string) {
        if (Objects.equals(this.header, string)) {
            return this;
        }
        return new ConfigurationOptions(this.mapFactory, string, this.serializers, this.acceptedTypes, this.objectMapperFactory, this.shouldCopyDefaults);
    }

    public @NonNull TypeSerializerCollection getSerializers() {
        return this.serializers;
    }

    @Deprecated
    public @NonNull ConfigurationOptions setSerializers(@NonNull TypeSerializerCollection typeSerializerCollection) {
        return this.withSerializers(typeSerializerCollection);
    }

    public @NonNull ConfigurationOptions withSerializers(@NonNull TypeSerializerCollection typeSerializerCollection) {
        Objects.requireNonNull(typeSerializerCollection, "serializers");
        if (this.serializers == typeSerializerCollection) {
            return this;
        }
        return new ConfigurationOptions(this.mapFactory, this.header, typeSerializerCollection, this.acceptedTypes, this.objectMapperFactory, this.shouldCopyDefaults);
    }

    public @NonNull ConfigurationOptions withSerializers(@NonNull Consumer<TypeSerializerCollection> consumer) {
        Objects.requireNonNull(consumer, "serializerBuilder");
        TypeSerializerCollection typeSerializerCollection = this.serializers.newChild();
        consumer.accept(typeSerializerCollection);
        return new ConfigurationOptions(this.mapFactory, this.header, typeSerializerCollection, this.acceptedTypes, this.objectMapperFactory, this.shouldCopyDefaults);
    }

    public @NonNull ObjectMapperFactory getObjectMapperFactory() {
        return this.objectMapperFactory;
    }

    @Deprecated
    public @NonNull ConfigurationOptions setObjectMapperFactory(@NonNull ObjectMapperFactory objectMapperFactory) {
        return this.withObjectMapperFactory(objectMapperFactory);
    }

    public @NonNull ConfigurationOptions withObjectMapperFactory(@NonNull ObjectMapperFactory objectMapperFactory) {
        Objects.requireNonNull(objectMapperFactory, "factory");
        if (this.objectMapperFactory == objectMapperFactory) {
            return this;
        }
        return new ConfigurationOptions(this.mapFactory, this.header, this.serializers, this.acceptedTypes, objectMapperFactory, this.shouldCopyDefaults);
    }

    public boolean acceptsType(@NonNull Class<?> clazz) {
        Objects.requireNonNull(clazz, "type");
        if (this.acceptedTypes == null) {
            return true;
        }
        if (this.acceptedTypes.contains(clazz)) {
            return true;
        }
        if (clazz.isPrimitive() && this.acceptedTypes.contains(Primitives.wrap(clazz))) {
            return true;
        }
        if (Primitives.isWrapperType(clazz) && this.acceptedTypes.contains(Primitives.unwrap(clazz))) {
            return true;
        }
        for (Class clazz2 : this.acceptedTypes) {
            if (!clazz2.isAssignableFrom(clazz)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public @NonNull ConfigurationOptions setAcceptedTypes(@Nullable Set<Class<?>> set) {
        return this.withNativeTypes(set);
    }

    public @NonNull ConfigurationOptions withNativeTypes(@Nullable Set<Class<?>> set) {
        if (Objects.equals(this.acceptedTypes, set)) {
            return this;
        }
        return new ConfigurationOptions(this.mapFactory, this.header, this.serializers, set, this.objectMapperFactory, this.shouldCopyDefaults);
    }

    public boolean shouldCopyDefaults() {
        return this.shouldCopyDefaults;
    }

    @Deprecated
    public @NonNull ConfigurationOptions setShouldCopyDefaults(boolean bl) {
        return this.withShouldCopyDefaults(bl);
    }

    public @NonNull ConfigurationOptions withShouldCopyDefaults(boolean bl) {
        if (this.shouldCopyDefaults == bl) {
            return this;
        }
        return new ConfigurationOptions(this.mapFactory, this.header, this.serializers, this.acceptedTypes, this.objectMapperFactory, bl);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ConfigurationOptions)) {
            return false;
        }
        ConfigurationOptions configurationOptions = (ConfigurationOptions)object;
        return Objects.equals(this.shouldCopyDefaults, configurationOptions.shouldCopyDefaults) && Objects.equals(this.mapFactory, configurationOptions.mapFactory) && Objects.equals(this.header, configurationOptions.header) && Objects.equals(this.serializers, configurationOptions.serializers) && Objects.equals(this.acceptedTypes, configurationOptions.acceptedTypes) && Objects.equals(this.objectMapperFactory, configurationOptions.objectMapperFactory);
    }

    public int hashCode() {
        return Objects.hash(this.mapFactory, this.header, this.serializers, this.acceptedTypes, this.objectMapperFactory, this.shouldCopyDefaults);
    }

    public String toString() {
        return "ConfigurationOptions{mapFactory=" + this.mapFactory + ", header='" + this.header + '\'' + ", serializers=" + this.serializers + ", acceptedTypes=" + this.acceptedTypes + ", objectMapperFactory=" + this.objectMapperFactory + ", shouldCopyDefaults=" + this.shouldCopyDefaults + '}';
    }
}

