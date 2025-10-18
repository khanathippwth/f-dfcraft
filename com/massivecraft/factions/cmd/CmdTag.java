/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class CmdTag
extends FCommand {
    public CmdTag() {
        this.aliases.add("tag");
        this.aliases.add("rename");
        this.requiredArgs.add("faction tag");
        this.requirements = new CommandRequirements.Builder(Permission.TAG).memberOnly().withRole(Role.MODERATOR).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        String string = commandContext.argAsString(0);
        if (Factions.getInstance().isTagTaken(string) && !MiscUtil.getComparisonString(string).equals(commandContext.faction.getComparisonTag())) {
            commandContext.msg(TL.COMMAND_TAG_TAKEN, new Object[0]);
            return;
        }
        ArrayList<String> arrayList = MiscUtil.validateTag(string);
        if (!arrayList.isEmpty()) {
            commandContext.sendMessage(arrayList);
            return;
        }
        if (!commandContext.canAffordCommand(FactionsPlugin.getInstance().conf().economy().getCostTag(), TL.COMMAND_TAG_TOCHANGE.toString())) {
            return;
        }
        FactionRenameEvent factionRenameEvent = new FactionRenameEvent(commandContext.fPlayer, string);
        Bukkit.getServer().getPluginManager().callEvent((Event)factionRenameEvent);
        if (factionRenameEvent.isCancelled()) {
            return;
        }
        if (!commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostTag(), TL.COMMAND_TAG_TOCHANGE, TL.COMMAND_TAG_FORCHANGE)) {
            return;
        }
        String string2 = commandContext.faction.getTag();
        commandContext.faction.setTag(string);
        for (FPlayer fPlayer : FPlayers.getInstance().getOnlinePlayers()) {
            if (fPlayer.getFactionIntId() == commandContext.faction.getIntId()) {
                fPlayer.msg(TL.COMMAND_TAG_FACTION, commandContext.fPlayer.describeTo(commandContext.faction, true), commandContext.faction.getTag(commandContext.faction));
                continue;
            }
            if (!FactionsPlugin.getInstance().conf().factions().chat().isBroadcastTagChanges()) continue;
            Faction faction = fPlayer.getFaction();
            fPlayer.msg(TL.COMMAND_TAG_CHANGED, commandContext.fPlayer.getColorStringTo(faction) + string2, commandContext.faction.getTag(faction));
        }
        FTeamWrapper.updatePrefixes(commandContext.faction);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TAG_DESCRIPTION;
    }
}

