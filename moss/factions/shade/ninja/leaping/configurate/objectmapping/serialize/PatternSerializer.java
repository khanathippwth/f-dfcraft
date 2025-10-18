/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class PatternSerializer
implements TypeSerializer<Pattern> {
    PatternSerializer() {
    }

    @Override
    public Pattern deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        try {
            return Pattern.compile(configurationNode.getString());
        } catch (PatternSyntaxException patternSyntaxException) {
            throw new ObjectMappingException(patternSyntaxException);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable Pattern pattern, @NonNull ConfigurationNode configurationNode) {
        configurationNode.setValue(pattern.pattern());
    }
}

