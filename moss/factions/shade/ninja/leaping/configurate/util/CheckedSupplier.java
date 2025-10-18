/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.util;

import java.util.function.Supplier;

@FunctionalInterface
public interface CheckedSupplier<V, E extends Throwable> {
    public V get() throws E;

    public static <V> CheckedSupplier<V, RuntimeException> fromSupplier(Supplier<V> consumer) {
        return consumer::get;
    }
}

