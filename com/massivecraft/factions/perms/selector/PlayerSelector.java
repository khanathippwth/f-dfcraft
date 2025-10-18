/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.perms.selector.AbstractSelector;
import java.util.Optional;
import java.util.UUID;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessage;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PlayerSelector
extends AbstractSelector {
    public static final String NAME = "player";
    public static final PermSelector.Descriptor DESCRIPTOR = new AbstractSelector.BasicDescriptor("player", FactionsPlugin.getInstance().tl().permissions().selectors().player()::getDisplayName, PlayerSelector::new).withInstructions(FactionsPlugin.getInstance().tl().permissions().selectors().player()::getInstructions);
    private final UUID uuid;

    public PlayerSelector(String string) {
        UUID uUID;
        block3: {
            super(DESCRIPTOR);
            uUID = null;
            try {
                uUID = UUID.fromString(string);
            } catch (IllegalArgumentException illegalArgumentException) {
                Optional<FPlayer> optional = FPlayers.getInstance().getAllFPlayers().stream().filter(fPlayer -> fPlayer.getName().equalsIgnoreCase(string)).findFirst();
                if (!optional.isPresent()) break block3;
                uUID = UUID.fromString(optional.get().getId());
            }
        }
        if (uUID == null) {
            throw new IllegalArgumentException("Unknown player name " + string);
        }
        this.uuid = uUID;
    }

    public PlayerSelector(UUID uUID) {
        super(DESCRIPTOR);
        if (uUID == null) {
            throw new IllegalArgumentException("Null UUID");
        }
        this.uuid = uUID;
    }

    @Override
    public boolean test(Selectable selectable, Faction faction) {
        return selectable instanceof FPlayer && ((FPlayer)selectable).getId().equals(this.uuid.toString());
    }

    @Override
    public String serializeValue() {
        return this.uuid.toString();
    }

    @Override
    public Component displayValue(Faction faction) {
        FPlayer fPlayer = FPlayers.getInstance().getById(this.uuid.toString());
        return fPlayer == null ? MiniMessage.miniMessage().deserialize(FactionsPlugin.getInstance().tl().permissions().selectors().player().getUuidValue(), (TagResolver)Placeholder.unparsed("uuid", this.uuid.toString())) : LegacyComponentSerializer.legacySection().deserialize(String.valueOf(fPlayer.getRelationTo(faction).getColor()) + fPlayer.getName());
    }
}

