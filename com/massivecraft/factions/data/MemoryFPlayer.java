/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Statistic
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.massivecraft.factions.data;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionAutoDisbandEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.IntegrationManager;
import com.massivecraft.factions.integration.LWC;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.landraidcontrol.PowerControl;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.scoreboards.sidebar.FInfoSidebar;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.WarmUpUtil;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class MemoryFPlayer
implements FPlayer {
    protected int factionId;
    protected Role role;
    protected String title;
    protected double power;
    protected double powerBoost;
    protected long lastPowerUpdateTime;
    protected long lastLoginTime;
    protected ChatMode chatMode;
    protected boolean ignoreAllianceChat = false;
    protected String id;
    protected String name;
    protected boolean monitorJoins;
    protected boolean spyingChat = false;
    protected boolean showScoreboard = true;
    protected WarmUpUtil.Warmup warmup;
    protected int warmupTask;
    protected boolean isAdminBypassing = false;
    protected int kills;
    protected int deaths;
    protected boolean willAutoLeave = true;
    protected int mapHeight = 8;
    protected boolean isFlying = false;
    protected boolean isAutoFlying = false;
    protected boolean flyTrailsState = false;
    protected String flyTrailsEffect = null;
    protected boolean seeingChunk = false;
    protected transient FLocation lastStoodAt = new FLocation();
    protected transient boolean mapAutoUpdating;
    protected transient Faction autoClaimFor;
    protected transient Faction autoUnclaimFor;
    protected transient boolean loginPvpDisabled;
    protected transient long lastFrostwalkerMessage;
    protected transient boolean shouldTakeFallDamage = true;
    protected transient OfflinePlayer offlinePlayer;

    @Override
    public void login() {
        this.kills = this.getPlayer().getStatistic(Statistic.PLAYER_KILLS);
        this.deaths = this.getPlayer().getStatistic(Statistic.DEATHS);
    }

    @Override
    public void logout() {
        this.kills = this.getPlayer().getStatistic(Statistic.PLAYER_KILLS);
        this.deaths = this.getPlayer().getStatistic(Statistic.DEATHS);
    }

    @Override
    public Faction getFaction() {
        Faction faction = Factions.getInstance().getFactionById(this.factionId);
        if (faction == null) {
            FactionsPlugin.getInstance().getLogger().warning("Found null faction (id " + this.factionId + ") for player " + this.getName());
            this.factionId = 0;
            faction = Factions.getInstance().getFactionById(this.factionId);
        }
        return faction;
    }

    @Override
    public String getFactionId() {
        return String.valueOf(this.factionId);
    }

    @Override
    public int getFactionIntId() {
        return this.factionId;
    }

    @Override
    public boolean hasFaction() {
        return this.factionId != 0;
    }

    @Override
    public void setFaction(Faction faction) {
        Faction faction2 = this.getFaction();
        if (faction2 != null) {
            faction2.removeFPlayer(this);
        }
        faction.addFPlayer(this);
        this.factionId = faction.getIntId();
    }

    @Override
    public void setMonitorJoins(boolean bl) {
        this.monitorJoins = bl;
    }

    @Override
    public boolean isMonitoringJoins() {
        return this.monitorJoins;
    }

    @Override
    public Role getRole() {
        if (this.role == null) {
            this.role = Role.NORMAL;
        }
        return this.role;
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
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
    public boolean willAutoLeave() {
        return this.willAutoLeave;
    }

    @Override
    public void setAutoLeave(boolean bl) {
        this.willAutoLeave = bl;
        FactionsPlugin.getInstance().debug(this.name + " set autoLeave to " + bl);
    }

    @Override
    public long getLastFrostwalkerMessage() {
        return this.lastFrostwalkerMessage;
    }

    @Override
    public void setLastFrostwalkerMessage() {
        this.lastFrostwalkerMessage = System.currentTimeMillis();
    }

    @Override
    public Faction getAutoClaimFor() {
        return this.autoClaimFor;
    }

    @Override
    public void setAutoClaimFor(Faction faction) {
        this.autoClaimFor = faction;
        if (faction != null) {
            this.autoUnclaimFor = null;
        }
    }

    @Override
    public Faction getAutoUnclaimFor() {
        return this.autoUnclaimFor;
    }

    @Override
    public void setAutoUnclaimFor(Faction faction) {
        this.autoUnclaimFor = faction;
        if (faction != null) {
            this.autoClaimFor = null;
        }
    }

    @Override
    @Deprecated
    public boolean isAutoSafeClaimEnabled() {
        return this.autoClaimFor != null && this.autoClaimFor.isSafeZone();
    }

    @Override
    @Deprecated
    public void setIsAutoSafeClaimEnabled(boolean bl) {
        this.setAutoClaimFor(bl ? Factions.getInstance().getSafeZone() : null);
    }

    @Override
    @Deprecated
    public boolean isAutoWarClaimEnabled() {
        return this.autoClaimFor != null && this.autoClaimFor.isWarZone();
    }

    @Override
    @Deprecated
    public void setIsAutoWarClaimEnabled(boolean bl) {
        this.setAutoClaimFor(bl ? Factions.getInstance().getWarZone() : null);
    }

    @Override
    public boolean isAdminBypassing() {
        return this.isAdminBypassing;
    }

    @Override
    public boolean isVanished() {
        Player player = this.getPlayer();
        if (FactionsPlugin.getInstance().getIntegrationManager().isEnabled(IntegrationManager.Integration.ESS) && Essentials.isVanished(player)) {
            return true;
        }
        if (player != null) {
            for (MetadataValue metadataValue : player.getMetadata("vanished")) {
                if (metadataValue == null || !metadataValue.asBoolean()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void setIsAdminBypassing(boolean bl) {
        this.isAdminBypassing = bl;
    }

    @Override
    public void setChatMode(ChatMode chatMode) {
        this.chatMode = chatMode;
    }

    @Override
    public ChatMode getChatMode() {
        if (this.chatMode == null || this.factionId == 0 || !FactionsPlugin.getInstance().conf().factions().chat().isFactionOnlyChat()) {
            this.chatMode = ChatMode.PUBLIC;
        }
        return this.chatMode;
    }

    @Override
    public void setIgnoreAllianceChat(boolean bl) {
        this.ignoreAllianceChat = bl;
    }

    @Override
    public boolean isIgnoreAllianceChat() {
        return this.ignoreAllianceChat;
    }

    @Override
    public void setSpyingChat(boolean bl) {
        this.spyingChat = bl;
    }

    @Override
    public boolean isSpyingChat() {
        return this.spyingChat;
    }

    @Override
    public String getAccountId() {
        return this.getId();
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        if (this.offlinePlayer == null) {
            UUID uUID = UUID.fromString(this.getId());
            this.offlinePlayer = Bukkit.getPlayer((UUID)uUID);
            if (this.offlinePlayer == null) {
                this.offlinePlayer = FactionsPlugin.getInstance().getOfflinePlayer(this.name, uUID);
            }
        }
        return this.offlinePlayer;
    }

    @Override
    public void setOfflinePlayer(Player player) {
        this.offlinePlayer = player;
    }

    public MemoryFPlayer() {
    }

    public MemoryFPlayer(String string) {
        this.id = string;
        this.resetFactionData(false);
        this.power = FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getPlayerStarting();
        this.lastPowerUpdateTime = System.currentTimeMillis();
        this.lastLoginTime = System.currentTimeMillis();
        this.mapAutoUpdating = false;
        this.autoClaimFor = null;
        this.loginPvpDisabled = FactionsPlugin.getInstance().conf().factions().pvp().getNoPVPDamageToOthersForXSecondsAfterLogin() > 0;
        this.powerBoost = 0.0;
        this.kills = 0;
        this.deaths = 0;
        this.mapHeight = FactionsPlugin.getInstance().conf().map().getHeight();
        if (FactionsPlugin.getInstance().conf().factions().other().getNewPlayerStartingFactionID() > 0 && Factions.getInstance().isValidFactionId(FactionsPlugin.getInstance().conf().factions().other().getNewPlayerStartingFactionID())) {
            this.factionId = FactionsPlugin.getInstance().conf().factions().other().getNewPlayerStartingFactionID();
        }
    }

    @Deprecated
    public MemoryFPlayer(MemoryFPlayer memoryFPlayer) {
        this.factionId = memoryFPlayer.factionId;
        this.id = memoryFPlayer.id;
        this.power = memoryFPlayer.power;
        this.lastLoginTime = memoryFPlayer.lastLoginTime;
        this.mapAutoUpdating = memoryFPlayer.mapAutoUpdating;
        this.autoClaimFor = memoryFPlayer.autoClaimFor;
        this.loginPvpDisabled = memoryFPlayer.loginPvpDisabled;
        this.powerBoost = memoryFPlayer.powerBoost;
        this.role = memoryFPlayer.role;
        this.title = memoryFPlayer.title;
        this.chatMode = memoryFPlayer.chatMode;
        this.spyingChat = memoryFPlayer.spyingChat;
        this.lastStoodAt = memoryFPlayer.lastStoodAt;
        this.isAdminBypassing = memoryFPlayer.isAdminBypassing;
        this.kills = memoryFPlayer.kills;
        this.deaths = memoryFPlayer.deaths;
        this.mapHeight = memoryFPlayer.mapHeight;
    }

    @Override
    public void resetFactionData(boolean bl) {
        if (Factions.getInstance().isValidFactionId(this.getFactionIntId())) {
            Faction faction = this.getFaction();
            faction.removeFPlayer(this);
            if (faction.isNormal()) {
                faction.clearClaimOwnership(this);
            }
        }
        this.factionId = 0;
        this.chatMode = ChatMode.PUBLIC;
        this.role = Role.NORMAL;
        this.title = "";
        this.autoClaimFor = null;
    }

    @Override
    public void resetFactionData() {
        this.resetFactionData(true);
    }

    @Override
    public long getLastLoginTime() {
        return this.lastLoginTime;
    }

    @Override
    public void setLastLoginTime(long l) {
        this.lastLoginTime = l;
        if (FactionsPlugin.getInstance().conf().factions().pvp().getNoPVPDamageToOthersForXSecondsAfterLogin() > 0) {
            this.loginPvpDisabled = true;
        }
    }

    @Override
    public boolean isMapAutoUpdating() {
        return this.mapAutoUpdating;
    }

    @Override
    public void setMapAutoUpdating(boolean bl) {
        this.mapAutoUpdating = bl;
    }

    @Override
    public boolean hasLoginPvpDisabled() {
        if (!this.loginPvpDisabled) {
            return false;
        }
        if (this.lastLoginTime + (long)FactionsPlugin.getInstance().conf().factions().pvp().getNoPVPDamageToOthersForXSecondsAfterLogin() * 1000L < System.currentTimeMillis()) {
            this.loginPvpDisabled = false;
            return false;
        }
        return true;
    }

    @Override
    public FLocation getLastStoodAt() {
        return this.lastStoodAt;
    }

    @Override
    public void setLastStoodAt(FLocation fLocation) {
        this.lastStoodAt = fLocation;
    }

    @Override
    public String getTitle() {
        return this.hasFaction() ? this.title : TL.NOFACTION_PREFIX.toString();
    }

    @Override
    public void setTitle(CommandSender commandSender, String string) {
        if (commandSender.hasPermission(Permission.TITLE_COLOR.node)) {
            string = ChatColor.translateAlternateColorCodes((char)'&', (String)string);
        }
        this.title = string;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)UUID.fromString(this.getId()));
            this.name = offlinePlayer.getName() != null ? offlinePlayer.getName() : this.getId();
        }
        return this.name;
    }

    public void setName(String string) {
        if (!string.equalsIgnoreCase(this.name)) {
            for (final FPlayer fPlayer : FPlayers.getInstance().getAllFPlayers()) {
                UUID uUID;
                String string2;
                if (!fPlayer.getName().equalsIgnoreCase(string)) continue;
                ((MemoryFPlayer)fPlayer).name = string2 = fPlayer.getId();
                try {
                    uUID = UUID.fromString(string2);
                } catch (IllegalArgumentException illegalArgumentException) {
                    continue;
                }
                if (uUID.version() != 4) continue;
                final String string3 = string2.replace("-", "");
                new BukkitRunnable(this){

                    public void run() {
                        try {
                            URL uRL = new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + string3).toURL();
                            NameLookup nameLookup = (NameLookup)FactionsPlugin.getInstance().getGson().fromJson((Reader)new InputStreamReader(uRL.openStream()), NameLookup.class);
                            final String string = nameLookup.name;
                            new BukkitRunnable(){

                                public void run() {
                                    if (string != null && fPlayer.getName().equals(string2)) {
                                        ((MemoryFPlayer)fPlayer).setName(string);
                                    }
                                }
                            }.runTask((Plugin)FactionsPlugin.getInstance());
                        } catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }.runTaskAsynchronously((Plugin)FactionsPlugin.getInstance());
            }
        }
        this.name = string;
    }

    @Override
    public String getTag() {
        return this.hasFaction() ? this.getFaction().getTag() : "";
    }

    @Override
    public String getNameAndSomething(String string) {
        Object object = this.role.getPrefix();
        if (string != null && !string.isEmpty()) {
            object = (String)object + string + " ";
        }
        object = (String)object + this.getName();
        return object;
    }

    @Override
    public String getNameAndTitle() {
        return this.getNameAndSomething(this.getTitle());
    }

    @Override
    public String getNameAndTag() {
        return this.getNameAndSomething(this.getTag());
    }

    @Override
    public String getNameAndTitle(Faction faction) {
        return this.getColorStringTo(faction) + this.getNameAndTitle();
    }

    public String getNameAndTitle(MemoryFPlayer memoryFPlayer) {
        return this.getColorStringTo(memoryFPlayer) + this.getNameAndTitle();
    }

    @Override
    public String getChatTag() {
        return this.hasFaction() ? String.format(FactionsPlugin.getInstance().conf().factions().chat().getTagFormat(), this.getRole().getPrefix() + this.getTag()) : TL.NOFACTION_PREFIX.toString();
    }

    @Override
    public String getChatTag(Faction faction) {
        return this.hasFaction() ? String.valueOf(this.getRelationTo(faction).getColor()) + this.getChatTag() : TL.NOFACTION_PREFIX.toString();
    }

    @Override
    public String getChatTag(FPlayer fPlayer) {
        return this.hasFaction() ? String.valueOf(this.getRelationTo(fPlayer).getColor()) + this.getChatTag() : TL.NOFACTION_PREFIX.toString();
    }

    @Override
    public int getKills() {
        return this.isOnline() ? this.getPlayer().getStatistic(Statistic.PLAYER_KILLS) : this.kills;
    }

    @Override
    public int getDeaths() {
        return this.isOnline() ? this.getPlayer().getStatistic(Statistic.DEATHS) : this.deaths;
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
    public Relation getRelationToLocation() {
        return Board.getInstance().getFactionAt(new FLocation(this)).getRelationTo(this);
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
    public void heal(int n) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.setHealth(player.getHealth() + (double)n);
    }

    @Override
    public double getPower() {
        this.updatePower();
        return this.power;
    }

    @Override
    public void alterPower(double d) {
        int n = (int)Math.round(this.power);
        this.power += d;
        if (this.power > this.getPowerMax()) {
            this.power = this.getPowerMax();
        } else if (this.power < this.getPowerMin()) {
            this.power = this.getPowerMin();
        }
        int n2 = (int)Math.round(this.power);
        if (this.hasFaction() && n2 != n && FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl) {
            ((PowerControl)FactionsPlugin.getInstance().getLandRaidControl()).onPowerChange(this.getFaction(), n, n2);
        }
    }

    @Override
    public double getPowerMax() {
        return FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getPlayerMax() + this.powerBoost;
    }

    @Override
    public double getPowerMin() {
        return FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getPlayerMin() + this.powerBoost;
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
    public int getPowerMinRounded() {
        return (int)Math.round(this.getPowerMin());
    }

    @Override
    public void updatePower() {
        if (this.isOffline()) {
            this.losePowerFromBeingOffline();
            if (!FactionsPlugin.getInstance().conf().factions().landRaidControl().power().isRegenOffline()) {
                return;
            }
        } else {
            if (this.hasFaction() && this.getFaction().isPowerFrozen()) {
                return;
            }
            if (FactionsPlugin.getInstance().conf().plugins().essentialsX().isPreventRegenWhileAfk() && Essentials.isAfk(this.getPlayer())) {
                return;
            }
        }
        long l = System.currentTimeMillis();
        long l2 = l - this.lastPowerUpdateTime;
        this.lastPowerUpdateTime = l;
        Player player = this.getPlayer();
        if (player != null && player.isDead()) {
            return;
        }
        int n = 60000;
        this.alterPower((double)l2 * FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getPowerPerMinute() / (double)n);
    }

    @Override
    public void losePowerFromBeingOffline() {
        long l = System.currentTimeMillis();
        if (FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getOfflineLossPerDay() > 0.0 && this.power > FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getOfflineLossLimit()) {
            long l2 = l - this.lastPowerUpdateTime;
            double d = (double)l2 * FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getOfflineLossPerDay() / 8.64E7;
            if (this.power - d < FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getOfflineLossLimit()) {
                d = this.power;
            }
            this.alterPower(-d);
        }
        this.lastPowerUpdateTime = l;
    }

    @Override
    public void onDeath() {
        if (this.hasFaction()) {
            this.getFaction().setLastDeath(System.currentTimeMillis());
        }
    }

    @Override
    public boolean isInOwnTerritory() {
        return Board.getInstance().getFactionAt(new FLocation(this)) == this.getFaction();
    }

    @Override
    public boolean isInOthersTerritory() {
        Faction faction = Board.getInstance().getFactionAt(new FLocation(this));
        return faction != null && faction.isNormal() && faction != this.getFaction();
    }

    @Override
    public boolean isInAllyTerritory() {
        return Board.getInstance().getFactionAt(new FLocation(this)).getRelationTo(this).isAlly();
    }

    @Override
    public boolean isInNeutralTerritory() {
        return Board.getInstance().getFactionAt(new FLocation(this)).getRelationTo(this).isNeutral();
    }

    @Override
    public boolean isInEnemyTerritory() {
        return Board.getInstance().getFactionAt(new FLocation(this)).getRelationTo(this).isEnemy();
    }

    @Override
    public void sendFactionHereMessage(Faction faction) {
        Faction faction2 = Board.getInstance().getFactionAt(this.getLastStoodAt());
        boolean bl = FactionsPlugin.getInstance().conf().factions().enterTitles().isEnabled();
        boolean bl2 = true;
        Player player = this.getPlayer();
        if (bl && player != null) {
            int n = FactionsPlugin.getInstance().conf().factions().enterTitles().getFadeIn();
            int n2 = FactionsPlugin.getInstance().conf().factions().enterTitles().getStay();
            int n3 = FactionsPlugin.getInstance().conf().factions().enterTitles().getFadeOut();
            String string = Tag.parsePlain(faction2, this, FactionsPlugin.getInstance().conf().factions().enterTitles().getTitle());
            String string2 = FactionsPlugin.getInstance().txt().parse(Tag.parsePlain(faction2, this, FactionsPlugin.getInstance().conf().factions().enterTitles().getSubtitle()));
            player.sendTitle(string, string2, n, n2, n3);
            bl2 = FactionsPlugin.getInstance().conf().factions().enterTitles().isAlsoShowChat();
        }
        if (this.showInfoBoard(faction2)) {
            FScoreboard.get(this).setTemporarySidebar(new FInfoSidebar(faction2));
            bl2 = FactionsPlugin.getInstance().conf().scoreboard().info().isAlsoSendChat();
        }
        if (bl2) {
            this.sendMessage(FactionsPlugin.getInstance().txt().parse(TL.FACTION_LEAVE.format(faction.getTag(this), faction2.getTag(this))));
        }
    }

    public boolean showInfoBoard(Faction faction) {
        return this.showScoreboard && !faction.isWarZone() && !faction.isWilderness() && !faction.isSafeZone() && FactionsPlugin.getInstance().conf().scoreboard().info().isEnabled() && FScoreboard.get(this) != null;
    }

    @Override
    public boolean showScoreboard() {
        return this.showScoreboard;
    }

    @Override
    public void setShowScoreboard(boolean bl) {
        this.showScoreboard = bl;
    }

    @Override
    public void leave(boolean bl) {
        boolean bl2;
        Faction faction = this.getFaction();
        boolean bl3 = bl2 = bl && Econ.shouldBeUsed() && !this.isAdminBypassing();
        if (faction == null) {
            this.resetFactionData();
            return;
        }
        boolean bl4 = faction.isPermanent();
        if (!bl4 && this.getRole() == Role.ADMIN && faction.getFPlayers().size() > 1) {
            this.msg(TL.LEAVE_PASSADMIN, new Object[0]);
            return;
        }
        if (bl && !FactionsPlugin.getInstance().getLandRaidControl().canLeaveFaction(this)) {
            return;
        }
        if (bl2 && !Econ.hasAtLeast(this, FactionsPlugin.getInstance().conf().economy().getCostLeave(), TL.LEAVE_TOLEAVE.toString())) {
            return;
        }
        FPlayerLeaveEvent fPlayerLeaveEvent = new FPlayerLeaveEvent(this, faction, FPlayerLeaveEvent.PlayerLeaveReason.LEAVE);
        Bukkit.getServer().getPluginManager().callEvent((Event)fPlayerLeaveEvent);
        if (fPlayerLeaveEvent.isCancelled()) {
            return;
        }
        if (bl2 && !Econ.modifyMoney(this, -FactionsPlugin.getInstance().conf().economy().getCostLeave(), TL.LEAVE_TOLEAVE.toString(), TL.LEAVE_FORLEAVE.toString())) {
            return;
        }
        if (faction.getFPlayers().size() == 1 && Econ.shouldBeUsed() && FactionsPlugin.getInstance().conf().economy().isBankEnabled() && (!bl4 || FactionsPlugin.getInstance().conf().economy().isBankPermanentFactionSendBalanceToLastLeaver())) {
            double d = Econ.getBalance(faction);
            Econ.transferMoney(this, faction, this, d, false);
            if (d > 0.0) {
                String string = Econ.moneyString(d);
                this.msg(TL.COMMAND_DISBAND_HOLDINGS, string);
                FactionsPlugin.getInstance().log(this.getName() + " has been given bank holdings of " + string + " from disbanding " + faction.getTag() + ".");
            }
        }
        if (faction.isNormal()) {
            for (FPlayer fPlayer : faction.getFPlayersWhereOnline(true)) {
                fPlayer.msg(TL.LEAVE_LEFT, this.describeTo(fPlayer, true), faction.describeTo(fPlayer));
            }
            if (FactionsPlugin.getInstance().conf().logging().isFactionLeave()) {
                FactionsPlugin.getInstance().log(TL.LEAVE_LEFT.format(this.getName(), faction.getTag()));
            }
        }
        faction.removeAnnouncements(this);
        this.resetFactionData();
        if (FactionsPlugin.getInstance().conf().commands().fly().isEnable()) {
            this.setFlying(false, false);
        }
        if (faction.isNormal() && !bl4 && faction.getFPlayers().isEmpty()) {
            for (FPlayer fPlayer : FPlayers.getInstance().getOnlinePlayers()) {
                fPlayer.msg(TL.LEAVE_DISBANDED, faction.describeTo(fPlayer, true));
            }
            FactionsPlugin.getInstance().getServer().getPluginManager().callEvent((Event)new FactionAutoDisbandEvent(faction));
            Factions.getInstance().removeFaction(faction);
            if (FactionsPlugin.getInstance().conf().logging().isFactionDisband()) {
                FactionsPlugin.getInstance().log(TL.LEAVE_DISBANDEDLOG.format(faction.getTag(), "" + faction.getIntId(), this.getName()));
            }
        }
    }

    @Override
    public boolean canClaimForFaction(Faction faction) {
        return this.isAdminBypassing() || !faction.isWilderness() && faction == this.getFaction() && this.getFaction().hasAccess(this, PermissibleActions.TERRITORY, null) || faction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has((CommandSender)this.getPlayer()) || faction.isWarZone() && Permission.MANAGE_WAR_ZONE.has((CommandSender)this.getPlayer());
    }

    @Override
    public boolean canClaimForFactionAtLocation(Faction faction, Location location, boolean bl) {
        return this.canClaimForFactionAtLocation(faction, new FLocation(location), bl);
    }

    @Override
    public boolean canClaimForFactionAtLocation(Faction faction, FLocation fLocation, boolean bl) {
        FactionsPlugin factionsPlugin = FactionsPlugin.getInstance();
        String string = null;
        Faction faction2 = this.getFaction();
        Faction faction3 = Board.getInstance().getFactionAt(fLocation);
        int n = faction.getLandRounded();
        int n2 = factionsPlugin.conf().factions().claims().getBufferZone();
        int n3 = factionsPlugin.conf().worldBorder().getBuffer();
        if (factionsPlugin.conf().worldGuard().isCheckingEither() && factionsPlugin.getWorldguard() != null && factionsPlugin.getWorldguard().checkForRegionsInChunk(fLocation.getChunk())) {
            string = factionsPlugin.txt().parse(TL.CLAIM_PROTECTED.toString());
        } else if (factionsPlugin.conf().factions().claims().getWorldsNoClaiming().contains(fLocation.getWorldName())) {
            string = factionsPlugin.txt().parse(TL.CLAIM_DISABLED.toString());
        } else {
            if (this.isAdminBypassing()) {
                return true;
            }
            if (faction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has((CommandSender)this.getPlayer())) {
                return true;
            }
            if (faction.isWarZone() && Permission.MANAGE_WAR_ZONE.has((CommandSender)this.getPlayer())) {
                return true;
            }
            if (!faction.hasAccess(this, PermissibleActions.TERRITORY, null)) {
                string = factionsPlugin.txt().parse(TL.CLAIM_CANTCLAIM.toString(), faction.describeTo(this));
            } else if (faction == faction3) {
                string = factionsPlugin.txt().parse(TL.CLAIM_ALREADYOWN.toString(), faction.describeTo(this, true));
            } else if (faction.getFPlayers().size() < factionsPlugin.conf().factions().claims().getRequireMinFactionMembers()) {
                string = factionsPlugin.txt().parse(TL.CLAIM_MEMBERS.toString(), factionsPlugin.conf().factions().claims().getRequireMinFactionMembers());
            } else if (faction3.isSafeZone()) {
                string = factionsPlugin.txt().parse(TL.CLAIM_SAFEZONE.toString());
            } else if (faction3.isWarZone()) {
                string = factionsPlugin.txt().parse(TL.CLAIM_WARZONE.toString());
            } else if (factionsPlugin.getLandRaidControl() instanceof PowerControl && n >= faction.getPowerRounded()) {
                string = factionsPlugin.txt().parse(TL.CLAIM_POWER.toString());
            } else if (factionsPlugin.getLandRaidControl() instanceof DTRControl && n >= factionsPlugin.getLandRaidControl().getLandLimit(faction)) {
                string = factionsPlugin.txt().parse(TL.CLAIM_DTR_LAND.toString());
            } else if (factionsPlugin.conf().factions().claims().getLandsMax() != 0 && n >= factionsPlugin.conf().factions().claims().getLandsMax() && faction.isNormal()) {
                string = factionsPlugin.txt().parse(TL.CLAIM_LIMIT.toString());
            } else if (faction3.getRelationTo(faction) == Relation.ALLY) {
                string = factionsPlugin.txt().parse(TL.CLAIM_ALLY.toString());
            } else if (!(!factionsPlugin.conf().factions().claims().isMustBeConnected() || this.isAdminBypassing() || faction2.getLandRoundedInWorld(fLocation.getWorldName()) <= 0 || Board.getInstance().isConnectedLocation(fLocation, faction2) || factionsPlugin.conf().factions().claims().isCanBeUnconnectedIfOwnedByOtherFaction() && faction3.isNormal())) {
                string = factionsPlugin.conf().factions().claims().isCanBeUnconnectedIfOwnedByOtherFaction() ? factionsPlugin.txt().parse(TL.CLAIM_CONTIGIOUS.toString()) : factionsPlugin.txt().parse(TL.CLAIM_FACTIONCONTIGUOUS.toString());
            } else if (!(faction3.isNormal() && factionsPlugin.conf().factions().claims().isAllowOverClaimAndIgnoringBuffer() && faction3.hasLandInflation() || n2 <= 0 || !Board.getInstance().hasFactionWithin(fLocation, faction2, n2))) {
                string = factionsPlugin.txt().parse(TL.CLAIM_TOOCLOSETOOTHERFACTION.format(n2));
            } else if (fLocation.isOutsideWorldBorder(n3)) {
                string = n3 > 0 ? factionsPlugin.txt().parse(TL.CLAIM_OUTSIDEBORDERBUFFER.format(n3)) : factionsPlugin.txt().parse(TL.CLAIM_OUTSIDEWORLDBORDER.toString());
            } else if (faction3.isNormal()) {
                if (faction2.isPeaceful()) {
                    string = factionsPlugin.txt().parse(TL.CLAIM_PEACEFUL.toString(), faction3.getTag(this));
                } else if (faction3.isPeaceful()) {
                    string = factionsPlugin.txt().parse(TL.CLAIM_PEACEFULTARGET.toString(), faction3.getTag(this));
                } else if (!faction3.hasLandInflation()) {
                    string = factionsPlugin.txt().parse(TL.CLAIM_THISISSPARTA.toString(), faction3.getTag(this));
                } else if (faction3.hasLandInflation() && !factionsPlugin.conf().factions().claims().isAllowOverClaim()) {
                    string = factionsPlugin.txt().parse(TL.CLAIM_OVERCLAIM_DISABLED.toString());
                } else if (!Board.getInstance().isBorderLocation(fLocation)) {
                    string = factionsPlugin.txt().parse(TL.CLAIM_BORDER.toString());
                }
            }
        }
        if (bl && string != null) {
            this.msg(string, new Object[0]);
        }
        return string == null;
    }

    @Override
    public boolean attemptClaim(Faction faction, Location location, boolean bl) {
        return this.attemptClaim(faction, new FLocation(location), bl);
    }

    @Override
    public boolean attemptClaim(Faction faction, FLocation fLocation, boolean bl) {
        Faction faction2 = Board.getInstance().getFactionAt(fLocation);
        int n = faction.getLandRounded();
        if (!this.canClaimForFactionAtLocation(faction, fLocation, bl)) {
            return false;
        }
        boolean bl2 = Econ.shouldBeUsed() && !this.isAdminBypassing() && !faction.isSafeZone() && !faction.isWarZone();
        double d = 0.0;
        EconomyParticipator economyParticipator = null;
        if (bl2) {
            d = Econ.calculateClaimCost(n, faction2.isNormal());
            if (FactionsPlugin.getInstance().conf().economy().getClaimUnconnectedFee() != 0.0 && faction.getLandRoundedInWorld(fLocation.getWorldName()) > 0 && !Board.getInstance().isConnectedLocation(fLocation, faction)) {
                d += FactionsPlugin.getInstance().conf().economy().getClaimUnconnectedFee();
            }
            if (!Econ.hasAtLeast(economyParticipator = FactionsPlugin.getInstance().conf().economy().isBankEnabled() && FactionsPlugin.getInstance().conf().economy().isBankFactionPaysLandCosts() && this.hasFaction() && this.getFaction().hasAccess(this, PermissibleActions.ECONOMY, null) ? this.getFaction() : this, d, TL.CLAIM_TOCLAIM.toString())) {
                return false;
            }
        }
        LandClaimEvent landClaimEvent = new LandClaimEvent(fLocation, faction, this);
        Bukkit.getServer().getPluginManager().callEvent((Event)landClaimEvent);
        if (landClaimEvent.isCancelled()) {
            return false;
        }
        if (bl2 && !Econ.modifyMoney(economyParticipator, -d, TL.CLAIM_TOCLAIM.toString(), TL.CLAIM_FORCLAIM.toString())) {
            return false;
        }
        if (bl2 && faction2.isNormal() && faction2.hasLandInflation()) {
            Econ.modifyMoney(economyParticipator, FactionsPlugin.getInstance().conf().economy().getOverclaimRewardMultiplier(), TL.CLAIM_TOOVERCLAIM.toString(), TL.CLAIM_FOROVERCLAIM.toString());
        }
        if (LWC.getEnabled() && faction.isNormal() && FactionsPlugin.getInstance().conf().lwc().isResetLocksOnCapture()) {
            LWC.clearOtherLocks(fLocation, this.getFaction());
        }
        HashSet<FPlayer> hashSet = new HashSet<FPlayer>();
        hashSet.add(this);
        hashSet.addAll(faction.getFPlayersWhereOnline(true));
        for (FPlayer fPlayer : hashSet) {
            fPlayer.msg(TL.CLAIM_CLAIMED, this.describeTo(fPlayer, true), faction.describeTo(fPlayer), faction2.describeTo(fPlayer));
        }
        Board.getInstance().setFactionAt(faction, fLocation);
        if (FactionsPlugin.getInstance().conf().logging().isLandClaims()) {
            FactionsPlugin.getInstance().log(TL.CLAIM_CLAIMEDLOG.toString(), this.getName(), fLocation.getCoordString(), faction.getTag());
        }
        return true;
    }

    @Override
    public boolean attemptUnclaim(Faction faction, FLocation fLocation, boolean bl) {
        Faction faction2 = Board.getInstance().getFactionAt(fLocation);
        if (!faction2.equals(faction)) {
            this.msg(TL.COMMAND_UNCLAIM_WRONGFACTIONOTHER, new Object[0]);
            return false;
        }
        if (faction2.isSafeZone()) {
            if (Permission.MANAGE_SAFE_ZONE.has((CommandSender)this.getPlayer())) {
                Board.getInstance().removeAt(fLocation);
                this.msg(TL.COMMAND_UNCLAIM_SAFEZONE_SUCCESS, new Object[0]);
                if (FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
                    FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIM_LOG.format(this.getName(), fLocation.getCoordString(), faction2.getTag()));
                }
                return true;
            }
            if (bl) {
                this.msg(TL.COMMAND_UNCLAIM_SAFEZONE_NOPERM, new Object[0]);
            }
            return false;
        }
        if (faction2.isWarZone()) {
            if (Permission.MANAGE_WAR_ZONE.has((CommandSender)this.getPlayer())) {
                Board.getInstance().removeAt(fLocation);
                this.msg(TL.COMMAND_UNCLAIM_WARZONE_SUCCESS, new Object[0]);
                if (FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
                    FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIM_LOG.format(this.getName(), fLocation.getCoordString(), faction2.getTag()));
                }
                return true;
            }
            if (bl) {
                this.msg(TL.COMMAND_UNCLAIM_WARZONE_NOPERM, new Object[0]);
            }
            return false;
        }
        if (this.isAdminBypassing()) {
            LandUnclaimEvent landUnclaimEvent = new LandUnclaimEvent(fLocation, faction2, this);
            Bukkit.getServer().getPluginManager().callEvent((Event)landUnclaimEvent);
            if (landUnclaimEvent.isCancelled()) {
                return false;
            }
            Board.getInstance().removeAt(fLocation);
            faction2.msg(TL.COMMAND_UNCLAIM_UNCLAIMED, this.describeTo(faction2, true));
            this.msg(TL.COMMAND_UNCLAIM_UNCLAIMS, new Object[0]);
            if (FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
                FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIM_LOG.format(this.getName(), fLocation.getCoordString(), faction2.getTag()));
            }
            return true;
        }
        if (!this.hasFaction()) {
            if (bl) {
                this.msg(TL.COMMAND_UNCLAIM_NOTAMEMBER, new Object[0]);
            }
            return false;
        }
        if (!faction2.hasAccess(this, PermissibleActions.TERRITORY, fLocation)) {
            if (bl) {
                this.msg(TL.CLAIM_CANTUNCLAIM, faction2.describeTo(this));
            }
            return false;
        }
        if (this.getFaction() != faction2) {
            if (bl) {
                this.msg(TL.COMMAND_UNCLAIM_WRONGFACTION, new Object[0]);
            }
            return false;
        }
        LandUnclaimEvent landUnclaimEvent = new LandUnclaimEvent(fLocation, faction2, this);
        Bukkit.getServer().getPluginManager().callEvent((Event)landUnclaimEvent);
        if (landUnclaimEvent.isCancelled()) {
            return false;
        }
        if (Econ.shouldBeUsed()) {
            double d = Econ.calculateClaimRefund(this.getFaction().getLandRounded());
            if (FactionsPlugin.getInstance().conf().economy().isBankEnabled() && FactionsPlugin.getInstance().conf().economy().isBankFactionPaysLandCosts() ? !Econ.modifyMoney(this.getFaction(), d, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString()) : !Econ.modifyMoney(this, d, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                return false;
            }
        }
        Board.getInstance().removeAt(fLocation);
        this.getFaction().msg(TL.COMMAND_UNCLAIM_FACTIONUNCLAIMED, this.describeTo(this.getFaction(), true));
        if (FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
            FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIM_LOG.format(this.getName(), fLocation.getCoordString(), faction2.getTag()));
        }
        return true;
    }

    public boolean shouldBeSaved() {
        return this.hasFaction() || FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl && ((double)this.getPowerRounded() != FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getPlayerStarting() || this.getPowerBoost() != 0.0);
    }

    @Override
    public void msg(String string, Object ... objectArray) {
        this.sendMessage(FactionsPlugin.getInstance().txt().parse(string, objectArray));
    }

    @Override
    public void msg(TL tL, Object ... objectArray) {
        this.msg(tL.toString(), objectArray);
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer((UUID)UUID.fromString(this.getId()));
    }

    @Override
    public boolean isOnline() {
        Player player = this.getPlayer();
        return player != null && FactionsPlugin.getInstance().worldUtil().isEnabled(player.getWorld());
    }

    @Override
    public boolean isOnlineAndVisibleTo(Player player) {
        Player player2 = this.getPlayer();
        return player2 != null && player.canSee(player2) && FactionsPlugin.getInstance().worldUtil().isEnabled(player.getWorld());
    }

    @Override
    public boolean isOffline() {
        return !this.isOnline();
    }

    @Override
    public void flightCheck() {
        if (FactionsPlugin.getInstance().conf().commands().fly().isEnable() && !this.isAdminBypassing()) {
            boolean bl = this.canFlyAtLocation(this.getLastStoodAt());
            if (this.isFlying() && !bl) {
                this.setFlying(false, false);
            } else if (this.isAutoFlying() && !this.isFlying() && bl) {
                this.setFlying(true);
            }
        }
    }

    @Override
    public boolean isFlying() {
        return this.isFlying;
    }

    @Override
    public void setFlying(boolean bl) {
        this.setFlying(bl, false);
    }

    @Override
    public void setFlying(boolean bl, boolean bl2) {
        int n;
        Player player = this.getPlayer();
        if (player != null) {
            player.setAllowFlight(bl);
            player.setFlying(bl);
        }
        if (!bl2) {
            this.msg(TL.COMMAND_FLY_CHANGE, bl ? "enabled" : "disabled");
        } else {
            this.msg(TL.COMMAND_FLY_DAMAGE, new Object[0]);
        }
        if (!bl && (n = FactionsPlugin.getInstance().conf().commands().fly().getFallDamageCooldown()) > 0) {
            this.setTakeFallDamage(false);
            new BukkitRunnable(){

                public void run() {
                    MemoryFPlayer.this.setTakeFallDamage(true);
                }
            }.runTaskLater((Plugin)FactionsPlugin.getInstance(), 20L * (long)n);
        }
        this.isFlying = bl;
    }

    @Override
    public boolean isAutoFlying() {
        return this.isAutoFlying;
    }

    @Override
    public void setAutoFlying(boolean bl) {
        this.msg(TL.COMMAND_FLY_AUTO, bl ? "enabled" : "disabled");
        this.isAutoFlying = bl;
    }

    @Override
    public boolean canFlyAtLocation() {
        return this.canFlyAtLocation(this.lastStoodAt);
    }

    @Override
    public boolean canFlyAtLocation(FLocation fLocation) {
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        if (faction.isWilderness()) {
            return Permission.FLY_WILDERNESS.has((CommandSender)this.getPlayer());
        }
        if (faction.isSafeZone()) {
            return Permission.FLY_SAFEZONE.has((CommandSender)this.getPlayer());
        }
        if (faction.isWarZone()) {
            return Permission.FLY_WARZONE.has((CommandSender)this.getPlayer());
        }
        if (this.isAdminBypassing) {
            return true;
        }
        return faction.hasAccess(this, PermissibleActions.FLY, fLocation);
    }

    @Override
    public boolean shouldTakeFallDamage() {
        return this.shouldTakeFallDamage;
    }

    @Override
    public void setTakeFallDamage(boolean bl) {
        this.shouldTakeFallDamage = bl;
    }

    @Override
    public boolean isSeeingChunk() {
        return this.seeingChunk;
    }

    @Override
    public void setSeeingChunk(boolean bl) {
        this.seeingChunk = bl;
        FactionsPlugin.getInstance().getSeeChunkUtil().updatePlayerInfo(UUID.fromString(this.getId()), bl);
    }

    @Override
    public boolean getFlyTrailsState() {
        return this.flyTrailsState;
    }

    @Override
    public void setFlyTrailsState(boolean bl) {
        this.flyTrailsState = bl;
        this.msg(TL.COMMAND_FLYTRAILS_CHANGE, bl ? "enabled" : "disabled");
    }

    @Override
    public String getFlyTrailsEffect() {
        return this.flyTrailsEffect;
    }

    @Override
    public void setFlyTrailsEffect(String string) {
        this.flyTrailsEffect = string;
        this.msg(TL.COMMAND_FLYTRAILS_PARTICLE_CHANGE, string);
    }

    @Override
    public void sendMessage(String string) {
        if (string.contains("{null}")) {
            return;
        }
        if (string.contains("/n/")) {
            for (String string2 : string.split("/n/")) {
                this.sendMessage(string2);
            }
            return;
        }
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(string);
    }

    @Override
    public void sendMessage(List<String> list) {
        for (String string : list) {
            this.sendMessage(string);
        }
    }

    @Override
    public int getMapHeight() {
        if (this.mapHeight < 1) {
            this.mapHeight = FactionsPlugin.getInstance().conf().map().getHeight();
        }
        return this.mapHeight;
    }

    @Override
    public void setMapHeight(int n) {
        this.mapHeight = Math.min(n, FactionsPlugin.getInstance().conf().map().getHeight() * 2);
    }

    @Override
    public String getNameAndTitle(FPlayer fPlayer) {
        return this.getColorStringTo(fPlayer) + this.getNameAndTitle();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String string) {
        this.id = string;
    }

    @Override
    public void clearWarmup() {
        if (this.warmup != null) {
            Bukkit.getScheduler().cancelTask(this.warmupTask);
            this.stopWarmup();
        }
    }

    @Override
    public void stopWarmup() {
        this.warmup = null;
    }

    @Override
    public boolean isWarmingUp() {
        return this.warmup != null;
    }

    @Override
    public WarmUpUtil.Warmup getWarmupType() {
        return this.warmup;
    }

    @Override
    public void addWarmup(WarmUpUtil.Warmup warmup, int n) {
        if (this.warmup != null) {
            this.clearWarmup();
        }
        this.warmup = warmup;
        this.warmupTask = n;
    }

    private static class NameLookup {
        String name;

        private NameLookup() {
        }
    }
}

