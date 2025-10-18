/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import java.util.Objects;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationVisitor;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import org.checkerframework.checker.nullness.qual.Nullable;

class VisitorNodeEnd {
    private final ConfigurationNode end;
    private final boolean isMap;

    VisitorNodeEnd(ConfigurationNode configurationNode, boolean bl) {
        this.end = configurationNode;
        this.isMap = bl;
    }

    ConfigurationNode getEnd() {
        return this.end;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof VisitorNodeEnd)) {
            return false;
        }
        VisitorNodeEnd visitorNodeEnd = (VisitorNodeEnd)object;
        return this.getEnd().equals(visitorNodeEnd.getEnd());
    }

    public int hashCode() {
        return Objects.hash(this.getEnd());
    }

    static <A extends SimpleConfigurationNode, S, E extends Exception> @Nullable A popFromVisitor(Object object, ConfigurationVisitor<S, ?, E> configurationVisitor, S s) {
        if (object instanceof VisitorNodeEnd) {
            VisitorNodeEnd visitorNodeEnd = (VisitorNodeEnd)object;
            if (visitorNodeEnd.isMap) {
                configurationVisitor.exitMappingNode(visitorNodeEnd.end, s);
            } else {
                configurationVisitor.exitListNode(visitorNodeEnd.end, s);
            }
            return null;
        }
        if (object instanceof SimpleConfigurationNode) {
            return (A)((SimpleConfigurationNode)object);
        }
        throw new IllegalStateException("Unknown value type " + object);
    }
}

