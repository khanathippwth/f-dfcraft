/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.net.URI;
import java.net.URISyntaxException;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class URISerializer
implements TypeSerializer<URI> {
    URISerializer() {
    }

    @Override
    public URI deserialize(@NonNull TypeToken<?> typeToken, @NonNull ConfigurationNode configurationNode) {
        URI uRI;
        String string = configurationNode.getString();
        if (string == null) {
            throw new ObjectMappingException("No value present in node " + configurationNode);
        }
        try {
            uRI = new URI(string);
        } catch (URISyntaxException uRISyntaxException) {
            throw new ObjectMappingException("Invalid URI string provided for " + configurationNode.getKey() + ": got " + string);
        }
        return uRI;
    }

    @Override
    public void serialize(@NonNull TypeToken<?> typeToken, @Nullable URI uRI, @NonNull ConfigurationNode configurationNode) {
        configurationNode.setValue(uRI.toString());
    }
}

