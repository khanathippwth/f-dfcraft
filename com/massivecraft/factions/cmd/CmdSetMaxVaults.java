/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.BrigadierProvider;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.bukkit.ChatColor;

public class CmdSetMaxVaults
extends FCommand {
    public CmdSetMaxVaults() {
        this.aliases.add("setmaxvaults");
        this.aliases.add("smv");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("number");
        this.requirements = new CommandRequirements.Builder(Permission.SETMAXVAULTS).noDisableOnLock().brigadier(MaxVaultBrigadier.class).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.argAsFaction(0);
        int n = commandContext.argAsInt(1, -1);
        if (n < 0) {
            commandContext.sender.sendMessage(String.valueOf(ChatColor.RED) + "Number must be greater than 0.");
            return;
        }
        if (faction == null) {
            commandContext.sender.sendMessage(String.valueOf(ChatColor.RED) + "Couldn't find Faction: " + String.valueOf(ChatColor.YELLOW) + commandContext.argAsString(0));
            return;
        }
        faction.setMaxVaults(n);
        commandContext.sender.sendMessage(TL.COMMAND_SETMAXVAULTS_SUCCESS.format(faction.getTag(), n));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETMAXVAULTS_DESCRIPTION;
    }

    protected static class MaxVaultBrigadier
    implements BrigadierProvider {
        protected MaxVaultBrigadier() {
        }

        @Override
        public ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> argumentBuilder) {
            return argumentBuilder.then(RequiredArgumentBuilder.argument((String)"faction", (ArgumentType)StringArgumentType.word()).then((ArgumentBuilder)RequiredArgumentBuilder.argument((String)"number", (ArgumentType)IntegerArgumentType.integer((int)0, (int)99))));
        }
    }
}

