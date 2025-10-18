/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigList;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.Container;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;

final class SimpleConfigList
extends AbstractConfigValue
implements ConfigList,
Container,
Serializable {
    private static final long serialVersionUID = 2L;
    private final List<AbstractConfigValue> value;
    private final boolean resolved;

    SimpleConfigList(ConfigOrigin configOrigin, List<AbstractConfigValue> list) {
        this(configOrigin, list, ResolveStatus.fromValues(list));
    }

    SimpleConfigList(ConfigOrigin configOrigin, List<AbstractConfigValue> list, ResolveStatus resolveStatus) {
        super(configOrigin);
        this.value = list;
        boolean bl = this.resolved = resolveStatus == ResolveStatus.RESOLVED;
        if (resolveStatus != ResolveStatus.fromValues(list)) {
            throw new ConfigException.BugOrBroken("SimpleConfigList created with wrong resolve status: " + this);
        }
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.LIST;
    }

    @Override
    public List<Object> unwrapped() {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        for (AbstractConfigValue abstractConfigValue : this.value) {
            arrayList.add(abstractConfigValue.unwrapped());
        }
        return arrayList;
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.fromBoolean(this.resolved);
    }

    @Override
    public SimpleConfigList replaceChild(AbstractConfigValue abstractConfigValue, AbstractConfigValue abstractConfigValue2) {
        List<AbstractConfigValue> list = SimpleConfigList.replaceChildInList(this.value, abstractConfigValue, abstractConfigValue2);
        if (list == null) {
            return null;
        }
        return new SimpleConfigList(this.origin(), list);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue abstractConfigValue) {
        return SimpleConfigList.hasDescendantInList(this.value, abstractConfigValue);
    }

    private SimpleConfigList modify(AbstractConfigValue.NoExceptionsModifier noExceptionsModifier, ResolveStatus resolveStatus) {
        try {
            return this.modifyMayThrow(noExceptionsModifier, resolveStatus);
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", exception);
        }
    }

    private SimpleConfigList modifyMayThrow(AbstractConfigValue.Modifier modifier, ResolveStatus resolveStatus) {
        ArrayList<AbstractConfigValue> arrayList = null;
        int n = 0;
        for (AbstractConfigValue abstractConfigValue : this.value) {
            AbstractConfigValue abstractConfigValue2 = modifier.modifyChildMayThrow(null, abstractConfigValue);
            if (arrayList == null && abstractConfigValue2 != abstractConfigValue) {
                arrayList = new ArrayList<AbstractConfigValue>();
                for (int i = 0; i < n; ++i) {
                    arrayList.add(this.value.get(i));
                }
            }
            if (arrayList != null && abstractConfigValue2 != null) {
                arrayList.add(abstractConfigValue2);
            }
            ++n;
        }
        if (arrayList != null) {
            if (resolveStatus != null) {
                return new SimpleConfigList(this.origin(), arrayList, resolveStatus);
            }
            return new SimpleConfigList(this.origin(), arrayList);
        }
        return this;
    }

    ResolveResult<? extends SimpleConfigList> resolveSubstitutions(ResolveContext resolveContext, ResolveSource resolveSource) {
        if (this.resolved) {
            return ResolveResult.make(resolveContext, this);
        }
        if (resolveContext.isRestrictedToChild()) {
            return ResolveResult.make(resolveContext, this);
        }
        try {
            ResolveModifier resolveModifier = new ResolveModifier(resolveContext, resolveSource.pushParent(this));
            SimpleConfigList simpleConfigList = this.modifyMayThrow(resolveModifier, resolveContext.options().getAllowUnresolved() ? null : ResolveStatus.RESOLVED);
            return ResolveResult.make(resolveModifier.context, simpleConfigList);
        } catch (AbstractConfigValue.NotPossibleToResolve notPossibleToResolve) {
            throw notPossibleToResolve;
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", exception);
        }
    }

    @Override
    SimpleConfigList relativized(final Path path) {
        return this.modify(new AbstractConfigValue.NoExceptionsModifier(){

            @Override
            public AbstractConfigValue modifyChild(String string, AbstractConfigValue abstractConfigValue) {
                return abstractConfigValue.relativized(path);
            }
        }, this.resolveStatus());
    }

    @Override
    protected boolean canEqual(Object object) {
        return object instanceof SimpleConfigList;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SimpleConfigList) {
            return this.canEqual(object) && (this.value == ((SimpleConfigList)object).value || this.value.equals(((SimpleConfigList)object).value));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    protected void render(StringBuilder stringBuilder, int n, boolean bl, ConfigRenderOptions configRenderOptions) {
        if (this.value.isEmpty()) {
            stringBuilder.append("[]");
        } else {
            stringBuilder.append("[");
            if (configRenderOptions.getFormatted()) {
                stringBuilder.append('\n');
            }
            for (AbstractConfigValue abstractConfigValue : this.value) {
                if (configRenderOptions.getOriginComments()) {
                    String[] stringArray = abstractConfigValue.origin().description().split("\n");
                    for (String string : stringArray) {
                        SimpleConfigList.indent(stringBuilder, n + 1, configRenderOptions);
                        stringBuilder.append('#');
                        if (!string.isEmpty()) {
                            stringBuilder.append(' ');
                        }
                        stringBuilder.append(string);
                        stringBuilder.append("\n");
                    }
                }
                if (configRenderOptions.getComments()) {
                    for (String string : abstractConfigValue.origin().comments()) {
                        SimpleConfigList.indent(stringBuilder, n + 1, configRenderOptions);
                        stringBuilder.append("# ");
                        stringBuilder.append(string);
                        stringBuilder.append("\n");
                    }
                }
                SimpleConfigList.indent(stringBuilder, n + 1, configRenderOptions);
                abstractConfigValue.render(stringBuilder, n + 1, bl, configRenderOptions);
                stringBuilder.append(",");
                if (!configRenderOptions.getFormatted()) continue;
                stringBuilder.append('\n');
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            if (configRenderOptions.getFormatted()) {
                stringBuilder.setLength(stringBuilder.length() - 1);
                stringBuilder.append('\n');
                SimpleConfigList.indent(stringBuilder, n, configRenderOptions);
            }
            stringBuilder.append("]");
        }
    }

    @Override
    public boolean contains(Object object) {
        return this.value.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.value.containsAll(collection);
    }

    @Override
    public AbstractConfigValue get(int n) {
        return this.value.get(n);
    }

    @Override
    public int indexOf(Object object) {
        return this.value.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public Iterator<ConfigValue> iterator() {
        final Iterator<AbstractConfigValue> iterator = this.value.iterator();
        return new Iterator<ConfigValue>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ConfigValue next() {
                return (ConfigValue)iterator.next();
            }

            @Override
            public void remove() {
                throw SimpleConfigList.weAreImmutable("iterator().remove");
            }
        };
    }

    @Override
    public int lastIndexOf(Object object) {
        return this.value.lastIndexOf(object);
    }

    private static ListIterator<ConfigValue> wrapListIterator(final ListIterator<AbstractConfigValue> listIterator) {
        return new ListIterator<ConfigValue>(){

            @Override
            public boolean hasNext() {
                return listIterator.hasNext();
            }

            @Override
            public ConfigValue next() {
                return (ConfigValue)listIterator.next();
            }

            @Override
            public void remove() {
                throw SimpleConfigList.weAreImmutable("listIterator().remove");
            }

            @Override
            public void add(ConfigValue configValue) {
                throw SimpleConfigList.weAreImmutable("listIterator().add");
            }

            @Override
            public boolean hasPrevious() {
                return listIterator.hasPrevious();
            }

            @Override
            public int nextIndex() {
                return listIterator.nextIndex();
            }

            @Override
            public ConfigValue previous() {
                return (ConfigValue)listIterator.previous();
            }

            @Override
            public int previousIndex() {
                return listIterator.previousIndex();
            }

            @Override
            public void set(ConfigValue configValue) {
                throw SimpleConfigList.weAreImmutable("listIterator().set");
            }
        };
    }

    @Override
    public ListIterator<ConfigValue> listIterator() {
        return SimpleConfigList.wrapListIterator(this.value.listIterator());
    }

    @Override
    public ListIterator<ConfigValue> listIterator(int n) {
        return SimpleConfigList.wrapListIterator(this.value.listIterator(n));
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public List<ConfigValue> subList(int n, int n2) {
        ArrayList<ConfigValue> arrayList = new ArrayList<ConfigValue>();
        for (AbstractConfigValue abstractConfigValue : this.value.subList(n, n2)) {
            arrayList.add(abstractConfigValue);
        }
        return arrayList;
    }

    @Override
    public Object[] toArray() {
        return this.value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] TArray) {
        return this.value.toArray(TArray);
    }

    private static UnsupportedOperationException weAreImmutable(String string) {
        return new UnsupportedOperationException("ConfigList is immutable, you can't call List.'" + string + "'");
    }

    @Override
    public boolean add(ConfigValue configValue) {
        throw SimpleConfigList.weAreImmutable("add");
    }

    @Override
    public void add(int n, ConfigValue configValue) {
        throw SimpleConfigList.weAreImmutable("add");
    }

    @Override
    public boolean addAll(Collection<? extends ConfigValue> collection) {
        throw SimpleConfigList.weAreImmutable("addAll");
    }

    @Override
    public boolean addAll(int n, Collection<? extends ConfigValue> collection) {
        throw SimpleConfigList.weAreImmutable("addAll");
    }

    @Override
    public void clear() {
        throw SimpleConfigList.weAreImmutable("clear");
    }

    @Override
    public boolean remove(Object object) {
        throw SimpleConfigList.weAreImmutable("remove");
    }

    @Override
    public ConfigValue remove(int n) {
        throw SimpleConfigList.weAreImmutable("remove");
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw SimpleConfigList.weAreImmutable("removeAll");
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw SimpleConfigList.weAreImmutable("retainAll");
    }

    @Override
    public ConfigValue set(int n, ConfigValue configValue) {
        throw SimpleConfigList.weAreImmutable("set");
    }

    @Override
    protected SimpleConfigList newCopy(ConfigOrigin configOrigin) {
        return new SimpleConfigList(configOrigin, this.value);
    }

    final SimpleConfigList concatenate(SimpleConfigList simpleConfigList) {
        ConfigOrigin configOrigin = SimpleConfigOrigin.mergeOrigins(this.origin(), simpleConfigList.origin());
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>(this.value.size() + simpleConfigList.value.size());
        arrayList.addAll(this.value);
        arrayList.addAll(simpleConfigList.value);
        return new SimpleConfigList(configOrigin, arrayList);
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }

    @Override
    public SimpleConfigList withOrigin(ConfigOrigin configOrigin) {
        return (SimpleConfigList)super.withOrigin(configOrigin);
    }

    private static class ResolveModifier
    implements AbstractConfigValue.Modifier {
        ResolveContext context;
        final ResolveSource source;

        ResolveModifier(ResolveContext resolveContext, ResolveSource resolveSource) {
            this.context = resolveContext;
            this.source = resolveSource;
        }

        @Override
        public AbstractConfigValue modifyChildMayThrow(String string, AbstractConfigValue abstractConfigValue) {
            ResolveResult<? extends AbstractConfigValue> resolveResult = this.context.resolve(abstractConfigValue, this.source);
            this.context = resolveResult.context;
            return resolveResult.value;
        }
    }
}

