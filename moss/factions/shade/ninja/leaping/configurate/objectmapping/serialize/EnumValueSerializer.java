/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.util.Optional;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import moss.factions.shade.ninja.leaping.configurate.util.EnumLookup;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class EnumValueSerializer
implements TypeSerializer<Enum<?>> {
    EnumValueSerializer() {
    }

    @Override
    public Enum<?> deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) {
        String string = configurationNode.getString();
        if (string == null) {
            throw new ObjectMappingException("No value present in node " + configurationNode);
        }
        Optional<Enum> optional = EnumLookup.lookupEnum(typeToken.getRawType().asSubclass(Enum.class), string);
        if (!optional.isPresent()) {
            throw new ObjectMappingException("Invalid enum constant provided for " + configurationNode.getKey() + ": Expected a value of enum " + typeToken + ", got " + string);
        }
        return optional.get();
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable Enum<?> enum_, @NonNull ConfigurationNode configurationNode) {
        configurationNode.setValue(enum_ == null ? null : enum_.name());
    }
}

