/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.MustBeInvokedByOverriders
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.UnknownNullability
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.match;

import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenType;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public abstract class MatchedTokenConsumer<T> {
    protected final String input;
    private int lastIndex = -1;

    public MatchedTokenConsumer(@NotNull String string) {
        this.input = string;
    }

    @MustBeInvokedByOverriders
    public void accept(int n, int n2, @NotNull TokenType tokenType) {
        this.lastIndex = n2;
    }

    public abstract @UnknownNullability T result();

    public final int lastEndIndex() {
        return this.lastIndex;
    }
}

