/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.transformation;

import java.util.Map;
import java.util.SortedMap;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.transformation.ConfigurationTransformation;
import org.checkerframework.checker.nullness.qual.NonNull;

class VersionedTransformation
extends ConfigurationTransformation {
    private final Object[] versionPath;
    private final SortedMap<Integer, ConfigurationTransformation> versionTransformations;

    VersionedTransformation(Object[] objectArray, SortedMap<Integer, ConfigurationTransformation> sortedMap) {
        this.versionPath = objectArray;
        this.versionTransformations = sortedMap;
    }

    @Override
    public void apply(@NonNull ConfigurationNode configurationNode) {
        ConfigurationNode configurationNode2 = configurationNode.getNode(this.versionPath);
        int n = configurationNode2.getInt(-1);
        for (Map.Entry<Integer, ConfigurationTransformation> entry : this.versionTransformations.entrySet()) {
            if (entry.getKey() <= n) continue;
            entry.getValue().apply(configurationNode);
            n = entry.getKey();
        }
        configurationNode2.setValue(n);
    }
}

