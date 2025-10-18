/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigBoolean;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.ConfigNull;
import moss.factions.shade.com.typesafe.config.impl.ConfigNumber;
import moss.factions.shade.com.typesafe.config.impl.ConfigString;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.TokenType;
import moss.factions.shade.com.typesafe.config.impl.Tokenizer;

final class Tokens {
    static final Token START = Token.newWithoutOrigin(TokenType.START, "start of file", "");
    static final Token END = Token.newWithoutOrigin(TokenType.END, "end of file", "");
    static final Token COMMA = Token.newWithoutOrigin(TokenType.COMMA, "','", ",");
    static final Token EQUALS = Token.newWithoutOrigin(TokenType.EQUALS, "'='", "=");
    static final Token COLON = Token.newWithoutOrigin(TokenType.COLON, "':'", ":");
    static final Token OPEN_CURLY = Token.newWithoutOrigin(TokenType.OPEN_CURLY, "'{'", "{");
    static final Token CLOSE_CURLY = Token.newWithoutOrigin(TokenType.CLOSE_CURLY, "'}'", "}");
    static final Token OPEN_SQUARE = Token.newWithoutOrigin(TokenType.OPEN_SQUARE, "'['", "[");
    static final Token CLOSE_SQUARE = Token.newWithoutOrigin(TokenType.CLOSE_SQUARE, "']'", "]");
    static final Token PLUS_EQUALS = Token.newWithoutOrigin(TokenType.PLUS_EQUALS, "'+='", "+=");

    Tokens() {
    }

    static boolean isValue(Token token) {
        return token instanceof Value;
    }

    static AbstractConfigValue getValue(Token token) {
        if (token instanceof Value) {
            return ((Value)token).value();
        }
        throw new ConfigException.BugOrBroken("tried to get value of non-value token " + token);
    }

    static boolean isValueWithType(Token token, ConfigValueType configValueType) {
        return Tokens.isValue(token) && Tokens.getValue(token).valueType() == configValueType;
    }

    static boolean isNewline(Token token) {
        return token instanceof Line;
    }

    static boolean isProblem(Token token) {
        return token instanceof Problem;
    }

    static String getProblemWhat(Token token) {
        if (token instanceof Problem) {
            return ((Problem)token).what();
        }
        throw new ConfigException.BugOrBroken("tried to get problem what from " + token);
    }

    static String getProblemMessage(Token token) {
        if (token instanceof Problem) {
            return ((Problem)token).message();
        }
        throw new ConfigException.BugOrBroken("tried to get problem message from " + token);
    }

    static boolean getProblemSuggestQuotes(Token token) {
        if (token instanceof Problem) {
            return ((Problem)token).suggestQuotes();
        }
        throw new ConfigException.BugOrBroken("tried to get problem suggestQuotes from " + token);
    }

    static Throwable getProblemCause(Token token) {
        if (token instanceof Problem) {
            return ((Problem)token).cause();
        }
        throw new ConfigException.BugOrBroken("tried to get problem cause from " + token);
    }

    static boolean isComment(Token token) {
        return token instanceof Comment;
    }

    static String getCommentText(Token token) {
        if (token instanceof Comment) {
            return ((Comment)token).text();
        }
        throw new ConfigException.BugOrBroken("tried to get comment text from " + token);
    }

    static boolean isUnquotedText(Token token) {
        return token instanceof UnquotedText;
    }

    static String getUnquotedText(Token token) {
        if (token instanceof UnquotedText) {
            return ((UnquotedText)token).value();
        }
        throw new ConfigException.BugOrBroken("tried to get unquoted text from " + token);
    }

    static boolean isIgnoredWhitespace(Token token) {
        return token instanceof IgnoredWhitespace;
    }

    static boolean isSubstitution(Token token) {
        return token instanceof Substitution;
    }

    static List<Token> getSubstitutionPathExpression(Token token) {
        if (token instanceof Substitution) {
            return ((Substitution)token).value();
        }
        throw new ConfigException.BugOrBroken("tried to get substitution from " + token);
    }

    static boolean getSubstitutionOptional(Token token) {
        if (token instanceof Substitution) {
            return ((Substitution)token).optional();
        }
        throw new ConfigException.BugOrBroken("tried to get substitution optionality from " + token);
    }

    static Token newLine(ConfigOrigin configOrigin) {
        return new Line(configOrigin);
    }

    static Token newProblem(ConfigOrigin configOrigin, String string, String string2, boolean bl, Throwable throwable) {
        return new Problem(configOrigin, string, string2, bl, throwable);
    }

    static Token newCommentDoubleSlash(ConfigOrigin configOrigin, String string) {
        return new Comment.DoubleSlashComment(configOrigin, string);
    }

    static Token newCommentHash(ConfigOrigin configOrigin, String string) {
        return new Comment.HashComment(configOrigin, string);
    }

    static Token newUnquotedText(ConfigOrigin configOrigin, String string) {
        return new UnquotedText(configOrigin, string);
    }

    static Token newIgnoredWhitespace(ConfigOrigin configOrigin, String string) {
        return new IgnoredWhitespace(configOrigin, string);
    }

    static Token newSubstitution(ConfigOrigin configOrigin, boolean bl, List<Token> list) {
        return new Substitution(configOrigin, bl, list);
    }

    static Token newValue(AbstractConfigValue abstractConfigValue) {
        return new Value(abstractConfigValue);
    }

    static Token newValue(AbstractConfigValue abstractConfigValue, String string) {
        return new Value(abstractConfigValue, string);
    }

    static Token newString(ConfigOrigin configOrigin, String string, String string2) {
        return Tokens.newValue(new ConfigString.Quoted(configOrigin, string), string2);
    }

    static Token newInt(ConfigOrigin configOrigin, int n, String string) {
        return Tokens.newValue(ConfigNumber.newNumber(configOrigin, n, string), string);
    }

    static Token newDouble(ConfigOrigin configOrigin, double d, String string) {
        return Tokens.newValue(ConfigNumber.newNumber(configOrigin, d, string), string);
    }

    static Token newLong(ConfigOrigin configOrigin, long l, String string) {
        return Tokens.newValue(ConfigNumber.newNumber(configOrigin, l, string), string);
    }

    static Token newNull(ConfigOrigin configOrigin) {
        return Tokens.newValue(new ConfigNull(configOrigin), "null");
    }

    static Token newBoolean(ConfigOrigin configOrigin, boolean bl) {
        return Tokens.newValue(new ConfigBoolean(configOrigin, bl), "" + bl);
    }

    private static class Substitution
    extends Token {
        private final boolean optional;
        private final List<Token> value;

        Substitution(ConfigOrigin configOrigin, boolean bl, List<Token> list) {
            super(TokenType.SUBSTITUTION, configOrigin);
            this.optional = bl;
            this.value = list;
        }

        boolean optional() {
            return this.optional;
        }

        List<Token> value() {
            return this.value;
        }

        @Override
        public String tokenText() {
            return "${" + (this.optional ? "?" : "") + Tokenizer.render(this.value.iterator()) + "}";
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (Token token : this.value) {
                stringBuilder.append(token.toString());
            }
            return "'${" + stringBuilder.toString() + "}'";
        }

        @Override
        protected boolean canEqual(Object object) {
            return object instanceof Substitution;
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object) && ((Substitution)object).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.value.hashCode();
        }
    }

    private static abstract class Comment
    extends Token {
        private final String text;

        Comment(ConfigOrigin configOrigin, String string) {
            super(TokenType.COMMENT, configOrigin);
            this.text = string;
        }

        String text() {
            return this.text;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("'#");
            stringBuilder.append(this.text);
            stringBuilder.append("' (COMMENT)");
            return stringBuilder.toString();
        }

        @Override
        protected boolean canEqual(Object object) {
            return object instanceof Comment;
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object) && ((Comment)object).text.equals(this.text);
        }

        @Override
        public int hashCode() {
            int n = 41 * (41 + super.hashCode());
            n = 41 * (n + this.text.hashCode());
            return n;
        }

        static final class HashComment
        extends Comment {
            HashComment(ConfigOrigin configOrigin, String string) {
                super(configOrigin, string);
            }

            @Override
            public String tokenText() {
                return "#" + ((Comment)this).text;
            }
        }

        static final class DoubleSlashComment
        extends Comment {
            DoubleSlashComment(ConfigOrigin configOrigin, String string) {
                super(configOrigin, string);
            }

            @Override
            public String tokenText() {
                return "//" + ((Comment)this).text;
            }
        }
    }

    private static class Problem
    extends Token {
        private final String what;
        private final String message;
        private final boolean suggestQuotes;
        private final Throwable cause;

        Problem(ConfigOrigin configOrigin, String string, String string2, boolean bl, Throwable throwable) {
            super(TokenType.PROBLEM, configOrigin);
            this.what = string;
            this.message = string2;
            this.suggestQuotes = bl;
            this.cause = throwable;
        }

        String what() {
            return this.what;
        }

        String message() {
            return this.message;
        }

        boolean suggestQuotes() {
            return this.suggestQuotes;
        }

        Throwable cause() {
            return this.cause;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('\'');
            stringBuilder.append(this.what);
            stringBuilder.append('\'');
            stringBuilder.append(" (");
            stringBuilder.append(this.message);
            stringBuilder.append(")");
            return stringBuilder.toString();
        }

        @Override
        protected boolean canEqual(Object object) {
            return object instanceof Problem;
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object) && ((Problem)object).what.equals(this.what) && ((Problem)object).message.equals(this.message) && ((Problem)object).suggestQuotes == this.suggestQuotes && ConfigImplUtil.equalsHandlingNull(((Problem)object).cause, this.cause);
        }

        @Override
        public int hashCode() {
            int n = 41 * (41 + super.hashCode());
            n = 41 * (n + this.what.hashCode());
            n = 41 * (n + this.message.hashCode());
            n = 41 * (n + Boolean.valueOf(this.suggestQuotes).hashCode());
            if (this.cause != null) {
                n = 41 * (n + this.cause.hashCode());
            }
            return n;
        }
    }

    private static class IgnoredWhitespace
    extends Token {
        private final String value;

        IgnoredWhitespace(ConfigOrigin configOrigin, String string) {
            super(TokenType.IGNORED_WHITESPACE, configOrigin);
            this.value = string;
        }

        @Override
        public String toString() {
            return "'" + this.value + "' (WHITESPACE)";
        }

        @Override
        protected boolean canEqual(Object object) {
            return object instanceof IgnoredWhitespace;
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object) && ((IgnoredWhitespace)object).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.value.hashCode();
        }

        @Override
        public String tokenText() {
            return this.value;
        }
    }

    private static class UnquotedText
    extends Token {
        private final String value;

        UnquotedText(ConfigOrigin configOrigin, String string) {
            super(TokenType.UNQUOTED_TEXT, configOrigin);
            this.value = string;
        }

        String value() {
            return this.value;
        }

        @Override
        public String toString() {
            return "'" + this.value + "'";
        }

        @Override
        protected boolean canEqual(Object object) {
            return object instanceof UnquotedText;
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object) && ((UnquotedText)object).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.value.hashCode();
        }

        @Override
        public String tokenText() {
            return this.value;
        }
    }

    private static class Line
    extends Token {
        Line(ConfigOrigin configOrigin) {
            super(TokenType.NEWLINE, configOrigin);
        }

        @Override
        public String toString() {
            return "'\\n'@" + this.lineNumber();
        }

        @Override
        protected boolean canEqual(Object object) {
            return object instanceof Line;
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object) && ((Line)object).lineNumber() == this.lineNumber();
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.lineNumber();
        }

        @Override
        public String tokenText() {
            return "\n";
        }
    }

    private static class Value
    extends Token {
        private final AbstractConfigValue value;

        Value(AbstractConfigValue abstractConfigValue) {
            this(abstractConfigValue, null);
        }

        Value(AbstractConfigValue abstractConfigValue, String string) {
            super(TokenType.VALUE, abstractConfigValue.origin(), string);
            this.value = abstractConfigValue;
        }

        AbstractConfigValue value() {
            return this.value;
        }

        @Override
        public String toString() {
            if (this.value().resolveStatus() == ResolveStatus.RESOLVED) {
                return "'" + this.value().unwrapped() + "' (" + this.value.valueType().name() + ")";
            }
            return "'<unresolved value>' (" + this.value.valueType().name() + ")";
        }

        @Override
        protected boolean canEqual(Object object) {
            return object instanceof Value;
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object) && ((Value)object).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.value.hashCode();
        }
    }
}

