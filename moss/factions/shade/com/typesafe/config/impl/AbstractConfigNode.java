/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Collection;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.parser.ConfigNode;

abstract class AbstractConfigNode
implements ConfigNode {
    AbstractConfigNode() {
    }

    abstract Collection<Token> tokens();

    @Override
    public final String render() {
        StringBuilder stringBuilder = new StringBuilder();
        Collection<Token> collection = this.tokens();
        for (Token token : collection) {
            stringBuilder.append(token.tokenText());
        }
        return stringBuilder.toString();
    }

    public final boolean equals(Object object) {
        return object instanceof AbstractConfigNode && this.render().equals(((AbstractConfigNode)object).render());
    }

    public final int hashCode() {
        return this.render().hashCode();
    }
}

