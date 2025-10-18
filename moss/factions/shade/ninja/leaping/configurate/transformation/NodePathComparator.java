/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.transformation;

import java.util.Comparator;
import moss.factions.shade.ninja.leaping.configurate.transformation.ConfigurationTransformation;

class NodePathComparator
implements Comparator<Object[]> {
    NodePathComparator() {
    }

    @Override
    public int compare(Object[] objectArray, Object[] objectArray2) {
        for (int i = 0; i < Math.min(objectArray.length, objectArray2.length); ++i) {
            if (objectArray[i] == ConfigurationTransformation.WILDCARD_OBJECT || objectArray2[i] == ConfigurationTransformation.WILDCARD_OBJECT) {
                if (objectArray[i] == ConfigurationTransformation.WILDCARD_OBJECT && objectArray2[i] == ConfigurationTransformation.WILDCARD_OBJECT) continue;
                return objectArray[i] == ConfigurationTransformation.WILDCARD_OBJECT ? 1 : -1;
            }
            if (objectArray[i] instanceof Comparable) {
                int n = ((Comparable)objectArray[i]).compareTo(objectArray2[i]);
                switch (n) {
                    case 0: {
                        break;
                    }
                    default: {
                        return n;
                    }
                }
                continue;
            }
            return objectArray[i].equals(objectArray2[i]) ? 0 : Integer.compare(objectArray[i].hashCode(), objectArray2[i].hashCode());
        }
        return Integer.compare(objectArray2.length, objectArray.length);
    }
}

