/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdSaveAll
extends FCommand {
    public CmdSaveAll() {
        this.aliases.add("saveall");
        this.aliases.add("save");
        this.requirements = new CommandRequirements.Builder(Permission.SAVE).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        FPlayers.getInstance().forceSave(false);
        Factions.getInstance().forceSave(false);
        Board.getInstance().forceSave(false);
        commandContext.msg(TL.COMMAND_SAVEALL_SUCCESS, new Object[0]);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SAVEALL_DESCRIPTION;
    }
}

