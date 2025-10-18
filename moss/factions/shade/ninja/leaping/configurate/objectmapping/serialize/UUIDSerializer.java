/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.util.UUID;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class UUIDSerializer
implements TypeSerializer<UUID> {
    UUIDSerializer() {
    }

    @Override
    public UUID deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        try {
            return UUID.fromString(configurationNode.getString());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new ObjectMappingException("Value not a UUID", illegalArgumentException);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable UUID uUID, @NonNull ConfigurationNode configurationNode) {
        configurationNode.setValue(uUID.toString());
    }
}

