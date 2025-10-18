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
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class StyleClaimImpl<V>
implements StyleClaim<V> {
    private final String claimKey;
    private final Function<Style, V> lens;
    private final Predicate<V> filter;
    private final BiConsumer<V, TokenEmitter> emitable;

    StyleClaimImpl(String string, Function<Style, @Nullable V> function, Predicate<V> predicate, BiConsumer<V, TokenEmitter> biConsumer) {
        this.claimKey = string;
        this.lens = function;
        this.filter = predicate;
        this.emitable = biConsumer;
    }

    @Override
    @NotNull
    public String claimKey() {
        return this.claimKey;
    }

    @Override
    @Nullable
    public Emitable apply(@NotNull Style style) {
        V v = this.lens.apply(style);
        if (v == null || !this.filter.test(v)) {
            return null;
        }
        return tokenEmitter -> this.emitable.accept(v, tokenEmitter);
    }

    public int hashCode() {
        return Objects.hash(this.claimKey);
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof StyleClaimImpl)) {
            return false;
        }
        StyleClaimImpl styleClaimImpl = (StyleClaimImpl)object;
        return Objects.equals(this.claimKey, styleClaimImpl.claimKey);
    }
}

