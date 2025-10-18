/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.transformation;

import java.util.Arrays;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.transformation.ConfigurationTransformation;
import org.checkerframework.checker.nullness.qual.NonNull;

class ChainedConfigurationTransformation
extends ConfigurationTransformation {
    private final ConfigurationTransformation[] transformations;

    ChainedConfigurationTransformation(ConfigurationTransformation[] configurationTransformationArray) {
        this.transformations = Arrays.copyOf(configurationTransformationArray, configurationTransformationArray.length);
    }

    @Override
    public void apply(@NonNull ConfigurationNode configurationNode) {
        for (ConfigurationTransformation configurationTransformation : this.transformations) {
            configurationTransformation.apply(configurationNode);
        }
    }
}

