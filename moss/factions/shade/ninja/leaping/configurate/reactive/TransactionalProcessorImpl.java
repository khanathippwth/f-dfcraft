/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.concurrent.Executor;
import moss.factions.shade.ninja.leaping.configurate.reactive.AbstractProcessor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Processor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionFailedException;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionalRegistration;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionalSubscriber;

class TransactionalProcessorImpl<V>
extends AbstractProcessor<V, TransactionalRegistration<V>>
implements Processor.TransactionalIso<V> {
    protected TransactionalProcessorImpl(Executor executor) {
        super(executor);
    }

    @Override
    public void submit(V v) {
        this.executor.execute(() -> Processor.TransactionalIso.super.submit(v));
    }

    @Override
    public void beginTransaction(V v) {
        if (this.subscriberCount.get() >= 0) {
            boolean bl = false;
            Object object = this.registrations.iterator();
            while (object.hasNext()) {
                TransactionalRegistration transactionalRegistration = (TransactionalRegistration)object.next();
                try {
                    bl = true;
                    transactionalRegistration.beginTransaction(v);
                } catch (TransactionFailedException transactionFailedException) {
                    throw transactionFailedException;
                } catch (Throwable throwable) {
                    object.remove();
                    this.subscriberCount.getAndDecrement();
                    transactionalRegistration.onError(throwable);
                }
            }
            if (!bl && (object = this.fallbackHandler) != null) {
                object.submit(v);
            }
        }
    }

    @Override
    public void commit() {
        this.forEachOrRemove(TransactionalRegistration::commit);
    }

    @Override
    public void rollback() {
        this.forEachOrRemove(TransactionalRegistration::rollback);
    }

    @Override
    protected TransactionalRegistration<V> createRegistration(Subscriber<? super V> subscriber) {
        if (subscriber instanceof TransactionalSubscriber) {
            return new TransactionalRegistration.Fully(this, (TransactionalSubscriber)subscriber);
        }
        return new TransactionalRegistration.Wrapped<V>(this, subscriber);
    }
}

