/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.transformation;

import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;

public enum MoveStrategy {
    MERGE{

        @Override
        public void move(ConfigurationNode configurationNode, ConfigurationNode configurationNode2) {
            configurationNode2.mergeValuesFrom(configurationNode);
        }
    }
    ,
    OVERWRITE{

        @Override
        public void move(ConfigurationNode configurationNode, ConfigurationNode configurationNode2) {
            configurationNode2.setValue(configurationNode);
        }
    };


    public abstract void move(ConfigurationNode var1, ConfigurationNode var2);
}

