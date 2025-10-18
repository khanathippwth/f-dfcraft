/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.money.MoneyCommand;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

public class CmdMoneyTransferPf
extends MoneyCommand {
    public CmdMoneyTransferPf() {
        this.aliases.add("pf");
        this.requiredArgs.add("amount");
        this.requiredArgs.add("player");
        this.requiredArgs.add("faction");
        this.requirements = new CommandRequirements.Builder(Permission.MONEY_P2F).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        double d = Math.abs(commandContext.argAsDouble(0, 0.0));
        FPlayer fPlayer = commandContext.argAsBestFPlayerMatch(1);
        if (fPlayer == null) {
            return;
        }
        Faction faction = commandContext.argAsFaction(2);
        if (faction == null) {
            return;
        }
        boolean bl = Econ.transferMoney(commandContext.fPlayer, fPlayer, faction, d);
        if (bl && FactionsPlugin.getInstance().conf().logging().isMoneyTransactions()) {
            FactionsPlugin.getInstance().log(ChatColor.stripColor((String)FactionsPlugin.getInstance().txt().parse(TL.COMMAND_MONEYTRANSFERPF_TRANSFER.toString(), commandContext.fPlayer.getName(), Econ.moneyString(d), fPlayer.describeTo(null), faction.describeTo(null))));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERPF_DESCRIPTION;
    }
}

