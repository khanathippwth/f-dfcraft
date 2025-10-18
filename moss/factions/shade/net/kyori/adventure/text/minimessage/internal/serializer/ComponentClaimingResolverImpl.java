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
import java.util.function.Function;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ComponentClaimingResolverImpl
implements TagResolver,
SerializableResolver.Single {
    @NotNull
    private final Set<String> names;
    @NotNull
    private final BiFunction<ArgumentQueue, Context, Tag> handler;
    private final @NotNull Function<Component, @Nullable Emitable> componentClaim;

    ComponentClaimingResolverImpl(Set<String> set, BiFunction<ArgumentQueue, Context, Tag> biFunction, Function<Component, @Nullable Emitable> function) {
        this.names = set;
        this.handler = biFunction;
        this.componentClaim = function;
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
    public Emitable claimComponent(@NotNull Component component) {
        return this.componentClaim.apply(component);
    }
}

