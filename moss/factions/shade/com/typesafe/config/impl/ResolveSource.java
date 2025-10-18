/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.Container;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;
import moss.factions.shade.com.typesafe.config.impl.SubstitutionExpression;

final class ResolveSource {
    final AbstractConfigObject root;
    final Node<Container> pathFromRoot;

    ResolveSource(AbstractConfigObject abstractConfigObject, Node<Container> node) {
        this.root = abstractConfigObject;
        this.pathFromRoot = node;
    }

    ResolveSource(AbstractConfigObject abstractConfigObject) {
        this.root = abstractConfigObject;
        this.pathFromRoot = null;
    }

    private AbstractConfigObject rootMustBeObj(Container container) {
        if (container instanceof AbstractConfigObject) {
            return (AbstractConfigObject)container;
        }
        return SimpleConfigObject.empty();
    }

    private static ResultWithPath findInObject(AbstractConfigObject abstractConfigObject, ResolveContext resolveContext, Path path) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("*** finding '" + path + "' in " + abstractConfigObject);
        }
        Path path2 = resolveContext.restrictToChild();
        ResolveResult<? extends AbstractConfigValue> resolveResult = resolveContext.restrict(path).resolve(abstractConfigObject, new ResolveSource(abstractConfigObject));
        ResolveContext resolveContext2 = resolveResult.context.restrict(path2);
        if (resolveResult.value instanceof AbstractConfigObject) {
            ValueWithPath valueWithPath = ResolveSource.findInObject((AbstractConfigObject)resolveResult.value, path);
            return new ResultWithPath(ResolveResult.make(resolveContext2, valueWithPath.value), valueWithPath.pathFromRoot);
        }
        throw new ConfigException.BugOrBroken("resolved object to non-object " + abstractConfigObject + " to " + resolveResult);
    }

    private static ValueWithPath findInObject(AbstractConfigObject abstractConfigObject, Path path) {
        try {
            return ResolveSource.findInObject(abstractConfigObject, path, null);
        } catch (ConfigException.NotResolved notResolved) {
            throw ConfigImpl.improveNotResolved(path, notResolved);
        }
    }

    private static ValueWithPath findInObject(AbstractConfigObject abstractConfigObject, Path path, Node<Container> node) {
        Node<Container> node2;
        String string = path.first();
        Path path2 = path.remainder();
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("*** looking up '" + string + "' in " + abstractConfigObject);
        }
        AbstractConfigValue abstractConfigValue = abstractConfigObject.attemptPeekWithPartialResolve(string);
        Node<Container> node3 = node2 = node == null ? new Node<Container>(abstractConfigObject) : node.prepend(abstractConfigObject);
        if (path2 == null) {
            return new ValueWithPath(abstractConfigValue, node2);
        }
        if (abstractConfigValue instanceof AbstractConfigObject) {
            return ResolveSource.findInObject((AbstractConfigObject)abstractConfigValue, path2, node2);
        }
        return new ValueWithPath(null, node2);
    }

    ResultWithPath lookupSubst(ResolveContext resolveContext, SubstitutionExpression substitutionExpression, int n) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(resolveContext.depth(), "searching for " + substitutionExpression);
        }
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(resolveContext.depth(), substitutionExpression + " - looking up relative to file it occurred in");
        }
        ResultWithPath resultWithPath = ResolveSource.findInObject(this.root, resolveContext, substitutionExpression.path());
        if (resultWithPath.result.value == null) {
            Path path = substitutionExpression.path().subPath(n);
            if (n > 0) {
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(resultWithPath.result.context.depth(), path + " - looking up relative to parent file");
                }
                resultWithPath = ResolveSource.findInObject(this.root, resultWithPath.result.context, path);
            }
            if (resultWithPath.result.value == null && resultWithPath.result.context.options().getUseSystemEnvironment()) {
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(resultWithPath.result.context.depth(), path + " - looking up in system environment");
                }
                resultWithPath = ResolveSource.findInObject(ConfigImpl.envVariablesAsConfigObject(), resolveContext, path);
            }
        }
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(resultWithPath.result.context.depth(), "resolved to " + resultWithPath);
        }
        return resultWithPath;
    }

    ResolveSource pushParent(Container container) {
        if (container == null) {
            throw new ConfigException.BugOrBroken("can't push null parent");
        }
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("pushing parent " + container + " ==root " + (container == this.root) + " onto " + this);
        }
        if (this.pathFromRoot == null) {
            if (container == this.root) {
                return new ResolveSource(this.root, new Node<Container>(container));
            }
            if (ConfigImpl.traceSubstitutionsEnabled() && this.root.hasDescendant((AbstractConfigValue)((Object)container))) {
                ConfigImpl.trace("***** BUG ***** tried to push parent " + container + " without having a path to it in " + this);
            }
            return this;
        }
        Container container2 = this.pathFromRoot.head();
        if (ConfigImpl.traceSubstitutionsEnabled() && container2 != null && !container2.hasDescendant((AbstractConfigValue)((Object)container))) {
            ConfigImpl.trace("***** BUG ***** trying to push non-child of " + container2 + ", non-child was " + container);
        }
        return new ResolveSource(this.root, this.pathFromRoot.prepend(container));
    }

    ResolveSource resetParents() {
        if (this.pathFromRoot == null) {
            return this;
        }
        return new ResolveSource(this.root);
    }

    private static Node<Container> replace(Node<Container> node, Container container, AbstractConfigValue abstractConfigValue) {
        Container container2;
        Container container3 = node.head();
        if (container3 != container) {
            throw new ConfigException.BugOrBroken("Can only replace() the top node we're resolving; had " + container3 + " on top and tried to replace " + container + " overall list was " + node);
        }
        Container container4 = container2 = node.tail() == null ? null : node.tail().head();
        if (abstractConfigValue == null || !(abstractConfigValue instanceof Container)) {
            if (container2 == null) {
                return null;
            }
            AbstractConfigValue abstractConfigValue2 = container2.replaceChild((AbstractConfigValue)((Object)container), null);
            return ResolveSource.replace(node.tail(), container2, abstractConfigValue2);
        }
        if (container2 == null) {
            return new Node<Container>((Container)((Object)abstractConfigValue));
        }
        AbstractConfigValue abstractConfigValue3 = container2.replaceChild((AbstractConfigValue)((Object)container), abstractConfigValue);
        Node<Container> node2 = ResolveSource.replace(node.tail(), container2, abstractConfigValue3);
        if (node2 != null) {
            return node2.prepend((Container)((Object)abstractConfigValue));
        }
        return new Node<Container>((Container)((Object)abstractConfigValue));
    }

    ResolveSource replaceCurrentParent(Container container, Container container2) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("replaceCurrentParent old " + container + "@" + System.identityHashCode(container) + " replacement " + container2 + "@" + System.identityHashCode(container) + " in " + this);
        }
        if (container == container2) {
            return this;
        }
        if (this.pathFromRoot != null) {
            Node<Container> node = ResolveSource.replace(this.pathFromRoot, container, (AbstractConfigValue)((Object)container2));
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace("replaced " + container + " with " + container2 + " in " + this);
                ConfigImpl.trace("path was: " + this.pathFromRoot + " is now " + node);
            }
            if (node != null) {
                return new ResolveSource((AbstractConfigObject)node.last(), node);
            }
            return new ResolveSource(SimpleConfigObject.empty());
        }
        if (container == this.root) {
            return new ResolveSource(this.rootMustBeObj(container2));
        }
        throw new ConfigException.BugOrBroken("attempt to replace root " + this.root + " with " + container2);
    }

    ResolveSource replaceWithinCurrentParent(AbstractConfigValue abstractConfigValue, AbstractConfigValue abstractConfigValue2) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("replaceWithinCurrentParent old " + abstractConfigValue + "@" + System.identityHashCode(abstractConfigValue) + " replacement " + abstractConfigValue2 + "@" + System.identityHashCode(abstractConfigValue) + " in " + this);
        }
        if (abstractConfigValue == abstractConfigValue2) {
            return this;
        }
        if (this.pathFromRoot != null) {
            Container container;
            AbstractConfigValue abstractConfigValue3 = (container = this.pathFromRoot.head()).replaceChild(abstractConfigValue, abstractConfigValue2);
            return this.replaceCurrentParent(container, abstractConfigValue3 instanceof Container ? (Container)((Object)abstractConfigValue3) : null);
        }
        if (abstractConfigValue == this.root && abstractConfigValue2 instanceof Container) {
            return new ResolveSource(this.rootMustBeObj((Container)((Object)abstractConfigValue2)));
        }
        throw new ConfigException.BugOrBroken("replace in parent not possible " + abstractConfigValue + " with " + abstractConfigValue2 + " in " + this);
    }

    public String toString() {
        return "ResolveSource(root=" + this.root + ", pathFromRoot=" + this.pathFromRoot + ")";
    }

    static final class ResultWithPath {
        final ResolveResult<? extends AbstractConfigValue> result;
        final Node<Container> pathFromRoot;

        ResultWithPath(ResolveResult<? extends AbstractConfigValue> resolveResult, Node<Container> node) {
            this.result = resolveResult;
            this.pathFromRoot = node;
        }

        public String toString() {
            return "ResultWithPath(result=" + this.result + ", pathFromRoot=" + this.pathFromRoot + ")";
        }
    }

    static final class ValueWithPath {
        final AbstractConfigValue value;
        final Node<Container> pathFromRoot;

        ValueWithPath(AbstractConfigValue abstractConfigValue, Node<Container> node) {
            this.value = abstractConfigValue;
            this.pathFromRoot = node;
        }

        public String toString() {
            return "ValueWithPath(value=" + this.value + ", pathFromRoot=" + this.pathFromRoot + ")";
        }
    }

    static final class Node<T> {
        final T value;
        final Node<T> next;

        Node(T t, Node<T> node) {
            this.value = t;
            this.next = node;
        }

        Node(T t) {
            this(t, null);
        }

        Node<T> prepend(T t) {
            return new Node<T>(t, this);
        }

        T head() {
            return this.value;
        }

        Node<T> tail() {
            return this.next;
        }

        T last() {
            Node<T> node = this;
            while (node.next != null) {
                node = node.next;
            }
            return node.value;
        }

        Node<T> reverse() {
            if (this.next == null) {
                return this;
            }
            Node<T> node = new Node<T>(this.value);
            Node<T> node2 = this.next;
            while (node2 != null) {
                node = node.prepend(node2.value);
                node2 = node2.next;
            }
            return node;
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("[");
            Node<T> node = this.reverse();
            while (node != null) {
                stringBuffer.append(node.value.toString());
                if (node.next != null) {
                    stringBuffer.append(" <= ");
                }
                node = node.next;
            }
            stringBuffer.append("]");
            return stringBuffer.toString();
        }
    }
}

