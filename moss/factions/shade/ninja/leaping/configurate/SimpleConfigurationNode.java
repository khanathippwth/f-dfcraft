/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import moss.factions.shade.ninja.leaping.configurate.ConfigValue;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationVisitor;
import moss.factions.shade.ninja.leaping.configurate.ListConfigValue;
import moss.factions.shade.ninja.leaping.configurate.MapConfigValue;
import moss.factions.shade.ninja.leaping.configurate.NullConfigValue;
import moss.factions.shade.ninja.leaping.configurate.ScalarConfigValue;
import moss.factions.shade.ninja.leaping.configurate.ValueType;
import moss.factions.shade.ninja.leaping.configurate.VisitorNodeEnd;
import moss.factions.shade.ninja.leaping.configurate.VisitorSafeNoopException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleConfigurationNode
implements ConfigurationNode {
    private final @NonNull ConfigurationOptions options;
    volatile boolean attached;
    volatile @Nullable Object key;
    private @Nullable SimpleConfigurationNode parent;
    private volatile @NonNull ConfigValue value;

    @Deprecated
    public static @NonNull SimpleConfigurationNode root() {
        return SimpleConfigurationNode.root(ConfigurationOptions.defaults());
    }

    @Deprecated
    public static @NonNull SimpleConfigurationNode root(@NonNull ConfigurationOptions configurationOptions) {
        return new SimpleConfigurationNode(null, null, configurationOptions);
    }

    protected SimpleConfigurationNode(@Nullable Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode, @NonNull ConfigurationOptions configurationOptions) {
        Objects.requireNonNull(configurationOptions, "options");
        this.key = object;
        this.options = configurationOptions;
        this.parent = simpleConfigurationNode;
        this.value = NullConfigValue.instance();
        if (simpleConfigurationNode == null) {
            this.attached = true;
        }
    }

    protected SimpleConfigurationNode(@Nullable SimpleConfigurationNode simpleConfigurationNode, SimpleConfigurationNode simpleConfigurationNode2) {
        this.options = simpleConfigurationNode2.options;
        this.attached = true;
        this.key = simpleConfigurationNode2.key;
        this.parent = simpleConfigurationNode;
        this.value = simpleConfigurationNode2.value.copy(this);
    }

    private <V> V storeDefault(V v) {
        if (v != null && this.getOptions().shouldCopyDefaults()) {
            this.setValue(v);
        }
        return v;
    }

    private <V> V storeDefault(TypeToken<V> typeToken, V v) {
        if (v != null && this.getOptions().shouldCopyDefaults()) {
            this.setValue(typeToken, v);
        }
        return v;
    }

    @Override
    public Object getValue(Object object) {
        Object object2 = this.value.getValue();
        return object2 == null ? this.storeDefault(object) : object2;
    }

    @Override
    public Object getValue(@NonNull Supplier<Object> supplier) {
        Object object = this.value.getValue();
        return object == null ? this.storeDefault(supplier.get()) : object;
    }

    @Override
    public <T> T getValue(@NonNull Function<Object, T> function, T t) {
        T t2 = function.apply(this.getValue());
        return t2 == null ? this.storeDefault(t) : t2;
    }

    @Override
    public <T> T getValue(@NonNull Function<Object, T> function, @NonNull Supplier<T> supplier) {
        T t = function.apply(this.getValue());
        return t == null ? this.storeDefault(supplier.get()) : t;
    }

    @Override
    public <T> @NonNull List<T> getList(@NonNull Function<Object, T> function) {
        ImmutableList.Builder builder = ImmutableList.builder();
        ConfigValue configValue = this.value;
        if (configValue instanceof ListConfigValue) {
            for (SimpleConfigurationNode simpleConfigurationNode : configValue.iterateChildren()) {
                T t = function.apply(simpleConfigurationNode.getValue());
                if (t == null) continue;
                builder.add(t);
            }
        } else {
            T t = function.apply(configValue.getValue());
            if (t != null) {
                builder.add(t);
            }
        }
        return builder.build();
    }

    @Override
    public <T> List<T> getList(@NonNull Function<Object, T> function, List<T> list) {
        List<T> list2 = this.getList(function);
        return list2.isEmpty() ? this.storeDefault(list) : list2;
    }

    @Override
    public <T> List<T> getList(@NonNull Function<Object, T> function, @NonNull Supplier<List<T>> supplier) {
        List<T> list = this.getList(function);
        return list.isEmpty() ? this.storeDefault(supplier.get()) : list;
    }

    @Override
    public <T> List<T> getList(@NonNull TypeToken<T> typeToken, List<T> list) {
        List<T> list2 = this.getValue(new TypeToken<List<T>>(){}.where(new TypeParameter<T>(){}, typeToken), list);
        return list2 == null || list2.isEmpty() ? this.storeDefault(list) : list2;
    }

    @Override
    public <T> List<T> getList(@NonNull TypeToken<T> typeToken, @NonNull Supplier<List<T>> supplier) {
        List<T> list = this.getValue(new TypeToken<List<T>>(){}.where(new TypeParameter<T>(){}, typeToken), supplier);
        return list == null || list.isEmpty() ? this.storeDefault(supplier.get()) : list;
    }

    @Override
    public <T> T getValue(@NonNull TypeToken<T> typeToken, T t) {
        Object object = this.getValue();
        if (object == null) {
            return this.storeDefault(typeToken, t);
        }
        TypeSerializer<T> typeSerializer = this.getOptions().getSerializers().get(typeToken);
        if (typeSerializer == null) {
            if (typeToken.getRawType().isInstance(object)) {
                return typeToken.getRawType().cast(object);
            }
            return this.storeDefault(typeToken, t);
        }
        return typeSerializer.deserialize(typeToken, this);
    }

    @Override
    public <T> T getValue(@NonNull TypeToken<T> typeToken, @NonNull Supplier<T> supplier) {
        Object object = this.getValue();
        if (object == null) {
            return this.storeDefault(typeToken, supplier.get());
        }
        TypeSerializer<T> typeSerializer = this.getOptions().getSerializers().get(typeToken);
        if (typeSerializer == null) {
            if (typeToken.getRawType().isInstance(object)) {
                return typeToken.getRawType().cast(object);
            }
            return this.storeDefault(typeToken, supplier.get());
        }
        return typeSerializer.deserialize(typeToken, this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @NonNull SimpleConfigurationNode setValue(@Nullable Object object) {
        if (object instanceof ConfigurationNode) {
            ConfigurationNode configurationNode = (ConfigurationNode)object;
            if (configurationNode == this) {
                return this;
            }
            if (configurationNode.isList()) {
                this.attachIfNecessary();
                ListConfigValue listConfigValue = new ListConfigValue(this);
                ConfigurationNode configurationNode2 = configurationNode;
                synchronized (configurationNode2) {
                    listConfigValue.setValue(configurationNode.getChildrenList());
                }
                this.value = listConfigValue;
                return this;
            }
            if (configurationNode.isMap()) {
                this.attachIfNecessary();
                MapConfigValue mapConfigValue = new MapConfigValue(this);
                ConfigurationNode configurationNode3 = configurationNode;
                synchronized (configurationNode3) {
                    mapConfigValue.setValue(configurationNode.getChildrenMap());
                }
                this.value = mapConfigValue;
                return this;
            }
            object = configurationNode.getValue();
        }
        if (object == null) {
            if (this.parent == null) {
                this.clear();
            } else {
                this.parent.removeChild(this.key);
            }
            return this;
        }
        this.insertNewValue(object, false);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void insertNewValue(Object object, boolean bl) {
        this.attachIfNecessary();
        SimpleConfigurationNode simpleConfigurationNode = this;
        synchronized (simpleConfigurationNode) {
            ConfigValue configValue;
            ConfigValue configValue2 = configValue = this.value;
            if (bl && !(configValue2 instanceof NullConfigValue)) {
                return;
            }
            if (object instanceof Collection) {
                if (!(configValue instanceof ListConfigValue)) {
                    configValue = new ListConfigValue(this);
                }
            } else if (object instanceof Map) {
                if (!(configValue instanceof MapConfigValue)) {
                    configValue = new MapConfigValue(this);
                }
            } else if (!(configValue instanceof ScalarConfigValue)) {
                configValue = new ScalarConfigValue(this);
            }
            configValue.setValue(object);
            this.value = configValue;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @NonNull ConfigurationNode mergeValuesFrom(@NonNull ConfigurationNode configurationNode) {
        if (configurationNode.isMap()) {
            SimpleConfigurationNode simpleConfigurationNode = this;
            synchronized (simpleConfigurationNode) {
                ConfigValue configValue;
                ConfigValue configValue2 = configValue = this.value;
                if (!(configValue2 instanceof MapConfigValue)) {
                    if (configValue2 instanceof NullConfigValue) {
                        configValue = new MapConfigValue(this);
                    } else {
                        return this;
                    }
                }
                for (Map.Entry<Object, ? extends ConfigurationNode> entry : configurationNode.getChildrenMap().entrySet()) {
                    SimpleConfigurationNode simpleConfigurationNode2 = configValue.getChild(entry.getKey());
                    if (simpleConfigurationNode2 != null && simpleConfigurationNode2.getValue() != null && entry.getValue().getValue() == null) continue;
                    SimpleConfigurationNode simpleConfigurationNode3 = this.createNode(entry.getKey());
                    simpleConfigurationNode3.attached = true;
                    simpleConfigurationNode3.setValue(entry.getValue());
                    SimpleConfigurationNode simpleConfigurationNode4 = configValue.putChildIfAbsent(entry.getKey(), simpleConfigurationNode3);
                    if (simpleConfigurationNode4 == null) continue;
                    simpleConfigurationNode4.mergeValuesFrom(simpleConfigurationNode3);
                }
                this.value = configValue;
            }
        } else if (configurationNode.getValue() != null) {
            this.insertNewValue(configurationNode.getValue(), true);
        }
        return this;
    }

    @Override
    public @NonNull SimpleConfigurationNode getNode(@NonNull Object @NonNull ... objectArray) {
        SimpleConfigurationNode simpleConfigurationNode = this;
        for (Object object : objectArray) {
            simpleConfigurationNode = simpleConfigurationNode.getChild(object, false);
        }
        return simpleConfigurationNode;
    }

    @Override
    public @NonNull SimpleConfigurationNode getNode(@NonNull Iterable<?> iterable) {
        SimpleConfigurationNode simpleConfigurationNode = this;
        for (Object obj : iterable) {
            simpleConfigurationNode = simpleConfigurationNode.getChild(obj, false);
        }
        return simpleConfigurationNode;
    }

    @Override
    public boolean isVirtual() {
        return !this.attached;
    }

    @Override
    public @NonNull ValueType getValueType() {
        return this.value.getType();
    }

    public @NonNull List<? extends SimpleConfigurationNode> getChildrenList() {
        ConfigValue configValue = this.value;
        return configValue instanceof ListConfigValue ? ImmutableList.copyOf((Collection)((ListConfigValue)configValue).values.get()) : Collections.emptyList();
    }

    public @NonNull Map<Object, ? extends SimpleConfigurationNode> getChildrenMap() {
        ConfigValue configValue = this.value;
        return configValue instanceof MapConfigValue ? ImmutableMap.copyOf(((MapConfigValue)configValue).values) : Collections.emptyMap();
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    protected SimpleConfigurationNode getChild(Object object, boolean bl) {
        SimpleConfigurationNode simpleConfigurationNode = this.value.getChild(object);
        if (simpleConfigurationNode == null) {
            if (bl) {
                this.attachIfNecessary();
                simpleConfigurationNode = this.createNode(object);
                SimpleConfigurationNode simpleConfigurationNode2 = this.value.putChildIfAbsent(object, simpleConfigurationNode);
                if (simpleConfigurationNode2 != null) {
                    simpleConfigurationNode = simpleConfigurationNode2;
                } else {
                    this.attachChild(simpleConfigurationNode);
                }
            } else {
                simpleConfigurationNode = this.createNode(object);
            }
        }
        return simpleConfigurationNode;
    }

    @Override
    public boolean removeChild(@NonNull Object object) {
        return SimpleConfigurationNode.detachIfNonNull(this.value.putChild(object, null)) != null;
    }

    private static SimpleConfigurationNode detachIfNonNull(SimpleConfigurationNode simpleConfigurationNode) {
        if (simpleConfigurationNode != null) {
            simpleConfigurationNode.attached = false;
            simpleConfigurationNode.clear();
        }
        return simpleConfigurationNode;
    }

    @Override
    @Deprecated
    public @NonNull SimpleConfigurationNode getAppendedNode() {
        return this.getChild(-1, false);
    }

    @Override
    public @Nullable Object getKey() {
        return this.key;
    }

    @Override
    public @NonNull Object[] getPath() {
        LinkedList<Object> linkedList = new LinkedList<Object>();
        ConfigurationNode configurationNode = this;
        if (configurationNode.getParent() == null) {
            return new Object[0];
        }
        do {
            linkedList.addFirst(configurationNode.getKey());
        } while ((configurationNode = configurationNode.getParent()).getParent() != null);
        return linkedList.toArray();
    }

    @Override
    public @Nullable SimpleConfigurationNode getParent() {
        return this.parent;
    }

    @Override
    public @NonNull ConfigurationOptions getOptions() {
        return this.options;
    }

    @Override
    public @NonNull SimpleConfigurationNode copy() {
        return this.copy(null);
    }

    protected @NonNull SimpleConfigurationNode copy(@Nullable SimpleConfigurationNode simpleConfigurationNode) {
        return new SimpleConfigurationNode(simpleConfigurationNode, this);
    }

    SimpleConfigurationNode getParentEnsureAttached() {
        SimpleConfigurationNode simpleConfigurationNode = this.parent;
        if (simpleConfigurationNode.isVirtual()) {
            simpleConfigurationNode = simpleConfigurationNode.getParentEnsureAttached().attachChildIfAbsent(simpleConfigurationNode);
        }
        this.parent = simpleConfigurationNode;
        return this.parent;
    }

    protected void attachIfNecessary() {
        if (!this.attached) {
            this.getParentEnsureAttached().attachChild(this);
        }
    }

    protected SimpleConfigurationNode createNode(Object object) {
        return new SimpleConfigurationNode(object, this, this.options);
    }

    protected SimpleConfigurationNode attachChildIfAbsent(SimpleConfigurationNode simpleConfigurationNode) {
        return this.attachChild(simpleConfigurationNode, true);
    }

    private void attachChild(SimpleConfigurationNode simpleConfigurationNode) {
        this.attachChild(simpleConfigurationNode, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SimpleConfigurationNode attachChild(SimpleConfigurationNode simpleConfigurationNode, boolean bl) {
        ConfigValue configValue;
        ConfigValue configValue2;
        if (this.isVirtual()) {
            throw new IllegalStateException("This parent is not currently attached. This is an internal state violation.");
        }
        if (!simpleConfigurationNode.getParentEnsureAttached().equals(this)) {
            throw new IllegalStateException("Child " + simpleConfigurationNode + " path is not a direct parent of me (" + this + "), cannot attach");
        }
        SimpleConfigurationNode simpleConfigurationNode2 = this;
        synchronized (simpleConfigurationNode2) {
            configValue = configValue2 = this.value;
            if (!(configValue2 instanceof MapConfigValue)) {
                if (simpleConfigurationNode.key instanceof Integer) {
                    if (configValue2 instanceof NullConfigValue) {
                        configValue = new ListConfigValue(this);
                    } else if (!(configValue2 instanceof ListConfigValue)) {
                        configValue = new ListConfigValue(this, configValue2.getValue());
                    }
                } else {
                    configValue = new MapConfigValue(this);
                }
            }
            if (bl) {
                SimpleConfigurationNode simpleConfigurationNode3 = configValue.putChildIfAbsent(simpleConfigurationNode.key, simpleConfigurationNode);
                if (simpleConfigurationNode3 != null) {
                    return simpleConfigurationNode3;
                }
            } else {
                SimpleConfigurationNode.detachIfNonNull(configValue.putChild(simpleConfigurationNode.key, simpleConfigurationNode));
            }
            this.value = configValue;
        }
        if (configValue != configValue2) {
            configValue2.clear();
        }
        simpleConfigurationNode.attached = true;
        return simpleConfigurationNode;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void clear() {
        SimpleConfigurationNode simpleConfigurationNode = this;
        synchronized (simpleConfigurationNode) {
            ConfigValue configValue = this.value;
            this.value = NullConfigValue.instance();
            configValue.clear();
        }
    }

    @Override
    public <S, T, E extends Exception> T visit(ConfigurationVisitor<S, T, E> configurationVisitor, S s) {
        return this.visitInternal(configurationVisitor, s);
    }

    @Override
    public <S, T> T visit(ConfigurationVisitor.Safe<S, T> safe, S s) {
        try {
            return this.visitInternal(safe, s);
        } catch (VisitorSafeNoopException visitorSafeNoopException) {
            throw new Error("Exception was thrown on a Safe visitor");
        }
    }

    private <S, T, E extends Exception> T visitInternal(ConfigurationVisitor<S, T, E> configurationVisitor, S s) {
        configurationVisitor.beginVisit(this, s);
        if (!(this.value instanceof NullConfigValue)) {
            Object e;
            LinkedList<Object> linkedList = new LinkedList<Object>();
            linkedList.add(this);
            while ((e = linkedList.pollFirst()) != null) {
                @Nullable A a = VisitorNodeEnd.popFromVisitor(e, configurationVisitor, s);
                if (a == null) continue;
                configurationVisitor.enterNode((ConfigurationNode)a, s);
                ConfigValue configValue = ((SimpleConfigurationNode)a).value;
                if (configValue instanceof MapConfigValue) {
                    configurationVisitor.enterMappingNode((ConfigurationNode)a, s);
                    linkedList.addFirst(new VisitorNodeEnd((ConfigurationNode)a, true));
                    linkedList.addAll(0, ((MapConfigValue)configValue).values.values());
                    continue;
                }
                if (configValue instanceof ListConfigValue) {
                    configurationVisitor.enterListNode((ConfigurationNode)a, s);
                    linkedList.addFirst(new VisitorNodeEnd((ConfigurationNode)a, false));
                    linkedList.addAll(0, (Collection)((ListConfigValue)configValue).values.get());
                    continue;
                }
                if (configValue instanceof ScalarConfigValue) {
                    configurationVisitor.enterScalarNode((ConfigurationNode)a, s);
                    continue;
                }
                if (configValue instanceof NullConfigValue) continue;
                throw new IllegalStateException("Unknown value type " + configValue.getClass());
            }
        }
        return configurationVisitor.endVisit(s);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SimpleConfigurationNode)) {
            return false;
        }
        SimpleConfigurationNode simpleConfigurationNode = (SimpleConfigurationNode)object;
        return Objects.equals(this.key, simpleConfigurationNode.key) && Objects.equals(this.value, simpleConfigurationNode.value);
    }

    public int hashCode() {
        return Objects.hashCode(this.key) ^ Objects.hashCode(this.value);
    }

    public String toString() {
        return "AbstractConfigurationNode{key=" + this.key + ", value=" + this.value + '}';
    }
}

