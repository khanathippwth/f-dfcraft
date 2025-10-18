/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNodeValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigReference;
import moss.factions.shade.com.typesafe.config.impl.ConfigString;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.PathParser;
import moss.factions.shade.com.typesafe.config.impl.SubstitutionExpression;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class ConfigNodeSimpleValue
extends AbstractConfigNodeValue {
    final Token token;

    ConfigNodeSimpleValue(Token token) {
        this.token = token;
    }

    @Override
    protected Collection<Token> tokens() {
        return Collections.singletonList(this.token);
    }

    protected Token token() {
        return this.token;
    }

    protected AbstractConfigValue value() {
        if (Tokens.isValue(this.token)) {
            return Tokens.getValue(this.token);
        }
        if (Tokens.isUnquotedText(this.token)) {
            return new ConfigString.Unquoted(this.token.origin(), Tokens.getUnquotedText(this.token));
        }
        if (Tokens.isSubstitution(this.token)) {
            List<Token> list = Tokens.getSubstitutionPathExpression(this.token);
            Path path = PathParser.parsePathExpression(list.iterator(), this.token.origin());
            boolean bl = Tokens.getSubstitutionOptional(this.token);
            return new ConfigReference(this.token.origin(), new SubstitutionExpression(path, bl));
        }
        throw new ConfigException.BugOrBroken("ConfigNodeSimpleValue did not contain a valid value token");
    }
}

