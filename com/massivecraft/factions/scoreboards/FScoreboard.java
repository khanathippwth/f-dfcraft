/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Scoreboard
 */
package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.scoreboards.BufferedObjective;
import com.massivecraft.factions.scoreboards.FSidebarProvider;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class FScoreboard {
    private static final Map<FPlayer, FScoreboard> fscoreboards = new HashMap<FPlayer, FScoreboard>();
    private final Scoreboard scoreboard;
    private final FPlayer fplayer;
    private final BufferedObjective bufferedObjective;
    private FSidebarProvider defaultProvider;
    private FSidebarProvider temporaryProvider;
    private boolean removed = false;

    public static boolean isSupportedByServer() {
        return Bukkit.getScoreboardManager() != null;
    }

    public static void init(FPlayer fPlayer) {
        FScoreboard fScoreboard = new FScoreboard(fPlayer);
        fscoreboards.put(fPlayer, fScoreboard);
        if (fPlayer.hasFaction()) {
            FTeamWrapper.applyUpdates(fPlayer.getFaction());
        }
        FTeamWrapper.track(fScoreboard);
    }

    public static void remove(FPlayer fPlayer, Player player) {
        FScoreboard fScoreboard = fscoreboards.remove(fPlayer);
        if (fScoreboard != null) {
            if (fScoreboard.scoreboard == player.getScoreboard()) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
            fScoreboard.removed = true;
            FTeamWrapper.untrack(fScoreboard);
        }
    }

    public static FScoreboard get(FPlayer fPlayer) {
        return fscoreboards.get(fPlayer);
    }

    public static FScoreboard get(Player player) {
        return fscoreboards.get(FPlayers.getInstance().getByPlayer(player));
    }

    private FScoreboard(FPlayer fPlayer) {
        this.fplayer = fPlayer;
        if (FScoreboard.isSupportedByServer()) {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.bufferedObjective = new BufferedObjective(this.scoreboard);
            fPlayer.getPlayer().setScoreboard(this.scoreboard);
        } else {
            this.scoreboard = null;
            this.bufferedObjective = null;
        }
    }

    protected FPlayer getFPlayer() {
        return this.fplayer;
    }

    protected Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setSidebarVisibility(boolean bl) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        this.bufferedObjective.setDisplaySlot((DisplaySlot)(bl ? DisplaySlot.SIDEBAR : null));
    }

    public void setDefaultSidebar(final FSidebarProvider fSidebarProvider) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        this.defaultProvider = fSidebarProvider;
        if (this.temporaryProvider == null) {
            this.updateObjective();
        }
        new BukkitRunnable(){

            public void run() {
                if (FScoreboard.this.removed || fSidebarProvider != FScoreboard.this.defaultProvider) {
                    this.cancel();
                    return;
                }
                if (FScoreboard.this.temporaryProvider == null) {
                    FScoreboard.this.updateObjective();
                }
            }
        }.runTaskTimer((Plugin)FactionsPlugin.getInstance(), 20L, 20L);
    }

    public void setTemporarySidebar(final FSidebarProvider fSidebarProvider) {
        if (!FScoreboard.isSupportedByServer()) {
            return;
        }
        this.temporaryProvider = fSidebarProvider;
        this.updateObjective();
        new BukkitRunnable(){

            public void run() {
                if (FScoreboard.this.removed) {
                    return;
                }
                if (FScoreboard.this.temporaryProvider == fSidebarProvider) {
                    FScoreboard.this.temporaryProvider = null;
                    FScoreboard.this.updateObjective();
                }
            }
        }.runTaskLater((Plugin)FactionsPlugin.getInstance(), (long)FactionsPlugin.getInstance().conf().scoreboard().info().getExpiration() * 20L);
    }

    private void updateObjective() {
        FSidebarProvider fSidebarProvider;
        FSidebarProvider fSidebarProvider2 = fSidebarProvider = this.temporaryProvider != null ? this.temporaryProvider : this.defaultProvider;
        if (fSidebarProvider == null) {
            this.bufferedObjective.hide();
        } else {
            this.bufferedObjective.setTitle(fSidebarProvider.getTitle(this.fplayer));
            this.bufferedObjective.setAllLines(fSidebarProvider.getLines(this.fplayer));
            this.bufferedObjective.flip();
        }
    }
}

