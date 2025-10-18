/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdPower
extends FCommand {
    public CmdPower() {
        this.aliases.add("power");
        this.aliases.add("pow");
        this.optionalArgs.put("player", "you");
        this.requirements = new CommandRequirements.Builder(Permission.POWER).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        FPlayer fPlayer = commandContext.argAsBestFPlayerMatch(0, commandContext.fPlayer);
        if (fPlayer == null) {
            return;
        }
        if (fPlayer != commandContext.fPlayer && !Permission.POWER_ANY.has(commandContext.sender, true)) {
            return;
        }
        if (!commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostPower(), TL.COMMAND_POWER_TOSHOW, TL.COMMAND_POWER_FORSHOW)) {
            return;
        }
        double d = fPlayer.getPowerBoost();
        String string = d == 0.0 ? "" : (d > 0.0 ? TL.COMMAND_POWER_BONUS.toString() : TL.COMMAND_POWER_PENALTY.toString()) + d + ")";
        commandContext.msg(TL.COMMAND_POWER_POWER, fPlayer.describeTo(commandContext.fPlayer, true), fPlayer.getPowerRounded(), fPlayer.getPowerMaxRounded(), string);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POWER_DESCRIPTION;
    }
}

