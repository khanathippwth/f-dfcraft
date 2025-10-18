/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import moss.factions.shade.ninja.leaping.configurate.ConfigValue;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.Types;
import moss.factions.shade.ninja.leaping.configurate.ValueType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class ListConfigValue
extends ConfigValue {
    final AtomicReference<List<SimpleConfigurationNode>> values = new AtomicReference(new ArrayList());

    ListConfigValue(SimpleConfigurationNode simpleConfigurationNode) {
        super(simpleConfigurationNode);
    }

    @Override
    ValueType getType() {
        return ValueType.LIST;
    }

    ListConfigValue(SimpleConfigurationNode simpleConfigurationNode, Object object) {
        super(simpleConfigurationNode);
        SimpleConfigurationNode simpleConfigurationNode2 = simpleConfigurationNode.createNode(0);
        simpleConfigurationNode2.attached = true;
        simpleConfigurationNode2.setValue(object);
        this.values.get().add(simpleConfigurationNode2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable Object getValue() {
        List<SimpleConfigurationNode> list;
        List<SimpleConfigurationNode> list2 = list = this.values.get();
        synchronized (list2) {
            ArrayList<Object> arrayList = new ArrayList<Object>(list.size());
            for (SimpleConfigurationNode simpleConfigurationNode : list) {
                arrayList.add(simpleConfigurationNode.getValue());
            }
            return arrayList;
        }
    }

    @Override
    public void setValue(@Nullable Object set) {
        if (!(set instanceof Collection)) {
            set = Collections.singleton(set);
        }
        Collection collection = set;
        ArrayList<SimpleConfigurationNode> arrayList = new ArrayList<SimpleConfigurationNode>(collection.size());
        int n = 0;
        for (Object e : collection) {
            if (e == null) continue;
            SimpleConfigurationNode simpleConfigurationNode = this.holder.createNode(n);
            arrayList.add(n, simpleConfigurationNode);
            simpleConfigurationNode.attached = true;
            simpleConfigurationNode.setValue(e);
            ++n;
        }
        ListConfigValue.detachNodes(this.values.getAndSet(arrayList));
    }

    @Override
    public @Nullable SimpleConfigurationNode putChild(@NonNull Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode) {
        return this.putChild((Integer)object, simpleConfigurationNode, false);
    }

    @Override
    @Nullable SimpleConfigurationNode putChildIfAbsent(@NonNull Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode) {
        return this.putChild((Integer)object, simpleConfigurationNode, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SimpleConfigurationNode putChild(int n, @Nullable SimpleConfigurationNode simpleConfigurationNode, boolean bl) {
        List<SimpleConfigurationNode> list;
        SimpleConfigurationNode simpleConfigurationNode2 = null;
        do {
            List<SimpleConfigurationNode> list2 = list = this.values.get();
            synchronized (list2) {
                if (simpleConfigurationNode == null) {
                    if (n >= 0 && n < list.size()) {
                        simpleConfigurationNode2 = list.remove(n);
                        for (int i = n; i < list.size(); ++i) {
                            list.get((int)i).key = n;
                        }
                    }
                } else if (n >= 0 && n < list.size()) {
                    if (bl) {
                        return list.get(n);
                    }
                    simpleConfigurationNode2 = list.set(n, simpleConfigurationNode);
                } else if (n == -1) {
                    list.add(simpleConfigurationNode);
                    simpleConfigurationNode.key = list.lastIndexOf(simpleConfigurationNode);
                } else {
                    list.add(n, simpleConfigurationNode);
                }
            }
        } while (!this.values.compareAndSet(list, list));
        return simpleConfigurationNode2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable SimpleConfigurationNode getChild(@Nullable Object object) {
        List<SimpleConfigurationNode> list;
        Integer n = Types.asInt(object);
        if (n == null || n < 0) {
            return null;
        }
        List<SimpleConfigurationNode> list2 = list = this.values.get();
        synchronized (list2) {
            if (n >= list.size()) {
                return null;
            }
            return list.get(n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @NonNull Iterable<SimpleConfigurationNode> iterateChildren() {
        List<SimpleConfigurationNode> list;
        List<SimpleConfigurationNode> list2 = list = this.values.get();
        synchronized (list2) {
            return ImmutableList.copyOf(list);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @NonNull ListConfigValue copy(@NonNull SimpleConfigurationNode simpleConfigurationNode) {
        ArrayList<SimpleConfigurationNode> arrayList;
        List<SimpleConfigurationNode> list;
        ListConfigValue listConfigValue = new ListConfigValue(simpleConfigurationNode);
        List<SimpleConfigurationNode> list2 = list = this.values.get();
        synchronized (list2) {
            arrayList = new ArrayList<SimpleConfigurationNode>(list.size());
            for (SimpleConfigurationNode simpleConfigurationNode2 : list) {
                arrayList.add(simpleConfigurationNode2.copy(simpleConfigurationNode));
            }
        }
        listConfigValue.values.set(arrayList);
        return listConfigValue;
    }

    @Override
    boolean isEmpty() {
        return this.values.get().isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void detachNodes(List<SimpleConfigurationNode> list) {
        List<SimpleConfigurationNode> list2 = list;
        synchronized (list2) {
            for (SimpleConfigurationNode simpleConfigurationNode : list) {
                simpleConfigurationNode.attached = false;
                simpleConfigurationNode.clear();
            }
        }
    }

    @Override
    public void clear() {
        List list = this.values.getAndSet(new ArrayList());
        ListConfigValue.detachNodes(list);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ListConfigValue listConfigValue = (ListConfigValue)object;
        return Objects.equals(this.values.get(), listConfigValue.values.get());
    }

    public int hashCode() {
        return this.values.get().hashCode();
    }

    public String toString() {
        return "ListConfigValue{values=" + this.values.get().toString() + '}';
    }
}

