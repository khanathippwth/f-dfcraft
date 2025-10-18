/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedConsumer<V, E extends Throwable> {
    public void accept(V var1) throws E;

    public static <V> CheckedConsumer<V, RuntimeException> fromConsumer(Consumer<V> consumer) {
        return consumer::accept;
    }
}

