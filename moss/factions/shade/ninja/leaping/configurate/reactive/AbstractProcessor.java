/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.NoOpDisposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.Processor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class AbstractProcessor<V, R extends Registration<V>>
implements Processor.Iso<V> {
    private static final int CLOSED_VALUE = -1073741824;
    final AtomicInteger subscriberCount = new AtomicInteger();
    volatile @Nullable Subscriber<V> fallbackHandler;
    protected final Set<R> registrations = ConcurrentHashMap.newKeySet();
    protected final Executor executor;

    protected AbstractProcessor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    protected abstract R createRegistration(Subscriber<? super V> var1);

    @Override
    public Disposable subscribe(Subscriber<? super V> subscriber) {
        if (this.subscriberCount.get() < 0 || this.subscriberCount.incrementAndGet() <= 0) {
            subscriber.onError(new IllegalStateException("Processor " + this + " is already closed!"));
            this.subscriberCount.set(-1073741824);
            return NoOpDisposable.INSTANCE;
        }
        R r = this.createRegistration(subscriber);
        this.registrations.add(r);
        return r;
    }

    @Override
    public boolean hasSubscribers() {
        return this.subscriberCount.get() > 0;
    }

    @Override
    public void onError(Throwable throwable) {
        Processor.Iso.super.onError(throwable);
        this.onClose();
    }

    @Override
    public void onClose() {
        this.executor.execute(() -> {
            this.subscriberCount.set(-1073741824);
            for (Registration registration : this.registrations) {
                try {
                    registration.onClose();
                } catch (Throwable throwable) {}
            }
            this.registrations.clear();
        });
    }

    protected void forEachOrRemove(Consumer<R> consumer) {
        Iterator<R> iterator = this.registrations.iterator();
        while (iterator.hasNext()) {
            Registration registration = (Registration)iterator.next();
            try {
                consumer.accept(registration);
            } catch (Throwable throwable) {
                iterator.remove();
                this.subscriberCount.getAndDecrement();
                try {
                    registration.onError(throwable);
                } catch (Throwable throwable2) {
                    Processor.Iso.super.onError(throwable2);
                }
            }
        }
    }

    @Override
    public void setFallbackHandler(@Nullable Subscriber<V> subscriber) {
        this.fallbackHandler = subscriber;
    }

    @Override
    public boolean closeIfUnsubscribed() {
        this.executor.execute(() -> {
            if (this.subscriberCount.compareAndSet(0, -1073741824)) {
                for (Registration registration : this.registrations) {
                    registration.onClose();
                }
                this.registrations.clear();
            }
        });
        return this.subscriberCount.get() <= 0;
    }

    protected static interface Registration<V>
    extends Disposable {
        public void submit(V var1);

        public void onClose();

        public void onError(Throwable var1);
    }
}

