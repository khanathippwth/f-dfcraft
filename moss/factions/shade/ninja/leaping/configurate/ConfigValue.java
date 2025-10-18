/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import java.util.Iterator;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ValueType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class ConfigValue {
    protected final @NonNull SimpleConfigurationNode holder;

    protected ConfigValue(@NonNull SimpleConfigurationNode simpleConfigurationNode) {
        this.holder = simpleConfigurationNode;
    }

    abstract ValueType getType();

    abstract @Nullable Object getValue();

    abstract void setValue(@Nullable Object var1);

    abstract @Nullable SimpleConfigurationNode putChild(@NonNull Object var1, @Nullable SimpleConfigurationNode var2);

    abstract @Nullable SimpleConfigurationNode putChildIfAbsent(@NonNull Object var1, @Nullable SimpleConfigurationNode var2);

    abstract @Nullable SimpleConfigurationNode getChild(@Nullable Object var1);

    abstract @NonNull Iterable<SimpleConfigurationNode> iterateChildren();

    abstract @NonNull ConfigValue copy(@NonNull SimpleConfigurationNode var1);

    abstract boolean isEmpty();

    void clear() {
        Iterator<SimpleConfigurationNode> iterator = this.iterateChildren().iterator();
        while (iterator.hasNext()) {
            SimpleConfigurationNode simpleConfigurationNode = iterator.next();
            simpleConfigurationNode.attached = false;
            iterator.remove();
            if (!simpleConfigurationNode.getParentEnsureAttached().equals(this.holder)) continue;
            simpleConfigurationNode.clear();
        }
    }
}

