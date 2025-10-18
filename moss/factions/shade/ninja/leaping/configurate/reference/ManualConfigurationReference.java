/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reference;

import com.google.common.reflect.TypeToken;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Function;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.loader.ConfigurationLoader;
import moss.factions.shade.ninja.leaping.configurate.reactive.Processor;
import moss.factions.shade.ninja.leaping.configurate.reactive.Publisher;
import moss.factions.shade.ninja.leaping.configurate.reference.ConfigurationReference;
import moss.factions.shade.ninja.leaping.configurate.reference.ValueReference;
import moss.factions.shade.ninja.leaping.configurate.reference.ValueReferenceImpl;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class ManualConfigurationReference<N extends ConfigurationNode>
implements ConfigurationReference<N> {
    protected volatile @MonotonicNonNull N node;
    private final ConfigurationLoader<? extends N> loader;
    protected final Processor.TransactionalIso<N> updateListener;
    protected final Processor.Iso<Map.Entry<ConfigurationReference.ErrorPhase, Throwable>> errorListener;

    ManualConfigurationReference(ConfigurationLoader<? extends N> configurationLoader, Executor executor) {
        this.loader = configurationLoader;
        this.updateListener = Processor.createTransactional(executor);
        this.errorListener = Processor.create(executor);
        this.errorListener.setFallbackHandler(entry -> {
            System.out.println("Unhandled error while performing a " + entry.getKey() + " for a configuration reference: " + entry.getValue());
            ((Throwable)entry.getValue()).printStackTrace();
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void load() {
        ConfigurationLoader<? extends N> configurationLoader = this.loader;
        synchronized (configurationLoader) {
            this.node = this.loader.load();
            this.updateListener.submit(this.node);
        }
    }

    @Override
    public void save() {
        this.save(this.node);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void save(N n) {
        ConfigurationLoader<? extends N> configurationLoader = this.loader;
        synchronized (configurationLoader) {
            this.node = (ConfigurationNode)Objects.requireNonNull(n);
            this.loader.save((ConfigurationNode)this.node);
        }
    }

    @Override
    public Publisher<N> saveAsync() {
        return Publisher.execute(() -> {
            this.save();
            return this.getNode();
        }, this.updateListener.getExecutor());
    }

    @Override
    public Publisher<N> updateAsync(Function<N, ? extends N> function) {
        return Publisher.execute(() -> {
            ConfigurationNode configurationNode = (ConfigurationNode)function.apply(this.getNode());
            this.save(configurationNode);
            return configurationNode;
        }, this.updateListener.getExecutor());
    }

    @Override
    public N getNode() {
        return this.node;
    }

    @Override
    public ConfigurationLoader<? extends N> getLoader() {
        return this.loader;
    }

    @Override
    public N get(Object ... objectArray) {
        return (N)this.getNode().getNode(objectArray);
    }

    @Override
    public <T> ValueReference<T> referenceTo(TypeToken<T> typeToken, Object[] objectArray, @Nullable T t) {
        return new ValueReferenceImpl<T>(this, objectArray, typeToken, t);
    }

    @Override
    public <T> ValueReference<T> referenceTo(Class<T> clazz, Object[] objectArray, @Nullable T t) {
        return new ValueReferenceImpl<T>(this, objectArray, clazz, t);
    }

    @Override
    public Publisher<N> updates() {
        return this.updateListener;
    }

    @Override
    public Publisher<Map.Entry<ConfigurationReference.ErrorPhase, Throwable>> errors() {
        return this.errorListener;
    }

    @Override
    public void close() {
        this.updateListener.onClose();
    }
}

