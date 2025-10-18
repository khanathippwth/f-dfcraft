/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNodeValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeField;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSingleToken;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

abstract class ConfigNodeComplexValue
extends AbstractConfigNodeValue {
    protected final ArrayList<AbstractConfigNode> children;

    ConfigNodeComplexValue(Collection<AbstractConfigNode> collection) {
        this.children = new ArrayList<AbstractConfigNode>(collection);
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

    protected ConfigNodeComplexValue indentText(AbstractConfigNode abstractConfigNode) {
        ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>(this.children);
        for (int i = 0; i < arrayList.size(); ++i) {
            AbstractConfigNode abstractConfigNode2 = arrayList.get(i);
            if (abstractConfigNode2 instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)abstractConfigNode2).token())) {
                arrayList.add(i + 1, abstractConfigNode);
                ++i;
                continue;
            }
            if (abstractConfigNode2 instanceof ConfigNodeField) {
                AbstractConfigNodeValue abstractConfigNodeValue = ((ConfigNodeField)abstractConfigNode2).value();
                if (!(abstractConfigNodeValue instanceof ConfigNodeComplexValue)) continue;
                arrayList.set(i, ((ConfigNodeField)abstractConfigNode2).replaceValue(((ConfigNodeComplexValue)abstractConfigNodeValue).indentText(abstractConfigNode)));
                continue;
            }
            if (!(abstractConfigNode2 instanceof ConfigNodeComplexValue)) continue;
            arrayList.set(i, ((ConfigNodeComplexValue)abstractConfigNode2).indentText(abstractConfigNode));
        }
        return this.newNode(arrayList);
    }

    abstract ConfigNodeComplexValue newNode(Collection<AbstractConfigNode> var1);
}

