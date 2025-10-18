/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.event.Event
 */
package com.massivecraft.factions.cmd.relations;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.event.FactionRelationWishEvent;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

public abstract class FRelationCommand
extends FCommand {
    public final Relation targetRelation;

    public FRelationCommand(Relation relation, String string) {
        this.targetRelation = relation;
        this.aliases.add(string);
        this.requiredArgs.add("faction tag");
        this.requirements = new CommandRequirements.Builder(Permission.RELATION).memberOnly().withRole(Role.MODERATOR).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.argAsFaction(0);
        if (faction == null) {
            return;
        }
        if (!faction.isNormal()) {
            commandContext.msg(TL.COMMAND_RELATIONS_ALLTHENOPE, new Object[0]);
            return;
        }
        if (faction == commandContext.faction) {
            commandContext.msg(TL.COMMAND_RELATIONS_MORENOPE, new Object[0]);
            return;
        }
        if (commandContext.faction.getRelationWish(faction) == this.targetRelation) {
            commandContext.msg(TL.COMMAND_RELATIONS_ALREADYINRELATIONSHIP, faction.getTag());
            return;
        }
        if (this.hasMaxRelations(faction, this.targetRelation, commandContext)) {
            return;
        }
        Relation relation = commandContext.faction.getRelationTo(faction, true);
        FactionRelationWishEvent factionRelationWishEvent = new FactionRelationWishEvent(commandContext.fPlayer, commandContext.faction, faction, relation, this.targetRelation);
        Bukkit.getPluginManager().callEvent((Event)factionRelationWishEvent);
        if (factionRelationWishEvent.isCancelled()) {
            return;
        }
        if (!commandContext.payForCommand(this.targetRelation.getRelationCost(), TL.COMMAND_RELATIONS_TOMARRY, TL.COMMAND_RELATIONS_FORMARRY)) {
            return;
        }
        commandContext.faction.setRelationWish(faction, this.targetRelation);
        Relation relation2 = commandContext.faction.getRelationTo(faction, true);
        ChatColor chatColor = relation2.getColor();
        if (this.targetRelation.value == relation2.value) {
            FactionRelationEvent factionRelationEvent = new FactionRelationEvent(commandContext.faction, faction, relation, relation2);
            Bukkit.getServer().getPluginManager().callEvent((Event)factionRelationEvent);
            faction.msg(TL.COMMAND_RELATIONS_MUTUAL, String.valueOf(chatColor) + this.targetRelation.getTranslation(), String.valueOf(chatColor) + commandContext.faction.getTag());
            commandContext.faction.msg(TL.COMMAND_RELATIONS_MUTUAL, String.valueOf(chatColor) + this.targetRelation.getTranslation(), String.valueOf(chatColor) + faction.getTag());
        } else {
            faction.msg(TL.COMMAND_RELATIONS_PROPOSAL_1, String.valueOf(chatColor) + commandContext.faction.getTag(), String.valueOf(this.targetRelation.getColor()) + this.targetRelation.getTranslation());
            faction.msg(TL.COMMAND_RELATIONS_PROPOSAL_2, FactionsPlugin.getInstance().conf().getCommandBase().getFirst(), this.targetRelation, commandContext.faction.getTag());
            commandContext.faction.msg(TL.COMMAND_RELATIONS_PROPOSAL_SENT, String.valueOf(chatColor) + faction.getTag(), String.valueOf(this.targetRelation.getColor()) + String.valueOf(this.targetRelation));
        }
        if (!this.targetRelation.isNeutral() && faction.isPeaceful()) {
            faction.msg(TL.COMMAND_RELATIONS_PEACEFUL, new Object[0]);
            commandContext.faction.msg(TL.COMMAND_RELATIONS_PEACEFULOTHER, new Object[0]);
        }
        if (!this.targetRelation.isNeutral() && commandContext.faction.isPeaceful()) {
            faction.msg(TL.COMMAND_RELATIONS_PEACEFULOTHER, new Object[0]);
            commandContext.faction.msg(TL.COMMAND_RELATIONS_PEACEFUL, new Object[0]);
        }
        FTeamWrapper.updatePrefixes(commandContext.faction);
        FTeamWrapper.updatePrefixes(faction);
    }

    private boolean hasMaxRelations(Faction faction, Relation relation, CommandContext commandContext) {
        int n;
        if (FactionsPlugin.getInstance().conf().factions().maxRelations().isEnabled() && (n = relation.getMax()) != -1) {
            if (commandContext.faction.getRelationCount(relation) >= n) {
                commandContext.msg(TL.COMMAND_RELATIONS_EXCEEDS_ME, n, relation.getPluralTranslation());
                return true;
            }
            if (faction.getRelationCount(relation) >= n) {
                commandContext.msg(TL.COMMAND_RELATIONS_EXCEEDS_THEY, n, relation.getPluralTranslation());
                return true;
            }
        }
        return false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_RELATIONS_DESCRIPTION;
    }
}

