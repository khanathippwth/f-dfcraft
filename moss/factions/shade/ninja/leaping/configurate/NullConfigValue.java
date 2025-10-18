/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import java.util.Collections;
import moss.factions.shade.ninja.leaping.configurate.ConfigValue;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ValueType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class NullConfigValue
extends ConfigValue {
    private static final NullConfigValue INSTANCE = new NullConfigValue();

    static NullConfigValue instance() {
        return INSTANCE;
    }

    private NullConfigValue() {
        super(null);
    }

    @Override
    ValueType getType() {
        return ValueType.NULL;
    }

    @Override
    public @Nullable Object getValue() {
        return null;
    }

    @Override
    public void setValue(@Nullable Object object) {
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
    @NonNull NullConfigValue copy(@NonNull SimpleConfigurationNode simpleConfigurationNode) {
        return NullConfigValue.instance();
    }

    @Override
    boolean isEmpty() {
        return true;
    }

    @Override
    public void clear() {
    }

    public boolean equals(Object object) {
        return object instanceof NullConfigValue;
    }

    public int hashCode() {
        return 1009;
    }

    public String toString() {
        return "NullConfigValue{}";
    }
}

