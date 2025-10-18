/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.function.BiConsumer;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.transformation.NodePath;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated
public abstract class ConfigurationNodeWalker {
    public static final ConfigurationNodeWalker BREADTH_FIRST = new ConfigurationNodeWalker(){

        @Override
        public <T extends ConfigurationNode> @NonNull Iterator<VisitedNode<T>> walkWithPath(@NonNull T t) {
            return new BreadthFirstIterator<T>(t);
        }
    };
    public static final ConfigurationNodeWalker DEPTH_FIRST_PRE_ORDER = new ConfigurationNodeWalker(){

        @Override
        public <T extends ConfigurationNode> @NonNull Iterator<VisitedNode<T>> walkWithPath(@NonNull T t) {
            return new DepthFirstPreOrderIterator<T>(t);
        }
    };
    public static final ConfigurationNodeWalker DEPTH_FIRST_POST_ORDER = new ConfigurationNodeWalker(){

        @Override
        public <T extends ConfigurationNode> @NonNull Iterator<VisitedNode<T>> walkWithPath(@NonNull T t) {
            return new DepthFirstPostOrderIterator<T>(t);
        }
    };

    public abstract <T extends ConfigurationNode> @NonNull Iterator<VisitedNode<T>> walkWithPath(@NonNull T var1);

    public <T extends ConfigurationNode> @NonNull Iterator<T> walk(@NonNull T t) {
        return Iterators.transform(this.walkWithPath(t), VisitedNode::getNode);
    }

    public <T extends ConfigurationNode> void walk(@NonNull T t, @NonNull BiConsumer<? super NodePath, ? super T> biConsumer) {
        Iterator<VisitedNode<T>> iterator = this.walkWithPath(t);
        while (iterator.hasNext()) {
            VisitedNode<T> visitedNode = iterator.next();
            biConsumer.accept((NodePath)((NodePath)visitedNode.getPath()), (NodePath)((Object)visitedNode.getNode()));
        }
    }

    private static Object[] calculatePath(Object[] objectArray, Object object) {
        if (objectArray.length == 1 && objectArray[0] == null) {
            return new Object[]{object};
        }
        Object[] objectArray2 = Arrays.copyOf(objectArray, objectArray.length + 1);
        objectArray2[objectArray2.length - 1] = object;
        return objectArray2;
    }

    private static <T extends ConfigurationNode> Iterator<VisitedNodeImpl<T>> getChildren(VisitedNodeImpl<T> visitedNodeImpl) {
        T t = visitedNodeImpl.getNode();
        switch (t.getValueType()) {
            case LIST: {
                Object[] objectArray = visitedNodeImpl.getRawPath();
                return Iterators.transform(t.getChildrenList().iterator(), configurationNode -> {
                    Objects.requireNonNull(configurationNode);
                    ConfigurationNode configurationNode2 = configurationNode;
                    Object[] objectArray2 = ConfigurationNodeWalker.calculatePath(objectArray, configurationNode.getKey());
                    return new VisitedNodeImpl<ConfigurationNode>(objectArray2, configurationNode2);
                });
            }
            case MAP: {
                Object[] objectArray = visitedNodeImpl.getRawPath();
                return Iterators.transform(t.getChildrenMap().entrySet().iterator(), entry -> {
                    Objects.requireNonNull(entry);
                    ConfigurationNode configurationNode = (ConfigurationNode)entry.getValue();
                    Object[] objectArray2 = ConfigurationNodeWalker.calculatePath(objectArray, entry.getKey());
                    return new VisitedNodeImpl<ConfigurationNode>(objectArray2, configurationNode);
                });
            }
        }
        return Collections.emptyIterator();
    }

    private static final class VisitedNodeImpl<T extends ConfigurationNode>
    implements VisitedNode<T>,
    NodePath {
        private final Object[] path;
        private final T node;

        VisitedNodeImpl(Object[] objectArray, T t) {
            this.path = objectArray;
            this.node = t;
        }

        Object[] getRawPath() {
            return this.path;
        }

        @Override
        public @NonNull T getNode() {
            return this.node;
        }

        @Override
        public @NonNull NodePath getPath() {
            return this;
        }

        @Override
        public Object get(int n) {
            return this.path[n];
        }

        @Override
        public int size() {
            return this.path.length;
        }

        @Override
        public Object[] getArray() {
            return Arrays.copyOf(this.path, this.path.length);
        }

        @Override
        public @NonNull Iterator<Object> iterator() {
            return Iterators.forArray(this.path);
        }
    }

    private static final class DepthFirstPostOrderIterator<N extends ConfigurationNode>
    extends AbstractIterator<VisitedNode<N>> {
        private final ArrayDeque<NodeAndChildren> stack = new ArrayDeque();

        DepthFirstPostOrderIterator(N n) {
            this.stack.addLast(new NodeAndChildren(null, Iterators.singletonIterator(new VisitedNodeImpl<N>(n.getPath(), n))));
        }

        @Override
        protected VisitedNode<N> computeNext() {
            while (!this.stack.isEmpty()) {
                NodeAndChildren nodeAndChildren = this.stack.getLast();
                if (nodeAndChildren.children.hasNext()) {
                    VisitedNodeImpl visitedNodeImpl = nodeAndChildren.children.next();
                    this.stack.addLast(new NodeAndChildren(visitedNodeImpl, ConfigurationNodeWalker.getChildren(visitedNodeImpl)));
                    continue;
                }
                this.stack.removeLast();
                if (nodeAndChildren.node == null) continue;
                return nodeAndChildren.node;
            }
            return (VisitedNode)this.endOfData();
        }

        private final class NodeAndChildren {
            final VisitedNodeImpl<N> node;
            final Iterator<VisitedNodeImpl<N>> children;

            NodeAndChildren(VisitedNodeImpl<N> visitedNodeImpl, Iterator<VisitedNodeImpl<N>> iterator) {
                this.node = visitedNodeImpl;
                this.children = iterator;
            }
        }
    }

    private static final class DepthFirstPreOrderIterator<N extends ConfigurationNode>
    implements Iterator<VisitedNode<N>> {
        private final Deque<Iterator<VisitedNodeImpl<N>>> stack = new ArrayDeque<Iterator<VisitedNodeImpl<N>>>();

        DepthFirstPreOrderIterator(N n) {
            this.stack.push(Iterators.singletonIterator(new VisitedNodeImpl<N>(n.getPath(), n)));
        }

        @Override
        public boolean hasNext() {
            return !this.stack.isEmpty();
        }

        @Override
        public VisitedNode<N> next() {
            Iterator iterator;
            Iterator<VisitedNodeImpl<N>> iterator2 = this.stack.getLast();
            VisitedNodeImpl<N> visitedNodeImpl = iterator2.next();
            if (!iterator2.hasNext()) {
                this.stack.removeLast();
            }
            if ((iterator = ConfigurationNodeWalker.getChildren(visitedNodeImpl)).hasNext()) {
                this.stack.addLast(iterator);
            }
            return visitedNodeImpl;
        }
    }

    private static final class BreadthFirstIterator<N extends ConfigurationNode>
    implements Iterator<VisitedNode<N>> {
        private final Queue<VisitedNodeImpl<N>> queue = new ArrayDeque<VisitedNodeImpl<N>>();

        BreadthFirstIterator(N n) {
            this.queue.add(new VisitedNodeImpl<N>(n.getPath(), n));
        }

        @Override
        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        @Override
        public VisitedNode<N> next() {
            VisitedNodeImpl<N> visitedNodeImpl = this.queue.remove();
            Iterators.addAll(this.queue, ConfigurationNodeWalker.getChildren(visitedNodeImpl));
            return visitedNodeImpl;
        }
    }

    public static interface VisitedNode<T extends ConfigurationNode> {
        public @NonNull T getNode();

        public @NonNull NodePath getPath();
    }
}

