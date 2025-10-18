/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import java.util.concurrent.Executor;
import moss.factions.shade.ninja.leaping.configurate.reactive.AbstractProcessor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Processor;
import moss.factions.shade.ninja.leaping.configurate.reactive.RegistrationImpl;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;

class ProcessorImpl<V>
extends AbstractProcessor<V, RegistrationImpl<V>>
implements Processor.Iso<V> {
    ProcessorImpl(Executor executor) {
        super(executor);
    }

    @Override
    public void submit(V v) {
        if (this.subscriberCount.get() >= 0) {
            boolean bl = false;
            Object object = this.registrations.iterator();
            while (object.hasNext()) {
                RegistrationImpl registrationImpl = (RegistrationImpl)object.next();
                try {
                    bl = true;
                    registrationImpl.submit(v);
                } catch (Throwable throwable) {
                    object.remove();
                    this.subscriberCount.getAndDecrement();
                    registrationImpl.subscriber.onError(throwable);
                }
            }
            if (!bl && (object = this.fallbackHandler) != null) {
                object.submit(v);
            }
        }
    }

    @Override
    protected RegistrationImpl<V> createRegistration(Subscriber<? super V> subscriber) {
        return new RegistrationImpl<V>(this, subscriber);
    }
}

