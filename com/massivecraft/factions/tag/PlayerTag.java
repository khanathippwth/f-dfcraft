/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;
import java.util.function.Function;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public enum PlayerTag implements Tag
{
    GROUP("group", fPlayer -> {
        if (fPlayer.isOnline()) {
            return FactionsPlugin.getInstance().getPrimaryGroup((OfflinePlayer)fPlayer.getPlayer());
        }
        return "";
    }),
    LAST_SEEN("lastSeen", fPlayer -> {
        if (fPlayer.isOnline() && !fPlayer.isVanished()) {
            return String.valueOf(ChatColor.GREEN) + TL.COMMAND_STATUS_ONLINE.toString();
        }
        long l = Math.max(System.currentTimeMillis() - fPlayer.getLastLoginTime(), (long)FactionsPlugin.getInstance().conf().factions().other().getMinimumLastSeenTime() * 1000L);
        String string = DurationFormatUtils.formatDurationWords(l, true, true) + String.valueOf((Object)TL.COMMAND_STATUS_AGOSUFFIX);
        return l < 432000000L ? String.valueOf(ChatColor.YELLOW) + string : String.valueOf(ChatColor.RED) + string;
    }),
    PLAYER_BALANCE("balance", fPlayer -> Econ.isSetup() ? Econ.getFriendlyBalance(fPlayer) : (Tag.isMinimalShow() ? null : TL.ECON_OFF.format("balance"))),
    PLAYER_POWER("player-power", fPlayer -> String.valueOf(fPlayer.getPowerRounded())),
    PLAYER_MAXPOWER("player-maxpower", fPlayer -> String.valueOf(fPlayer.getPowerMaxRounded())),
    PLAYER_KILLS("player-kills", fPlayer -> String.valueOf(fPlayer.getKills())),
    PLAYER_DEATHS("player-deaths", fPlayer -> String.valueOf(fPlayer.getDeaths())),
    PLAYER_DISPLAYNAME("player-displayname", fPlayer -> {
        if (fPlayer.isOnline()) {
            return fPlayer.getPlayer().getDisplayName();
        }
        return fPlayer.getName();
    }),
    PLAYER_NAME("name", FPlayer::getName),
    PLAYER_ROLE("player-role-prefix", fPlayer -> fPlayer.hasFaction() ? fPlayer.getRole().getPrefix() : ""),
    TOTAL_ONLINE_VISIBLE("total-online-visible", fPlayer -> {
        if (fPlayer == null) {
            return String.valueOf(Bukkit.getOnlinePlayers().size());
        }
        int n = 0;
        Player player = fPlayer.getPlayer();
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (!player.canSee(player2)) continue;
            ++n;
        }
        return String.valueOf(n);
    });

    private final String tag;
    private final Function<FPlayer, String> function;

    public static String parse(String string, FPlayer fPlayer) {
        for (PlayerTag playerTag : PlayerTag.values()) {
            string = playerTag.replace(string, fPlayer);
        }
        return string;
    }

    private PlayerTag(String string2, Function<FPlayer, String> function) {
        this.tag = "{" + string2 + "}";
        this.function = function;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean foundInString(String string) {
        return string != null && string.contains(this.tag);
    }

    public String replace(String string, FPlayer fPlayer) {
        if (!this.foundInString(string)) {
            return string;
        }
        String string2 = this.function.apply(fPlayer);
        return string2 == null ? null : string.replace(this.tag, string2);
    }
}

