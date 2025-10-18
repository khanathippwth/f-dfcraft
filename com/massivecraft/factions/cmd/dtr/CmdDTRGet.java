/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.dtr;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdDTRGet
extends FCommand {
    public CmdDTRGet() {
        this.aliases.add("get");
        this.optionalArgs.put("faction", "yours");
        this.requirements = new CommandRequirements.Builder(Permission.DTR).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.argAsFaction(0, commandContext.faction);
        if (faction == null) {
            return;
        }
        if (faction != commandContext.faction && !Permission.DTR_ANY.has(commandContext.sender, true)) {
            return;
        }
        if (!commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostDTR(), TL.COMMAND_DTR_TOSHOW, TL.COMMAND_DTR_FORSHOW)) {
            return;
        }
        DTRControl dTRControl = (DTRControl)FactionsPlugin.getInstance().getLandRaidControl();
        commandContext.msg(TL.COMMAND_DTR_DTR, faction.describeTo(commandContext.fPlayer, false), DTRControl.round(faction.getDTR()), DTRControl.round(dTRControl.getMaxDTR(faction)));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DTR_DESCRIPTION;
    }
}

