/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.Types;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class BooleanSerializer
implements TypeSerializer<Boolean> {
    BooleanSerializer() {
    }

    @Override
    public Boolean deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        return configurationNode.getBoolean();
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable Boolean bl, @NonNull ConfigurationNode configurationNode) {
        configurationNode.setValue(Types.asBoolean(bl));
    }
}

