/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.money.MoneyCommand;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

public class CmdMoneyWithdraw
extends MoneyCommand {
    public CmdMoneyWithdraw() {
        this.aliases.add("w");
        this.aliases.add("withdraw");
        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");
        this.requirements = new CommandRequirements.Builder(Permission.MONEY_F2P).playerOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        double d = Math.abs(commandContext.argAsDouble(0, 0.0));
        Faction faction = commandContext.argAsFaction(1, commandContext.faction);
        if (faction == null) {
            return;
        }
        if (!commandContext.faction.hasAccess(commandContext.fPlayer, PermissibleActions.ECONOMY, commandContext.fPlayer.getLastStoodAt())) {
            commandContext.msg(TL.GENERIC_NOPERMISSION, PermissibleActions.ECONOMY.getShortDescription());
            return;
        }
        boolean bl = Econ.transferMoney(commandContext.fPlayer, faction, commandContext.fPlayer, d);
        if (bl && FactionsPlugin.getInstance().conf().logging().isMoneyTransactions()) {
            FactionsPlugin.getInstance().log(ChatColor.stripColor((String)FactionsPlugin.getInstance().txt().parse(TL.COMMAND_MONEYWITHDRAW_WITHDRAW.toString(), commandContext.fPlayer.getName(), Econ.moneyString(d), faction.describeTo(null))));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYWITHDRAW_DESCRIPTION;
    }
}

