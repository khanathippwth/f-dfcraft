/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;

public class CmdClaimAt
extends FCommand {
    public CmdClaimAt() {
        this.aliases.add("claimat");
        this.requiredArgs.add("world");
        this.requiredArgs.add("x");
        this.requiredArgs.add("z");
        this.requirements = new CommandRequirements.Builder(Permission.CLAIMAT).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        int n = commandContext.argAsInt(1);
        int n2 = commandContext.argAsInt(2);
        FLocation fLocation = new FLocation(commandContext.argAsString(0), n, n2);
        commandContext.fPlayer.attemptClaim(commandContext.faction, fLocation, true);
    }
}

