/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.selector.AbstractRelationSelector;

public class RelationAtLeastSelector
extends AbstractRelationSelector {
    public static final String NAME = "relation-atleast";
    public static final PermSelector.Descriptor DESCRIPTOR = new AbstractRelationSelector.RelationDescriptor("relation-atleast", FactionsPlugin.getInstance().tl().permissions().selectors().relationAtLeast()::getDisplayName, RelationAtLeastSelector::new);

    public RelationAtLeastSelector(Relation relation) {
        super(DESCRIPTOR, relation);
    }

    @Override
    public boolean test(Relation relation) {
        return relation != null && relation != Relation.MEMBER && relation.isAtLeast(this.relation);
    }
}

