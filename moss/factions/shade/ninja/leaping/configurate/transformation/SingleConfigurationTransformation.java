/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.transformation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.transformation.ConfigurationTransformation;
import moss.factions.shade.ninja.leaping.configurate.transformation.MoveStrategy;
import moss.factions.shade.ninja.leaping.configurate.transformation.TransformAction;
import org.checkerframework.checker.nullness.qual.NonNull;

class SingleConfigurationTransformation
extends ConfigurationTransformation {
    private final MoveStrategy strategy;
    private final Map<Object[], TransformAction> actions;
    private final ThreadLocal<ConfigurationTransformation.NodePath> sharedPath = ThreadLocal.withInitial(ConfigurationTransformation.NodePath::new);

    SingleConfigurationTransformation(Map<Object[], TransformAction> map, MoveStrategy moveStrategy) {
        this.actions = map;
        this.strategy = moveStrategy;
    }

    @Override
    public void apply(@NonNull ConfigurationNode configurationNode) {
        for (Map.Entry<Object[], TransformAction> entry : this.actions.entrySet()) {
            this.applySingleAction(configurationNode, entry.getKey(), 0, configurationNode, entry.getValue());
        }
    }

    private void applySingleAction(ConfigurationNode configurationNode, Object[] objectArray, int n, ConfigurationNode configurationNode2, TransformAction transformAction) {
        for (int i = n; i < objectArray.length; ++i) {
            if (objectArray[i] == WILDCARD_OBJECT) {
                if (configurationNode2.isList()) {
                    List<? extends ConfigurationNode> list = configurationNode2.getChildrenList();
                    for (int j = 0; j < list.size(); ++j) {
                        objectArray[i] = j;
                        this.applySingleAction(configurationNode, objectArray, i + 1, list.get(j), transformAction);
                    }
                    objectArray[i] = WILDCARD_OBJECT;
                } else if (configurationNode2.isMap()) {
                    for (Map.Entry<Object, ? extends ConfigurationNode> entry : configurationNode2.getChildrenMap().entrySet()) {
                        objectArray[i] = entry.getKey();
                        this.applySingleAction(configurationNode, objectArray, i + 1, entry.getValue(), transformAction);
                    }
                    objectArray[i] = WILDCARD_OBJECT;
                } else {
                    return;
                }
                return;
            }
            if (!(configurationNode2 = configurationNode2.getNode(objectArray[i])).isVirtual()) continue;
            return;
        }
        ConfigurationTransformation.NodePath nodePath = this.sharedPath.get();
        nodePath.arr = objectArray;
        Object[] objectArray2 = transformAction.visitPath(nodePath, configurationNode2);
        if (objectArray2 != null && !Arrays.equals(objectArray, objectArray2)) {
            this.strategy.move(configurationNode2, configurationNode.getNode(objectArray2));
            configurationNode2.setValue(null);
        }
    }
}

