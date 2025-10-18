/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.ClaimConsumer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.MappableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CachingTagResolver
implements TagResolver.WithoutArguments,
MappableResolver,
SerializableResolver {
    private static final Tag NULL_REPLACEMENT = () -> {
        throw new UnsupportedOperationException("no-op null tag");
    };
    private final Map<String, Tag> cache = new HashMap<String, Tag>();
    private final TagResolver.WithoutArguments resolver;

    CachingTagResolver(TagResolver.WithoutArguments withoutArguments) {
        this.resolver = withoutArguments;
    }

    private Tag query(@NotNull String string2) {
        return this.cache.computeIfAbsent(string2, string -> {
            @Nullable Tag tag = this.resolver.resolve((String)string);
            return tag == null ? NULL_REPLACEMENT : tag;
        });
    }

    @Override
    @Nullable
    public Tag resolve(@NotNull String string) {
        Tag tag = this.query(string);
        return tag == NULL_REPLACEMENT ? null : tag;
    }

    @Override
    public boolean has(@NotNull String string) {
        return this.query(string) != NULL_REPLACEMENT;
    }

    @Override
    public boolean contributeToMap(@NotNull Map<String, Tag> map) {
        if (this.resolver instanceof MappableResolver) {
            return ((MappableResolver)((Object)this.resolver)).contributeToMap(map);
        }
        return false;
    }

    @Override
    public void handle(@NotNull Component component, @NotNull ClaimConsumer claimConsumer) {
        if (this.resolver instanceof SerializableResolver) {
            ((SerializableResolver)((Object)this.resolver)).handle(component, claimConsumer);
        }
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CachingTagResolver)) {
            return false;
        }
        CachingTagResolver cachingTagResolver = (CachingTagResolver)object;
        return Objects.equals(this.resolver, cachingTagResolver.resolver);
    }

    public int hashCode() {
        return Objects.hash(this.resolver);
    }
}

