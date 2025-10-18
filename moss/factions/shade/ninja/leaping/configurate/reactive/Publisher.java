/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.ExecutePublisher;
import moss.factions.shade.ninja.leaping.configurate.reactive.MappedProcessor;
import moss.factions.shade.ninja.leaping.configurate.reactive.PublisherCached;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionFailedException;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedFunction;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedSupplier;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Publisher<V> {
    public static <V, E extends Exception> Publisher<V> execute(CheckedSupplier<V, E> action) {
        return Publisher.execute(action, ForkJoinPool.commonPool());
    }

    public static <V, E extends Exception> Publisher<V> execute(CheckedSupplier<V, E> action, Executor executor) {
        return new ExecutePublisher<V>(Objects.requireNonNull(action, "action"), Objects.requireNonNull(executor, "executor"));
    }

    public Disposable subscribe(Subscriber<? super V> var1);

    public boolean hasSubscribers();

    default public <R> Publisher<R> map(CheckedFunction<? super V, ? extends R, TransactionFailedException> mapper) {
        return new MappedProcessor<V, R>(mapper, this);
    }

    default public Cached<V> cache() {
        return this.cache(null);
    }

    default public Cached<V> cache(@Nullable V initialValue) {
        return new PublisherCached<V>(this, initialValue);
    }

    public Executor getExecutor();

    public static interface Cached<V>
    extends Publisher<V> {
        public V get();

        public void submit(V var1);
    }
}

