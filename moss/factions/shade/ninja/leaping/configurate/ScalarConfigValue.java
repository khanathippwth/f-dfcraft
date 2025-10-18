/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import moss.factions.shade.ninja.leaping.configurate.ConfigValue;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ValueType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class ScalarConfigValue
extends ConfigValue {
    private volatile Object value;

    ScalarConfigValue(SimpleConfigurationNode simpleConfigurationNode) {
        super(simpleConfigurationNode);
    }

    @Override
    ValueType getType() {
        return ValueType.SCALAR;
    }

    @Override
    public @Nullable Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(@Nullable Object object) {
        if (!this.holder.getOptions().acceptsType(Objects.requireNonNull(object).getClass())) {
            throw new IllegalArgumentException("Configuration does not accept objects of type " + object.getClass());
        }
        this.value = object;
    }

    @Override
    @Nullable SimpleConfigurationNode putChild(@NonNull Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode) {
        return null;
    }

    @Override
    @Nullable SimpleConfigurationNode putChildIfAbsent(@NonNull Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode) {
        return null;
    }

    @Override
    public @Nullable SimpleConfigurationNode getChild(@Nullable Object object) {
        return null;
    }

    @Override
    public @NonNull Iterable<SimpleConfigurationNode> iterateChildren() {
        return Collections.emptySet();
    }

    @Override
    @NonNull ScalarConfigValue copy(@NonNull SimpleConfigurationNode simpleConfigurationNode) {
        ScalarConfigValue scalarConfigValue = new ScalarConfigValue(simpleConfigurationNode);
        scalarConfigValue.value = this.value;
        return scalarConfigValue;
    }

    @Override
    boolean isEmpty() {
        Object object = this.value;
        return object instanceof String && ((String)object).isEmpty() || object instanceof Collection && ((Collection)object).isEmpty();
    }

    @Override
    public void clear() {
        this.value = null;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ScalarConfigValue scalarConfigValue = (ScalarConfigValue)object;
        return Objects.equals(this.value, scalarConfigValue.value);
    }

    public int hashCode() {
        return 7 + Objects.hashCode(this.value);
    }

    public String toString() {
        return "ScalarConfigValue{value=" + this.value + '}';
    }
}

