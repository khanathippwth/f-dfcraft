/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;

public abstract class MoneyCommand
extends FCommand {
    @Override
    public boolean isEnabled(CommandContext commandContext) {
        if (!super.isEnabled(commandContext)) {
            return false;
        }
        if (!FactionsPlugin.getInstance().conf().economy().isEnabled()) {
            commandContext.msg("<b>Faction economy features are disabled on this server.", new Object[0]);
            return false;
        }
        if (!FactionsPlugin.getInstance().conf().economy().isBankEnabled()) {
            commandContext.msg("<b>The faction bank system is disabled on this server.", new Object[0]);
            return false;
        }
        return true;
    }
}

