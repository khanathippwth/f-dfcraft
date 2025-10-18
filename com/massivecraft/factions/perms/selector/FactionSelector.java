/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.perms.selector.AbstractSelector;
import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessage;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class FactionSelector
extends AbstractSelector {
    public static final String NAME = "faction";
    public static final PermSelector.Descriptor DESCRIPTOR = new AbstractSelector.BasicDescriptor("faction", FactionsPlugin.getInstance().tl().permissions().selectors().faction()::getDisplayName, FactionSelector::new).withInstructions(FactionsPlugin.getInstance().tl().permissions().selectors().faction()::getInstructions);
    private final int id;
    private final String lastKnown;
    private static final String delimiter = "\u00a4";

    public FactionSelector(String string) {
        super(DESCRIPTOR);
        String[] stringArray = string.split(" ");
        if (stringArray.length == 2) {
            Faction faction = Factions.getInstance().getFactionById(Integer.parseInt(stringArray[0]));
            this.id = Integer.parseInt(stringArray[0]);
            this.lastKnown = faction == null ? stringArray[1] : faction.getTag();
            return;
        }
        stringArray = string.split(delimiter);
        if (stringArray.length == 1) {
            Faction faction = Factions.getInstance().getByTag(string);
            this.id = faction.getIntId();
            this.lastKnown = faction.getTag();
        } else {
            Faction faction = Factions.getInstance().getFactionById(Integer.parseInt(stringArray[0]));
            this.id = Integer.parseInt(stringArray[0]);
            this.lastKnown = faction == null ? stringArray[1] : faction.getTag();
        }
    }

    @Override
    public boolean test(Selectable selectable, Faction faction) {
        Faction faction2 = null;
        if (selectable instanceof Faction) {
            faction2 = (Faction)selectable;
        } else if (selectable instanceof FPlayer) {
            faction2 = ((FPlayer)selectable).getFaction();
        }
        return faction2 != null && faction2.getIntId() == this.id;
    }

    @Override
    public String serializeValue() {
        Faction faction = Factions.getInstance().getFactionById(this.id);
        return this.id + delimiter + (faction == null ? this.lastKnown : faction.getTag());
    }

    @Override
    public Component displayValue(Faction faction) {
        Faction faction2 = Factions.getInstance().getFactionById(this.id);
        return faction2 == null ? MiniMessage.miniMessage().deserialize(FactionsPlugin.getInstance().tl().permissions().selectors().faction().getDisbandedValue(), (TagResolver)Placeholder.unparsed("lastknown", this.lastKnown)) : LegacyComponentSerializer.legacySection().deserialize(faction2.getTag(faction));
    }

    @Override
    public int hashCode() {
        return Objects.hash("factionselector", this.id);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FactionSelector)) return false;
        FactionSelector factionSelector = (FactionSelector)object;
        if (factionSelector.id != this.id) return false;
        return true;
    }
}

