/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reference;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.Processor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import org.checkerframework.checker.nullness.qual.Nullable;

class DirectoryListenerRegistration
implements Subscriber<WatchEvent<?>> {
    private final Lock lock = new ReentrantLock();
    private final WatchKey key;
    private final ConcurrentHashMap<Path, Processor<WatchEvent<?>, WatchEvent<?>>> fileListeners = new ConcurrentHashMap();
    private final Executor executor;
    private final Processor<WatchEvent<?>, WatchEvent<?>> dirListeners;

    DirectoryListenerRegistration(WatchKey watchKey, Executor executor) {
        this.key = Objects.requireNonNull(watchKey, "key");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.dirListeners = Processor.create(executor);
    }

    public WatchKey getKey() {
        return this.key;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void submit(WatchEvent<?> watchEvent) {
        Path path2 = (Path)watchEvent.context();
        this.lock.lock();
        try {
            @Nullable Processor processor2 = this.fileListeners.computeIfPresent(path2, (path, processor) -> processor.closeIfUnsubscribed() ? null : processor);
            this.dirListeners.submit(watchEvent);
            if (processor2 != null) {
                processor2.submit(watchEvent);
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void onClose() {
        this.lock.lock();
        try {
            try {
                this.dirListeners.onClose();
            } catch (Throwable throwable) {
                this.dirListeners.onError(throwable);
            }
            this.fileListeners.forEach((path, processor) -> {
                try {
                    processor.onClose();
                } catch (Throwable throwable) {
                    processor.onError(throwable);
                }
            });
            this.fileListeners.clear();
            this.key.cancel();
        } finally {
            this.lock.unlock();
        }
    }

    public Disposable subscribe(Subscriber<WatchEvent<?>> subscriber) {
        this.lock.lock();
        try {
            Disposable disposable = this.dirListeners.subscribe(subscriber);
            return disposable;
        } finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Disposable subscribe(Path path2, Subscriber<WatchEvent<?>> subscriber) {
        this.lock.lock();
        try {
            Disposable disposable = this.fileListeners.computeIfAbsent(path2, path -> Processor.create(this.executor)).subscribe(subscriber);
            return disposable;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean hasSubscribers() {
        this.lock.lock();
        try {
            boolean bl = this.dirListeners.hasSubscribers() || !this.fileListeners.isEmpty();
            return bl;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DirectoryListenerRegistration)) {
            return false;
        }
        DirectoryListenerRegistration directoryListenerRegistration = (DirectoryListenerRegistration)object;
        return this.getKey().equals(directoryListenerRegistration.getKey()) && this.fileListeners.equals(directoryListenerRegistration.fileListeners) && this.dirListeners.equals(directoryListenerRegistration.dirListeners);
    }

    public int hashCode() {
        return Objects.hash(this.getKey(), this.fileListeners, this.dirListeners);
    }

    public boolean closeIfEmpty() {
        this.lock.lock();
        try {
            if (!this.hasSubscribers()) {
                this.onClose();
                boolean bl = true;
                return bl;
            }
        } finally {
            this.lock.unlock();
        }
        return false;
    }
}

