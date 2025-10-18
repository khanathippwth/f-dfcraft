/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ParsingException;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.TagInternals;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.TagPattern;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.CachingTagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.EmptyTagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.SingleResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolverBuilderImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TagResolver {
    @NotNull
    public static Builder builder() {
        return new TagResolverBuilderImpl();
    }

    @NotNull
    public static TagResolver standard() {
        return StandardTags.defaults();
    }

    @NotNull
    public static TagResolver empty() {
        return EmptyTagResolver.INSTANCE;
    }

    public static @NotNull Single resolver(@TagPattern @NotNull String name, @NotNull Tag tag) {
        TagInternals.assertValidTagName(name);
        return new SingleResolver(name, Objects.requireNonNull(tag, "tag"));
    }

    @NotNull
    public static TagResolver resolver(@TagPattern @NotNull String name, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler) {
        return TagResolver.resolver(Collections.singleton(name), handler);
    }

    @NotNull
    public static TagResolver resolver(final @NotNull Set<String> names, final @NotNull BiFunction<ArgumentQueue, Context, Tag> handler) {
        HashSet<String> ownNames = new HashSet<String>(names);
        for (String name : ownNames) {
            TagInternals.assertValidTagName(name);
        }
        Objects.requireNonNull(handler, "handler");
        return new TagResolver(){

            @Override
            @Nullable
            public Tag resolve(@NotNull String string, @NotNull ArgumentQueue argumentQueue, @NotNull Context context) {
                if (!names.contains(string)) {
                    return null;
                }
                return (Tag)handler.apply(argumentQueue, context);
            }

            @Override
            public boolean has(@NotNull String string) {
                return names.contains(string);
            }
        };
    }

    @NotNull
    public static TagResolver resolver(@NotNull @NotNull TagResolver @NotNull ... resolvers) {
        if (Objects.requireNonNull(resolvers, "resolvers").length == 1) {
            return Objects.requireNonNull(resolvers[0], "resolvers must not contain null elements");
        }
        return TagResolver.builder().resolvers(resolvers).build();
    }

    @NotNull
    public static TagResolver resolver(@NotNull Iterable<? extends TagResolver> resolvers) {
        if (resolvers instanceof Collection) {
            int size = ((Collection)resolvers).size();
            if (size == 0) {
                return TagResolver.empty();
            }
            if (size == 1) {
                return Objects.requireNonNull(resolvers.iterator().next(), "resolvers must not contain null elements");
            }
        }
        return TagResolver.builder().resolvers(resolvers).build();
    }

    @NotNull
    public static TagResolver caching(@NotNull WithoutArguments resolver) {
        if (resolver instanceof CachingTagResolver) {
            return resolver;
        }
        return new CachingTagResolver(Objects.requireNonNull(resolver, "resolver"));
    }

    @NotNull
    public static Collector<TagResolver, ?, TagResolver> toTagResolver() {
        return TagResolverBuilderImpl.COLLECTOR;
    }

    @Nullable
    public Tag resolve(@TagPattern @NotNull String var1, @NotNull ArgumentQueue var2, @NotNull Context var3) throws ParsingException;

    public boolean has(@NotNull String var1);

    public static interface Builder {
        @NotNull
        public Builder tag(@TagPattern @NotNull String var1, @NotNull Tag var2);

        @NotNull
        default public Builder tag(@TagPattern @NotNull String name, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler) {
            return this.tag(Collections.singleton(name), handler);
        }

        @NotNull
        default public Builder tag(@NotNull Set<String> names, @NotNull BiFunction<ArgumentQueue, Context, Tag> handler) {
            return this.resolver(TagResolver.resolver(names, handler));
        }

        @NotNull
        public Builder resolver(@NotNull TagResolver var1);

        @NotNull
        public Builder resolvers(@NotNull @NotNull TagResolver @NotNull ... var1);

        @NotNull
        public Builder resolvers(@NotNull Iterable<? extends TagResolver> var1);

        @NotNull
        default public Builder caching(@NotNull WithoutArguments dynamic) {
            return this.resolver(TagResolver.caching(dynamic));
        }

        @NotNull
        public TagResolver build();
    }

    @FunctionalInterface
    public static interface WithoutArguments
    extends TagResolver {
        @Nullable
        public Tag resolve(@TagPattern @NotNull String var1);

        @Override
        default public boolean has(@NotNull String name) {
            return this.resolve(name) != null;
        }

        @Override
        @Nullable
        default public Tag resolve(@TagPattern @NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
            Tag resolved = this.resolve(name);
            if (resolved != null && arguments.hasNext()) {
                throw ctx.newException("Tag '<" + name + ">' does not accept any arguments");
            }
            return resolved;
        }
    }

    @ApiStatus.NonExtendable
    public static interface Single
    extends WithoutArguments {
        @NotNull
        public String key();

        @NotNull
        public Tag tag();

        @Override
        @Nullable
        default public Tag resolve(@TagPattern @NotNull String name) {
            if (this.has(name)) {
                return this.tag();
            }
            return null;
        }

        @Override
        default public boolean has(@NotNull String name) {
            return name.equalsIgnoreCase(this.key());
        }
    }
}

