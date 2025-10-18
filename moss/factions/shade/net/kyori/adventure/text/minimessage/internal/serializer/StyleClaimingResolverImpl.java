/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer;

import java.util.Set;
import java.util.function.BiFunction;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class StyleClaimingResolverImpl
implements TagResolver,
SerializableResolver.Single {
    @NotNull
    private final Set<String> names;
    @NotNull
    private final BiFunction<ArgumentQueue, Context, Tag> handler;
    @NotNull
    private final StyleClaim<?> styleClaim;

    StyleClaimingResolverImpl(@NotNull Set<String> set, @NotNull BiFunction<ArgumentQueue, Context, Tag> biFunction, @NotNull StyleClaim<?> styleClaim) {
        this.names = set;
        this.handler = biFunction;
        this.styleClaim = styleClaim;
    }

    @Override
    @Nullable
    public Tag resolve(@NotNull String string, @NotNull ArgumentQueue argumentQueue, @NotNull Context context) {
        if (!this.names.contains(string)) {
            return null;
        }
        return this.handler.apply(argumentQueue, context);
    }

    @Override
    public boolean has(@NotNull String string) {
        return this.names.contains(string);
    }

    @Override
    @Nullable
    public StyleClaim<?> claimStyle() {
        return this.styleClaim;
    }
}

