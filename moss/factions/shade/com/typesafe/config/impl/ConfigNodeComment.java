/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSingleToken;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class ConfigNodeComment
extends ConfigNodeSingleToken {
    ConfigNodeComment(Token token) {
        super(token);
        if (!Tokens.isComment(this.token)) {
            throw new ConfigException.BugOrBroken("Tried to create a ConfigNodeComment from a non-comment token");
        }
    }

    protected String commentText() {
        return Tokens.getCommentText(this.token);
    }
}

