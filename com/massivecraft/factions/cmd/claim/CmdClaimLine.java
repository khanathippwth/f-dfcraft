/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 */
package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class CmdClaimLine
extends FCommand {
    public static final BlockFace[] axis = new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

    public CmdClaimLine() {
        this.aliases.add("claimline");
        this.aliases.add("cl");
        this.optionalArgs.put("amount", "1");
        this.optionalArgs.put("direction", "facing");
        this.optionalArgs.put("faction", "you");
        this.requirements = new CommandRequirements.Builder(Permission.CLAIM_LINE).playerOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        BlockFace blockFace;
        Integer n = commandContext.argAsInt(0, 1);
        if (n > FactionsPlugin.getInstance().conf().factions().claims().getLineClaimLimit()) {
            commandContext.msg(TL.COMMAND_CLAIMLINE_ABOVEMAX, FactionsPlugin.getInstance().conf().factions().claims().getLineClaimLimit());
            return;
        }
        String string = commandContext.argAsString(1);
        if (string == null) {
            blockFace = axis[Math.round(commandContext.player.getLocation().getYaw() / 90.0f) & 3];
        } else if (string.equalsIgnoreCase("north")) {
            blockFace = BlockFace.NORTH;
        } else if (string.equalsIgnoreCase("east")) {
            blockFace = BlockFace.EAST;
        } else if (string.equalsIgnoreCase("south")) {
            blockFace = BlockFace.SOUTH;
        } else if (string.equalsIgnoreCase("west")) {
            blockFace = BlockFace.WEST;
        } else {
            commandContext.fPlayer.msg(TL.COMMAND_CLAIMLINE_NOTVALID, string);
            return;
        }
        Faction faction = commandContext.argAsFaction(2, commandContext.faction);
        Location location = commandContext.player.getLocation();
        for (int i = 0; i < n; ++i) {
            commandContext.fPlayer.attemptClaim(faction, location, true);
            location = location.add((double)(blockFace.getModX() * 16), 0.0, (double)(blockFace.getModZ() * 16));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CLAIMLINE_DESCRIPTION;
    }
}

