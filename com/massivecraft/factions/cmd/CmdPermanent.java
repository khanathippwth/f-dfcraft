/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdPermanent
extends FCommand {
    public CmdPermanent() {
        this.aliases.add("permanent");
        this.requiredArgs.add("faction");
        this.requirements = new CommandRequirements.Builder(Permission.SET_PERMANENT).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        String string;
        Faction faction = commandContext.argAsFaction(0);
        if (faction == null) {
            return;
        }
        if (faction.isPermanent()) {
            string = TL.COMMAND_PERMANENT_REVOKE.toString();
            faction.setPermanent(false);
        } else {
            string = TL.COMMAND_PERMANENT_GRANT.toString();
            faction.setPermanent(true);
        }
        FactionsPlugin.getInstance().log((commandContext.fPlayer == null ? "A server admin" : commandContext.fPlayer.getName()) + " " + string + " the faction \"" + faction.getTag() + "\".");
        for (FPlayer fPlayer : FPlayers.getInstance().getOnlinePlayers()) {
            String string2;
            String string3 = string2 = commandContext.fPlayer == null ? TL.GENERIC_SERVERADMIN.toString() : commandContext.fPlayer.describeTo(fPlayer, true);
            if (fPlayer.getFaction() == faction) {
                fPlayer.msg(TL.COMMAND_PERMANENT_YOURS, string2, string);
                continue;
            }
            fPlayer.msg(TL.COMMAND_PERMANENT_OTHER, string2, string, faction.getTag(fPlayer));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PERMANENT_DESCRIPTION;
    }
}

