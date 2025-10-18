/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface TypeSerializer<T> {
    public @Nullable T deserialize(@NonNull TypeToken<?> var1, @NonNull ConfigurationNode var2) throws ObjectMappingException;

    public void serialize(@NonNull TypeToken<?> var1, @Nullable T var2, @NonNull ConfigurationNode var3) throws ObjectMappingException;
}

