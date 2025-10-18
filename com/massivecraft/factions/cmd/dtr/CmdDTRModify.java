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

public class CmdDTRModify
extends FCommand {
    public CmdDTRModify() {
        this.aliases.add("modify");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("amount");
        this.requirements = new CommandRequirements.Builder(Permission.MODIFY_DTR).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.argAsFaction(0, null);
        if (faction == null) {
            return;
        }
        double d = commandContext.argAsDouble(1, 0.0);
        if (d == 0.0) {
            return;
        }
        DTRControl dTRControl = (DTRControl)FactionsPlugin.getInstance().getLandRaidControl();
        faction.setDTR(Math.max(Math.min(faction.getDTR() + d, dTRControl.getMaxDTR(faction)), FactionsPlugin.getInstance().conf().factions().landRaidControl().dtr().getMinDTR()));
        commandContext.msg(TL.COMMAND_DTR_MODIFY_DONE, faction.describeTo(commandContext.fPlayer, false), DTRControl.round(faction.getDTR()));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DTR_MODIFY_DESCRIPTION;
    }
}

