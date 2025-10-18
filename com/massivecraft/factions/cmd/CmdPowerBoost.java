/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

public class CmdPowerBoost
extends FCommand {
    public CmdPowerBoost() {
        this.aliases.add("powerboost");
        this.requiredArgs.add("set/add");
        this.requiredArgs.add("p/f/player/faction");
        this.requiredArgs.add("name");
        this.optionalArgs.put("#/reset", "");
        this.requirements = new CommandRequirements.Builder(Permission.POWERBOOST).brigadier(Brigadier.class).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        String string;
        boolean bl = commandContext.args.size() == 3;
        String string2 = "add";
        if (!(bl || (string2 = commandContext.argAsString(0).toLowerCase()).equals("set") || string2.equals("add"))) {
            commandContext.msg(TL.COMMAND_POWERBOOST_UNKNOWN_SUBCOMMAND, string2);
            return;
        }
        int n = bl ? 0 : 1;
        String string3 = commandContext.argAsString(n).toLowerCase();
        boolean bl2 = true;
        if (string3.equals("f") || string3.equals("faction")) {
            bl2 = false;
        } else if (!string3.equals("p") && !string3.equals("player")) {
            commandContext.msg(TL.COMMAND_POWERBOOST_HELP_1, new Object[0]);
            commandContext.msg(TL.COMMAND_POWERBOOST_HELP_2, new Object[0]);
            return;
        }
        Double d = commandContext.argAsDouble(2 + n);
        if (d == null) {
            if (commandContext.argAsString(2 + n).equalsIgnoreCase("reset")) {
                d = 0.0;
            } else {
                commandContext.msg(TL.COMMAND_POWERBOOST_INVALIDNUM, new Object[0]);
                return;
            }
        }
        if (bl2) {
            var9_8 = commandContext.argAsBestFPlayerMatch(1 + n);
            if (var9_8 == null) {
                return;
            }
            if (string2.equals("add") && d != 0.0) {
                d = d + var9_8.getPowerBoost();
            }
            var9_8.setPowerBoost(d);
            string = TL.COMMAND_POWERBOOST_PLAYER.format(var9_8.getName());
        } else {
            var9_8 = commandContext.argAsFaction(1 + n);
            if (var9_8 == null) {
                return;
            }
            if (string2.equals("add") && d != 0.0) {
                d = d + var9_8.getPowerBoost();
            }
            var9_8.setPowerBoost(d);
            string = TL.COMMAND_POWERBOOST_FACTION.format(var9_8.getTag());
        }
        int n2 = (int)Math.round(d);
        commandContext.msg(TL.COMMAND_POWERBOOST_BOOST, string, n2);
        if (commandContext.player != null) {
            FactionsPlugin.getInstance().log(TL.COMMAND_POWERBOOST_BOOSTLOG.toString(), commandContext.fPlayer.getName(), string, n2);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POWERBOOST_DESCRIPTION;
    }

    protected static class Brigadier
    implements BrigadierProvider {
        protected Brigadier() {
        }

        @Override
        public ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> argumentBuilder) {
            ArgumentCommandNode argumentCommandNode = ((RequiredArgumentBuilder)RequiredArgumentBuilder.argument((String)"p/f/player/faction", (ArgumentType)StringArgumentType.word()).then(RequiredArgumentBuilder.argument((String)"name", (ArgumentType)StringArgumentType.word()).then((ArgumentBuilder)RequiredArgumentBuilder.argument((String)"amount", (ArgumentType)IntegerArgumentType.integer())))).build();
            return argumentBuilder.then((CommandNode)argumentCommandNode).then(LiteralArgumentBuilder.literal((String)"set").then((CommandNode)argumentCommandNode)).then(LiteralArgumentBuilder.literal((String)"add").then((CommandNode)argumentCommandNode));
        }
    }
}

