/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.selector.AbstractRelationSelector;

public class RelationSingleSelector
extends AbstractRelationSelector {
    public static final String NAME = "relation-single";
    public static final PermSelector.Descriptor DESCRIPTOR = new AbstractRelationSelector.RelationDescriptor("relation-single", FactionsPlugin.getInstance().tl().permissions().selectors().relationSingle()::getDisplayName, RelationSingleSelector::new);

    public RelationSingleSelector(Relation relation) {
        super(DESCRIPTOR, relation);
    }

    @Override
    public boolean test(Relation relation) {
        return relation == this.relation;
    }
}

