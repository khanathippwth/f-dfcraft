/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Collection;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComplexValue;

final class ConfigNodeArray
extends ConfigNodeComplexValue {
    ConfigNodeArray(Collection<AbstractConfigNode> collection) {
        super(collection);
    }

    @Override
    protected ConfigNodeArray newNode(Collection<AbstractConfigNode> collection) {
        return new ConfigNodeArray(collection);
    }
}

