/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  be.maximvdw.placeholderapi.PlaceholderAPI
 *  me.clip.placeholderapi.PlaceholderAPI
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.tag;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.tag.FactionTag;
import com.massivecraft.factions.tag.GeneralTag;
import com.massivecraft.factions.tag.PlayerTag;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Tag {
    public static final int ARBITRARY_LIMIT = 20000;

    public static String parsePlain(Faction faction, String line) {
        return GeneralTag.parse(FactionTag.parse(line, faction));
    }

    public static String parsePlain(FPlayer fplayer, String line) {
        return Tag.parsePlain(fplayer.getFaction(), fplayer, line);
    }

    public static String parsePlain(Faction faction, FPlayer fplayer, String line) {
        return GeneralTag.parse(PlayerTag.parse(FactionTag.parse(line, faction, fplayer), fplayer));
    }

    public static String parsePlaceholders(Player player, String line) {
        String maybe;
        if (player == null || line == null) {
            return line;
        }
        if (FactionsPlugin.getInstance().isClipPlaceholderAPIHooked() && player.isOnline()) {
            line = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player)player, (String)line);
        }
        if (FactionsPlugin.getInstance().isMVdWPlaceholderAPIHooked() && player.isOnline() && (maybe = PlaceholderAPI.replacePlaceholders((OfflinePlayer)player, (String)line)) != null) {
            line = maybe;
        }
        return line;
    }

    public static boolean isMinimalShow() {
        return FactionsPlugin.getInstance().conf().commands().show().isMinimal();
    }

    public String getTag();

    public boolean foundInString(String var1);
}

