/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.landraidcontrol.PowerControl;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;

public class CmdStatus
extends FCommand {
    public CmdStatus() {
        this.aliases.add("status");
        this.aliases.add("s");
        this.requirements = new CommandRequirements.Builder(Permission.STATUS).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (FPlayer fPlayer : commandContext.faction.getFPlayers()) {
            String string = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fPlayer.getLastLoginTime(), true, true) + String.valueOf((Object)TL.COMMAND_STATUS_AGOSUFFIX);
            String string2 = fPlayer.isOnline() ? String.valueOf(ChatColor.GREEN) + TL.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fPlayer.getLastLoginTime() < 432000000L ? String.valueOf(ChatColor.YELLOW) + string : String.valueOf(ChatColor.RED) + string);
            Object object = FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl ? String.valueOf(ChatColor.YELLOW) + String.valueOf(fPlayer.getPowerRounded()) + " / " + fPlayer.getPowerMaxRounded() + String.valueOf(ChatColor.RESET) : "n/a";
            arrayList.add(String.format(TL.COMMAND_STATUS_FORMAT.toString(), String.valueOf(ChatColor.GOLD) + fPlayer.getRole().getPrefix() + fPlayer.getName() + String.valueOf(ChatColor.RESET), object, string2).trim());
        }
        commandContext.fPlayer.sendMessage(arrayList);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STATUS_DESCRIPTION;
    }
}

