/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reference;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class PrefixedNameThreadFactory
implements ThreadFactory {
    private final String name;
    private final boolean daemon;
    private final AtomicInteger counter = new AtomicInteger();

    PrefixedNameThreadFactory(String string, boolean bl) {
        this.name = string.endsWith("-") ? string : string + "-";
        this.daemon = bl;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, this.name + this.counter.getAndIncrement());
        thread.setDaemon(this.daemon);
        return thread;
    }
}

