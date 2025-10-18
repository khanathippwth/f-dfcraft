/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scoreboard.Scoreboard
 *  org.bukkit.scoreboard.Team
 */
package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.tag.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class FTeamWrapper {
    private static final Map<Faction, FTeamWrapper> wrappers = new HashMap<Faction, FTeamWrapper>();
    private static final List<FScoreboard> tracking = new ArrayList<FScoreboard>();
    private static int factionTeamPtr;
    private static final Set<Faction> updating;
    private final Map<FScoreboard, Team> teams = new HashMap<FScoreboard, Team>();
    private final String teamName;
    private final Faction faction;
    private final Set<String> members = new HashSet<String>();

    public static void applyUpdatesLater(final Faction faction) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        if (faction.isWilderness()) {
            return;
        }
        if (!FactionsPlugin.getInstance().conf().scoreboard().constant().isPrefixes() && !FactionsPlugin.getInstance().conf().scoreboard().constant().isSuffixes()) {
            return;
        }
        if (updating.add(faction)) {
            new BukkitRunnable(){

                public void run() {
                    updating.remove(faction);
                    FTeamWrapper.applyUpdates(faction);
                }
            }.runTask((Plugin)FactionsPlugin.getInstance());
        }
    }

    public static void applyUpdates(Faction faction) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        if (faction.isWilderness()) {
            return;
        }
        if (!FactionsPlugin.getInstance().conf().scoreboard().constant().isPrefixes() && !FactionsPlugin.getInstance().conf().scoreboard().constant().isSuffixes()) {
            return;
        }
        if (updating.contains(faction)) {
            return;
        }
        FTeamWrapper fTeamWrapper = wrappers.get(faction);
        Set<FPlayer> set = faction.getFPlayers();
        if (fTeamWrapper != null && Factions.getInstance().getFactionById(faction.getIntId()) == null) {
            fTeamWrapper.unregister();
            wrappers.remove(faction);
            return;
        }
        if (fTeamWrapper == null) {
            fTeamWrapper = new FTeamWrapper(faction);
            wrappers.put(faction, fTeamWrapper);
        }
        for (String object : fTeamWrapper.getPlayers()) {
            Player player = Bukkit.getPlayerExact((String)object);
            if (player != null && player.isOnline() && set.contains(FPlayers.getInstance().getByOfflinePlayer((OfflinePlayer)player))) continue;
            fTeamWrapper.removePlayer(object);
        }
        for (FPlayer fPlayer : set) {
            if (!fPlayer.isOnline()) continue;
            fTeamWrapper.addPlayer(fPlayer.getName());
        }
        fTeamWrapper.updatePrefixesAndSuffixes();
    }

    public static void updatePrefixes(Faction faction) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        if (!wrappers.containsKey(faction)) {
            FTeamWrapper.applyUpdates(faction);
        } else {
            wrappers.get(faction).updatePrefixesAndSuffixes();
        }
    }

    protected static void track(FScoreboard fScoreboard) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        tracking.add(fScoreboard);
        for (FTeamWrapper fTeamWrapper : wrappers.values()) {
            fTeamWrapper.add(fScoreboard);
        }
    }

    protected static void untrack(FScoreboard fScoreboard) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        tracking.remove(fScoreboard);
        for (FTeamWrapper fTeamWrapper : wrappers.values()) {
            fTeamWrapper.remove(fScoreboard);
        }
    }

    private FTeamWrapper(Faction faction) {
        this.teamName = "faction_" + factionTeamPtr++;
        this.faction = faction;
        for (FScoreboard fScoreboard : tracking) {
            this.add(fScoreboard);
        }
    }

    private void add(FScoreboard fScoreboard) {
        Scoreboard scoreboard = fScoreboard.getScoreboard();
        Team team = scoreboard.registerNewTeam(this.teamName);
        this.teams.put(fScoreboard, team);
        for (String string : this.getPlayers()) {
            team.addEntry(string);
        }
        this.updatePrefixAndSuffix(fScoreboard);
    }

    private void remove(FScoreboard fScoreboard) {
        this.teams.remove(fScoreboard).unregister();
    }

    private void updatePrefixesAndSuffixes() {
        if (FactionsPlugin.getInstance().conf().scoreboard().constant().isPrefixes() || FactionsPlugin.getInstance().conf().scoreboard().constant().isSuffixes()) {
            for (FScoreboard fScoreboard : this.teams.keySet()) {
                this.updatePrefixAndSuffix(fScoreboard);
            }
        }
    }

    private void updatePrefixAndSuffix(FScoreboard fScoreboard) {
        String string;
        Team team;
        MainConfig.Scoreboard.Constant constant = FactionsPlugin.getInstance().conf().scoreboard().constant();
        if (constant.isPrefixes()) {
            team = this.teams.get(fScoreboard);
            string = this.apply(constant.getPrefixTemplate(), fScoreboard.getFPlayer(), constant.getPrefixLength());
            if (!string.equals(team.getPrefix())) {
                team.setPrefix(string);
            }
        }
        if (constant.isSuffixes()) {
            team = this.teams.get(fScoreboard);
            string = this.apply(constant.getSuffixTemplate(), fScoreboard.getFPlayer(), constant.getSuffixLength());
            if (!string.equals(team.getSuffix())) {
                team.setSuffix(string);
            }
        }
    }

    private String apply(String string, FPlayer fPlayer, int n) {
        string = Tag.parsePlaceholders(fPlayer.getPlayer(), string);
        string = string.replace("{relationcolor}", this.faction.getRelationTo(fPlayer).getColor().toString());
        int n2 = Math.min("{faction}".length() + n - string.length(), this.faction.getTag().length());
        string = string.replace("{faction}", n2 > 0 ? this.faction.getTag().substring(0, n2) : "");
        string = Tag.parsePlain(fPlayer, string);
        if ((string = ChatColor.translateAlternateColorCodes((char)'&', (String)string)).length() > n) {
            string = string.substring(0, n);
        }
        return string;
    }

    private void addPlayer(String string) {
        if (this.members.add(string)) {
            for (Team team : this.teams.values()) {
                team.addEntry(string);
            }
        }
    }

    private void removePlayer(String string) {
        if (this.members.remove(string)) {
            for (Team team : this.teams.values()) {
                team.removeEntry(string);
            }
        }
    }

    private Set<String> getPlayers() {
        return new HashSet<String>(this.members);
    }

    private void unregister() {
        for (Team team : this.teams.values()) {
            team.unregister();
        }
        this.teams.clear();
    }

    static {
        updating = new HashSet<Faction>();
    }
}

