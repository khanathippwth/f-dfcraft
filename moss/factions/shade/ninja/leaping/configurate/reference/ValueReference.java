/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.reference;

import java.util.function.Function;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.reactive.Publisher;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ValueReference<T>
extends Publisher<T> {
    public @Nullable T get();

    public boolean set(@Nullable T var1);

    public boolean setAndSave(@Nullable T var1);

    public Publisher<Boolean> setAndSaveAsync(@Nullable T var1);

    public boolean update(Function<@Nullable T, ? extends T> var1);

    public Publisher<Boolean> updateAsync(Function<T, ? extends T> var1);

    public ConfigurationNode getNode();
}

