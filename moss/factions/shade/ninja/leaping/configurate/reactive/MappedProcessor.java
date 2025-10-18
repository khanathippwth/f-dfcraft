/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.NoOpDisposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.Processor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Publisher;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionFailedException;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedFunction;
import org.checkerframework.checker.nullness.qual.Nullable;

class MappedProcessor<I, O>
implements Processor.Transactional<I, O> {
    private final Processor.TransactionalIso<O> processor;
    private final AtomicReference<Disposable> disposable = new AtomicReference();
    private final CheckedFunction<? super I, ? extends O, TransactionFailedException> mapper;
    private final @Nullable Publisher<I> parent;

    MappedProcessor(CheckedFunction<? super I, ? extends O, TransactionFailedException> checkedFunction, @Nullable Publisher<I> publisher) {
        this.processor = Processor.createTransactional(publisher.getExecutor());
        this.mapper = checkedFunction;
        this.parent = publisher;
    }

    @Override
    public Disposable subscribe(Subscriber<? super O> subscriber) {
        Disposable disposable2 = this.processor.subscribe(subscriber);
        if (disposable2 != NoOpDisposable.INSTANCE) {
            Disposable disposable3 = this.disposable.updateAndGet(disposable -> disposable == null && this.parent != null ? this.parent.subscribe(this) : disposable);
            if (disposable3 == NoOpDisposable.INSTANCE) {
                this.processor.onClose();
                return NoOpDisposable.INSTANCE;
            }
            return () -> {
                disposable2.dispose();
                if (!this.hasSubscribers()) {
                    Disposable disposable2 = this.disposable.getAndSet(null);
                    disposable2.dispose();
                }
            };
        }
        return disposable2;
    }

    @Override
    public boolean hasSubscribers() {
        return this.processor.hasSubscribers();
    }

    @Override
    public Executor getExecutor() {
        return this.processor.getExecutor();
    }

    @Override
    public void beginTransaction(I i) {
        this.processor.beginTransaction(this.mapper.apply(i));
    }

    @Override
    public void commit() {
        this.processor.commit();
    }

    @Override
    public void rollback() {
        this.processor.rollback();
    }

    @Override
    public void onError(Throwable throwable) {
        this.processor.onError(throwable);
    }

    @Override
    public void onClose() {
        Disposable disposable = this.disposable.getAndSet(null);
        if (disposable != null) {
            disposable.dispose();
        }
        this.processor.onClose();
    }

    @Override
    public void inject(O o) {
        this.processor.submit(o);
    }

    @Override
    public void setFallbackHandler(@Nullable Subscriber<O> subscriber) {
        this.processor.setFallbackHandler(subscriber);
    }

    @Override
    public boolean closeIfUnsubscribed() {
        if (this.processor.closeIfUnsubscribed()) {
            Disposable disposable = this.disposable.getAndSet(null);
            if (disposable != null) {
                disposable.dispose();
            }
            return true;
        }
        return false;
    }
}

