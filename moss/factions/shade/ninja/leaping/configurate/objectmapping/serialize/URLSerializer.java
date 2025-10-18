/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.net.MalformedURLException;
import java.net.URL;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class URLSerializer
implements TypeSerializer<URL> {
    URLSerializer() {
    }

    @Override
    public URL deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        URL uRL;
        String string = configurationNode.getString();
        if (string == null) {
            throw new ObjectMappingException("No value present in node " + configurationNode);
        }
        try {
            uRL = new URL(string);
        } catch (MalformedURLException malformedURLException) {
            throw new ObjectMappingException("Invalid URL string provided for " + configurationNode.getKey() + ": got " + string);
        }
        return uRL;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable URL uRL, @NonNull ConfigurationNode configurationNode) {
        configurationNode.setValue(uRL.toString());
    }
}

