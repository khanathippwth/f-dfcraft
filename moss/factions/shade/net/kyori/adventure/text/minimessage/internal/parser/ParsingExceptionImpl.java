/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser;

import java.util.Arrays;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ParsingException;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ParsingExceptionImpl
extends ParsingException {
    private static final long serialVersionUID = 2507190809441787202L;
    private final String originalText;
    private Token @NotNull [] tokens;

    public ParsingExceptionImpl(String string, @Nullable String string2, @NotNull @NotNull Token @NotNull ... tokenArray) {
        super(string, null, true, false);
        this.tokens = tokenArray;
        this.originalText = string2;
    }

    public ParsingExceptionImpl(String string, @Nullable String string2, @Nullable Throwable throwable, boolean bl, @NotNull @NotNull Token @NotNull ... tokenArray) {
        super(string, throwable, true, bl);
        this.tokens = tokenArray;
        this.originalText = string2;
    }

    @Override
    public String getMessage() {
        String string = this.tokens().length != 0 ? "\n\t" + this.arrow() : "";
        String string2 = this.originalText() != null ? "\n\t" + this.originalText() + string : "";
        return super.getMessage() + string2;
    }

    @Override
    @Nullable
    public String detailMessage() {
        return super.getMessage();
    }

    @Override
    @Nullable
    public String originalText() {
        return this.originalText;
    }

    @NotNull
    public @NotNull Token @NotNull [] tokens() {
        return this.tokens;
    }

    public void tokens(@NotNull @NotNull Token @NotNull [] tokenArray) {
        this.tokens = tokenArray;
    }

    private String arrow() {
        @NotNull Token[] tokenArray = this.tokens();
        char[] cArray = new char[tokenArray[tokenArray.length - 1].endIndex()];
        int n = 0;
        for (Token token : tokenArray) {
            Arrays.fill(cArray, n, token.startIndex(), ' ');
            cArray[token.startIndex()] = 94;
            if (Math.abs(token.startIndex() - token.endIndex()) > 1) {
                Arrays.fill(cArray, token.startIndex() + 1, token.endIndex() - 1, '~');
            }
            cArray[token.endIndex() - 1] = 94;
            n = token.endIndex();
        }
        return new String(cArray);
    }

    @Override
    public int startIndex() {
        if (this.tokens.length == 0) {
            return -1;
        }
        return this.tokens[0].startIndex();
    }

    @Override
    public int endIndex() {
        if (this.tokens.length == 0) {
            return -1;
        }
        return this.tokens[this.tokens.length - 1].endIndex();
    }
}

