/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class ConfigurationNodeSerializer
implements TypeSerializer<ConfigurationNode> {
    static final TypeToken<ConfigurationNode> TYPE = TypeToken.of(ConfigurationNode.class);

    ConfigurationNodeSerializer() {
    }

    @Override
    public @Nullable ConfigurationNode deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        return configurationNode.copy();
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable ConfigurationNode configurationNode, @NonNull ConfigurationNode configurationNode2) {
        configurationNode2.setValue(configurationNode);
    }
}

