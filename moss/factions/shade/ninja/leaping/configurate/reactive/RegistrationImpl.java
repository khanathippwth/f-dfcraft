/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import moss.factions.shade.ninja.leaping.configurate.reactive.AbstractProcessor;
import moss.factions.shade.ninja.leaping.configurate.reactive.ProcessorImpl;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;

class RegistrationImpl<V>
implements AbstractProcessor.Registration<V> {
    final ProcessorImpl<V> holder;
    final Subscriber<? super V> subscriber;

    RegistrationImpl(ProcessorImpl<V> processorImpl, Subscriber<? super V> subscriber) {
        this.holder = processorImpl;
        this.subscriber = subscriber;
    }

    @Override
    public void dispose() {
        if (this.holder.registrations.remove(this)) {
            this.holder.subscriberCount.getAndDecrement();
        }
    }

    @Override
    public void submit(V v) {
        this.subscriber.submit(v);
    }

    @Override
    public void onClose() {
        this.subscriber.onClose();
    }

    @Override
    public void onError(Throwable throwable) {
        this.subscriber.onError(throwable);
    }
}

