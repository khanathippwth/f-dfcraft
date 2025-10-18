/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.util.Map;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.ClaimConsumer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.MappableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EmptyTagResolver
implements TagResolver,
MappableResolver,
SerializableResolver {
    static final EmptyTagResolver INSTANCE = new EmptyTagResolver();

    private EmptyTagResolver() {
    }

    @Override
    @Nullable
    public Tag resolve(@NotNull String string, @NotNull ArgumentQueue argumentQueue, @NotNull Context context) {
        return null;
    }

    @Override
    public boolean has(@NotNull String string) {
        return false;
    }

    @Override
    public boolean contributeToMap(@NotNull Map<String, Tag> map) {
        return true;
    }

    @Override
    public void handle(@NotNull Component component, @NotNull ClaimConsumer claimConsumer) {
    }
}

