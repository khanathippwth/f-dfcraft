/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;

public class CmdTitle
extends FCommand {
    public CmdTitle() {
        this.aliases.add("title");
        this.requiredArgs.add("player");
        this.optionalArgs.put("title", "title");
        this.requirements = new CommandRequirements.Builder(Permission.TITLE).memberOnly().withRole(Role.MODERATOR).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        FPlayer fPlayer = commandContext.argAsBestFPlayerMatch(0);
        if (fPlayer == null) {
            return;
        }
        commandContext.args.removeFirst();
        String string = TextUtil.implode(commandContext.args, " ");
        string = string.replaceAll(",", "");
        if (!commandContext.canIAdministerYou(commandContext.fPlayer, fPlayer)) {
            return;
        }
        if (!commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostTitle(), TL.COMMAND_TITLE_TOCHANGE, TL.COMMAND_TITLE_FORCHANGE)) {
            return;
        }
        fPlayer.setTitle(commandContext.sender, string);
        commandContext.faction.msg(TL.COMMAND_TITLE_CHANGED, commandContext.fPlayer.describeTo(commandContext.faction, true), fPlayer.describeTo(commandContext.faction, true));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TITLE_DESCRIPTION;
    }
}

