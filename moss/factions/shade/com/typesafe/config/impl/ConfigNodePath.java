/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class ConfigNodePath
extends AbstractConfigNode {
    private final Path path;
    final ArrayList<Token> tokens;

    ConfigNodePath(Path path, Collection<Token> collection) {
        this.path = path;
        this.tokens = new ArrayList<Token>(collection);
    }

    @Override
    protected Collection<Token> tokens() {
        return this.tokens;
    }

    protected Path value() {
        return this.path;
    }

    protected ConfigNodePath subPath(int n) {
        int n2 = 0;
        ArrayList<Token> arrayList = new ArrayList<Token>(this.tokens);
        for (int i = 0; i < arrayList.size(); ++i) {
            if (Tokens.isUnquotedText(arrayList.get(i)) && arrayList.get(i).tokenText().equals(".")) {
                ++n2;
            }
            if (n2 != n) continue;
            return new ConfigNodePath(this.path.subPath(n), arrayList.subList(i + 1, arrayList.size()));
        }
        throw new ConfigException.BugOrBroken("Tried to remove too many elements from a Path node");
    }

    protected ConfigNodePath first() {
        ArrayList<Token> arrayList = new ArrayList<Token>(this.tokens);
        for (int i = 0; i < arrayList.size(); ++i) {
            if (!Tokens.isUnquotedText(arrayList.get(i)) || !arrayList.get(i).tokenText().equals(".")) continue;
            return new ConfigNodePath(this.path.subPath(0, 1), arrayList.subList(0, i));
        }
        return this;
    }
}

