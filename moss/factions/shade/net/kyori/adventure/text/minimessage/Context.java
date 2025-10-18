/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage;

import moss.factions.shade.net.kyori.adventure.pointer.Pointered;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ParsingException;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface Context {
    @Nullable
    public Pointered target();

    @NotNull
    public Pointered targetOrThrow();

    @NotNull
    public <T extends Pointered> T targetAsType(@NotNull Class<T> var1);

    @NotNull
    public Component deserialize(@NotNull String var1);

    @NotNull
    public Component deserialize(@NotNull String var1, @NotNull TagResolver var2);

    @NotNull
    public Component deserialize(@NotNull String var1, @NotNull @NotNull TagResolver @NotNull ... var2);

    @NotNull
    public ParsingException newException(@NotNull String var1, @NotNull ArgumentQueue var2);

    @NotNull
    public ParsingException newException(@NotNull String var1);

    @NotNull
    public ParsingException newException(@NotNull String var1, @Nullable Throwable var2, @NotNull ArgumentQueue var3);
}

