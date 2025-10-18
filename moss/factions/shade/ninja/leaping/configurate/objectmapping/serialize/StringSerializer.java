/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class StringSerializer
implements TypeSerializer<String> {
    StringSerializer() {
    }

    @Override
    public String deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        return configurationNode.getString();
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable String string, @NonNull ConfigurationNode configurationNode) {
        configurationNode.setValue(string);
    }
}

