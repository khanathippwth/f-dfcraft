/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaimImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StyleClaim<V> {
    @NotNull
    public static <T> StyleClaim<T> claim(@NotNull String claimKey, @NotNull Function<Style, @Nullable T> lens, @NotNull BiConsumer<T, TokenEmitter> emitable) {
        return StyleClaim.claim(claimKey, lens, $ -> true, emitable);
    }

    @NotNull
    public static <T> StyleClaim<T> claim(@NotNull String claimKey, @NotNull Function<Style, @Nullable T> lens, @NotNull Predicate<T> filter, @NotNull BiConsumer<T, TokenEmitter> emitable) {
        return new StyleClaimImpl<T>(Objects.requireNonNull(claimKey, "claimKey"), Objects.requireNonNull(lens, "lens"), Objects.requireNonNull(filter, "filter"), Objects.requireNonNull(emitable, "emitable"));
    }

    @NotNull
    public String claimKey();

    @Nullable
    public Emitable apply(@NotNull Style var1);
}

