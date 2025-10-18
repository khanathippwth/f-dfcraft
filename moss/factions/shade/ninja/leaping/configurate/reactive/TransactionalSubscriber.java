/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionFailedException;

public interface TransactionalSubscriber<V>
extends Subscriber<V> {
    @Override
    default public void submit(V item) {
        try {
            this.beginTransaction(item);
            this.commit();
        } catch (TransactionFailedException ex) {
            this.rollback();
        } catch (Exception ex) {
            this.rollback();
            throw ex;
        }
    }

    public void beginTransaction(V var1) throws TransactionFailedException;

    public void commit();

    public void rollback();
}

