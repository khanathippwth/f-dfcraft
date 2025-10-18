/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.Publisher;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedSupplier;

class ExecutePublisher<V>
implements Publisher<V> {
    private final CompletableFuture<V> actor = new CompletableFuture();
    private final Executor executor;

    public ExecutePublisher(CheckedSupplier<V, ?> checkedSupplier, Executor executor) {
        executor.execute(() -> {
            try {
                this.actor.complete(checkedSupplier.get());
            } catch (Throwable throwable) {
                this.actor.completeExceptionally(throwable);
            }
        });
        this.executor = executor;
    }

    @Override
    public Disposable subscribe(Subscriber<? super V> subscriber) {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        this.actor.whenCompleteAsync((object, throwable) -> {
            if (atomicBoolean.compareAndSet(true, false)) {
                if (throwable != null) {
                    subscriber.onError((Throwable)throwable);
                } else {
                    try {
                        subscriber.submit(object);
                        subscriber.onClose();
                    } catch (Throwable throwable2) {
                        subscriber.onError(throwable2);
                    }
                }
            }
        }, this.executor);
        return () -> atomicBoolean.set(false);
    }

    @Override
    public boolean hasSubscribers() {
        return !this.actor.isDone() && this.actor.getNumberOfDependents() > 0;
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }
}

