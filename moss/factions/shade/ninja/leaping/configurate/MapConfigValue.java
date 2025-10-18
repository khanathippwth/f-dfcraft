/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import moss.factions.shade.ninja.leaping.configurate.ConfigValue;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ValueType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class MapConfigValue
extends ConfigValue {
    volatile ConcurrentMap<Object, SimpleConfigurationNode> values = this.newMap();

    public MapConfigValue(SimpleConfigurationNode simpleConfigurationNode) {
        super(simpleConfigurationNode);
    }

    @Override
    ValueType getType() {
        return ValueType.MAP;
    }

    private ConcurrentMap<Object, SimpleConfigurationNode> newMap() {
        return this.holder.getOptions().getMapFactory().create();
    }

    @Override
    public @Nullable Object getValue() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry entry : this.values.entrySet()) {
            linkedHashMap.put(entry.getKey(), ((SimpleConfigurationNode)entry.getValue()).getValue());
        }
        return linkedHashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setValue(@Nullable Object object) {
        if (object instanceof Map) {
            ConcurrentMap<Object, SimpleConfigurationNode> concurrentMap = this.newMap();
            for (Map.Entry entry : ((Map)object).entrySet()) {
                if (entry.getValue() == null) continue;
                SimpleConfigurationNode simpleConfigurationNode = this.holder.createNode(entry.getKey());
                concurrentMap.put(entry.getKey(), simpleConfigurationNode);
                simpleConfigurationNode.attached = true;
                simpleConfigurationNode.setValue(entry.getValue());
            }
            MapConfigValue mapConfigValue = this;
            synchronized (mapConfigValue) {
                Map.Entry entry;
                entry = this.values;
                this.values = concurrentMap;
                MapConfigValue.detachChildren((Map<Object, SimpleConfigurationNode>)((Object)entry));
            }
        } else {
            throw new IllegalArgumentException("Map configuration values can only be set to values of type Map");
        }
    }

    @Override
    @Nullable SimpleConfigurationNode putChild(@NonNull Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode) {
        if (simpleConfigurationNode == null) {
            return (SimpleConfigurationNode)this.values.remove(object);
        }
        return this.values.put(object, simpleConfigurationNode);
    }

    @Override
    @Nullable SimpleConfigurationNode putChildIfAbsent(@NonNull Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode) {
        if (simpleConfigurationNode == null) {
            return (SimpleConfigurationNode)this.values.remove(object);
        }
        return this.values.putIfAbsent(object, simpleConfigurationNode);
    }

    @Override
    public @Nullable SimpleConfigurationNode getChild(@Nullable Object object) {
        return (SimpleConfigurationNode)this.values.get(object);
    }

    @Override
    public @NonNull Iterable<SimpleConfigurationNode> iterateChildren() {
        return this.values.values();
    }

    @Override
    @NonNull MapConfigValue copy(@NonNull SimpleConfigurationNode simpleConfigurationNode) {
        MapConfigValue mapConfigValue = new MapConfigValue(simpleConfigurationNode);
        for (Map.Entry entry : this.values.entrySet()) {
            mapConfigValue.values.put(entry.getKey(), ((SimpleConfigurationNode)entry.getValue()).copy(simpleConfigurationNode));
        }
        return mapConfigValue;
    }

    @Override
    boolean isEmpty() {
        return this.values.isEmpty();
    }

    private static void detachChildren(Map<Object, SimpleConfigurationNode> map) {
        for (SimpleConfigurationNode simpleConfigurationNode : map.values()) {
            simpleConfigurationNode.attached = false;
            simpleConfigurationNode.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        MapConfigValue mapConfigValue = this;
        synchronized (mapConfigValue) {
            ConcurrentMap<Object, SimpleConfigurationNode> concurrentMap = this.values;
            this.values = this.newMap();
            MapConfigValue.detachChildren(concurrentMap);
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        MapConfigValue mapConfigValue = (MapConfigValue)object;
        return Objects.equals(this.values, mapConfigValue.values);
    }

    public int hashCode() {
        return this.values.hashCode();
    }

    public String toString() {
        return "MapConfigValue{values=" + this.values + '}';
    }
}

