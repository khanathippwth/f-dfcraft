/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.util.Map;
import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.MappableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MapTagResolver
implements TagResolver.WithoutArguments,
MappableResolver {
    private final Map<String, ? extends Tag> tagMap;

    MapTagResolver(@NotNull Map<String, ? extends Tag> map) {
        this.tagMap = map;
    }

    @Override
    @Nullable
    public Tag resolve(@NotNull String string) {
        return this.tagMap.get(string);
    }

    @Override
    public boolean contributeToMap(@NotNull Map<String, Tag> map) {
        map.putAll(this.tagMap);
        return true;
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof MapTagResolver)) {
            return false;
        }
        MapTagResolver mapTagResolver = (MapTagResolver)object;
        return Objects.equals(this.tagMap, mapTagResolver.tagMap);
    }

    public int hashCode() {
        return Objects.hash(this.tagMap);
    }
}

