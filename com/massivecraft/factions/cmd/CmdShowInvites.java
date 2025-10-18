/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ComponentBuilder;
import moss.factions.shade.net.kyori.adventure.text.TextComponent;
import moss.factions.shade.net.kyori.adventure.text.event.ClickEvent;
import moss.factions.shade.net.kyori.adventure.text.event.HoverEventSource;
import moss.factions.shade.net.kyori.adventure.text.format.NamedTextColor;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CmdShowInvites
extends FCommand {
    public CmdShowInvites() {
        this.aliases.add("showinvites");
        this.requirements = new CommandRequirements.Builder(Permission.SHOW_INVITES).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacySection();
        Component component = legacyComponentSerializer.deserialize(TL.COMMAND_SHOWINVITES_PENDING.toString()).color(NamedTextColor.GOLD);
        for (String string : commandContext.faction.getInvites()) {
            FPlayer fPlayer = FPlayers.getInstance().getById(string);
            String string2 = fPlayer != null ? fPlayer.getName() : string;
            component = component.append((ComponentBuilder<?, ?>)((TextComponent.Builder)((TextComponent.Builder)Component.text().color(NamedTextColor.WHITE)).content(string2 + " ").hoverEvent((HoverEventSource)legacyComponentSerializer.deserialize(TL.COMMAND_SHOWINVITES_CLICKTOREVOKE.format(string2)).asHoverEvent())).clickEvent(ClickEvent.runCommand("/" + (String)FactionsPlugin.getInstance().conf().getCommandBase().getFirst() + " deinvite " + string2)));
        }
        FactionsPlugin.getInstance().getAdventure().player(commandContext.player).sendMessage(component);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOWINVITES_DESCRIPTION;
    }
}

