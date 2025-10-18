/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.drtshock.playervaults.PlayerVaults
 *  com.drtshock.playervaults.vaultmanagement.VaultManager
 *  com.drtshock.playervaults.vaultmanagement.VaultOperations
 *  com.drtshock.playervaults.vaultmanagement.VaultViewInfo
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.drtshock.playervaults.PlayerVaults;
import com.drtshock.playervaults.vaultmanagement.VaultManager;
import com.drtshock.playervaults.vaultmanagement.VaultOperations;
import com.drtshock.playervaults.vaultmanagement.VaultViewInfo;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.BrigadierProvider;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CmdVault
extends FCommand {
    public CmdVault() {
        this.aliases.add("vault");
        this.optionalArgs.put("number", "number");
        this.requirements = new CommandRequirements.Builder(Permission.VAULT).memberOnly().noDisableOnLock().brigadier(VaultBrigadier.class).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        int n = commandContext.argAsInt(0, 0);
        Player player = commandContext.player;
        if (PlayerVaults.getInstance().getInVault().containsKey(player.getUniqueId().toString())) {
            return;
        }
        int n2 = commandContext.faction.getMaxVaults();
        if (n > n2) {
            player.sendMessage(TL.COMMAND_VAULT_TOOHIGH.format(n, n2));
            return;
        }
        String string = String.format(FactionsPlugin.getInstance().conf().playerVaults().getVaultPrefix(), "" + commandContext.faction.getIntId());
        if (n < 1) {
            YamlConfiguration yamlConfiguration = VaultManager.getInstance().getPlayerVaultFile(string, false);
            if (yamlConfiguration == null) {
                PlayerVaults.getInstance().getTL().vaultDoesNotExist().title().send(commandContext.sender);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (String string2 : yamlConfiguration.getKeys(false)) {
                    stringBuilder.append(string2.replace("vault", "")).append(" ");
                }
                PlayerVaults.getInstance().getTL().existingVaults().title().with("player", commandContext.fPlayer.getTag()).with("vault", stringBuilder.toString().trim()).send(commandContext.sender);
            }
            return;
        }
        if (VaultOperations.openOtherVault((Player)player, (String)string, (String)String.valueOf(n))) {
            PlayerVaults.getInstance().getInVault().put(player.getUniqueId().toString(), new VaultViewInfo(string, n));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VAULT_DESCRIPTION;
    }

    protected static class VaultBrigadier
    implements BrigadierProvider {
        protected VaultBrigadier() {
        }

        @Override
        public ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> argumentBuilder) {
            return argumentBuilder.then((ArgumentBuilder)RequiredArgumentBuilder.argument((String)"number", (ArgumentType)IntegerArgumentType.integer((int)0, (int)99)));
        }
    }
}

