/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.util.Map;
import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.MappableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

final class SingleResolver
implements TagResolver.Single,
MappableResolver {
    private final String key;
    private final Tag tag;

    SingleResolver(String string, Tag tag) {
        this.key = string;
        this.tag = tag;
    }

    @Override
    @NotNull
    public String key() {
        return this.key;
    }

    @Override
    @NotNull
    public Tag tag() {
        return this.tag;
    }

    @Override
    public boolean contributeToMap(@NotNull Map<String, Tag> map) {
        map.put(this.key, this.tag);
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.key, this.tag);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        SingleResolver singleResolver = (SingleResolver)object;
        return Objects.equals(this.key, singleResolver.key) && Objects.equals(this.tag, singleResolver.tag);
    }
}

