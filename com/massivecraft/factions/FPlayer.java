/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.util.WarmUpUtil;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface FPlayer
extends EconomyParticipator,
Selectable {
    public void login();

    public void logout();

    public Faction getFaction();

    @Deprecated
    public String getFactionId();

    public int getFactionIntId();

    public boolean hasFaction();

    public void setFaction(Faction var1);

    public boolean willAutoLeave();

    public void setAutoLeave(boolean var1);

    public long getLastFrostwalkerMessage();

    public void setLastFrostwalkerMessage();

    public void setMonitorJoins(boolean var1);

    public boolean isMonitoringJoins();

    public Role getRole();

    public void setRole(Role var1);

    public boolean shouldTakeFallDamage();

    public void setTakeFallDamage(boolean var1);

    public double getPowerBoost();

    public void setPowerBoost(double var1);

    public Faction getAutoClaimFor();

    public void setAutoClaimFor(Faction var1);

    public Faction getAutoUnclaimFor();

    public void setAutoUnclaimFor(Faction var1);

    @Deprecated
    public boolean isAutoSafeClaimEnabled();

    @Deprecated
    public void setIsAutoSafeClaimEnabled(boolean var1);

    @Deprecated
    public boolean isAutoWarClaimEnabled();

    @Deprecated
    public void setIsAutoWarClaimEnabled(boolean var1);

    public boolean isAdminBypassing();

    public boolean isVanished();

    public void setIsAdminBypassing(boolean var1);

    public void setChatMode(ChatMode var1);

    public ChatMode getChatMode();

    public void setIgnoreAllianceChat(boolean var1);

    public boolean isIgnoreAllianceChat();

    public void setSpyingChat(boolean var1);

    public boolean isSpyingChat();

    public boolean showScoreboard();

    public void setShowScoreboard(boolean var1);

    public void resetFactionData(boolean var1);

    public void resetFactionData();

    public long getLastLoginTime();

    public void setLastLoginTime(long var1);

    public boolean isMapAutoUpdating();

    public void setMapAutoUpdating(boolean var1);

    public boolean hasLoginPvpDisabled();

    public FLocation getLastStoodAt();

    public void setLastStoodAt(FLocation var1);

    public String getTitle();

    public void setTitle(CommandSender var1, String var2);

    public String getName();

    public String getTag();

    public String getNameAndSomething(String var1);

    public String getNameAndTitle();

    public String getNameAndTag();

    public String getNameAndTitle(Faction var1);

    public String getNameAndTitle(FPlayer var1);

    public String getChatTag();

    public String getChatTag(Faction var1);

    public String getChatTag(FPlayer var1);

    public int getKills();

    public int getDeaths();

    public Relation getRelationToLocation();

    public void heal(int var1);

    public double getPower();

    public void alterPower(double var1);

    public double getPowerMax();

    public double getPowerMin();

    public int getPowerRounded();

    public int getPowerMaxRounded();

    public int getPowerMinRounded();

    public void updatePower();

    public void losePowerFromBeingOffline();

    public void onDeath();

    public boolean isInOwnTerritory();

    public boolean isInOthersTerritory();

    public boolean isInAllyTerritory();

    public boolean isInNeutralTerritory();

    public boolean isInEnemyTerritory();

    public void sendFactionHereMessage(Faction var1);

    public void leave(boolean var1);

    public boolean canClaimForFaction(Faction var1);

    @Deprecated
    public boolean canClaimForFactionAtLocation(Faction var1, Location var2, boolean var3);

    public boolean canClaimForFactionAtLocation(Faction var1, FLocation var2, boolean var3);

    public boolean attemptClaim(Faction var1, Location var2, boolean var3);

    public boolean attemptClaim(Faction var1, FLocation var2, boolean var3);

    public boolean attemptUnclaim(Faction var1, FLocation var2, boolean var3);

    public String getId();

    public Player getPlayer();

    public boolean isOnline();

    public void sendMessage(String var1);

    public void sendMessage(List<String> var1);

    public int getMapHeight();

    public void setMapHeight(int var1);

    public boolean isOnlineAndVisibleTo(Player var1);

    public void remove();

    public boolean isOffline();

    public void setId(String var1);

    public void flightCheck();

    public boolean isFlying();

    public void setFlying(boolean var1);

    public void setFlying(boolean var1, boolean var2);

    public boolean isAutoFlying();

    public void setAutoFlying(boolean var1);

    public boolean canFlyAtLocation();

    public boolean canFlyAtLocation(FLocation var1);

    @Deprecated
    default public boolean canFlyInFactionTerritory(Faction faction) {
        return false;
    }

    public boolean isSeeingChunk();

    public void setSeeingChunk(boolean var1);

    public boolean getFlyTrailsState();

    public void setFlyTrailsState(boolean var1);

    public String getFlyTrailsEffect();

    public void setFlyTrailsEffect(String var1);

    public boolean isWarmingUp();

    public WarmUpUtil.Warmup getWarmupType();

    public void addWarmup(WarmUpUtil.Warmup var1, int var2);

    public void stopWarmup();

    public void clearWarmup();

    public void setOfflinePlayer(Player var1);
}

