/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.TL;

public class CmdSetWarp
extends FCommand {
    public CmdSetWarp() {
        this.aliases.add("setwarp");
        this.aliases.add("sw");
        this.requiredArgs.add("warp");
        this.optionalArgs.put("password", "password");
        this.requirements = new CommandRequirements.Builder(Permission.SETWARP).memberOnly().withAction(PermissibleActions.SETWARP).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (commandContext.fPlayer.getRelationToLocation() != Relation.MEMBER) {
            commandContext.fPlayer.msg(TL.COMMAND_SETFWARP_NOTCLAIMED, new Object[0]);
            return;
        }
        int n = FactionsPlugin.getInstance().conf().commands().warp().getMaxWarps();
        if (n <= commandContext.faction.getWarps().size()) {
            commandContext.fPlayer.msg(TL.COMMAND_SETFWARP_LIMIT, n);
            return;
        }
        if (FactionsPlugin.getInstance().conf().factions().homes().isRequiredToHaveHomeBeforeSettingWarps() && !commandContext.faction.hasHome()) {
            commandContext.msg(TL.COMMAND_SETFWARP_HOMEREQUIRED, new Object[0]);
        }
        if (!this.transact(commandContext.fPlayer, commandContext)) {
            return;
        }
        String string = commandContext.argAsString(0);
        String string2 = commandContext.argAsString(1);
        LazyLocation lazyLocation = new LazyLocation(commandContext.fPlayer.getPlayer().getLocation());
        commandContext.faction.setWarp(string, lazyLocation);
        if (string2 != null) {
            commandContext.faction.setWarpPassword(string, string2);
        }
        commandContext.fPlayer.msg(TL.COMMAND_SETFWARP_SET, string, string2 != null ? string2 : "");
    }

    private boolean transact(FPlayer fPlayer, CommandContext commandContext) {
        return fPlayer.isAdminBypassing() || commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostSetWarp(), TL.COMMAND_SETFWARP_TOSET.toString(), TL.COMMAND_SETFWARP_FORSET.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETFWARP_DESCRIPTION;
    }
}

