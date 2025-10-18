/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.SeeChunkUtil;
import com.massivecraft.factions.util.TL;

public class CmdSeeChunk
extends FCommand {
    private final boolean useParticles;

    public CmdSeeChunk() {
        this.aliases.add("seechunk");
        this.aliases.add("sc");
        this.requirements = new CommandRequirements.Builder(Permission.SEECHUNK).playerOnly().build();
        this.useParticles = FactionsPlugin.getInstance().conf().commands().seeChunk().isParticles();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (this.useParticles) {
            boolean bl = false;
            if (commandContext.args.isEmpty()) {
                bl = !commandContext.fPlayer.isSeeingChunk();
            } else if (commandContext.args.size() == 1) {
                bl = commandContext.argAsBool(0);
            }
            commandContext.fPlayer.setSeeingChunk(bl);
            commandContext.msg(TL.COMMAND_SEECHUNK_TOGGLE, bl ? "enabled" : "disabled");
        } else {
            SeeChunkUtil.showPillars(commandContext.player, commandContext.fPlayer, null, false);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SEECHUNK_DESCRIPTION;
    }
}

