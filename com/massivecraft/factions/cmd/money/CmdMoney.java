/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.money.CmdMoneyBalance;
import com.massivecraft.factions.cmd.money.CmdMoneyDeposit;
import com.massivecraft.factions.cmd.money.CmdMoneyModify;
import com.massivecraft.factions.cmd.money.CmdMoneyTransferFf;
import com.massivecraft.factions.cmd.money.CmdMoneyTransferFp;
import com.massivecraft.factions.cmd.money.CmdMoneyTransferPf;
import com.massivecraft.factions.cmd.money.CmdMoneyWithdraw;
import com.massivecraft.factions.cmd.money.MoneyCommand;
import com.massivecraft.factions.util.TL;

public class CmdMoney
extends MoneyCommand {
    public CmdMoney() {
        this.aliases.add("money");
        this.helpLong.add(this.plugin.txt().parseTags(TL.COMMAND_MONEY_LONG.toString()));
        this.addSubCommand(new CmdMoneyBalance());
        this.addSubCommand(new CmdMoneyDeposit());
        this.addSubCommand(new CmdMoneyWithdraw());
        this.addSubCommand(new CmdMoneyTransferFf());
        this.addSubCommand(new CmdMoneyTransferFp());
        this.addSubCommand(new CmdMoneyTransferPf());
        this.addSubCommand(new CmdMoneyModify());
    }

    @Override
    public void perform(CommandContext commandContext) {
        commandContext.commandChain.add(this);
        FCmdRoot.getInstance().cmdAutoHelp.execute(commandContext);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEY_DESCRIPTION;
    }
}

