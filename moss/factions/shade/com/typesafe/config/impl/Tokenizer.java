/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class Tokenizer {
    Tokenizer() {
    }

    private static String asString(int n) {
        if (n == 10) {
            return "newline";
        }
        if (n == 9) {
            return "tab";
        }
        if (n == -1) {
            return "end of file";
        }
        if (ConfigImplUtil.isC0Control(n)) {
            return String.format("control character 0x%x", n);
        }
        return String.format("%c", n);
    }

    static Iterator<Token> tokenize(ConfigOrigin configOrigin, Reader reader, ConfigSyntax configSyntax) {
        return new TokenIterator(configOrigin, reader, configSyntax != ConfigSyntax.JSON);
    }

    static String render(Iterator<Token> iterator) {
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().tokenText());
        }
        return stringBuilder.toString();
    }

    private static class TokenIterator
    implements Iterator<Token> {
        private final SimpleConfigOrigin origin;
        private final Reader input;
        private final LinkedList<Integer> buffer;
        private int lineNumber;
        private ConfigOrigin lineOrigin;
        private final Queue<Token> tokens;
        private final WhitespaceSaver whitespaceSaver;
        private final boolean allowComments;
        static final String firstNumberChars = "0123456789-";
        static final String numberChars = "0123456789eE+-.";
        static final String notInUnquotedText = "$\"{}[]:=,+#`^?!@*&\\";

        TokenIterator(ConfigOrigin configOrigin, Reader reader, boolean bl) {
            this.origin = (SimpleConfigOrigin)configOrigin;
            this.input = reader;
            this.allowComments = bl;
            this.buffer = new LinkedList();
            this.lineNumber = 1;
            this.lineOrigin = this.origin.withLineNumber(this.lineNumber);
            this.tokens = new LinkedList<Token>();
            this.tokens.add(Tokens.START);
            this.whitespaceSaver = new WhitespaceSaver();
        }

        private int nextCharRaw() {
            if (this.buffer.isEmpty()) {
                try {
                    return this.input.read();
                } catch (IOException iOException) {
                    throw new ConfigException.IO(this.origin, "read error: " + iOException.getMessage(), iOException);
                }
            }
            int n = this.buffer.pop();
            return n;
        }

        private void putBack(int n) {
            if (this.buffer.size() > 2) {
                throw new ConfigException.BugOrBroken("bug: putBack() three times, undesirable look-ahead");
            }
            this.buffer.push(n);
        }

        static boolean isWhitespace(int n) {
            return ConfigImplUtil.isWhitespace(n);
        }

        static boolean isWhitespaceNotNewline(int n) {
            return n != 10 && ConfigImplUtil.isWhitespace(n);
        }

        private boolean startOfComment(int n) {
            if (n == -1) {
                return false;
            }
            if (this.allowComments) {
                if (n == 35) {
                    return true;
                }
                if (n == 47) {
                    int n2 = this.nextCharRaw();
                    this.putBack(n2);
                    return n2 == 47;
                }
                return false;
            }
            return false;
        }

        private int nextCharAfterWhitespace(WhitespaceSaver whitespaceSaver) {
            int n;
            while (true) {
                if ((n = this.nextCharRaw()) == -1) {
                    return -1;
                }
                if (!TokenIterator.isWhitespaceNotNewline(n)) break;
                whitespaceSaver.add(n);
            }
            return n;
        }

        private ProblemException problem(String string) {
            return this.problem("", string, null);
        }

        private ProblemException problem(String string, String string2) {
            return this.problem(string, string2, null);
        }

        private ProblemException problem(String string, String string2, boolean bl) {
            return this.problem(string, string2, bl, null);
        }

        private ProblemException problem(String string, String string2, Throwable throwable) {
            return TokenIterator.problem(this.lineOrigin, string, string2, throwable);
        }

        private ProblemException problem(String string, String string2, boolean bl, Throwable throwable) {
            return TokenIterator.problem(this.lineOrigin, string, string2, bl, throwable);
        }

        private static ProblemException problem(ConfigOrigin configOrigin, String string, String string2, Throwable throwable) {
            return TokenIterator.problem(configOrigin, string, string2, false, throwable);
        }

        private static ProblemException problem(ConfigOrigin configOrigin, String string, String string2, boolean bl, Throwable throwable) {
            if (string == null || string2 == null) {
                throw new ConfigException.BugOrBroken("internal error, creating bad ProblemException");
            }
            return new ProblemException(Tokens.newProblem(configOrigin, string, string2, bl, throwable));
        }

        private static ProblemException problem(ConfigOrigin configOrigin, String string) {
            return TokenIterator.problem(configOrigin, "", string, null);
        }

        private static ConfigOrigin lineOrigin(ConfigOrigin configOrigin, int n) {
            return ((SimpleConfigOrigin)configOrigin).withLineNumber(n);
        }

        private Token pullComment(int n) {
            boolean bl = false;
            if (n == 47) {
                int n2 = this.nextCharRaw();
                if (n2 != 47) {
                    throw new ConfigException.BugOrBroken("called pullComment but // not seen");
                }
                bl = true;
            }
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                int n3;
                if ((n3 = this.nextCharRaw()) == -1 || n3 == 10) {
                    this.putBack(n3);
                    if (bl) {
                        return Tokens.newCommentDoubleSlash(this.lineOrigin, stringBuilder.toString());
                    }
                    return Tokens.newCommentHash(this.lineOrigin, stringBuilder.toString());
                }
                stringBuilder.appendCodePoint(n3);
            }
        }

        private Token pullUnquotedText() {
            String string;
            ConfigOrigin configOrigin = this.lineOrigin;
            StringBuilder stringBuilder = new StringBuilder();
            int n = this.nextCharRaw();
            while (n != -1 && notInUnquotedText.indexOf(n) < 0 && !TokenIterator.isWhitespace(n) && !this.startOfComment(n)) {
                stringBuilder.appendCodePoint(n);
                if (stringBuilder.length() == 4) {
                    string = stringBuilder.toString();
                    if (string.equals("true")) {
                        return Tokens.newBoolean(configOrigin, true);
                    }
                    if (string.equals("null")) {
                        return Tokens.newNull(configOrigin);
                    }
                } else if (stringBuilder.length() == 5 && (string = stringBuilder.toString()).equals("false")) {
                    return Tokens.newBoolean(configOrigin, false);
                }
                n = this.nextCharRaw();
            }
            this.putBack(n);
            string = stringBuilder.toString();
            return Tokens.newUnquotedText(configOrigin, string);
        }

        private Token pullNumber(int n) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.appendCodePoint(n);
            boolean bl = false;
            int n2 = this.nextCharRaw();
            while (n2 != -1 && numberChars.indexOf(n2) >= 0) {
                if (n2 == 46 || n2 == 101 || n2 == 69) {
                    bl = true;
                }
                stringBuilder.appendCodePoint(n2);
                n2 = this.nextCharRaw();
            }
            this.putBack(n2);
            String string = stringBuilder.toString();
            try {
                if (bl) {
                    return Tokens.newDouble(this.lineOrigin, Double.parseDouble(string), string);
                }
                return Tokens.newLong(this.lineOrigin, Long.parseLong(string), string);
            } catch (NumberFormatException numberFormatException) {
                for (char c : string.toCharArray()) {
                    if (notInUnquotedText.indexOf(c) < 0) continue;
                    throw this.problem(Tokenizer.asString(c), "Reserved character '" + Tokenizer.asString(c) + "' is not allowed outside quotes", true);
                }
                return Tokens.newUnquotedText(this.lineOrigin, string);
            }
        }

        private void pullEscapeSequence(StringBuilder stringBuilder, StringBuilder stringBuilder2) {
            int n = this.nextCharRaw();
            if (n == -1) {
                throw this.problem("End of input but backslash in string had nothing after it");
            }
            stringBuilder2.appendCodePoint(92);
            stringBuilder2.appendCodePoint(n);
            switch (n) {
                case 34: {
                    stringBuilder.append('\"');
                    break;
                }
                case 92: {
                    stringBuilder.append('\\');
                    break;
                }
                case 47: {
                    stringBuilder.append('/');
                    break;
                }
                case 98: {
                    stringBuilder.append('\b');
                    break;
                }
                case 102: {
                    stringBuilder.append('\f');
                    break;
                }
                case 110: {
                    stringBuilder.append('\n');
                    break;
                }
                case 114: {
                    stringBuilder.append('\r');
                    break;
                }
                case 116: {
                    stringBuilder.append('\t');
                    break;
                }
                case 117: {
                    char[] cArray = new char[4];
                    for (int i = 0; i < 4; ++i) {
                        int n2 = this.nextCharRaw();
                        if (n2 == -1) {
                            throw this.problem("End of input but expecting 4 hex digits for \\uXXXX escape");
                        }
                        cArray[i] = (char)n2;
                    }
                    String string = new String(cArray);
                    stringBuilder2.append(cArray);
                    try {
                        stringBuilder.appendCodePoint(Integer.parseInt(string, 16));
                        break;
                    } catch (NumberFormatException numberFormatException) {
                        throw this.problem(string, String.format("Malformed hex digits after \\u escape in string: '%s'", string), numberFormatException);
                    }
                }
                default: {
                    throw this.problem(Tokenizer.asString(n), String.format("backslash followed by '%s', this is not a valid escape sequence (quoted strings use JSON escaping, so use double-backslash \\\\ for literal backslash)", Tokenizer.asString(n)));
                }
            }
        }

        private void appendTripleQuotedString(StringBuilder stringBuilder, StringBuilder stringBuilder2) {
            int n;
            int n2 = 0;
            while (true) {
                if ((n = this.nextCharRaw()) == 34) {
                    ++n2;
                } else {
                    if (n2 >= 3) break;
                    n2 = 0;
                    if (n == -1) {
                        throw this.problem("End of input but triple-quoted string was still open");
                    }
                    if (n == 10) {
                        ++this.lineNumber;
                        this.lineOrigin = this.origin.withLineNumber(this.lineNumber);
                    }
                }
                stringBuilder.appendCodePoint(n);
                stringBuilder2.appendCodePoint(n);
            }
            stringBuilder.setLength(stringBuilder.length() - 3);
            this.putBack(n);
        }

        private Token pullQuotedString() {
            int n;
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.appendCodePoint(34);
            while (true) {
                if ((n = this.nextCharRaw()) == -1) {
                    throw this.problem("End of input but string quote was still open");
                }
                if (n == 92) {
                    this.pullEscapeSequence(stringBuilder, stringBuilder2);
                    continue;
                }
                if (n == 34) break;
                if (ConfigImplUtil.isC0Control(n)) {
                    throw this.problem(Tokenizer.asString(n), "JSON does not allow unescaped " + Tokenizer.asString(n) + " in quoted strings, use a backslash escape");
                }
                stringBuilder.appendCodePoint(n);
                stringBuilder2.appendCodePoint(n);
            }
            stringBuilder2.appendCodePoint(n);
            if (stringBuilder.length() == 0) {
                n = this.nextCharRaw();
                if (n == 34) {
                    stringBuilder2.appendCodePoint(n);
                    this.appendTripleQuotedString(stringBuilder, stringBuilder2);
                } else {
                    this.putBack(n);
                }
            }
            return Tokens.newString(this.lineOrigin, stringBuilder.toString(), stringBuilder2.toString());
        }

        private Token pullPlusEquals() {
            int n = this.nextCharRaw();
            if (n != 61) {
                throw this.problem(Tokenizer.asString(n), "'+' not followed by =, '" + Tokenizer.asString(n) + "' not allowed after '+'", true);
            }
            return Tokens.PLUS_EQUALS;
        }

        private Token pullSubstitution() {
            Token token;
            ConfigOrigin configOrigin = this.lineOrigin;
            int n = this.nextCharRaw();
            if (n != 123) {
                throw this.problem(Tokenizer.asString(n), "'$' not followed by {, '" + Tokenizer.asString(n) + "' not allowed after '$'", true);
            }
            boolean bl = false;
            n = this.nextCharRaw();
            if (n == 63) {
                bl = true;
            } else {
                this.putBack(n);
            }
            WhitespaceSaver whitespaceSaver = new WhitespaceSaver();
            ArrayList<Token> arrayList = new ArrayList<Token>();
            while ((token = this.pullNextToken(whitespaceSaver)) != Tokens.CLOSE_CURLY) {
                if (token == Tokens.END) {
                    throw TokenIterator.problem(configOrigin, "Substitution ${ was not closed with a }");
                }
                Token token2 = whitespaceSaver.check(token, configOrigin, this.lineNumber);
                if (token2 != null) {
                    arrayList.add(token2);
                }
                arrayList.add(token);
            }
            return Tokens.newSubstitution(configOrigin, bl, arrayList);
        }

        private Token pullNextToken(WhitespaceSaver whitespaceSaver) {
            Token token;
            int n = this.nextCharAfterWhitespace(whitespaceSaver);
            if (n == -1) {
                return Tokens.END;
            }
            if (n == 10) {
                Token token2 = Tokens.newLine(this.lineOrigin);
                ++this.lineNumber;
                this.lineOrigin = this.origin.withLineNumber(this.lineNumber);
                return token2;
            }
            if (this.startOfComment(n)) {
                token = this.pullComment(n);
            } else {
                switch (n) {
                    case 34: {
                        token = this.pullQuotedString();
                        break;
                    }
                    case 36: {
                        token = this.pullSubstitution();
                        break;
                    }
                    case 58: {
                        token = Tokens.COLON;
                        break;
                    }
                    case 44: {
                        token = Tokens.COMMA;
                        break;
                    }
                    case 61: {
                        token = Tokens.EQUALS;
                        break;
                    }
                    case 123: {
                        token = Tokens.OPEN_CURLY;
                        break;
                    }
                    case 125: {
                        token = Tokens.CLOSE_CURLY;
                        break;
                    }
                    case 91: {
                        token = Tokens.OPEN_SQUARE;
                        break;
                    }
                    case 93: {
                        token = Tokens.CLOSE_SQUARE;
                        break;
                    }
                    case 43: {
                        token = this.pullPlusEquals();
                        break;
                    }
                    default: {
                        token = null;
                    }
                }
                if (token == null) {
                    if (firstNumberChars.indexOf(n) >= 0) {
                        token = this.pullNumber(n);
                    } else {
                        if (notInUnquotedText.indexOf(n) >= 0) {
                            throw this.problem(Tokenizer.asString(n), "Reserved character '" + Tokenizer.asString(n) + "' is not allowed outside quotes", true);
                        }
                        this.putBack(n);
                        token = this.pullUnquotedText();
                    }
                }
            }
            if (token == null) {
                throw new ConfigException.BugOrBroken("bug: failed to generate next token");
            }
            return token;
        }

        private static boolean isSimpleValue(Token token) {
            return Tokens.isSubstitution(token) || Tokens.isUnquotedText(token) || Tokens.isValue(token);
        }

        private void queueNextToken() {
            Token token = this.pullNextToken(this.whitespaceSaver);
            Token token2 = this.whitespaceSaver.check(token, this.origin, this.lineNumber);
            if (token2 != null) {
                this.tokens.add(token2);
            }
            this.tokens.add(token);
        }

        @Override
        public boolean hasNext() {
            return !this.tokens.isEmpty();
        }

        @Override
        public Token next() {
            Token token = this.tokens.remove();
            if (this.tokens.isEmpty() && token != Tokens.END) {
                try {
                    this.queueNextToken();
                } catch (ProblemException problemException) {
                    this.tokens.add(problemException.problem());
                }
                if (this.tokens.isEmpty()) {
                    throw new ConfigException.BugOrBroken("bug: tokens queue should not be empty here");
                }
            }
            return token;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Does not make sense to remove items from token stream");
        }

        private static class WhitespaceSaver {
            private StringBuilder whitespace = new StringBuilder();
            private boolean lastTokenWasSimpleValue = false;

            WhitespaceSaver() {
            }

            void add(int n) {
                this.whitespace.appendCodePoint(n);
            }

            Token check(Token token, ConfigOrigin configOrigin, int n) {
                if (TokenIterator.isSimpleValue(token)) {
                    return this.nextIsASimpleValue(configOrigin, n);
                }
                return this.nextIsNotASimpleValue(configOrigin, n);
            }

            private Token nextIsNotASimpleValue(ConfigOrigin configOrigin, int n) {
                this.lastTokenWasSimpleValue = false;
                return this.createWhitespaceTokenFromSaver(configOrigin, n);
            }

            private Token nextIsASimpleValue(ConfigOrigin configOrigin, int n) {
                Token token = this.createWhitespaceTokenFromSaver(configOrigin, n);
                if (!this.lastTokenWasSimpleValue) {
                    this.lastTokenWasSimpleValue = true;
                }
                return token;
            }

            private Token createWhitespaceTokenFromSaver(ConfigOrigin configOrigin, int n) {
                if (this.whitespace.length() > 0) {
                    Token token = this.lastTokenWasSimpleValue ? Tokens.newUnquotedText(TokenIterator.lineOrigin(configOrigin, n), this.whitespace.toString()) : Tokens.newIgnoredWhitespace(TokenIterator.lineOrigin(configOrigin, n), this.whitespace.toString());
                    this.whitespace.setLength(0);
                    return token;
                }
                return null;
            }
        }
    }

    private static class ProblemException
    extends Exception {
        private static final long serialVersionUID = 1L;
        private final Token problem;

        ProblemException(Token token) {
            this.problem = token;
        }

        Token problem() {
            return this.problem;
        }
    }
}

