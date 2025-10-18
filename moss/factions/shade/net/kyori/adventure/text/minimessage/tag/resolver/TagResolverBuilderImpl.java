/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.TagInternals;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.EmptyTagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.MapTagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.MappableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.SequentialTagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

final class TagResolverBuilderImpl
implements TagResolver.Builder {
    static final Collector<TagResolver, TagResolver.Builder, TagResolver> COLLECTOR = Collector.of(TagResolver::builder, TagResolver.Builder::resolver, (builder, builder2) -> TagResolver.builder().resolvers(builder.build(), builder2.build()), TagResolver.Builder::build, new Collector.Characteristics[0]);
    private final Map<String, Tag> replacements = new HashMap<String, Tag>();
    private final List<TagResolver> resolvers = new ArrayList<TagResolver>();

    TagResolverBuilderImpl() {
    }

    @Override
    public @NotNull TagResolver.Builder tag(@NotNull String string, @NotNull Tag tag) {
        TagInternals.assertValidTagName(Objects.requireNonNull(string, "name"));
        this.replacements.put(string, Objects.requireNonNull(tag, "tag"));
        return this;
    }

    @Override
    public @NotNull TagResolver.Builder resolver(@NotNull TagResolver tagResolver) {
        if (tagResolver instanceof SequentialTagResolver) {
            this.resolvers(((SequentialTagResolver)tagResolver).resolvers, false);
        } else if (!this.consumePotentialMappable(tagResolver)) {
            this.popMap();
            this.resolvers.add(Objects.requireNonNull(tagResolver, "resolver"));
        }
        return this;
    }

    @Override
    public @NotNull TagResolver.Builder resolvers(@NotNull @NotNull TagResolver @NotNull ... tagResolverArray) {
        return this.resolvers(tagResolverArray, true);
    }

    private @NotNull TagResolver.Builder resolvers(@NotNull @NotNull TagResolver @NotNull [] tagResolverArray, boolean bl) {
        boolean bl2 = false;
        Objects.requireNonNull(tagResolverArray, "resolvers");
        if (bl) {
            for (TagResolver tagResolver : tagResolverArray) {
                bl2 = this.single(tagResolver, bl2);
            }
        } else {
            for (int i = tagResolverArray.length - 1; i >= 0; --i) {
                bl2 = this.single(tagResolverArray[i], bl2);
            }
        }
        return this;
    }

    @Override
    public @NotNull TagResolver.Builder resolvers(@NotNull Iterable<? extends TagResolver> iterable) {
        boolean bl = false;
        for (TagResolver tagResolver : Objects.requireNonNull(iterable, "resolvers")) {
            bl = this.single(tagResolver, bl);
        }
        return this;
    }

    private boolean single(TagResolver tagResolver, boolean bl) {
        if (tagResolver instanceof SequentialTagResolver) {
            this.resolvers(((SequentialTagResolver)tagResolver).resolvers, false);
        } else if (!this.consumePotentialMappable(tagResolver)) {
            if (!bl) {
                this.popMap();
            }
            this.resolvers.add(Objects.requireNonNull(tagResolver, "resolvers[?]"));
            return true;
        }
        return false;
    }

    private void popMap() {
        if (!this.replacements.isEmpty()) {
            this.resolvers.add(new MapTagResolver(new HashMap<String, Tag>(this.replacements)));
            this.replacements.clear();
        }
    }

    private boolean consumePotentialMappable(TagResolver tagResolver) {
        if (tagResolver instanceof MappableResolver) {
            return ((MappableResolver)((Object)tagResolver)).contributeToMap(this.replacements);
        }
        return false;
    }

    @Override
    @NotNull
    public TagResolver build() {
        this.popMap();
        if (this.resolvers.size() == 0) {
            return EmptyTagResolver.INSTANCE;
        }
        if (this.resolvers.size() == 1) {
            return this.resolvers.get(0);
        }
        TagResolver[] tagResolverArray = this.resolvers.toArray(new TagResolver[0]);
        Collections.reverse(Arrays.asList(tagResolverArray));
        return new SequentialTagResolver(tagResolverArray);
    }
}

