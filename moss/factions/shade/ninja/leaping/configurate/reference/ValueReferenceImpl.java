/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reference;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.function.Function;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import moss.factions.shade.ninja.leaping.configurate.reactive.Disposable;
import moss.factions.shade.ninja.leaping.configurate.reactive.Publisher;
import moss.factions.shade.ninja.leaping.configurate.reactive.Subscriber;
import moss.factions.shade.ninja.leaping.configurate.reactive.TransactionFailedException;
import moss.factions.shade.ninja.leaping.configurate.reference.ConfigurationReference;
import moss.factions.shade.ninja.leaping.configurate.reference.ManualConfigurationReference;
import moss.factions.shade.ninja.leaping.configurate.reference.ValueReference;
import org.checkerframework.checker.nullness.qual.Nullable;

class ValueReferenceImpl<@Nullable T>
implements ValueReference<T>,
Publisher<T> {
    private final ManualConfigurationReference<?> root;
    private final Object[] path;
    private final TypeToken<T> type;
    private final TypeSerializer<T> serializer;
    private final Publisher.Cached<T> deserialized;

    ValueReferenceImpl(ManualConfigurationReference<?> manualConfigurationReference, Object[] objectArray, TypeToken<T> typeToken, @Nullable T t) {
        this.root = manualConfigurationReference;
        this.path = Arrays.copyOf(objectArray, objectArray.length);
        this.type = typeToken;
        this.serializer = manualConfigurationReference.getNode().getOptions().getSerializers().get(typeToken);
        if (this.serializer == null) {
            throw new ObjectMappingException("Unsupported type" + typeToken);
        }
        this.deserialized = manualConfigurationReference.updateListener.map((I configurationNode) -> {
            try {
                return this.deserializedValueFrom((ConfigurationNode)configurationNode, t);
            } catch (ObjectMappingException objectMappingException) {
                manualConfigurationReference.errorListener.submit(Maps.immutableEntry(ConfigurationReference.ErrorPhase.VALUE, objectMappingException));
                throw new TransactionFailedException(objectMappingException);
            }
        }).cache(this.deserializedValueFrom((ConfigurationNode)manualConfigurationReference.getNode(), t));
    }

    ValueReferenceImpl(ManualConfigurationReference<?> manualConfigurationReference, Object[] objectArray, Class<T> clazz, @Nullable T t) {
        this(manualConfigurationReference, objectArray, TypeToken.of(clazz), t);
    }

    private @Nullable T deserializedValueFrom(ConfigurationNode configurationNode, @Nullable T t) {
        ConfigurationNode configurationNode2 = configurationNode.getNode(this.path);
        @Nullable T t2 = this.serializer.deserialize(this.type, configurationNode2);
        if (t2 != null) {
            return t2;
        }
        if (t != null && configurationNode2.getOptions().shouldCopyDefaults()) {
            this.serializer.serialize(this.type, t, configurationNode2);
        }
        return t;
    }

    @Override
    public @Nullable T get() {
        return this.deserialized.get();
    }

    @Override
    public boolean set(@Nullable T t) {
        try {
            this.serializer.serialize(this.type, t, this.getNode());
            this.deserialized.submit(t);
            return true;
        } catch (ObjectMappingException objectMappingException) {
            this.root.errorListener.submit(Maps.immutableEntry(ConfigurationReference.ErrorPhase.SAVING, objectMappingException));
            return false;
        }
    }

    @Override
    public boolean setAndSave(@Nullable T t) {
        try {
            if (this.set(t)) {
                this.root.save();
                return true;
            }
        } catch (IOException iOException) {
            this.root.errorListener.submit(Maps.immutableEntry(ConfigurationReference.ErrorPhase.SAVING, iOException));
        }
        return false;
    }

    @Override
    public Publisher<Boolean> setAndSaveAsync(@Nullable T t) {
        return Publisher.execute(() -> {
            this.serializer.serialize(this.type, t, this.getNode());
            this.deserialized.submit(t);
            this.root.save();
            return true;
        }, this.root.updates().getExecutor());
    }

    @Override
    public boolean update(Function<@Nullable T, ? extends T> function) {
        try {
            return this.set(function.apply(this.get()));
        } catch (Throwable throwable) {
            this.root.errorListener.submit(Maps.immutableEntry(ConfigurationReference.ErrorPhase.VALUE, throwable));
            return false;
        }
    }

    @Override
    public Publisher<Boolean> updateAsync(Function<T, ? extends T> function) {
        return Publisher.execute(() -> {
            @Nullable T t = this.get();
            Object r = function.apply(t);
            this.serializer.serialize(this.type, r, this.getNode());
            this.deserialized.submit(r);
            this.root.save();
            return true;
        }, this.root.updates().getExecutor());
    }

    @Override
    public ConfigurationNode getNode() {
        return this.root.getNode().getNode(this.path);
    }

    @Override
    public Disposable subscribe(Subscriber<? super T> subscriber) {
        return this.deserialized.subscribe(subscriber);
    }

    @Override
    public boolean hasSubscribers() {
        return this.deserialized.hasSubscribers();
    }

    @Override
    public Executor getExecutor() {
        return this.deserialized.getExecutor();
    }
}

