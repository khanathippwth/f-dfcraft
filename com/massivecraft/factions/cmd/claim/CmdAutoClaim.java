/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdAutoClaim
extends FCommand {
    public CmdAutoClaim() {
        this.aliases.add("autoclaim");
        this.optionalArgs.put("faction", "your");
        this.requirements = new CommandRequirements.Builder(Permission.AUTOCLAIM).playerOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.argAsFaction(0, commandContext.faction);
        if (faction == null || faction == commandContext.fPlayer.getAutoClaimFor()) {
            commandContext.fPlayer.setAutoClaimFor(null);
            commandContext.msg(TL.COMMAND_AUTOCLAIM_DISABLED, new Object[0]);
            return;
        }
        if (!commandContext.fPlayer.canClaimForFaction(faction)) {
            if (commandContext.faction == faction) {
                commandContext.msg(TL.CLAIM_CANTCLAIM, faction.describeTo(commandContext.fPlayer));
            } else {
                commandContext.msg(TL.COMMAND_AUTOCLAIM_OTHERFACTION, faction.describeTo(commandContext.fPlayer));
            }
            return;
        }
        commandContext.fPlayer.setAutoClaimFor(faction);
        commandContext.msg(TL.COMMAND_AUTOCLAIM_ENABLED, faction.describeTo(commandContext.fPlayer));
        commandContext.fPlayer.attemptClaim(faction, commandContext.player.getLocation(), true);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_AUTOCLAIM_DESCRIPTION;
    }
}

