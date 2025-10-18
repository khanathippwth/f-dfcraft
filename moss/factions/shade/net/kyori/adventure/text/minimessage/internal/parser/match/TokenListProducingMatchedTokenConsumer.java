/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenType;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.match.MatchedTokenConsumer;
import org.jetbrains.annotations.NotNull;

public final class TokenListProducingMatchedTokenConsumer
extends MatchedTokenConsumer<List<Token>> {
    private List<Token> result = null;

    public TokenListProducingMatchedTokenConsumer(@NotNull String string) {
        super(string);
    }

    @Override
    public void accept(int n, int n2, @NotNull TokenType tokenType) {
        super.accept(n, n2, tokenType);
        if (this.result == null) {
            this.result = new ArrayList<Token>();
        }
        this.result.add(new Token(n, n2, tokenType));
    }

    @Override
    @NotNull
    public List<Token> result() {
        return this.result == null ? Collections.emptyList() : this.result;
    }
}

