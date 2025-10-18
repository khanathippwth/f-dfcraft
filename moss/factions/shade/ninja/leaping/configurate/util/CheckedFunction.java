/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.util;

import java.util.Objects;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface CheckedFunction<I, O, E extends Exception> {
    public O apply(I var1) throws E;

    public static <I, O> CheckedFunction<I, O, RuntimeException> fromFunction(Function<I, @NonNull O> func) {
        return Objects.requireNonNull(func, "func")::apply;
    }
}

