/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.NoOpDisposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.Publisher;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class PublisherCached<V>
implements Publisher.Cached<V>,
AutoCloseable {
    private final Publisher<V> parent;
    private final Set<Subscriber<? super V>> subscribers = ConcurrentHashMap.newKeySet();
    private volatile @MonotonicNonNull V value;
    private final Disposable closer;

    public PublisherCached(Publisher<V> publisher, @Nullable V v) {
        this.parent = publisher;
        this.value = v;
        this.closer = this.parent.subscribe(object -> {
            this.value = object;
        });
    }

    @Override
    public Disposable subscribe(Subscriber<? super V> subscriber) {
        Disposable disposable = this.parent.subscribe(subscriber);
        if (disposable != NoOpDisposable.INSTANCE) {
            this.subscribers.add(subscriber);
            V v = this.value;
            if (v != null) {
                subscriber.submit(v);
            }
            return () -> {
                this.subscribers.remove(subscriber);
                disposable.dispose();
            };
        }
        return disposable;
    }

    @Override
    public boolean hasSubscribers() {
        return !this.subscribers.isEmpty();
    }

    @Override
    public Publisher.Cached<V> cache() {
        return this;
    }

    @Override
    public Publisher.Cached<V> cache(@Nullable V v) {
        if (this.value == null) {
            this.value = v;
        }
        return this;
    }

    @Override
    public Executor getExecutor() {
        return this.parent.getExecutor();
    }

    @Override
    public V get() {
        return this.value;
    }

    @Override
    public void submit(V v) {
        this.value = v;
        this.subscribers.forEach(subscriber -> subscriber.submit(v));
    }

    @Override
    public void close() {
        this.closer.dispose();
    }
}

