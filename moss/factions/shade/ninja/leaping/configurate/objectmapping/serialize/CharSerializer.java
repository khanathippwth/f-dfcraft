/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class CharSerializer
implements TypeSerializer<Character> {
    CharSerializer() {
    }

    @Override
    public @Nullable Character deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        if (configurationNode.isList() || configurationNode.isMap()) {
            return null;
        }
        Object object = configurationNode.getValue();
        if (object instanceof String) {
            String string = (String)object;
            if (string.length() == 1) {
                return Character.valueOf(string.charAt(0));
            }
        } else {
            if (object instanceof Character) {
                return (Character)object;
            }
            if (object instanceof Number) {
                return Character.valueOf((char)((Number)object).shortValue());
            }
        }
        return null;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable Character c, @NonNull ConfigurationNode configurationNode) {
        if (configurationNode.getOptions().acceptsType(Character.TYPE)) {
            configurationNode.setValue(c);
        } else {
            configurationNode.setValue(c == null ? null : c.toString());
        }
    }
}

