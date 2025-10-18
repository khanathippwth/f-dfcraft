/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.me.lucko.commodore.file;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import moss.factions.shade.me.lucko.commodore.file.AbstractIterator;
import moss.factions.shade.me.lucko.commodore.file.ParseException;
import moss.factions.shade.me.lucko.commodore.file.Token;
import moss.factions.shade.me.lucko.commodore.file.TokenStream;

class Lexer
extends AbstractIterator<Token>
implements TokenStream {
    private final StreamTokenizer tokenizer;
    private boolean end = false;

    Lexer(Reader reader) {
        this.tokenizer = new StreamTokenizer(reader);
        this.tokenizer.resetSyntax();
        this.tokenizer.wordChars(33, 126);
        this.tokenizer.quoteChar(34);
        this.tokenizer.whitespaceChars(0, 32);
        "{};".chars().forEach(this.tokenizer::ordinaryChar);
        this.tokenizer.slashSlashComments(true);
        this.tokenizer.slashStarComments(true);
    }

    @Override
    protected Token computeNext() {
        if (this.end) {
            return (Token)this.endOfData();
        }
        try {
            int n = this.tokenizer.nextToken();
            switch (n) {
                case -1: {
                    this.end = true;
                    return Token.ConstantToken.EOF;
                }
                case -3: {
                    return new Token.StringToken(this.tokenizer.sval);
                }
                case 123: {
                    return Token.ConstantToken.OPEN_BRACKET;
                }
                case 125: {
                    return Token.ConstantToken.CLOSE_BRACKET;
                }
                case 59: {
                    return Token.ConstantToken.SEMICOLON;
                }
            }
            throw this.createException("Unknown token: " + (char)n + "(" + n + ")");
        } catch (IOException iOException) {
            throw this.createException(iOException);
        }
    }

    @Override
    public ParseException createException(String string) {
        return new ParseException(string, this.tokenizer.lineno());
    }

    @Override
    public ParseException createException(Throwable throwable) {
        return new ParseException(throwable, this.tokenizer.lineno());
    }

    @Override
    public ParseException createException(String string, Throwable throwable) {
        return new ParseException(string, throwable, this.tokenizer.lineno());
    }
}

