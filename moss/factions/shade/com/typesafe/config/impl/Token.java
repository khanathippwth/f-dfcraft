/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.TokenType;

class Token {
    private final TokenType tokenType;
    private final String debugString;
    private final ConfigOrigin origin;
    private final String tokenText;

    Token(TokenType tokenType, ConfigOrigin configOrigin) {
        this(tokenType, configOrigin, null);
    }

    Token(TokenType tokenType, ConfigOrigin configOrigin, String string) {
        this(tokenType, configOrigin, string, null);
    }

    Token(TokenType tokenType, ConfigOrigin configOrigin, String string, String string2) {
        this.tokenType = tokenType;
        this.origin = configOrigin;
        this.debugString = string2;
        this.tokenText = string;
    }

    static Token newWithoutOrigin(TokenType tokenType, String string, String string2) {
        return new Token(tokenType, null, string2, string);
    }

    final TokenType tokenType() {
        return this.tokenType;
    }

    public String tokenText() {
        return this.tokenText;
    }

    final ConfigOrigin origin() {
        if (this.origin == null) {
            throw new ConfigException.BugOrBroken("tried to get origin from token that doesn't have one: " + this);
        }
        return this.origin;
    }

    final int lineNumber() {
        if (this.origin != null) {
            return this.origin.lineNumber();
        }
        return -1;
    }

    public String toString() {
        if (this.debugString != null) {
            return this.debugString;
        }
        return this.tokenType.name();
    }

    protected boolean canEqual(Object object) {
        return object instanceof Token;
    }

    public boolean equals(Object object) {
        if (object instanceof Token) {
            return this.canEqual(object) && this.tokenType == ((Token)object).tokenType;
        }
        return false;
    }

    public int hashCode() {
        return this.tokenType.hashCode();
    }
}

