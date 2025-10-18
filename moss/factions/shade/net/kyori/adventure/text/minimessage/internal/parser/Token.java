/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.internal.Internals;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenType;
import moss.factions.shade.net.kyori.examination.Examinable;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

public final class Token
implements Examinable {
    private final int startIndex;
    private final int endIndex;
    private final TokenType type;
    private List<Token> childTokens = null;

    public Token(int n, int n2, TokenType tokenType) {
        this.startIndex = n;
        this.endIndex = n2;
        this.type = tokenType;
    }

    public int startIndex() {
        return this.startIndex;
    }

    public int endIndex() {
        return this.endIndex;
    }

    public TokenType type() {
        return this.type;
    }

    public List<Token> childTokens() {
        return this.childTokens;
    }

    public void childTokens(List<Token> list) {
        this.childTokens = list;
    }

    public CharSequence get(CharSequence charSequence) {
        return charSequence.subSequence(this.startIndex, this.endIndex);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("startIndex", this.startIndex), ExaminableProperty.of("endIndex", this.endIndex), ExaminableProperty.of("type", (Object)this.type));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Token)) {
            return false;
        }
        Token token = (Token)object;
        return this.startIndex == token.startIndex && this.endIndex == token.endIndex && this.type == token.type;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.startIndex, this.endIndex, this.type});
    }

    public String toString() {
        return Internals.toString(this);
    }
}

