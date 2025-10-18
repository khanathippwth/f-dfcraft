/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import moss.factions.shade.ninja.leaping.configurate.reactive.AbstractProcessor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionFailedException;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionalProcessorImpl;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionalSubscriber;

interface TransactionalRegistration<V>
extends AbstractProcessor.Registration<V> {
    public TransactionalProcessorImpl<V> getHolder();

    @Override
    default public void dispose() {
        if (this.getHolder().registrations.remove(this)) {
            this.getHolder().subscriberCount.getAndDecrement();
        }
    }

    @Override
    default public void submit(V value) {
        try {
            this.beginTransaction(value);
            this.commit();
        } catch (TransactionFailedException ex) {
            this.rollback();
        }
    }

    public void beginTransaction(V var1) throws TransactionFailedException;

    public void commit();

    public void rollback();

    public static class Fully<V>
    implements TransactionalRegistration<V> {
        private final TransactionalProcessorImpl<V> holder;
        private final TransactionalSubscriber<? super V> sub;
        private final Lock lock = new ReentrantLock();

        Fully(TransactionalProcessorImpl<V> transactionalProcessorImpl, TransactionalSubscriber<? super V> transactionalSubscriber) {
            this.holder = transactionalProcessorImpl;
            this.sub = transactionalSubscriber;
        }

        @Override
        public TransactionalProcessorImpl<V> getHolder() {
            return this.holder;
        }

        @Override
        public void beginTransaction(V v) {
            this.lock.lock();
            this.sub.beginTransaction(v);
        }

        @Override
        public void commit() {
            try {
                this.sub.commit();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public void rollback() {
            try {
                this.sub.rollback();
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public void onClose() {
            this.sub.onClose();
        }

        @Override
        public void onError(Throwable throwable) {
            this.sub.onError(throwable);
        }
    }

    public static class Wrapped<V>
    implements TransactionalRegistration<V> {
        private final AtomicReference<V> active = new AtomicReference();
        private final TransactionalProcessorImpl<V> holder;
        private final Subscriber<? super V> sub;

        Wrapped(TransactionalProcessorImpl<V> transactionalProcessorImpl, Subscriber<? super V> subscriber) {
            this.holder = transactionalProcessorImpl;
            this.sub = subscriber;
        }

        @Override
        public TransactionalProcessorImpl<V> getHolder() {
            return this.holder;
        }

        @Override
        public void beginTransaction(V v) {
            this.active.set(v);
        }

        @Override
        public void commit() {
            Object v = this.active.getAndSet(null);
            if (v != null) {
                this.sub.submit(v);
            }
        }

        @Override
        public void rollback() {
            this.active.set(null);
        }

        @Override
        public void onClose() {
            this.sub.onClose();
        }

        @Override
        public void onError(Throwable throwable) {
            this.sub.onError(throwable);
        }
    }
}

