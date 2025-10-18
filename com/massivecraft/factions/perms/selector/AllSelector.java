/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.perms.selector.AbstractSelector;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessage;

public class AllSelector
extends AbstractSelector {
    public static final String NAME = "all";
    public static final PermSelector.Descriptor DESCRIPTOR = new AbstractSelector.BasicDescriptor("all", FactionsPlugin.getInstance().tl().permissions().selectors().all()::getDisplayName, string -> new AllSelector()).acceptEmpty();

    public AllSelector() {
        super(DESCRIPTOR);
    }

    @Override
    public boolean test(Selectable selectable, Faction faction) {
        return true;
    }

    @Override
    public String serializeValue() {
        return NAME;
    }

    @Override
    public Component displayValue(Faction faction) {
        return MiniMessage.miniMessage().deserialize(FactionsPlugin.getInstance().tl().permissions().selectors().all().getDisplayValue());
    }
}

