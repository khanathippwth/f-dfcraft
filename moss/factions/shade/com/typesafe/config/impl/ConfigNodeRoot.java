/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNodeValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeArray;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComplexValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeObject;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.PathParser;

final class ConfigNodeRoot
extends ConfigNodeComplexValue {
    private final ConfigOrigin origin;

    ConfigNodeRoot(Collection<AbstractConfigNode> collection, ConfigOrigin configOrigin) {
        super(collection);
        this.origin = configOrigin;
    }

    @Override
    protected ConfigNodeRoot newNode(Collection<AbstractConfigNode> collection) {
        throw new ConfigException.BugOrBroken("Tried to indent the root object");
    }

    protected ConfigNodeComplexValue value() {
        for (AbstractConfigNode abstractConfigNode : this.children) {
            if (!(abstractConfigNode instanceof ConfigNodeComplexValue)) continue;
            return (ConfigNodeComplexValue)abstractConfigNode;
        }
        throw new ConfigException.BugOrBroken("ConfigNodeRoot did not contain a value");
    }

    protected ConfigNodeRoot setValue(String string, AbstractConfigNodeValue abstractConfigNodeValue, ConfigSyntax configSyntax) {
        ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>(this.children);
        for (int i = 0; i < arrayList.size(); ++i) {
            AbstractConfigNode abstractConfigNode = (AbstractConfigNode)arrayList.get(i);
            if (!(abstractConfigNode instanceof ConfigNodeComplexValue)) continue;
            if (abstractConfigNode instanceof ConfigNodeArray) {
                throw new ConfigException.WrongType(this.origin, "The ConfigDocument had an array at the root level, and values cannot be modified inside an array.");
            }
            if (!(abstractConfigNode instanceof ConfigNodeObject)) continue;
            if (abstractConfigNodeValue == null) {
                arrayList.set(i, ((ConfigNodeObject)abstractConfigNode).removeValueOnPath(string, configSyntax));
            } else {
                arrayList.set(i, ((ConfigNodeObject)abstractConfigNode).setValueOnPath(string, abstractConfigNodeValue, configSyntax));
            }
            return new ConfigNodeRoot(arrayList, this.origin);
        }
        throw new ConfigException.BugOrBroken("ConfigNodeRoot did not contain a value");
    }

    protected boolean hasValue(String string) {
        Path path = PathParser.parsePath(string);
        ArrayList arrayList = new ArrayList(this.children);
        for (int i = 0; i < arrayList.size(); ++i) {
            AbstractConfigNode abstractConfigNode = (AbstractConfigNode)arrayList.get(i);
            if (!(abstractConfigNode instanceof ConfigNodeComplexValue)) continue;
            if (abstractConfigNode instanceof ConfigNodeArray) {
                throw new ConfigException.WrongType(this.origin, "The ConfigDocument had an array at the root level, and values cannot be modified inside an array.");
            }
            if (!(abstractConfigNode instanceof ConfigNodeObject)) continue;
            return ((ConfigNodeObject)abstractConfigNode).hasValue(path);
        }
        throw new ConfigException.BugOrBroken("ConfigNodeRoot did not contain a value");
    }
}

