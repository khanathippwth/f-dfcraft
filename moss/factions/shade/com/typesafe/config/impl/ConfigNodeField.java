/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNodeValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComment;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodePath;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSingleToken;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class ConfigNodeField
extends AbstractConfigNode {
    private final ArrayList<AbstractConfigNode> children;

    public ConfigNodeField(Collection<AbstractConfigNode> collection) {
        this.children = new ArrayList<AbstractConfigNode>(collection);
    }

    @Override
    protected Collection<Token> tokens() {
        ArrayList<Token> arrayList = new ArrayList<Token>();
        for (AbstractConfigNode abstractConfigNode : this.children) {
            arrayList.addAll(abstractConfigNode.tokens());
        }
        return arrayList;
    }

    public ConfigNodeField replaceValue(AbstractConfigNodeValue abstractConfigNodeValue) {
        ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>(this.children);
        for (int i = 0; i < arrayList.size(); ++i) {
            if (!(arrayList.get(i) instanceof AbstractConfigNodeValue)) continue;
            arrayList.set(i, abstractConfigNodeValue);
            return new ConfigNodeField(arrayList);
        }
        throw new ConfigException.BugOrBroken("Field node doesn't have a value");
    }

    public AbstractConfigNodeValue value() {
        for (int i = 0; i < this.children.size(); ++i) {
            if (!(this.children.get(i) instanceof AbstractConfigNodeValue)) continue;
            return (AbstractConfigNodeValue)this.children.get(i);
        }
        throw new ConfigException.BugOrBroken("Field node doesn't have a value");
    }

    public ConfigNodePath path() {
        for (int i = 0; i < this.children.size(); ++i) {
            if (!(this.children.get(i) instanceof ConfigNodePath)) continue;
            return (ConfigNodePath)this.children.get(i);
        }
        throw new ConfigException.BugOrBroken("Field node doesn't have a path");
    }

    protected Token separator() {
        for (AbstractConfigNode abstractConfigNode : this.children) {
            Token token;
            if (!(abstractConfigNode instanceof ConfigNodeSingleToken) || (token = ((ConfigNodeSingleToken)abstractConfigNode).token()) != Tokens.PLUS_EQUALS && token != Tokens.COLON && token != Tokens.EQUALS) continue;
            return token;
        }
        return null;
    }

    protected List<String> comments() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (AbstractConfigNode abstractConfigNode : this.children) {
            if (!(abstractConfigNode instanceof ConfigNodeComment)) continue;
            arrayList.add(((ConfigNodeComment)abstractConfigNode).commentText());
        }
        return arrayList;
    }
}

