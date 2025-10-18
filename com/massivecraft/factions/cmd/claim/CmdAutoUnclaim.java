/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdAutoUnclaim
extends FCommand {
    public CmdAutoUnclaim() {
        this.aliases.add("autounclaim");
        this.optionalArgs.put("faction", "your");
        this.requirements = new CommandRequirements.Builder(Permission.AUTOCLAIM).playerOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.argAsFaction(0, commandContext.faction);
        if (faction == null || faction == commandContext.fPlayer.getAutoUnclaimFor()) {
            commandContext.fPlayer.setAutoUnclaimFor(null);
            commandContext.msg(TL.COMMAND_AUTOUNCLAIM_DISABLED, new Object[0]);
            return;
        }
        if (!commandContext.fPlayer.canClaimForFaction(faction)) {
            if (commandContext.faction == faction) {
                commandContext.msg(TL.CLAIM_CANTUNCLAIM, faction.describeTo(commandContext.fPlayer));
            } else {
                commandContext.msg(TL.COMMAND_AUTOUNCLAIM_OTHERFACTION, faction.describeTo(commandContext.fPlayer));
            }
            return;
        }
        commandContext.fPlayer.setAutoUnclaimFor(faction);
        commandContext.msg(TL.COMMAND_AUTOUNCLAIM_ENABLED, faction.describeTo(commandContext.fPlayer));
        commandContext.fPlayer.attemptUnclaim(faction, new FLocation(commandContext.player.getLocation()), true);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_AUTOUNCLAIM_DESCRIPTION;
    }
}

