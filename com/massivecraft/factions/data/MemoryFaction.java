/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package com.massivecraft.factions.data;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.PermissionsConfig;
import com.massivecraft.factions.data.MemoryBoard;
import com.massivecraft.factions.event.FactionAutoDisbandEvent;
import com.massivecraft.factions.event.FactionNewAdminEvent;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.LWC;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.landraidcontrol.LandRaidControl;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class MemoryFaction
implements Faction,
EconomyParticipator {
    protected int id = Integer.MIN_VALUE;
    protected boolean peacefulExplosionsEnabled;
    protected boolean permanent;
    protected String tag;
    protected String description;
    protected String link;
    protected boolean open;
    protected boolean peaceful;
    protected Integer permanentPower;
    protected LazyLocation home;
    protected long foundedDate;
    protected transient long lastPlayerLoggedOffTime;
    protected double powerBoost;
    protected Map<String, Relation> relationWish = new HashMap<String, Relation>();
    protected Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<FLocation, Set<String>>();
    protected transient Set<FPlayer> fplayers = new HashSet<FPlayer>();
    protected Set<String> invites = new HashSet<String>();
    protected HashMap<String, List<String>> announcements = new HashMap();
    protected ConcurrentHashMap<String, LazyLocation> warps = new ConcurrentHashMap();
    protected ConcurrentHashMap<String, String> warpPasswords = new ConcurrentHashMap();
    private long lastDeath;
    protected int maxVaults;
    protected Role defaultRole;
    protected LinkedHashMap<PermSelector, Map<String, Boolean>> permissions = new LinkedHashMap();
    protected Set<BanInfo> bans = new HashSet<BanInfo>();
    protected double dtr;
    protected long lastDTRUpdateTime;
    protected long frozenDTRUntilTime;
    protected int tntBank;
    protected transient OfflinePlayer offlinePlayer;

    public HashMap<String, List<String>> getAnnouncements() {
        return this.announcements;
    }

    @Override
    public void addAnnouncement(FPlayer fPlayer, String string) {
        ArrayList<String> arrayList = this.announcements.containsKey(fPlayer.getId()) ? this.announcements.get(fPlayer.getId()) : new ArrayList<String>();
        arrayList.add(string);
        this.announcements.put(fPlayer.getId(), arrayList);
    }

    @Override
    public void sendUnreadAnnouncements(FPlayer fPlayer) {
        if (!this.announcements.containsKey(fPlayer.getId())) {
            return;
        }
        fPlayer.msg(TL.FACTIONS_ANNOUNCEMENT_TOP, new Object[0]);
        for (String string : this.announcements.get(fPlayer.getPlayer().getUniqueId().toString())) {
            fPlayer.sendMessage(string);
        }
        fPlayer.msg(TL.FACTIONS_ANNOUNCEMENT_BOTTOM, new Object[0]);
        this.announcements.remove(fPlayer.getId());
    }

    @Override
    public void removeAnnouncements(FPlayer fPlayer) {
        this.announcements.remove(fPlayer.getId());
    }

    public ConcurrentHashMap<String, LazyLocation> getWarps() {
        return this.warps;
    }

    @Override
    public LazyLocation getWarp(String string) {
        return this.warps.get(string);
    }

    @Override
    public void setWarp(String string, LazyLocation lazyLocation) {
        this.warps.put(string, lazyLocation);
    }

    @Override
    public boolean isWarp(String string) {
        return this.warps.containsKey(string);
    }

    @Override
    public boolean removeWarp(String string) {
        this.warpPasswords.remove(string);
        return this.warps.remove(string) != null;
    }

    @Override
    public boolean isWarpPassword(String string, String string2) {
        return this.hasWarpPassword(string) && this.warpPasswords.get(string.toLowerCase()).equals(string2);
    }

    @Override
    public boolean hasWarpPassword(String string) {
        return this.warpPasswords.containsKey(string.toLowerCase());
    }

    @Override
    public void setWarpPassword(String string, String string2) {
        this.warpPasswords.put(string.toLowerCase(), string2);
    }

    @Override
    public void clearWarps() {
        this.warps.clear();
    }

    @Override
    public int getMaxVaults() {
        return this.maxVaults;
    }

    @Override
    public void setMaxVaults(int n) {
        this.maxVaults = n;
    }

    @Override
    public Set<String> getInvites() {
        return this.invites;
    }

    @Override
    public String getId() {
        return String.valueOf(this.id);
    }

    @Override
    public int getIntId() {
        return this.id;
    }

    public void setId(String string) {
        this.setId(Integer.parseInt(string));
    }

    public void setId(int n) {
        this.id = n;
        this.offlinePlayer = null;
    }

    @Override
    public void invite(FPlayer fPlayer) {
        this.invites.add(fPlayer.getId());
    }

    @Override
    public void deinvite(FPlayer fPlayer) {
        this.invites.remove(fPlayer.getId());
    }

    @Override
    public boolean isInvited(FPlayer fPlayer) {
        return this.invites.contains(fPlayer.getId());
    }

    @Override
    public void ban(FPlayer fPlayer, FPlayer fPlayer2) {
        BanInfo banInfo = new BanInfo(fPlayer2.getId(), fPlayer.getId(), System.currentTimeMillis());
        this.bans.add(banInfo);
    }

    @Override
    public void unban(FPlayer fPlayer) {
        this.bans.removeIf(banInfo -> banInfo.getBanned().equalsIgnoreCase(fPlayer.getId()));
    }

    @Override
    public boolean isBanned(FPlayer fPlayer) {
        for (BanInfo banInfo : this.bans) {
            if (!banInfo.getBanned().equalsIgnoreCase(fPlayer.getId())) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<BanInfo> getBannedPlayers() {
        return this.bans;
    }

    @Override
    public boolean getOpen() {
        return this.open;
    }

    @Override
    public void setOpen(boolean bl) {
        this.open = bl;
    }

    @Override
    public boolean isPeaceful() {
        return this.peaceful;
    }

    @Override
    public void setPeaceful(boolean bl) {
        this.peaceful = bl;
    }

    @Override
    public void setPeacefulExplosionsEnabled(boolean bl) {
        this.peacefulExplosionsEnabled = bl;
    }

    @Override
    public boolean getPeacefulExplosionsEnabled() {
        return this.peacefulExplosionsEnabled;
    }

    @Override
    public boolean noExplosionsInTerritory() {
        return this.peaceful && !this.peacefulExplosionsEnabled;
    }

    @Override
    public boolean isPermanent() {
        return this.permanent || !this.isNormal();
    }

    @Override
    public void setPermanent(boolean bl) {
        this.permanent = bl;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public String getTag(String string) {
        return string + this.tag;
    }

    @Override
    public String getTag(Faction faction) {
        if (faction == null) {
            return this.getTag();
        }
        return this.getTag(this.getColorStringTo(faction));
    }

    @Override
    public String getTag(FPlayer fPlayer) {
        if (fPlayer == null) {
            return this.getTag();
        }
        return this.getTag(this.getColorStringTo(fPlayer));
    }

    @Override
    public void setTag(String string) {
        if (FactionsPlugin.getInstance().conf().factions().other().isTagForceUpperCase()) {
            string = string.toUpperCase();
        }
        this.tag = string;
    }

    @Override
    public String getComparisonTag() {
        return MiscUtil.getComparisonString(this.tag);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String string) {
        this.description = string;
    }

    @Override
    public String getLink() {
        if (this.link == null) {
            this.link = FactionsPlugin.getInstance().conf().commands().link().getDefaultURL();
        }
        return this.link;
    }

    @Override
    public void setLink(String string) {
        this.link = string;
    }

    @Override
    public void setHome(Location location) {
        this.home = new LazyLocation(location);
    }

    @Override
    public void delHome() {
        this.home = null;
    }

    @Override
    public boolean hasHome() {
        return this.getHome() != null;
    }

    @Override
    public Location getHome() {
        this.confirmValidHome();
        return this.home != null ? this.home.getLocation() : null;
    }

    @Override
    public long getFoundedDate() {
        if (this.foundedDate == 0L) {
            this.setFoundedDate(System.currentTimeMillis());
        }
        return this.foundedDate;
    }

    @Override
    public void setFoundedDate(long l) {
        this.foundedDate = l;
    }

    @Override
    public void confirmValidHome() {
        if (!FactionsPlugin.getInstance().conf().factions().homes().isMustBeInClaimedTerritory() || this.home == null || this.home.getLocation() != null && Board.getInstance().getFactionAt(new FLocation(this.home.getLocation())) == this) {
            return;
        }
        this.msg(TL.FACTION_HOME_UNSET, new Object[0]);
        this.home = null;
    }

    @Override
    public String getAccountId() {
        return "faction-" + this.getIntId();
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        if (this.offlinePlayer == null) {
            this.offlinePlayer = FactionsPlugin.getInstance().getFactionOfflinePlayer(this.getAccountId());
        }
        return this.offlinePlayer;
    }

    @Override
    public void setLastDeath(long l) {
        this.lastDeath = l;
    }

    public long getLastDeath() {
        return this.lastDeath;
    }

    @Override
    public int getKills() {
        int n = 0;
        for (FPlayer fPlayer : this.getFPlayers()) {
            n += fPlayer.getKills();
        }
        return n;
    }

    @Override
    public int getDeaths() {
        int n = 0;
        for (FPlayer fPlayer : this.getFPlayers()) {
            n += fPlayer.getDeaths();
        }
        return n;
    }

    @Override
    public boolean hasAccess(Selectable selectable, PermissibleAction permissibleAction, FLocation fLocation) {
        Boolean bl;
        if (selectable == null || permissibleAction == null) {
            return false;
        }
        if (selectable == Role.ADMIN || selectable instanceof FPlayer && ((FPlayer)selectable).getFaction() == this && ((FPlayer)selectable).getRole() == Role.ADMIN) {
            return true;
        }
        PermissionsConfig permissionsConfig = FactionsPlugin.getInstance().getConfigManager().getPermissionsConfig();
        List list = permissionsConfig.getOverridePermissionsOrder().stream().filter(permSelector -> permSelector.test(selectable, this)).toList();
        for (PermSelector object : list) {
            bl = permissionsConfig.getOverridePermissions().get(object).get(permissibleAction.getName());
            if (bl == null) continue;
            return bl;
        }
        if (fLocation != null) {
            // empty if block
        }
        for (Map.Entry entry : this.permissions.entrySet()) {
            if (!((PermSelector)entry.getKey()).test(selectable, this) || (bl = (Boolean)((Map)entry.getValue()).get(permissibleAction.getName())) == null) continue;
            return bl;
        }
        return false;
    }

    public LinkedHashMap<PermSelector, Map<String, Boolean>> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(LinkedHashMap<PermSelector, Map<String, Boolean>> linkedHashMap) {
        this.permissions = linkedHashMap;
    }

    public void checkPerms() {
        if (this.permissions == null || this.permissions.isEmpty()) {
            this.resetPerms();
        }
    }

    public void resetPerms() {
        if (this.permissions == null) {
            this.permissions = new LinkedHashMap();
        } else {
            this.permissions.clear();
        }
        PermissionsConfig permissionsConfig = FactionsPlugin.getInstance().getConfigManager().getPermissionsConfig();
        for (PermSelector permSelector : permissionsConfig.getDefaultPermissionsOrder()) {
            LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>(permissionsConfig.getDefaultPermissions().get(permSelector));
            this.permissions.put(permSelector, linkedHashMap);
        }
    }

    @Override
    public Role getDefaultRole() {
        return this.defaultRole;
    }

    @Override
    public void setDefaultRole(Role role) {
        this.defaultRole = role;
    }

    protected MemoryFaction() {
    }

    public MemoryFaction(int n) {
        this.id = n;
        this.open = FactionsPlugin.getInstance().conf().factions().other().isNewFactionsDefaultOpen();
        this.tag = "???";
        this.description = TL.GENERIC_DEFAULTDESCRIPTION.toString();
        this.lastPlayerLoggedOffTime = 0L;
        this.peaceful = FactionsPlugin.getInstance().conf().factions().other().isNewFactionsDefaultPeaceful();
        this.peacefulExplosionsEnabled = false;
        this.permanent = false;
        this.powerBoost = 0.0;
        this.foundedDate = System.currentTimeMillis();
        this.maxVaults = FactionsPlugin.getInstance().conf().playerVaults().getDefaultMaxVaults();
        this.defaultRole = FactionsPlugin.getInstance().conf().factions().other().getDefaultRole();
        this.dtr = FactionsPlugin.getInstance().conf().factions().landRaidControl().dtr().getStartingDTR();
        this.resetPerms();
    }

    @Deprecated
    public MemoryFaction(MemoryFaction memoryFaction) {
        this.id = memoryFaction.id;
        this.peacefulExplosionsEnabled = memoryFaction.peacefulExplosionsEnabled;
        this.permanent = memoryFaction.permanent;
        this.tag = memoryFaction.tag;
        this.description = memoryFaction.description;
        this.open = memoryFaction.open;
        this.foundedDate = memoryFaction.foundedDate;
        this.peaceful = memoryFaction.peaceful;
        this.permanentPower = memoryFaction.permanentPower;
        this.home = memoryFaction.home;
        this.lastPlayerLoggedOffTime = memoryFaction.lastPlayerLoggedOffTime;
        this.powerBoost = memoryFaction.powerBoost;
        this.relationWish = memoryFaction.relationWish;
        this.claimOwnership = memoryFaction.claimOwnership;
        this.fplayers = new HashSet<FPlayer>();
        this.invites = memoryFaction.invites;
        this.announcements = memoryFaction.announcements;
        this.defaultRole = memoryFaction.defaultRole;
        this.dtr = memoryFaction.dtr;
        this.resetPerms();
    }

    @Override
    public boolean noPvPInTerritory() {
        return this.isSafeZone() || this.peaceful && FactionsPlugin.getInstance().conf().factions().specialCase().isPeacefulTerritoryDisablePVP();
    }

    @Override
    public boolean noMonstersInTerritory() {
        return this.isSafeZone() || this.peaceful && FactionsPlugin.getInstance().conf().factions().specialCase().isPeacefulTerritoryDisableMonsters();
    }

    @Override
    public boolean isNormal() {
        return !this.isWilderness() && !this.isSafeZone() && !this.isWarZone();
    }

    @Override
    public boolean isWilderness() {
        return this.id == 0;
    }

    @Override
    public boolean isSafeZone() {
        return this.id == -1;
    }

    @Override
    public boolean isWarZone() {
        return this.id == -2;
    }

    @Override
    public boolean isPlayerFreeType() {
        return this.isSafeZone() || this.isWarZone();
    }

    @Override
    public String describeTo(RelationParticipator relationParticipator, boolean bl) {
        return RelationUtil.describeThatToMe(this, relationParticipator, bl);
    }

    @Override
    public String describeTo(RelationParticipator relationParticipator) {
        return RelationUtil.describeThatToMe(this, relationParticipator);
    }

    @Override
    public Relation getRelationTo(RelationParticipator relationParticipator) {
        return RelationUtil.getRelationTo(this, relationParticipator);
    }

    @Override
    public Relation getRelationTo(RelationParticipator relationParticipator, boolean bl) {
        return RelationUtil.getRelationTo(this, relationParticipator, bl);
    }

    @Override
    @Deprecated
    public ChatColor getColorTo(RelationParticipator relationParticipator) {
        return RelationUtil.getColorOfThatToMe(this, relationParticipator);
    }

    @Override
    public String getColorStringTo(RelationParticipator relationParticipator) {
        return RelationUtil.getColorStringOfThatToMe(this, relationParticipator);
    }

    @Override
    public Relation getRelationWish(Faction faction) {
        if (this.relationWish.containsKey(faction.getId())) {
            return this.relationWish.get(faction.getId());
        }
        return Relation.fromString(FactionsPlugin.getInstance().conf().factions().other().getDefaultRelation());
    }

    @Override
    public void setRelationWish(Faction faction, Relation relation) {
        if (this.relationWish.containsKey(faction.getId()) && relation.equals(Relation.NEUTRAL)) {
            this.relationWish.remove(faction.getId());
        } else {
            this.relationWish.put(faction.getId(), relation);
        }
    }

    @Override
    public int getRelationCount(Relation relation) {
        int n = 0;
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.getRelationTo(this) != relation) continue;
            ++n;
        }
        return n;
    }

    @Override
    public double getDTR() {
        LandRaidControl landRaidControl = FactionsPlugin.getInstance().getLandRaidControl();
        if (landRaidControl instanceof DTRControl) {
            ((DTRControl)landRaidControl).updateDTR(this);
        }
        return this.dtr;
    }

    @Override
    public double getDTRWithoutUpdate() {
        return this.dtr;
    }

    @Override
    public void setDTR(double d) {
        double d2 = this.dtr;
        this.dtr = d;
        this.lastDTRUpdateTime = System.currentTimeMillis();
        if (d2 != this.dtr && FactionsPlugin.getInstance().getLandRaidControl() instanceof DTRControl) {
            ((DTRControl)FactionsPlugin.getInstance().getLandRaidControl()).onDTRChange(this, d2, this.dtr);
        }
    }

    @Override
    public long getLastDTRUpdateTime() {
        return this.lastDTRUpdateTime;
    }

    @Override
    public long getFrozenDTRUntilTime() {
        return this.frozenDTRUntilTime;
    }

    @Override
    public void setFrozenDTR(long l) {
        this.frozenDTRUntilTime = l;
    }

    @Override
    public boolean isFrozenDTR() {
        return System.currentTimeMillis() < this.frozenDTRUntilTime;
    }

    @Override
    @Deprecated
    public double getPower() {
        if (this.hasPermanentPower()) {
            return this.getPermanentPower().intValue();
        }
        double d = 0.0;
        for (FPlayer fPlayer : this.fplayers) {
            d += fPlayer.getPower();
        }
        if (FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getFactionMax() > 0.0 && d > FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getFactionMax()) {
            d = FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getFactionMax();
        }
        return d + this.powerBoost;
    }

    @Override
    @Deprecated
    public double getPowerMax() {
        if (this.hasPermanentPower()) {
            return this.getPermanentPower().intValue();
        }
        double d = 0.0;
        for (FPlayer fPlayer : this.fplayers) {
            d += fPlayer.getPowerMax();
        }
        if (FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getFactionMax() > 0.0 && d > FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getFactionMax()) {
            d = FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getFactionMax();
        }
        return d + this.powerBoost;
    }

    @Override
    public int getPowerRounded() {
        return (int)Math.round(this.getPower());
    }

    @Override
    public int getPowerMaxRounded() {
        return (int)Math.round(this.getPowerMax());
    }

    @Override
    public boolean hasLandInflation() {
        return FactionsPlugin.getInstance().getLandRaidControl().hasLandInflation(this);
    }

    @Override
    public Integer getPermanentPower() {
        return this.permanentPower;
    }

    @Override
    public void setPermanentPower(Integer n) {
        this.permanentPower = n;
    }

    @Override
    public boolean hasPermanentPower() {
        return this.permanentPower != null;
    }

    @Override
    public double getPowerBoost() {
        return this.powerBoost;
    }

    @Override
    public void setPowerBoost(double d) {
        this.powerBoost = d;
    }

    @Override
    public boolean isPowerFrozen() {
        int n = FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getPowerFreeze();
        return n != 0 && System.currentTimeMillis() - this.lastDeath < (long)n * 1000L;
    }

    @Override
    public int getLandRounded() {
        return Board.getInstance().getFactionCoordCount(this);
    }

    @Override
    public int getLandRoundedInWorld(String string) {
        return Board.getInstance().getFactionCoordCountInWorld(this, string);
    }

    @Override
    public int getTNTBank() {
        return this.tntBank;
    }

    @Override
    public void setTNTBank(int n) {
        this.tntBank = n;
    }

    @Override
    public void refreshFPlayers() {
        this.fplayers.clear();
        if (this.isPlayerFreeType()) {
            return;
        }
        for (FPlayer fPlayer : FPlayers.getInstance().getAllFPlayers()) {
            if (fPlayer.getFactionIntId() != this.id) continue;
            this.fplayers.add(fPlayer);
        }
    }

    @Override
    public boolean addFPlayer(FPlayer fPlayer) {
        return !this.isPlayerFreeType() && this.fplayers.add(fPlayer);
    }

    @Override
    public boolean removeFPlayer(FPlayer fPlayer) {
        return !this.isPlayerFreeType() && this.fplayers.remove(fPlayer);
    }

    @Override
    public int getSize() {
        return this.fplayers.size();
    }

    @Override
    public Set<FPlayer> getFPlayers() {
        return new HashSet<FPlayer>(this.fplayers);
    }

    @Override
    public Set<FPlayer> getFPlayersWhereOnline(boolean bl) {
        HashSet<FPlayer> hashSet = new HashSet<FPlayer>();
        if (!this.isNormal()) {
            return hashSet;
        }
        for (FPlayer fPlayer : this.fplayers) {
            if (fPlayer.isOnline() != bl) continue;
            hashSet.add(fPlayer);
        }
        return hashSet;
    }

    @Override
    public Set<FPlayer> getFPlayersWhereOnline(boolean bl, FPlayer fPlayer) {
        if (fPlayer == null) {
            return this.getFPlayersWhereOnline(bl);
        }
        HashSet<FPlayer> hashSet = new HashSet<FPlayer>();
        if (!this.isNormal()) {
            return hashSet;
        }
        for (FPlayer fPlayer2 : this.fplayers) {
            if (fPlayer2.isOnline() != bl) continue;
            if (bl && fPlayer2.getPlayer() != null && fPlayer.getPlayer() != null && fPlayer.getPlayer().canSee(fPlayer2.getPlayer())) {
                hashSet.add(fPlayer2);
                continue;
            }
            if (bl) continue;
            hashSet.add(fPlayer2);
        }
        return hashSet;
    }

    @Override
    public FPlayer getFPlayerAdmin() {
        if (!this.isNormal()) {
            return null;
        }
        for (FPlayer fPlayer : this.fplayers) {
            if (fPlayer.getRole() != Role.ADMIN) continue;
            return fPlayer;
        }
        return null;
    }

    public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
        ArrayList<FPlayer> arrayList = new ArrayList<FPlayer>();
        if (!this.isNormal()) {
            return arrayList;
        }
        for (FPlayer fPlayer : this.fplayers) {
            if (fPlayer.getRole() != role) continue;
            arrayList.add(fPlayer);
        }
        return arrayList;
    }

    public ArrayList<Player> getOnlinePlayers() {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        if (this.isPlayerFreeType()) {
            return arrayList;
        }
        for (Player player : FactionsPlugin.getInstance().getServer().getOnlinePlayers()) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            if (fPlayer.getFaction() != this) continue;
            arrayList.add(player);
        }
        return arrayList;
    }

    @Override
    public boolean hasPlayersOnline() {
        if (this.isPlayerFreeType()) {
            return false;
        }
        for (Player player : FactionsPlugin.getInstance().getServer().getOnlinePlayers()) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            if (fPlayer == null || fPlayer.getFaction() != this) continue;
            return true;
        }
        return FactionsPlugin.getInstance().conf().factions().other().getConsiderFactionsReallyOfflineAfterXMinutes() > 0.0 && (double)System.currentTimeMillis() < (double)this.lastPlayerLoggedOffTime + FactionsPlugin.getInstance().conf().factions().other().getConsiderFactionsReallyOfflineAfterXMinutes() * 60000.0;
    }

    @Override
    public void memberLoggedOff() {
        if (this.isNormal()) {
            this.lastPlayerLoggedOffTime = System.currentTimeMillis();
        }
    }

    @Override
    public void promoteNewLeader() {
        if (!this.isNormal()) {
            return;
        }
        if (this.isPermanent() && FactionsPlugin.getInstance().conf().factions().specialCase().isPermanentFactionsDisableLeaderPromotion()) {
            return;
        }
        FPlayer fPlayer = this.getFPlayerAdmin();
        List list = this.getFPlayersWhereRole(Role.COLEADER);
        if (list == null || ((ArrayList)list).isEmpty()) {
            list = this.getFPlayersWhereRole(Role.MODERATOR);
        }
        if (list == null || ((ArrayList)list).isEmpty()) {
            list = this.getFPlayersWhereRole(Role.NORMAL);
        }
        if (list == null || ((ArrayList)list).isEmpty()) {
            if (this.isPermanent()) {
                if (fPlayer != null) {
                    fPlayer.setRole(Role.NORMAL);
                }
                return;
            }
            if (FactionsPlugin.getInstance().conf().logging().isFactionDisband()) {
                FactionsPlugin.getInstance().log("The faction " + this.getTag() + " (" + this.getIntId() + ") has been disbanded since it has no members left.");
            }
            for (FPlayer fPlayer2 : FPlayers.getInstance().getOnlinePlayers()) {
                fPlayer2.msg(TL.LEAVE_DISBANDED, this.getTag(fPlayer2));
            }
            FactionsPlugin.getInstance().getServer().getPluginManager().callEvent((Event)new FactionAutoDisbandEvent(this));
            Factions.getInstance().removeFaction(this);
        } else {
            Bukkit.getServer().getPluginManager().callEvent((Event)new FactionNewAdminEvent((FPlayer)list.getFirst(), this));
            if (fPlayer != null) {
                fPlayer.setRole(Role.COLEADER);
            }
            ((FPlayer)list.getFirst()).setRole(Role.ADMIN);
            this.msg(TL.FACTION_NEWLEADER, fPlayer == null ? "" : fPlayer.getName(), ((FPlayer)list.getFirst()).getName());
            FactionsPlugin.getInstance().log("Faction " + this.getTag() + " (" + this.getIntId() + ") admin was removed. Replacement admin: " + ((FPlayer)list.getFirst()).getName());
        }
    }

    @Override
    public void msg(String string, Object ... objectArray) {
        string = FactionsPlugin.getInstance().txt().parse(string, objectArray);
        for (FPlayer fPlayer : this.getFPlayersWhereOnline(true)) {
            fPlayer.sendMessage(string);
        }
    }

    @Override
    public void msg(TL tL, Object ... objectArray) {
        this.msg(tL.toString(), objectArray);
    }

    @Override
    public void sendMessage(String string) {
        for (FPlayer fPlayer : this.getFPlayersWhereOnline(true)) {
            fPlayer.sendMessage(string);
        }
    }

    @Override
    public void sendMessage(List<String> list) {
        for (FPlayer fPlayer : this.getFPlayersWhereOnline(true)) {
            fPlayer.sendMessage(list);
        }
    }

    @Override
    public Map<FLocation, Set<String>> getClaimOwnership() {
        return this.claimOwnership;
    }

    @Override
    public void clearAllClaimOwnership() {
        this.claimOwnership.clear();
    }

    @Override
    public void clearClaimOwnership(FLocation fLocation) {
        if (LWC.getEnabled() && FactionsPlugin.getInstance().conf().lwc().isResetLocksOnUnclaim()) {
            LWC.clearAllLocks(fLocation);
        }
        this.claimOwnership.remove(fLocation);
    }

    @Override
    public void clearClaimOwnership(FPlayer fPlayer) {
        for (Map.Entry<FLocation, Set<String>> entry : this.claimOwnership.entrySet()) {
            Set<String> set = entry.getValue();
            if (set == null) continue;
            set.removeIf(string -> string.equals(fPlayer.getId()));
            if (!set.isEmpty()) continue;
            if (LWC.getEnabled() && FactionsPlugin.getInstance().conf().lwc().isResetLocksOnUnclaim()) {
                LWC.clearAllLocks(entry.getKey());
            }
            this.claimOwnership.remove(entry.getKey());
        }
    }

    @Override
    public int getCountOfClaimsWithOwners() {
        return this.claimOwnership.isEmpty() ? 0 : this.claimOwnership.size();
    }

    @Override
    public boolean doesLocationHaveOwnersSet(FLocation fLocation) {
        if (this.claimOwnership.isEmpty() || !this.claimOwnership.containsKey(fLocation)) {
            return false;
        }
        Set<String> set = this.claimOwnership.get(fLocation);
        return set != null && !set.isEmpty();
    }

    @Override
    public boolean isPlayerInOwnerList(FPlayer fPlayer, FLocation fLocation) {
        if (this.claimOwnership.isEmpty()) {
            return false;
        }
        Set<String> set = this.claimOwnership.get(fLocation);
        return set != null && set.contains(fPlayer.getId());
    }

    @Override
    public void setPlayerAsOwner(FPlayer fPlayer, FLocation fLocation) {
        Set<String> set = this.claimOwnership.get(fLocation);
        if (set == null) {
            set = new HashSet<String>();
        }
        set.add(fPlayer.getId());
        this.claimOwnership.put(fLocation, set);
    }

    @Override
    public void removePlayerAsOwner(FPlayer fPlayer, FLocation fLocation) {
        Set<String> set = this.claimOwnership.get(fLocation);
        if (set == null) {
            return;
        }
        set.remove(fPlayer.getId());
        this.claimOwnership.put(fLocation, set);
    }

    @Override
    public Set<String> getOwnerList(FLocation fLocation) {
        return this.claimOwnership.get(fLocation);
    }

    @Override
    public String getOwnerListString(FLocation fLocation) {
        Set<String> set = this.claimOwnership.get(fLocation);
        if (set == null || set.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : set) {
            OfflinePlayer offlinePlayer;
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(", ");
            }
            stringBuilder.append((offlinePlayer = Bukkit.getOfflinePlayer((UUID)UUID.fromString(string))) != null ? offlinePlayer.getName() : "null player");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean playerHasOwnershipRights(FPlayer fPlayer, FLocation fLocation) {
        if (fPlayer.getFaction() == this && (fPlayer.getRole().isAtLeast(FactionsPlugin.getInstance().conf().factions().ownedArea().isModeratorsBypass() ? Role.MODERATOR : Role.ADMIN) || Permission.OWNERSHIP_BYPASS.has((CommandSender)fPlayer.getPlayer()))) {
            return true;
        }
        if (this.claimOwnership.isEmpty()) {
            return true;
        }
        Set<String> set = this.claimOwnership.get(fLocation);
        return set == null || set.isEmpty() || set.contains(fPlayer.getId());
    }

    @Override
    public void remove() {
        if (Econ.shouldBeUsed() && FactionsPlugin.getInstance().conf().economy().isBankEnabled()) {
            Econ.setBalance(this, 0.0);
        }
        ((MemoryBoard)Board.getInstance()).clean(this.id);
        for (FPlayer fPlayer : this.fplayers) {
            fPlayer.resetFactionData(false);
        }
    }

    @Override
    public Set<FLocation> getAllClaims() {
        return Board.getInstance().getAllClaims(this);
    }
}

