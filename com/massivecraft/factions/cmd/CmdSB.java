/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdSB
extends FCommand {
    public CmdSB() {
        this.aliases.add("sb");
        this.aliases.add("scoreboard");
        this.requirements = new CommandRequirements.Builder(Permission.SCOREBOARD).playerOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        boolean bl = !commandContext.fPlayer.showScoreboard();
        FScoreboard fScoreboard = FScoreboard.get(commandContext.fPlayer);
        if (fScoreboard == null) {
            commandContext.player.sendMessage(TL.COMMAND_TOGGLESB_DISABLED.toString());
        } else {
            commandContext.player.sendMessage(TL.TOGGLE_SB.toString().replace("{value}", String.valueOf(bl)));
            fScoreboard.setSidebarVisibility(bl);
        }
        commandContext.fPlayer.setShowScoreboard(bl);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SCOREBOARD_DESCRIPTION;
    }
}

