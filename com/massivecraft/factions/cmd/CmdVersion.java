/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdVersion
extends FCommand {
    public CmdVersion() {
        this.aliases.add("version");
        this.aliases.add("ver");
        this.requirements = new CommandRequirements.Builder(Permission.VERSION).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        commandContext.msg(TL.COMMAND_VERSION_VERSION, FactionsPlugin.getInstance().getDescription().getFullName());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VERSION_DESCRIPTION;
    }
}

