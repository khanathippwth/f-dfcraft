/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdUnban
extends FCommand {
    public CmdUnban() {
        this.aliases.add("unban");
        this.requiredArgs.add("target");
        this.requirements = new CommandRequirements.Builder(Permission.BAN).memberOnly().withAction(PermissibleActions.BAN).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        FPlayer fPlayer = commandContext.argAsFPlayer(0);
        if (fPlayer == null) {
            return;
        }
        if (!commandContext.faction.isBanned(fPlayer)) {
            commandContext.msg(TL.COMMAND_UNBAN_NOTBANNED, fPlayer.getName());
            return;
        }
        commandContext.faction.unban(fPlayer);
        commandContext.faction.msg(TL.COMMAND_UNBAN_UNBANNED, commandContext.fPlayer.getName(), fPlayer.getName());
        fPlayer.msg(TL.COMMAND_UNBAN_TARGET, commandContext.faction.getTag(fPlayer));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNBAN_DESCRIPTION;
    }
}

