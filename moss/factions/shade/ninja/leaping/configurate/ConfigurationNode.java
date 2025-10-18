/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationVisitor;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.Types;
import moss.factions.shade.ninja.leaping.configurate.ValueType;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ConfigurationNode {
    public static final int NUMBER_DEF = 0;

    public static @NonNull ConfigurationNode root() {
        return ConfigurationNode.root(ConfigurationOptions.defaults());
    }

    public static @NonNull ConfigurationNode root(Consumer<ConfigurationNode> action) {
        return ConfigurationNode.root(ConfigurationOptions.defaults(), action);
    }

    public static @NonNull ConfigurationNode root(@NonNull ConfigurationOptions options) {
        return new SimpleConfigurationNode(null, null, options);
    }

    public static @NonNull ConfigurationNode root(@NonNull ConfigurationOptions options, Consumer<ConfigurationNode> action) {
        return new SimpleConfigurationNode(null, null, options).act(action);
    }

    public @Nullable Object getKey();

    public @NonNull Object[] getPath();

    public @Nullable ConfigurationNode getParent();

    public @NonNull ConfigurationNode getNode(@NonNull Object @NonNull ... var1);

    default public @NonNull ConfigurationNode getNode(@NonNull Iterable<?> path) {
        return this.getNode(Iterables.toArray(Objects.requireNonNull(path, "path"), Object.class));
    }

    public boolean isVirtual();

    public @NonNull ConfigurationOptions getOptions();

    @Deprecated
    public @NonNull ValueType getValueType();

    default public boolean isList() {
        return this.getValueType() == ValueType.LIST;
    }

    default public boolean isMap() {
        return this.getValueType() == ValueType.MAP;
    }

    @Deprecated
    default public boolean hasListChildren() {
        return this.isList();
    }

    @Deprecated
    default public boolean hasMapChildren() {
        return this.isMap();
    }

    default public boolean isEmpty() {
        if (this.isVirtual()) {
            return true;
        }
        if (this.isMap()) {
            return this.getChildrenMap().isEmpty();
        }
        if (this.isList()) {
            return this.getChildrenList().isEmpty();
        }
        return this.getValue() == null;
    }

    public @NonNull List<? extends ConfigurationNode> getChildrenList();

    public @NonNull Map<Object, ? extends ConfigurationNode> getChildrenMap();

    default public @Nullable Object getValue() {
        return this.getValue((Object)null);
    }

    public Object getValue(@Nullable Object var1);

    public Object getValue(@NonNull Supplier<Object> var1);

    default public <T> @Nullable T getValue(@NonNull Function<Object, T> transformer) {
        return this.getValue(transformer, (T)null);
    }

    public <T> T getValue(@NonNull Function<Object, T> var1, @Nullable T var2);

    public <T> T getValue(@NonNull Function<Object, T> var1, @NonNull Supplier<T> var2);

    public <T> @NonNull List<T> getList(@NonNull Function<Object, T> var1);

    public <T> List<T> getList(@NonNull Function<Object, T> var1, @Nullable List<T> var2);

    public <T> List<T> getList(@NonNull Function<Object, T> var1, @NonNull Supplier<List<T>> var2);

    default public <T> @NonNull List<T> getList(@NonNull TypeToken<T> type) throws ObjectMappingException {
        return this.getList(type, ImmutableList.of());
    }

    public <T> List<T> getList(@NonNull TypeToken<T> var1, @Nullable List<T> var2) throws ObjectMappingException;

    public <T> List<T> getList(@NonNull TypeToken<T> var1, @NonNull Supplier<List<T>> var2) throws ObjectMappingException;

    default public @Nullable String getString() {
        return this.getString(null);
    }

    default public String getString(@Nullable String def) {
        return this.getValue(Types::asString, def);
    }

    default public float getFloat() {
        return this.getFloat(0.0f);
    }

    default public float getFloat(float def) {
        return this.getValue(Types::asFloat, Float.valueOf(def)).floatValue();
    }

    default public double getDouble() {
        return this.getDouble(0.0);
    }

    default public double getDouble(double def) {
        return this.getValue(Types::asDouble, Double.valueOf(def));
    }

    default public int getInt() {
        return this.getInt(0);
    }

    default public int getInt(int def) {
        return this.getValue(Types::asInt, Integer.valueOf(def));
    }

    default public long getLong() {
        return this.getLong(0L);
    }

    default public long getLong(long def) {
        return this.getValue(Types::asLong, Long.valueOf(def));
    }

    default public boolean getBoolean() {
        return this.getBoolean(false);
    }

    default public boolean getBoolean(boolean def) {
        return this.getValue(Types::asBoolean, Boolean.valueOf(def));
    }

    default public <T> @Nullable T getValue(@NonNull TypeToken<T> type) throws ObjectMappingException {
        return this.getValue(type, (T)null);
    }

    public <T> T getValue(@NonNull TypeToken<T> var1, T var2) throws ObjectMappingException;

    public <T> T getValue(@NonNull TypeToken<T> var1, @NonNull Supplier<T> var2) throws ObjectMappingException;

    public @NonNull ConfigurationNode setValue(@Nullable Object var1);

    default public <T> @NonNull ConfigurationNode setValue(@NonNull TypeToken<T> type, @Nullable T value) throws ObjectMappingException {
        if (value == null) {
            this.setValue(null);
            return this;
        }
        TypeSerializer<T> serial = this.getOptions().getSerializers().get(type);
        if (serial != null) {
            serial.serialize(type, value, this);
        } else if (this.getOptions().acceptsType(value.getClass())) {
            this.setValue(value);
        } else {
            throw new ObjectMappingException("No serializer available for type " + type);
        }
        return this;
    }

    public @NonNull ConfigurationNode mergeValuesFrom(@NonNull ConfigurationNode var1);

    public boolean removeChild(@NonNull Object var1);

    @Deprecated
    public @NonNull ConfigurationNode getAppendedNode();

    default public @NonNull ConfigurationNode appendListNode() {
        return this.getAppendedNode();
    }

    public @NonNull ConfigurationNode copy();

    default public ConfigurationNode act(Consumer<? super ConfigurationNode> action) {
        action.accept(this);
        return this;
    }

    default public <S, T, E extends Exception> T visit(ConfigurationVisitor<S, T, E> visitor) throws E {
        return this.visit(visitor, visitor.newState());
    }

    default public <S, T, E extends Exception> T visit(ConfigurationVisitor<S, T, E> visitor, S state) throws E {
        throw new UnsupportedOperationException("Nodes of type " + this.getClass() + " do not support visitations!");
    }

    default public <S, T> T visit(ConfigurationVisitor.Safe<S, T> visitor) {
        return this.visit(visitor, visitor.newState());
    }

    default public <S, T> T visit(ConfigurationVisitor.Safe<S, T> visitor, S state) {
        throw new UnsupportedOperationException("Nodes of type " + this.getClass() + " do not support visitations!");
    }
}

