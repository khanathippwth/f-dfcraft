/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reference;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.loader.ConfigurationLoader;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reference.ConfigurationReference;
import moss.factions.shade.ninja.leaping.configurate.reference.DirectoryListenerRegistration;
import moss.factions.shade.ninja.leaping.configurate.reference.PrefixedNameThreadFactory;
import org.checkerframework.checker.nullness.qual.Nullable;

public class WatchServiceListener
implements AutoCloseable {
    private static final WatchEvent.Kind<?>[] DEFAULT_WATCH_EVENTS = new WatchEvent.Kind[]{StandardWatchEventKinds.OVERFLOW, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY};
    private static final int PARALLEL_THRESHOLD = 100;
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new PrefixedNameThreadFactory("Configurate-WatchService", true);
    private final WatchService watchService;
    private volatile boolean open = true;
    private final Thread executor;
    final Executor taskExecutor;
    private final ConcurrentHashMap<Path, DirectoryListenerRegistration> activeListeners = new ConcurrentHashMap();
    private static final ThreadLocal<IOException> exceptionHolder = new ThreadLocal();

    public static Builder builder() {
        return new Builder();
    }

    public static WatchServiceListener create() {
        return new WatchServiceListener(DEFAULT_THREAD_FACTORY, FileSystems.getDefault(), ForkJoinPool.commonPool());
    }

    private WatchServiceListener(ThreadFactory threadFactory, FileSystem fileSystem, Executor executor) {
        this.watchService = fileSystem.newWatchService();
        this.executor = threadFactory.newThread(() -> {
            while (this.open) {
                WatchKey watchKey;
                try {
                    watchKey = this.watchService.take();
                } catch (InterruptedException | ClosedWatchServiceException exception) {
                    break;
                }
                Path path = (Path)watchKey.watchable();
                DirectoryListenerRegistration directoryListenerRegistration = this.activeListeners.get(path);
                if (directoryListenerRegistration == null) continue;
                HashSet hashSet = new HashSet();
                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    if (!watchKey.isValid()) break;
                    if (!hashSet.add(watchEvent.context())) continue;
                    directoryListenerRegistration.submit(watchEvent);
                    if (!directoryListenerRegistration.closeIfEmpty()) continue;
                    watchKey.cancel();
                    break;
                }
                if (watchKey.reset()) continue;
                DirectoryListenerRegistration directoryListenerRegistration2 = this.activeListeners.remove(path);
                directoryListenerRegistration2.onClose();
            }
        });
        this.taskExecutor = executor;
        this.executor.start();
    }

    private DirectoryListenerRegistration getRegistration(Path path2) {
        @Nullable DirectoryListenerRegistration directoryListenerRegistration = this.activeListeners.computeIfAbsent(path2, path -> {
            try {
                return new DirectoryListenerRegistration(path.register(this.watchService, DEFAULT_WATCH_EVENTS), this.taskExecutor);
            } catch (IOException iOException) {
                exceptionHolder.set(iOException);
                return null;
            }
        });
        if (directoryListenerRegistration == null) {
            throw exceptionHolder.get();
        }
        return directoryListenerRegistration;
    }

    public Disposable listenToFile(Path path, Subscriber<WatchEvent<?>> subscriber) {
        if (Files.isDirectory(path = path.toAbsolutePath(), new LinkOption[0])) {
            throw new IllegalArgumentException("Path " + path + " must be a file");
        }
        Path path2 = path.getFileName();
        return this.getRegistration(path.getParent()).subscribe(path2, subscriber);
    }

    public Disposable listenToDirectory(Path path, Subscriber<WatchEvent<?>> subscriber) {
        if (!Files.isDirectory(path = path.toAbsolutePath(), new LinkOption[0]) && Files.exists(path, new LinkOption[0])) {
            throw new IllegalArgumentException("Path " + path + " must be a directory");
        }
        return this.getRegistration(path).subscribe(subscriber);
    }

    public <N extends ConfigurationNode> ConfigurationReference<N> listenToConfiguration(Function<Path, ConfigurationLoader<? extends N>> function, Path path) {
        return ConfigurationReference.createWatching(function, path, this);
    }

    @Override
    public void close() {
        this.open = false;
        this.watchService.close();
        this.activeListeners.forEachValue(100L, DirectoryListenerRegistration::onClose);
        this.activeListeners.clear();
        try {
            this.executor.join();
        } catch (InterruptedException interruptedException) {
            throw new IOException("Failed to await termination of executor thread!");
        }
    }

    public static class Builder {
        private @Nullable ThreadFactory threadFactory;
        private @Nullable FileSystem fileSystem;
        private @Nullable Executor taskExecutor;

        private Builder() {
        }

        public Builder setThreadFactory(ThreadFactory threadFactory) {
            this.threadFactory = Objects.requireNonNull(threadFactory, "factory");
            return this;
        }

        public Builder setTaskExecutor(Executor executor) {
            this.taskExecutor = Objects.requireNonNull(executor, "executor");
            return this;
        }

        public Builder setFileSystem(FileSystem fileSystem) {
            this.fileSystem = fileSystem;
            return this;
        }

        public WatchServiceListener build() {
            if (this.threadFactory == null) {
                this.threadFactory = DEFAULT_THREAD_FACTORY;
            }
            if (this.fileSystem == null) {
                this.fileSystem = FileSystems.getDefault();
            }
            if (this.taskExecutor == null) {
                this.taskExecutor = ForkJoinPool.commonPool();
            }
            return new WatchServiceListener(this.threadFactory, this.fileSystem, this.taskExecutor);
        }
    }
}

