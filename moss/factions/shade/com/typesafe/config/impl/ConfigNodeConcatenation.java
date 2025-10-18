/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Collection;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComplexValue;

final class ConfigNodeConcatenation
extends ConfigNodeComplexValue {
    ConfigNodeConcatenation(Collection<AbstractConfigNode> collection) {
        super(collection);
    }

    @Override
    protected ConfigNodeConcatenation newNode(Collection<AbstractConfigNode> collection) {
        return new ConfigNodeConcatenation(collection);
    }
}

