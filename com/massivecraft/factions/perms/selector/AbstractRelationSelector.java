/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.data.MemoryFaction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.perms.selector.AbstractSelector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public abstract class AbstractRelationSelector
extends AbstractSelector {
    protected final Relation relation;

    public AbstractRelationSelector(PermSelector.Descriptor descriptor, Relation relation) {
        super(descriptor);
        this.relation = relation;
    }

    public Relation getRelation() {
        return this.relation;
    }

    @Override
    public String serializeValue() {
        return this.relation.name();
    }

    @Override
    public Component displayValue(Faction faction) {
        return LegacyComponentSerializer.legacySection().deserialize(String.valueOf(this.relation.getColor()) + this.relation.getTranslation());
    }

    @Override
    public final boolean test(Selectable selectable, Faction faction) {
        Relation relation = null;
        if (selectable instanceof RelationParticipator) {
            relation = ((RelationParticipator)((Object)selectable)).getRelationTo(faction);
        } else if (selectable instanceof Relation) {
            relation = (Relation)selectable;
        }
        return this.test(relation);
    }

    public abstract boolean test(Relation var1);

    public static class RelationDescriptor
    extends AbstractSelector.BasicDescriptor {
        private List<PermSelector> relationSelectors;
        private final Function<Relation, PermSelector> function;

        public RelationDescriptor(String string2, Supplier<String> supplier, Function<Relation, PermSelector> function) {
            super(string2, supplier, (String string) -> (PermSelector)function.apply(Relation.fromString(string)));
            this.function = function;
        }

        @Override
        public Map<String, String> getOptions(Faction faction) {
            ArrayList<PermSelector> arrayList = new ArrayList<PermSelector>(this.relationSelectors == null ? (this.relationSelectors = Arrays.stream(Relation.values()).map(this.function).collect(Collectors.toList())) : this.relationSelectors);
            arrayList.removeAll(((MemoryFaction)faction).getPermissions().keySet());
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
            for (PermSelector permSelector : arrayList) {
                linkedHashMap.put(permSelector.serialize(), ((AbstractRelationSelector)permSelector).getRelation().getTranslation());
            }
            return linkedHashMap;
        }
    }
}

