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

public class CmdMoneyTransferFp
extends MoneyCommand {
    public CmdMoneyTransferFp() {
        this.aliases.add("fp");
        this.requiredArgs.add("amount");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("player");
        this.requirements = new CommandRequirements.Builder(Permission.MONEY_F2P).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        double d = Math.abs(commandContext.argAsDouble(0, 0.0));
        Faction faction = commandContext.argAsFaction(1);
        if (faction == null) {
            return;
        }
        FPlayer fPlayer = commandContext.argAsBestFPlayerMatch(2);
        if (fPlayer == null) {
            return;
        }
        boolean bl = Econ.transferMoney(commandContext.fPlayer, faction, fPlayer, d);
        if (bl && FactionsPlugin.getInstance().conf().logging().isMoneyTransactions()) {
            FactionsPlugin.getInstance().log(ChatColor.stripColor((String)FactionsPlugin.getInstance().txt().parse(TL.COMMAND_MONEYTRANSFERFP_TRANSFER.toString(), commandContext.fPlayer.getName(), Econ.moneyString(d), faction.describeTo(null), fPlayer.describeTo(null))));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERFP_DESCRIPTION;
    }
}

