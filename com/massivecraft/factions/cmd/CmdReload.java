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

public class CmdReload
extends FCommand {
    public CmdReload() {
        this.aliases.add("reload");
        this.requirements = new CommandRequirements.Builder(Permission.RELOAD).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        long l = System.currentTimeMillis();
        FactionsPlugin.getInstance().getConfigManager().loadConfigs();
        FactionsPlugin.getInstance().reloadConfig();
        FactionsPlugin.getInstance().loadLang();
        long l2 = System.currentTimeMillis() - l;
        commandContext.msg(TL.COMMAND_RELOAD_TIME, l2);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_RELOAD_DESCRIPTION;
    }
}

