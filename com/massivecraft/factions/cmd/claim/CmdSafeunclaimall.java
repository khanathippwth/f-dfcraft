/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class CmdSafeunclaimall
extends FCommand {
    public CmdSafeunclaimall() {
        this.aliases.add("safeunclaimall");
        this.aliases.add("safedeclaimall");
        this.optionalArgs.put("world", "all");
        this.requirements = new CommandRequirements.Builder(Permission.MANAGE_SAFE_ZONE).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        String string = commandContext.argAsString(0);
        World world = null;
        if (string != null) {
            world = Bukkit.getWorld((String)string);
        }
        if (world == null) {
            Board.getInstance().unclaimAll(Factions.getInstance().getSafeZone());
        } else {
            Board.getInstance().unclaimAllInWorld(Factions.getInstance().getSafeZone(), world);
        }
        commandContext.msg(TL.COMMAND_SAFEUNCLAIMALL_UNCLAIMED, new Object[0]);
        if (FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
            FactionsPlugin.getInstance().log(TL.COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG.format(commandContext.sender.getName()));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SAFEUNCLAIMALL_DESCRIPTION;
    }
}

