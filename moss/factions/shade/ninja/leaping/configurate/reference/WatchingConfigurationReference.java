/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reference;

import com.google.common.collect.Maps;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.concurrent.Executor;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.loader.ConfigurationLoader;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reference.ConfigurationReference;
import moss.factions.shade.ninja.leaping.configurate.reference.ManualConfigurationReference;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

class WatchingConfigurationReference<N extends ConfigurationNode>
extends ManualConfigurationReference<N>
implements Subscriber<WatchEvent<?>> {
    private volatile boolean saveSuppressed = false;
    private @MonotonicNonNull Disposable disposable;

    WatchingConfigurationReference(ConfigurationLoader<? extends N> configurationLoader, Executor executor) {
        super(configurationLoader, executor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void save(N n) {
        ConfigurationLoader configurationLoader = this.getLoader();
        synchronized (configurationLoader) {
            try {
                this.saveSuppressed = true;
                super.save(n);
            } finally {
                this.saveSuppressed = false;
            }
        }
    }

    @Override
    public void close() {
        super.close();
        if (this.disposable != null) {
            this.disposable.dispose();
        }
    }

    @Override
    public void submit(WatchEvent<?> watchEvent) {
        if (!this.saveSuppressed || watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
            try {
                this.load();
            } catch (Exception exception) {
                this.errorListener.submit(Maps.immutableEntry(ConfigurationReference.ErrorPhase.LOADING, exception));
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        this.errorListener.submit(Maps.immutableEntry(ConfigurationReference.ErrorPhase.UNKNOWN, throwable));
    }

    @Override
    public void onClose() {
        this.close();
    }

    void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }
}

