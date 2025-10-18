/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import moss.factions.shade.ninja.leaping.configurate.reactive.MappedProcessor;
import moss.factions.shade.ninja.leaping.configurate.reactive.ProcessorImpl;
import moss.factions.shade.ninja.leaping.configurate.reactive.Publisher;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionFailedException;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionalProcessorImpl;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionalSubscriber;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedFunction;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Processor<I, O>
extends Publisher<O>,
Subscriber<I> {
    public static <V> Iso<V> create() {
        return Processor.create(ForkJoinPool.commonPool());
    }

    public static <V> TransactionalIso<V> createTransactional() {
        return Processor.createTransactional(ForkJoinPool.commonPool());
    }

    public static <V> Iso<V> create(Executor executor) {
        return new ProcessorImpl(executor);
    }

    public static <V> TransactionalIso<V> createTransactional(Executor exec) {
        return new TransactionalProcessorImpl(exec);
    }

    default public <R> Processor<O, R> map(CheckedFunction<? super O, ? extends R, TransactionFailedException> mapper) {
        return new MappedProcessor<O, R>(mapper, this);
    }

    public void inject(O var1);

    public void setFallbackHandler(@Nullable Subscriber<O> var1);

    public boolean closeIfUnsubscribed();

    public static interface TransactionalIso<V>
    extends Transactional<V, V>,
    Iso<V> {
    }

    public static interface Transactional<I, O>
    extends Processor<I, O>,
    Publisher<O>,
    TransactionalSubscriber<I> {
    }

    public static interface Iso<V>
    extends Processor<V, V> {
        @Override
        default public void inject(V element) {
            this.submit(element);
        }
    }
}

