/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.transformation;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.transformation.ChainedConfigurationTransformation;
import moss.factions.shade.ninja.leaping.configurate.transformation.MoveStrategy;
import moss.factions.shade.ninja.leaping.configurate.transformation.NodePathComparator;
import moss.factions.shade.ninja.leaping.configurate.transformation.SingleConfigurationTransformation;
import moss.factions.shade.ninja.leaping.configurate.transformation.TransformAction;
import moss.factions.shade.ninja.leaping.configurate.transformation.VersionedTransformation;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class ConfigurationTransformation {
    public static final Object WILDCARD_OBJECT = new Object();

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static @NonNull VersionedBuilder versionedBuilder() {
        return new VersionedBuilder();
    }

    public static @NonNull ConfigurationTransformation chain(ConfigurationTransformation ... configurationTransformationArray) {
        return new ChainedConfigurationTransformation(configurationTransformationArray);
    }

    public abstract void apply(@NonNull ConfigurationNode var1);

    public static final class NodePath
    implements moss.factions.shade.ninja.leaping.configurate.transformation.NodePath {
        Object[] arr;

        NodePath() {
        }

        @Override
        public Object get(int n) {
            return this.arr[n];
        }

        @Override
        public int size() {
            return this.arr.length;
        }

        @Override
        public Object[] getArray() {
            return Arrays.copyOf(this.arr, this.arr.length);
        }

        @Override
        public @NonNull Iterator<Object> iterator() {
            return Iterators.forArray(this.arr);
        }
    }

    public static final class VersionedBuilder {
        private Object[] versionKey = new Object[]{"version"};
        private final SortedMap<Integer, ConfigurationTransformation> versions = new TreeMap<Integer, ConfigurationTransformation>();

        protected VersionedBuilder() {
        }

        public @NonNull VersionedBuilder setVersionKey(@NonNull Object... objectArray) {
            this.versionKey = Arrays.copyOf(objectArray, objectArray.length, Object[].class);
            return this;
        }

        public @NonNull VersionedBuilder addVersion(int n, @NonNull ConfigurationTransformation configurationTransformation) {
            this.versions.put(n, configurationTransformation);
            return this;
        }

        public @NonNull ConfigurationTransformation build() {
            return new VersionedTransformation(this.versionKey, this.versions);
        }
    }

    public static final class Builder {
        private MoveStrategy strategy = MoveStrategy.OVERWRITE;
        private final SortedMap<Object[], TransformAction> actions = new TreeMap<Object[], TransformAction>(new NodePathComparator());

        protected Builder() {
        }

        public @NonNull Builder addAction(Object[] objectArray, TransformAction transformAction) {
            this.actions.put(objectArray, transformAction);
            return this;
        }

        public @NonNull MoveStrategy getMoveStrategy() {
            return this.strategy;
        }

        public @NonNull Builder setMoveStrategy(@NonNull MoveStrategy moveStrategy) {
            this.strategy = moveStrategy;
            return this;
        }

        public @NonNull ConfigurationTransformation build() {
            return new SingleConfigurationTransformation(this.actions, this.strategy);
        }
    }
}

