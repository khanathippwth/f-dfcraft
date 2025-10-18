/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.dtr;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdDTRResetAll
extends FCommand {
    public CmdDTRResetAll() {
        this.aliases.add("resetall");
        this.requirements = new CommandRequirements.Builder(Permission.MODIFY_DTR).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (commandContext.fPlayer != null) {
            return;
        }
        DTRControl dTRControl = (DTRControl)FactionsPlugin.getInstance().getLandRaidControl();
        Factions.getInstance().getAllFactions().forEach(faction -> faction.setDTR(dTRControl.getMaxDTR((Faction)faction)));
        commandContext.msg(TL.COMMAND_DTR_MODIFY_DONE, "EVERYONE", "MAX");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DTR_MODIFY_DESCRIPTION;
    }
}

