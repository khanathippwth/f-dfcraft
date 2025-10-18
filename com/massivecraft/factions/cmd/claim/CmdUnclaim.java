/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.util.TL;

public class CmdUnclaim
extends FCommand {
    public CmdUnclaim() {
        this.aliases.add("unclaim");
        this.aliases.add("declaim");
        this.optionalArgs.put("radius", "1");
        this.optionalArgs.put("faction", "your");
        this.requirements = new CommandRequirements.Builder(Permission.UNCLAIM).playerOnly().build();
    }

    @Override
    public void perform(final CommandContext commandContext) {
        int n = commandContext.argAsInt(0, 1);
        final Faction faction = commandContext.argAsFaction(1, commandContext.faction);
        if (n < 1) {
            commandContext.msg(TL.COMMAND_CLAIM_INVALIDRADIUS, new Object[0]);
            return;
        }
        if (n < 2) {
            commandContext.fPlayer.attemptUnclaim(faction, new FLocation(commandContext.player), true);
        } else {
            if (!Permission.CLAIM_RADIUS.has(commandContext.sender, false)) {
                commandContext.msg(TL.COMMAND_CLAIM_DENIED, new Object[0]);
                return;
            }
            new SpiralTask(this, new FLocation(commandContext.player), n){
                private int failCount;
                private final int limit;
                {
                    super(fLocation, n);
                    this.failCount = 0;
                    this.limit = FactionsPlugin.getInstance().conf().factions().claims().getRadiusClaimFailureLimit() - 1;
                }

                @Override
                public boolean work() {
                    boolean bl = commandContext.fPlayer.attemptUnclaim(faction, this.currentFLocation(), true);
                    if (bl) {
                        this.failCount = 0;
                    } else if (this.failCount++ >= this.limit) {
                        this.stop();
                        return false;
                    }
                    return true;
                }
            };
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIM_DESCRIPTION;
    }
}

