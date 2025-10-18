/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdPermanentPower
extends FCommand {
    public CmdPermanentPower() {
        this.aliases.add("permanentpower");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("power");
        this.requirements = new CommandRequirements.Builder(Permission.SET_PERMANENTPOWER).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.argAsFaction(0);
        if (faction == null) {
            return;
        }
        Integer n = commandContext.argAsInt(1);
        faction.setPermanentPower(n);
        String string = TL.COMMAND_PERMANENTPOWER_REVOKE.toString();
        if (faction.hasPermanentPower()) {
            string = TL.COMMAND_PERMANENTPOWER_GRANT.toString();
        }
        commandContext.msg(TL.COMMAND_PERMANENTPOWER_SUCCESS, string, faction.describeTo(commandContext.fPlayer));
        for (FPlayer fPlayer : faction.getFPlayersWhereOnline(true)) {
            if (fPlayer == commandContext.fPlayer) continue;
            String string2 = commandContext.fPlayer == null ? TL.GENERIC_SERVERADMIN.toString() : commandContext.fPlayer.describeTo(fPlayer, true);
            fPlayer.msg(TL.COMMAND_PERMANENTPOWER_FACTION, string2, string);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PERMANENTPOWER_DESCRIPTION;
    }
}

