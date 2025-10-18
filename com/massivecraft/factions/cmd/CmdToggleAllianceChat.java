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

public class CmdToggleAllianceChat
extends FCommand {
    public CmdToggleAllianceChat() {
        this.aliases.add("tac");
        this.aliases.add("togglealliancechat");
        this.aliases.add("ac");
        this.requirements = new CommandRequirements.Builder(Permission.TOGGLE_ALLIANCE_CHAT).memberOnly().noDisableOnLock().build();
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION;
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (!FactionsPlugin.getInstance().conf().factions().chat().isFactionOnlyChat()) {
            commandContext.msg(TL.COMMAND_CHAT_DISABLED.toString(), new Object[0]);
            return;
        }
        boolean bl = commandContext.fPlayer.isIgnoreAllianceChat();
        commandContext.msg(bl ? TL.COMMAND_TOGGLEALLIANCECHAT_UNIGNORE : TL.COMMAND_TOGGLEALLIANCECHAT_IGNORE, new Object[0]);
        commandContext.fPlayer.setIgnoreAllianceChat(!bl);
    }
}

