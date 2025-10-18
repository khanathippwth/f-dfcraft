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
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

public class CmdMoneyModify
extends MoneyCommand {
    public CmdMoneyModify() {
        this.aliases.add("modify");
        this.requiredArgs.add("amount");
        this.requiredArgs.add("faction");
        this.optionalArgs.put("notify", "true/false");
        this.requirements = new CommandRequirements.Builder(Permission.MONEY_MODIFY).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (!FactionsPlugin.getInstance().conf().economy().isBankEnabled()) {
            commandContext.sendMessage(TL.ECON_DISABLED.toString());
            return;
        }
        double d = commandContext.argAsDouble(0, 0.0);
        Faction faction = commandContext.argAsFaction(1);
        if (faction == null) {
            return;
        }
        boolean bl = commandContext.argAsBool(2, false);
        if (Econ.modifyBalance(faction, d)) {
            String string = FactionsPlugin.getInstance().txt().parse(TL.COMMAND_MONEYMODIFY_MODIFIED.toString(), faction.describeTo(null), Econ.moneyString(d));
            commandContext.sendMessage(string);
            if (bl) {
                faction.msg(FactionsPlugin.getInstance().txt().parse(TL.COMMAND_MONEYMODIFY_NOTIFY.toString(), faction.describeTo(null), Econ.moneyString(d)), new Object[0]);
            }
            if (FactionsPlugin.getInstance().conf().logging().isMoneyTransactions()) {
                FactionsPlugin.getInstance().log(ChatColor.stripColor((String)string));
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYMODIFY_DESCRIPTION;
    }
}

