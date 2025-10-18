/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.ConfigIncludeKind;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSimpleValue;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class ConfigNodeInclude
extends AbstractConfigNode {
    private final ArrayList<AbstractConfigNode> children;
    private final ConfigIncludeKind kind;
    private final boolean isRequired;

    ConfigNodeInclude(Collection<AbstractConfigNode> collection, ConfigIncludeKind configIncludeKind, boolean bl) {
        this.children = new ArrayList<AbstractConfigNode>(collection);
        this.kind = configIncludeKind;
        this.isRequired = bl;
    }

    public final Collection<AbstractConfigNode> children() {
        return this.children;
    }

    @Override
    protected Collection<Token> tokens() {
        ArrayList<Token> arrayList = new ArrayList<Token>();
        for (AbstractConfigNode abstractConfigNode : this.children) {
            arrayList.addAll(abstractConfigNode.tokens());
        }
        return arrayList;
    }

    protected ConfigIncludeKind kind() {
        return this.kind;
    }

    protected boolean isRequired() {
        return this.isRequired;
    }

    protected String name() {
        for (AbstractConfigNode abstractConfigNode : this.children) {
            if (!(abstractConfigNode instanceof ConfigNodeSimpleValue)) continue;
            return (String)Tokens.getValue(((ConfigNodeSimpleValue)abstractConfigNode).token()).unwrapped();
        }
        return null;
    }
}

