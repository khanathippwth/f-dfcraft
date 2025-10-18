/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reference;

import com.google.common.reflect.TypeToken;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.loader.ConfigurationLoader;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.reactive.Publisher;
import moss.factions.shade.ninja.leaping.configurate.reference.ManualConfigurationReference;
import moss.factions.shade.ninja.leaping.configurate.reference.ValueReference;
import moss.factions.shade.ninja.leaping.configurate.reference.WatchServiceListener;
import moss.factions.shade.ninja.leaping.configurate.reference.WatchingConfigurationReference;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ConfigurationReference<N extends ConfigurationNode>
extends AutoCloseable {
    public static <N extends ConfigurationNode> ConfigurationReference<N> createFixed(ConfigurationLoader<? extends N> loader) throws IOException {
        ManualConfigurationReference<N> ret = new ManualConfigurationReference<N>(loader, ForkJoinPool.commonPool());
        ret.load();
        return ret;
    }

    public static <T extends ConfigurationNode> ConfigurationReference<T> createWatching(Function<Path, ConfigurationLoader<? extends T>> loaderCreator, Path file, WatchServiceListener listener) throws IOException {
        WatchingConfigurationReference<T> ret = new WatchingConfigurationReference<T>(loaderCreator.apply(file), listener.taskExecutor);
        ret.load();
        ret.setDisposable(listener.listenToFile(file, ret));
        return ret;
    }

    public void load() throws IOException;

    public void save() throws IOException;

    public void save(N var1) throws IOException;

    public Publisher<N> saveAsync();

    public Publisher<N> updateAsync(Function<N, ? extends N> var1);

    public N getNode();

    public ConfigurationLoader<? extends N> getLoader();

    public N get(Object ... var1);

    default public void set(Object[] path, @Nullable Object value) {
        this.getNode().getNode(path).setValue(value);
    }

    default public <T> void set(Object[] path, TypeToken<T> type, @Nullable T value) throws ObjectMappingException {
        this.getNode().getNode(path).setValue(type, value);
    }

    default public <T> ValueReference<T> referenceTo(TypeToken<T> type, Object ... path) throws ObjectMappingException {
        return this.referenceTo(type, path, (T)null);
    }

    default public <T> ValueReference<T> referenceTo(Class<T> type, Object ... path) throws ObjectMappingException {
        return this.referenceTo(type, path, (T)null);
    }

    public <T> ValueReference<T> referenceTo(TypeToken<T> var1, Object[] var2, @Nullable T var3) throws ObjectMappingException;

    public <T> ValueReference<T> referenceTo(Class<T> var1, Object[] var2, @Nullable T var3) throws ObjectMappingException;

    public Publisher<N> updates();

    public Publisher<Map.Entry<ErrorPhase, Throwable>> errors();

    @Override
    public void close();

    public static enum ErrorPhase {
        LOADING,
        SAVING,
        UNKNOWN,
        VALUE;

    }
}

