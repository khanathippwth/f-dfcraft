/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.money.MoneyCommand;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdMoneyTransferFf
extends MoneyCommand {
    public CmdMoneyTransferFf() {
        this.aliases.add("ff");
        this.requiredArgs.add("amount");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("faction");
        this.requirements = new CommandRequirements.Builder(Permission.MONEY_F2F).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        double d = Math.abs(commandContext.argAsDouble(0, 0.0));
        Faction faction = commandContext.argAsFaction(1);
        if (faction == null) {
            return;
        }
        Faction faction2 = commandContext.argAsFaction(2);
        if (faction2 == null) {
            return;
        }
        boolean bl = Econ.transferMoney(commandContext.fPlayer, faction, faction2, d);
        if (bl && FactionsPlugin.getInstance().conf().logging().isMoneyTransactions()) {
            String string = commandContext.sender instanceof Player ? commandContext.fPlayer.getName() : commandContext.sender.getName();
            FactionsPlugin.getInstance().log(ChatColor.stripColor((String)FactionsPlugin.getInstance().txt().parse(TL.COMMAND_MONEYTRANSFERFF_TRANSFER.toString(), string, Econ.moneyString(d), faction.describeTo(null), faction2.describeTo(null))));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERFF_DESCRIPTION;
    }
}

