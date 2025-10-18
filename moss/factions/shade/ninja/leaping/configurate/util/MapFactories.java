/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.util;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import moss.factions.shade.ninja.leaping.configurate.util.MapFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MapFactories {
    private MapFactories() {
    }

    public static MapFactory unordered() {
        return DefaultFactory.UNORDERED;
    }

    public static MapFactory sorted(Comparator<Object> comparator) {
        return new SortedMapFactory(Objects.requireNonNull(comparator, "comparator"));
    }

    public static MapFactory sortedNatural() {
        return DefaultFactory.SORTED_NATURAL;
    }

    public static MapFactory insertionOrdered() {
        return DefaultFactory.INSERTION_ORDERED;
    }

    private static class SynchronizedWrapper<K, V>
    implements ConcurrentMap<K, V> {
        private final Map<K, V> wrapped;

        private SynchronizedWrapper(Map<K, V> map) {
            this.wrapped = map;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V putIfAbsent(K k, V v) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                if (this.wrapped.containsKey(k)) {
                    return this.wrapped.get(k);
                }
                this.wrapped.put(k, v);
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object object, Object object2) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                if (Objects.equals(object2, this.wrapped.get(object))) {
                    return this.wrapped.remove(object) != null;
                }
            }
            return false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K k, V v, V v2) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                if (Objects.equals(v, this.wrapped.get(k))) {
                    this.wrapped.put(k, v2);
                    return true;
                }
            }
            return false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V replace(K k, V v) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                if (this.wrapped.containsKey(k)) {
                    return this.wrapped.put(k, v);
                }
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(Object object) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.containsKey(object);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(Object object) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.containsKey(object);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V get(Object object) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.get(object);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V put(K k, V v) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V remove(Object object) {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.remove(object);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            Map<K, V> map2 = this.wrapped;
            synchronized (map2) {
                this.wrapped.putAll(map);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                this.wrapped.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<K> keySet() {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return ImmutableSet.copyOf(this.wrapped.keySet());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Collection<V> values() {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return ImmutableSet.copyOf(this.wrapped.values());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return ImmutableSet.copyOf(this.wrapped.entrySet());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.equals(object);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return this.wrapped.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Map<K, V> map = this.wrapped;
            synchronized (map) {
                return "SynchronizedWrapper{backing=" + this.wrapped.toString() + '}';
            }
        }
    }

    private static final class SortedMapFactory
    implements MapFactory {
        private final Comparator<Object> comparator;

        private SortedMapFactory(Comparator<Object> comparator) {
            this.comparator = comparator;
        }

        @Override
        public <K, V> @NonNull ConcurrentMap<K, V> create() {
            return new ConcurrentSkipListMap(this.comparator);
        }

        public boolean equals(Object object) {
            return object instanceof SortedMapFactory && this.comparator.equals(((SortedMapFactory)object).comparator);
        }

        public int hashCode() {
            return this.comparator.hashCode();
        }

        public String toString() {
            return "SortedMapFactory{comparator=" + this.comparator + '}';
        }
    }

    private static enum DefaultFactory implements MapFactory
    {
        UNORDERED{

            @Override
            public <K, V> @NonNull ConcurrentMap<K, V> create() {
                return new ConcurrentHashMap();
            }
        }
        ,
        SORTED_NATURAL{

            @Override
            public <K, V> @NonNull ConcurrentMap<K, V> create() {
                return new ConcurrentSkipListMap();
            }
        }
        ,
        INSERTION_ORDERED{

            @Override
            public <K, V> @NonNull ConcurrentMap<K, V> create() {
                return new SynchronizedWrapper(new LinkedHashMap());
            }
        };

    }
}

