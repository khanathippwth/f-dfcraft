/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Collection;
import java.util.Collections;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.Token;

class ConfigNodeSingleToken
extends AbstractConfigNode {
    final Token token;

    ConfigNodeSingleToken(Token token) {
        this.token = token;
    }

    @Override
    protected Collection<Token> tokens() {
        return Collections.singletonList(this.token);
    }

    protected Token token() {
        return this.token;
    }
}

