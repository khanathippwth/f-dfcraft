/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.util.LazyLocation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Faction
extends EconomyParticipator,
Selectable {
    public Map<String, List<String>> getAnnouncements();

    public Map<String, LazyLocation> getWarps();

    public LazyLocation getWarp(String var1);

    public void setWarp(String var1, LazyLocation var2);

    public boolean isWarp(String var1);

    public boolean hasWarpPassword(String var1);

    public boolean isWarpPassword(String var1, String var2);

    public void setWarpPassword(String var1, String var2);

    public boolean removeWarp(String var1);

    public void clearWarps();

    public int getMaxVaults();

    public void setMaxVaults(int var1);

    public void addAnnouncement(FPlayer var1, String var2);

    public void sendUnreadAnnouncements(FPlayer var1);

    public void removeAnnouncements(FPlayer var1);

    public Set<String> getInvites();

    @Deprecated
    public String getId();

    public int getIntId();

    public void invite(FPlayer var1);

    public void deinvite(FPlayer var1);

    public boolean isInvited(FPlayer var1);

    public void ban(FPlayer var1, FPlayer var2);

    public void unban(FPlayer var1);

    public boolean isBanned(FPlayer var1);

    public Set<BanInfo> getBannedPlayers();

    public boolean getOpen();

    public void setOpen(boolean var1);

    public boolean isPeaceful();

    public void setPeaceful(boolean var1);

    public void setPeacefulExplosionsEnabled(boolean var1);

    public boolean getPeacefulExplosionsEnabled();

    public boolean noExplosionsInTerritory();

    public boolean isPermanent();

    public void setPermanent(boolean var1);

    public String getTag();

    public String getTag(String var1);

    public String getTag(Faction var1);

    public String getTag(FPlayer var1);

    public void setTag(String var1);

    public String getComparisonTag();

    public String getDescription();

    public void setDescription(String var1);

    public String getLink();

    public void setLink(String var1);

    public void setHome(Location var1);

    public void delHome();

    public boolean hasHome();

    public Location getHome();

    public long getFoundedDate();

    public void setFoundedDate(long var1);

    public void confirmValidHome();

    public boolean noPvPInTerritory();

    public boolean noMonstersInTerritory();

    public boolean isNormal();

    @Deprecated
    default public boolean isNone() {
        return this.isWilderness();
    }

    public boolean isWilderness();

    public boolean isSafeZone();

    public boolean isWarZone();

    public boolean isPlayerFreeType();

    public void setLastDeath(long var1);

    public int getKills();

    public int getDeaths();

    public boolean hasAccess(Selectable var1, PermissibleAction var2, FLocation var3);

    public int getLandRounded();

    public int getLandRoundedInWorld(String var1);

    public int getTNTBank();

    public void setTNTBank(int var1);

    public Relation getRelationWish(Faction var1);

    public void setRelationWish(Faction var1, Relation var2);

    public int getRelationCount(Relation var1);

    public double getDTR();

    public double getDTRWithoutUpdate();

    public void setDTR(double var1);

    public long getLastDTRUpdateTime();

    public long getFrozenDTRUntilTime();

    public void setFrozenDTR(long var1);

    public boolean isFrozenDTR();

    public double getPower();

    public double getPowerMax();

    public int getPowerRounded();

    public int getPowerMaxRounded();

    public Integer getPermanentPower();

    public void setPermanentPower(Integer var1);

    public boolean hasPermanentPower();

    public double getPowerBoost();

    public void setPowerBoost(double var1);

    public boolean hasLandInflation();

    public boolean isPowerFrozen();

    public void refreshFPlayers();

    public boolean addFPlayer(FPlayer var1);

    public boolean removeFPlayer(FPlayer var1);

    public int getSize();

    public Set<FPlayer> getFPlayers();

    public Set<FPlayer> getFPlayersWhereOnline(boolean var1);

    public Set<FPlayer> getFPlayersWhereOnline(boolean var1, FPlayer var2);

    public FPlayer getFPlayerAdmin();

    public List<FPlayer> getFPlayersWhereRole(Role var1);

    public List<Player> getOnlinePlayers();

    public boolean hasPlayersOnline();

    public void memberLoggedOff();

    public void promoteNewLeader();

    public Role getDefaultRole();

    public void setDefaultRole(Role var1);

    public void sendMessage(String var1);

    public void sendMessage(List<String> var1);

    public Map<FLocation, Set<String>> getClaimOwnership();

    public void clearAllClaimOwnership();

    public void clearClaimOwnership(FLocation var1);

    public void clearClaimOwnership(FPlayer var1);

    public int getCountOfClaimsWithOwners();

    public boolean doesLocationHaveOwnersSet(FLocation var1);

    public boolean isPlayerInOwnerList(FPlayer var1, FLocation var2);

    public void setPlayerAsOwner(FPlayer var1, FLocation var2);

    public void removePlayerAsOwner(FPlayer var1, FLocation var2);

    public Set<String> getOwnerList(FLocation var1);

    public String getOwnerListString(FLocation var1);

    public boolean playerHasOwnershipRights(FPlayer var1, FLocation var2);

    public void remove();

    public Set<FLocation> getAllClaims();
}

