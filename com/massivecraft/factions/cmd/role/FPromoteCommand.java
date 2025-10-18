/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd.role;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class FPromoteCommand
extends FCommand {
    public int relative = 0;

    public FPromoteCommand() {
        this.requiredArgs.add("player");
        this.requirements = new CommandRequirements.Builder(Permission.PROMOTE).memberOnly().withAction(PermissibleActions.PROMOTE).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        FPlayer fPlayer = commandContext.argAsBestFPlayerMatch(0);
        if (fPlayer == null) {
            return;
        }
        if (!fPlayer.getFaction().equals(commandContext.faction)) {
            commandContext.msg(TL.COMMAND_PROMOTE_WRONGFACTION, fPlayer.getName());
            return;
        }
        Role role = fPlayer.getRole();
        Role role2 = Role.getRelative(role, this.relative);
        if (role2 == null) {
            commandContext.msg(TL.COMMAND_PROMOTE_NOTTHATPLAYER, new Object[0]);
            return;
        }
        if (commandContext.fPlayer.getRole().value <= role2.value) {
            commandContext.msg(TL.COMMAND_PROMOTE_NOT_ALLOWED, new Object[0]);
            return;
        }
        if (role2 == Role.COLEADER && !FactionsPlugin.getInstance().conf().factions().other().isAllowMultipleColeaders() && !fPlayer.getFaction().getFPlayersWhereRole(Role.COLEADER).isEmpty()) {
            commandContext.msg(TL.COMMAND_COLEADER_ALREADY_COLEADER, new Object[0]);
            return;
        }
        String string = this.relative > 0 ? TL.COMMAND_PROMOTE_PROMOTED.toString() : TL.COMMAND_PROMOTE_DEMOTED.toString();
        fPlayer.setRole(role2);
        if (fPlayer.isOnline()) {
            fPlayer.msg(TL.COMMAND_PROMOTE_TARGET, string, role2.nicename);
        }
        commandContext.msg(TL.COMMAND_PROMOTE_SUCCESS, string, fPlayer.getName(), role2.nicename);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PROMOTE_DESCRIPTION;
    }
}

