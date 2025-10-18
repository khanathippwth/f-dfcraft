/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reactive;

import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import org.checkerframework.checker.interning.qual.InternedDistinct;

class NoOpDisposable
implements Disposable {
    static final @InternedDistinct NoOpDisposable INSTANCE = new NoOpDisposable();

    private NoOpDisposable() {
    }

    @Override
    public void dispose() {
    }
}

