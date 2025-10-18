/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.money.MoneyCommand;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdMoneyBalance
extends MoneyCommand {
    public CmdMoneyBalance() {
        this.aliases.add("b");
        this.aliases.add("balance");
        this.optionalArgs.put("faction", "yours");
        this.requirements = new CommandRequirements.Builder(Permission.MONEY_BALANCE).build();
        this.setHelpShort(TL.COMMAND_MONEYBALANCE_SHORT.toString());
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.faction;
        if (commandContext.argIsSet(0)) {
            faction = commandContext.argAsFaction(0);
        }
        if (faction == null) {
            return;
        }
        if (faction != commandContext.faction && !Permission.MONEY_BALANCE_ANY.has(commandContext.sender, true)) {
            return;
        }
        if (commandContext.fPlayer != null) {
            Econ.sendBalanceInfo(commandContext.fPlayer, (EconomyParticipator)faction);
        } else {
            Econ.sendBalanceInfo(commandContext.sender, faction);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYBALANCE_DESCRIPTION;
    }
}

