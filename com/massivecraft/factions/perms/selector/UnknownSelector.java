/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.perms.selector.AbstractSelector;
import moss.factions.shade.net.kyori.adventure.text.Component;

public class UnknownSelector
extends AbstractSelector {
    private final String text;

    public UnknownSelector(String string) {
        super(new AbstractSelector.BasicDescriptor("unknown", FactionsPlugin.getInstance().tl().permissions().selectors().unknown()::getDisplayName, UnknownSelector::new));
        this.text = string;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public boolean test(Selectable selectable, Faction faction) {
        return false;
    }

    @Override
    public String serialize() {
        return this.text;
    }

    @Override
    public String serializeValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component displayValue(Faction faction) {
        return Component.text(this.text);
    }
}

