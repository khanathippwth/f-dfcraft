/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.VisitorSafeNoopException;

public interface ConfigurationVisitor<S, T, E extends Exception> {
    public S newState() throws E;

    public void beginVisit(ConfigurationNode var1, S var2) throws E;

    public void enterNode(ConfigurationNode var1, S var2) throws E;

    public void enterMappingNode(ConfigurationNode var1, S var2) throws E;

    public void enterListNode(ConfigurationNode var1, S var2) throws E;

    public void enterScalarNode(ConfigurationNode var1, S var2) throws E;

    public void exitMappingNode(ConfigurationNode var1, S var2) throws E;

    public void exitListNode(ConfigurationNode var1, S var2) throws E;

    public T endVisit(S var1) throws E;

    public static interface Safe<S, T>
    extends ConfigurationVisitor<S, T, VisitorSafeNoopException> {
        @Override
        public S newState();

        @Override
        public void beginVisit(ConfigurationNode var1, S var2);

        @Override
        public void enterNode(ConfigurationNode var1, S var2);

        @Override
        public void enterMappingNode(ConfigurationNode var1, S var2);

        @Override
        public void enterListNode(ConfigurationNode var1, S var2);

        @Override
        public void enterScalarNode(ConfigurationNode var1, S var2);

        @Override
        public void exitMappingNode(ConfigurationNode var1, S var2);

        @Override
        public void exitListNode(ConfigurationNode var1, S var2);

        @Override
        public T endVisit(S var1);
    }

    @FunctionalInterface
    public static interface Stateless<E extends Exception>
    extends ConfigurationVisitor<Void, Void, E> {
        @Override
        default public Void newState() {
            return null;
        }

        @Override
        default public void beginVisit(ConfigurationNode node, Void state) throws E {
            this.beginVisit(node);
        }

        @Override
        default public void enterNode(ConfigurationNode node, Void state) throws E {
            this.enterNode(node);
        }

        @Override
        default public void enterMappingNode(ConfigurationNode node, Void state) throws E {
            this.enterMappingNode(node);
        }

        @Override
        default public void enterListNode(ConfigurationNode node, Void state) throws E {
            this.enterListNode(node);
        }

        @Override
        default public void enterScalarNode(ConfigurationNode node, Void state) throws E {
            this.enterScalarNode(node);
        }

        @Override
        default public void exitMappingNode(ConfigurationNode node, Void state) throws E {
            this.exitMappingNode(node);
        }

        @Override
        default public void exitListNode(ConfigurationNode node, Void state) throws E {
            this.exitListNode(node);
        }

        @Override
        default public Void endVisit(Void state) throws E {
            this.endVisit();
            return null;
        }

        default public void beginVisit(ConfigurationNode node) throws E {
        }

        public void enterNode(ConfigurationNode var1) throws E;

        default public void enterMappingNode(ConfigurationNode node) throws E {
        }

        default public void enterListNode(ConfigurationNode node) throws E {
        }

        default public void enterScalarNode(ConfigurationNode node) throws E {
        }

        default public void exitMappingNode(ConfigurationNode node) throws E {
        }

        default public void exitListNode(ConfigurationNode node) throws E {
        }

        default public void endVisit() throws E {
        }
    }
}

