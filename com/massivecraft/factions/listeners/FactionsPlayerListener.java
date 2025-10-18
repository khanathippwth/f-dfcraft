/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.player.PlayerArmorStandManipulateEvent
 *  org.bukkit.event.player.PlayerBucketEmptyEvent
 *  org.bukkit.event.player.PlayerBucketFillEvent
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerGameModeChangeEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.player.PlayerTakeLecternBookEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.util.NumberConversions
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.data.MemoryFPlayer;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.gui.GUI;
import com.massivecraft.factions.integration.Graves;
import com.massivecraft.factions.listeners.AbstractListener;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.scoreboards.sidebar.FDefaultSidebar;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import com.massivecraft.factions.util.VisualizeUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import moss.factions.shade.net.kyori.adventure.audience.Audience;
import moss.factions.shade.net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

public class FactionsPlayerListener
extends AbstractListener {
    private final FactionsPlugin plugin;
    private final HashMap<UUID, Long> showTimes = new HashMap();
    private final Map<String, InteractAttemptSpam> interactSpammers = new HashMap<String, InteractAttemptSpam>();

    public FactionsPlayerListener(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
        for (Player player : factionsPlugin.getServer().getOnlinePlayers()) {
            this.initPlayer(player);
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        this.initPlayer(playerJoinEvent.getPlayer());
        this.plugin.updateNotification(playerJoinEvent.getPlayer());
    }

    private void initPlayer(Player player) {
        Faction faction;
        Relation relation;
        MainConfig.Factions.Protection.TerritoryTeleport territoryTeleport;
        long l;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        ((MemoryFPlayer)fPlayer).setName(player.getName());
        this.plugin.getLandRaidControl().onJoin(fPlayer);
        FLocation fLocation = new FLocation(player.getLocation());
        fPlayer.setLastStoodAt(fLocation);
        if (this.plugin.conf().factions().protection().territoryTeleport().isEnabled() && (l = System.currentTimeMillis() - fPlayer.getLastLoginTime()) > 1000L * (territoryTeleport = this.plugin.conf().factions().protection().territoryTeleport()).getTimeSinceLastSignedIn() && territoryTeleport.isRelationToTeleportOut(relation = fPlayer.getRelationTo(faction = Board.getInstance().getFactionAt(fLocation)), faction)) {
            Location location = null;
            for (String string : territoryTeleport.getDestination().split(",")) {
                switch (string.trim().toLowerCase()) {
                    case "spawn": {
                        World world = this.plugin.getServer().getWorld(territoryTeleport.getDestinationSpawnWorld());
                        if (world == null) break;
                        location = world.getSpawnLocation();
                        break;
                    }
                    case "home": {
                        if (!fPlayer.hasFaction()) break;
                        location = fPlayer.getFaction().getHome();
                        break;
                    }
                    case "bed": {
                        location = player.getRespawnLocation();
                    }
                }
                if (location != null) break;
            }
            if (location == null) {
                location = ((World)this.plugin.getServer().getWorlds().getFirst()).getSpawnLocation();
            }
            this.plugin.teleport(player, location).thenAccept(bl -> {
                if (bl.booleanValue()) {
                    fPlayer.msg(TL.PLAYER_TELEPORTEDONJOIN, relation.nicename);
                }
            });
        }
        fPlayer.setLastLoginTime(System.currentTimeMillis());
        fPlayer.login();
        fPlayer.setOfflinePlayer(player);
        if (fPlayer.isSpyingChat() && !player.hasPermission(Permission.CHATSPY.node)) {
            fPlayer.setSpyingChat(false);
            FactionsPlugin.getInstance().log(Level.INFO, "Found %s spying chat without permission on login. Disabled their chat spying.", player.getName());
        }
        if (fPlayer.isAdminBypassing() && !player.hasPermission(Permission.BYPASS.node)) {
            fPlayer.setIsAdminBypassing(false);
            FactionsPlugin.getInstance().log(Level.INFO, "Found %s on admin Bypass without permission on login. Disabled it for them.", player.getName());
        }
        if (this.plugin.worldUtil().isEnabled(player.getWorld())) {
            this.initFactionWorld(fPlayer);
        }
    }

    private void initFactionWorld(final FPlayer fPlayer) {
        Faction faction;
        new BukkitRunnable(this){

            public void run() {
                if (fPlayer.isOnline()) {
                    fPlayer.getFaction().sendUnreadAnnouncements(fPlayer);
                }
            }
        }.runTaskLater((Plugin)FactionsPlugin.getInstance(), 33L);
        if (FactionsPlugin.getInstance().conf().scoreboard().constant().isEnabled()) {
            FScoreboard.init(fPlayer);
            FScoreboard.get(fPlayer).setDefaultSidebar(new FDefaultSidebar());
            FScoreboard.get(fPlayer).setSidebarVisibility(fPlayer.showScoreboard());
        }
        if (!(faction = fPlayer.getFaction()).isWilderness()) {
            for (FPlayer fPlayer2 : faction.getFPlayersWhereOnline(true)) {
                if (fPlayer2 == fPlayer || !fPlayer2.isMonitoringJoins()) continue;
                fPlayer2.msg(TL.FACTION_LOGIN, fPlayer.getName());
            }
        }
        fPlayer.setAutoLeave(!fPlayer.getPlayer().hasPermission(Permission.AUTO_LEAVE_BYPASS.node));
        fPlayer.setTakeFallDamage(true);
        if (fPlayer.isFlying()) {
            fPlayer.getPlayer().setAllowFlight(true);
            fPlayer.getPlayer().setFlying(true);
        }
        fPlayer.flightCheck();
        if (FactionsPlugin.getInstance().getSeeChunkUtil() != null) {
            FactionsPlugin.getInstance().getSeeChunkUtil().updatePlayerInfo(UUID.fromString(fPlayer.getId()), fPlayer.isSeeingChunk());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Faction faction;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(playerQuitEvent.getPlayer());
        FactionsPlugin.getInstance().getLandRaidControl().onQuit(fPlayer);
        fPlayer.setLastLoginTime(System.currentTimeMillis());
        fPlayer.logout();
        if (FactionsPlugin.getInstance().getStuckMap().containsKey(fPlayer.getPlayer().getUniqueId())) {
            FPlayers.getInstance().getByPlayer(fPlayer.getPlayer()).msg(TL.COMMAND_STUCK_CANCELLED, new Object[0]);
            FactionsPlugin.getInstance().getStuckMap().remove(fPlayer.getPlayer().getUniqueId());
            FactionsPlugin.getInstance().getTimers().remove(fPlayer.getPlayer().getUniqueId());
        }
        if (!(faction = fPlayer.getFaction()).isWilderness()) {
            faction.memberLoggedOff();
        }
        if (!faction.isWilderness()) {
            for (FPlayer fPlayer2 : faction.getFPlayersWhereOnline(true)) {
                if (fPlayer2 == fPlayer || !fPlayer2.isMonitoringJoins()) continue;
                fPlayer2.msg(TL.FACTION_LOGOUT, fPlayer.getName());
            }
        }
        FScoreboard.remove(fPlayer, playerQuitEvent.getPlayer());
        if (FactionsPlugin.getInstance().getSeeChunkUtil() != null) {
            FactionsPlugin.getInstance().getSeeChunkUtil().updatePlayerInfo(UUID.fromString(fPlayer.getId()), false);
        }
        fPlayer.setOfflinePlayer(null);
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onGameMode(PlayerGameModeChangeEvent playerGameModeChangeEvent) {
        if (!this.plugin.worldUtil().isEnabled((CommandSender)playerGameModeChangeEvent.getPlayer())) {
            return;
        }
        if (playerGameModeChangeEvent.getNewGameMode() == GameMode.SURVIVAL) {
            final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(playerGameModeChangeEvent.getPlayer());
            new BukkitRunnable(this){

                public void run() {
                    if (fPlayer.isFlying() && fPlayer.getPlayer() != null) {
                        fPlayer.getPlayer().setAllowFlight(true);
                        fPlayer.getPlayer().setFlying(true);
                    }
                    fPlayer.flightCheck();
                }
            }.runTask((Plugin)this.plugin);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        this.handleMovement(playerMoveEvent.getPlayer(), playerMoveEvent.getFrom(), playerMoveEvent.getTo());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        this.handleMovement(playerTeleportEvent.getPlayer(), playerTeleportEvent.getFrom(), playerTeleportEvent.getTo());
    }

    private void handleMovement(Player player, Location location, Location location2) {
        boolean bl;
        if (!this.plugin.worldUtil().isEnabled((CommandSender)player)) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (location.getBlockX() != location2.getBlockX() || location.getBlockY() != location2.getBlockY() || location.getBlockZ() != location2.getBlockZ() || location.getWorld() != location2.getWorld()) {
            VisualizeUtil.clear(player);
            if (fPlayer.isWarmingUp()) {
                fPlayer.clearWarmup();
                fPlayer.msg(TL.WARMUPS_CANCELLED, new Object[0]);
            }
        }
        if (location.getBlockX() >> 4 == location2.getBlockX() >> 4 && location.getBlockZ() >> 4 == location2.getBlockZ() >> 4 && location.getWorld() == location2.getWorld()) {
            return;
        }
        if (!this.plugin.worldUtil().isEnabled(location2.getWorld())) {
            return;
        }
        FLocation fLocation = new FLocation(location);
        FLocation fLocation2 = new FLocation(location2);
        if (fLocation.equals(fLocation2)) {
            return;
        }
        fPlayer.setLastStoodAt(fLocation2);
        boolean bl2 = fPlayer.canFlyAtLocation();
        if (fPlayer.getAutoClaimFor() != null) {
            fPlayer.attemptClaim(fPlayer.getAutoClaimFor(), fLocation2, true);
        } else if (fPlayer.getAutoUnclaimFor() != null) {
            fPlayer.attemptUnclaim(fPlayer.getAutoUnclaimFor(), fLocation2, true);
        }
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        Faction faction2 = Board.getInstance().getFactionAt(fLocation2);
        boolean bl3 = bl = faction != faction2;
        if (this.plugin.conf().commands().fly().isEnable() && !fPlayer.isAdminBypassing()) {
            boolean bl4 = fPlayer.canFlyAtLocation(fLocation2);
            if (!bl) {
                if (bl4 && !bl2 && fPlayer.isFlying() && this.plugin.conf().commands().fly().isDisableFlightDuringAutoclaim()) {
                    fPlayer.setFlying(false);
                }
            } else if (fPlayer.isFlying() && !bl4) {
                fPlayer.setFlying(false);
            } else if (fPlayer.isAutoFlying() && !fPlayer.isFlying() && bl4) {
                fPlayer.setFlying(true);
            }
        }
        if (fPlayer.isMapAutoUpdating()) {
            if (!this.showTimes.containsKey(player.getUniqueId()) || this.showTimes.get(player.getUniqueId()) < System.currentTimeMillis()) {
                Audience audience = FactionsPlugin.getInstance().getAdventure().player(player);
                for (Component component : Board.getInstance().getMap(fPlayer, fLocation2, player.getLocation().getYaw())) {
                    audience.sendMessage(component);
                }
                this.showTimes.put(player.getUniqueId(), System.currentTimeMillis() + (long)FactionsPlugin.getInstance().conf().commands().map().getCooldown());
            }
        } else {
            Faction faction3 = fPlayer.getFaction();
            String string = faction3.getOwnerListString(fLocation2);
            if (bl) {
                fPlayer.sendFactionHereMessage(faction);
                if (FactionsPlugin.getInstance().conf().factions().ownedArea().isEnabled() && FactionsPlugin.getInstance().conf().factions().ownedArea().isMessageOnBorder() && faction3 == faction2 && !string.isEmpty()) {
                    fPlayer.sendMessage(TL.GENERIC_OWNERS.format(string));
                }
            } else if (FactionsPlugin.getInstance().conf().factions().ownedArea().isEnabled() && FactionsPlugin.getInstance().conf().factions().ownedArea().isMessageInsideTerritory() && faction3 == faction2 && !faction3.isWilderness()) {
                String string2 = faction3.getOwnerListString(fLocation);
                if (FactionsPlugin.getInstance().conf().factions().ownedArea().isMessageByChunk() || !string2.equals(string)) {
                    if (!string.isEmpty()) {
                        fPlayer.sendMessage(TL.GENERIC_OWNERS.format(string));
                    } else if (!TL.GENERIC_PUBLICLAND.toString().isEmpty()) {
                        fPlayer.sendMessage(TL.GENERIC_PUBLICLAND.toString());
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEntityEvent playerInteractEntityEvent) {
        if (!this.plugin.worldUtil().isEnabled(playerInteractEntityEvent.getPlayer().getWorld())) {
            return;
        }
        boolean bl = false;
        EntityType entityType = playerInteractEntityEvent.getRightClicked().getType();
        if (entityType == EntityType.ITEM_FRAME || entityType == EntityType.GLOW_ITEM_FRAME) {
            if (!this.canPlayerUseBlock(playerInteractEntityEvent.getPlayer(), Material.ITEM_FRAME, playerInteractEntityEvent.getRightClicked().getLocation(), false)) {
                playerInteractEntityEvent.setCancelled(true);
            }
        } else if (entityType == EntityType.HORSE || entityType == EntityType.SKELETON_HORSE || entityType == EntityType.ZOMBIE_HORSE || entityType == EntityType.DONKEY || entityType == EntityType.MULE || entityType == EntityType.LLAMA || entityType == EntityType.TRADER_LLAMA || entityType == EntityType.PIG || entityType == EntityType.LEASH_HITCH || entityType == EntityType.MINECART_CHEST || entityType == EntityType.MINECART_FURNACE || entityType == EntityType.MINECART_HOPPER || entityType == EntityType.CHEST_BOAT) {
            bl = true;
        }
        if (bl && !FactionsPlugin.getInstance().conf().factions().protection().getEntityInteractExceptions().contains(playerInteractEntityEvent.getRightClicked().getType().name()) && !this.playerCanInteractHere(playerInteractEntityEvent.getPlayer(), playerInteractEntityEvent.getRightClicked().getLocation())) {
            playerInteractEntityEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        if (!this.plugin.worldUtil().isEnabled(playerInteractEvent.getPlayer().getWorld())) {
            return;
        }
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK && playerInteractEvent.getAction() != Action.PHYSICAL) {
            return;
        }
        Block block = playerInteractEvent.getClickedBlock();
        Player player = playerInteractEvent.getPlayer();
        if (block == null) {
            return;
        }
        if (Graves.allowAnyway(block)) {
            return;
        }
        if (playerInteractEvent.getAction() == Action.PHYSICAL && block.getType() == Material.FARMLAND && !FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), PermissibleActions.DESTROY, false)) {
            playerInteractEvent.setCancelled(true);
        }
        if (!this.canPlayerUseBlock(player, block.getType(), block.getLocation(), false)) {
            playerInteractEvent.setCancelled(true);
            if (block.getType().name().endsWith("_PLATE")) {
                return;
            }
            if (FactionsPlugin.getInstance().conf().exploits().isInteractionSpam()) {
                int n;
                String string = player.getName();
                InteractAttemptSpam interactAttemptSpam = this.interactSpammers.get(string);
                if (interactAttemptSpam == null) {
                    interactAttemptSpam = new InteractAttemptSpam();
                    this.interactSpammers.put(string, interactAttemptSpam);
                }
                if ((n = interactAttemptSpam.increment()) >= 10) {
                    FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                    fPlayer.msg(TL.PLAYER_OUCH, new Object[0]);
                    player.damage((double)NumberConversions.floor((double)((double)n / 10.0)));
                }
            }
            return;
        }
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack itemStack = playerInteractEvent.getItem();
        if (itemStack != null) {
            Material material = itemStack.getType();
            String string = itemStack.getType().name();
            if (!(material != Material.ARMOR_STAND && material != Material.END_CRYSTAL && !string.contains("MINECART") || FactionsPlugin.getInstance().conf().factions().specialCase().getIgnoreBuildMaterials().contains(itemStack.getType()) || FactionsBlockListener.playerCanBuildDestroyBlock(playerInteractEvent.getPlayer(), playerInteractEvent.getClickedBlock().getRelative(playerInteractEvent.getBlockFace()).getLocation(), PermissibleActions.BUILD, false))) {
                playerInteractEvent.setCancelled(true);
            }
        }
        if (!this.playerCanUseItemHere(player, block.getLocation(), playerInteractEvent.getMaterial(), false)) {
            playerInteractEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent signChangeEvent) {
        if (!this.plugin.worldUtil().isEnabled(signChangeEvent.getBlock().getWorld())) {
            return;
        }
        if (!this.playerCanUseItemHere(signChangeEvent.getPlayer(), signChangeEvent.getBlock().getLocation(), signChangeEvent.getBlock().getType(), false, false)) {
            signChangeEvent.setCancelled(true);
        }
    }

    public boolean playerCanUseItemHere(Player player, Location location, Material material, boolean bl) {
        return this.playerCanUseItemHere(player, location, material, true, bl);
    }

    public boolean playerCanUseItemHere(Player player, Location location, Material material, boolean bl, boolean bl2) {
        String string = player.getName();
        MainConfig.Factions factions = FactionsPlugin.getInstance().conf().factions();
        if (factions.protection().getPlayersWhoBypassAllProtection().contains(string)) {
            return true;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer.isAdminBypassing()) {
            return true;
        }
        FLocation fLocation = new FLocation(location);
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        if (FactionsPlugin.getInstance().getLandRaidControl().isRaidable(faction)) {
            return true;
        }
        if (bl && (faction.hasPlayersOnline() ? !factions.protection().getTerritoryDenyUsageMaterials().contains(material) : !factions.protection().getTerritoryDenyUsageMaterialsWhenOffline().contains(material))) {
            return true;
        }
        if (faction.isWilderness()) {
            if (!factions.protection().isWildernessDenyUsage() || factions.protection().getWorldsNoWildernessProtection().contains(location.getWorld().getName())) {
                return true;
            }
            if (!bl2) {
                fPlayer.msg(TL.PLAYER_USE_WILDERNESS, TextUtil.getMaterialName(material));
            }
            return false;
        }
        if (faction.isSafeZone()) {
            if (!factions.protection().isSafeZoneDenyUsage() || Permission.MANAGE_SAFE_ZONE.has((CommandSender)player)) {
                return true;
            }
            if (!bl2) {
                fPlayer.msg(TL.PLAYER_USE_SAFEZONE, TextUtil.getMaterialName(material));
            }
            return false;
        }
        if (faction.isWarZone()) {
            if (!factions.protection().isWarZoneDenyUsage() || Permission.MANAGE_WAR_ZONE.has((CommandSender)player)) {
                return true;
            }
            if (!bl2) {
                fPlayer.msg(TL.PLAYER_USE_WARZONE, TextUtil.getMaterialName(material));
            }
            return false;
        }
        if (!faction.hasAccess(fPlayer, PermissibleActions.ITEM, fLocation)) {
            if (!bl2) {
                fPlayer.msg(TL.PLAYER_USE_TERRITORY, TextUtil.getMaterialName(material), faction.getTag(fPlayer.getFaction()));
            }
            return false;
        }
        if (factions.ownedArea().isEnabled() && factions.ownedArea().isDenyUsage() && !faction.playerHasOwnershipRights(fPlayer, fLocation)) {
            if (!bl2) {
                fPlayer.msg(TL.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), faction.getOwnerListString(fLocation));
            }
            return false;
        }
        return true;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        if (!this.plugin.worldUtil().isEnabled(playerRespawnEvent.getPlayer().getWorld())) {
            return;
        }
        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(playerRespawnEvent.getPlayer());
        FactionsPlugin.getInstance().getLandRaidControl().onRespawn(fPlayer);
        Location location = fPlayer.getFaction().getHome();
        MainConfig.Factions factions = FactionsPlugin.getInstance().conf().factions();
        if (factions.homes().isEnabled() && factions.homes().isTeleportToOnDeath() && location != null && (factions.landRaidControl().power().isRespawnHomeFromNoPowerLossWorlds() || !factions.landRaidControl().power().getWorldsNoPowerLoss().contains(playerRespawnEvent.getPlayer().getWorld().getName()))) {
            playerRespawnEvent.setRespawnLocation(location);
        }
        new BukkitRunnable(this){

            public void run() {
                fPlayer.flightCheck();
            }
        }.runTask((Plugin)FactionsPlugin.getInstance());
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onChangedWorld(PlayerChangedWorldEvent playerChangedWorldEvent) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(playerChangedWorldEvent.getPlayer());
        boolean bl = this.plugin.worldUtil().isEnabled(playerChangedWorldEvent.getPlayer().getWorld());
        if (!bl) {
            FScoreboard.remove(fPlayer, playerChangedWorldEvent.getPlayer());
            if (fPlayer.isFlying()) {
                fPlayer.setFlying(false);
            }
            return;
        }
        FLocation fLocation = new FLocation(playerChangedWorldEvent.getPlayer().getLocation());
        fPlayer.setLastStoodAt(fLocation);
        fPlayer.flightCheck();
        if (!playerChangedWorldEvent.getFrom().equals((Object)playerChangedWorldEvent.getPlayer().getWorld()) && !this.plugin.worldUtil().isEnabled(playerChangedWorldEvent.getFrom())) {
            FactionsPlugin.getInstance().getLandRaidControl().update(fPlayer);
            this.initFactionWorld(fPlayer);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent playerBucketEmptyEvent) {
        if (!this.plugin.worldUtil().isEnabled(playerBucketEmptyEvent.getPlayer().getWorld())) {
            return;
        }
        Block block = playerBucketEmptyEvent.getBlockClicked();
        Player player = playerBucketEmptyEvent.getPlayer();
        if (!this.playerCanUseItemHere(player, block.getRelative(playerBucketEmptyEvent.getBlockFace()).getLocation(), playerBucketEmptyEvent.getBucket(), false)) {
            playerBucketEmptyEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerBucketFill(PlayerBucketFillEvent playerBucketFillEvent) {
        if (!this.plugin.worldUtil().isEnabled(playerBucketFillEvent.getPlayer().getWorld())) {
            return;
        }
        Block block = playerBucketFillEvent.getBlockClicked();
        Player player = playerBucketFillEvent.getPlayer();
        if (!this.playerCanUseItemHere(player, block.getLocation(), playerBucketFillEvent.getBucket(), false)) {
            playerBucketFillEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void doYouHaveALibraryCard(PlayerTakeLecternBookEvent playerTakeLecternBookEvent) {
        Player player = playerTakeLecternBookEvent.getPlayer();
        if (this.plugin.conf().factions().protection().getPlayersWhoBypassAllProtection().contains(player.getName())) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer.isAdminBypassing()) {
            return;
        }
        FLocation fLocation = new FLocation(playerTakeLecternBookEvent.getLectern().getLocation());
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        if (this.plugin.getLandRaidControl().isRaidable(faction)) {
            return;
        }
        PermissibleActions permissibleActions = PermissibleActions.CONTAINER;
        if (!faction.hasAccess(fPlayer, permissibleActions, fLocation)) {
            fPlayer.msg(TL.GENERIC_NOPERMISSION, permissibleActions.getShortDescription());
            playerTakeLecternBookEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerInteract(PlayerArmorStandManipulateEvent playerArmorStandManipulateEvent) {
        if (!this.plugin.worldUtil().isEnabled(playerArmorStandManipulateEvent.getPlayer().getWorld())) {
            return;
        }
        if (!this.canPlayerUseBlock(playerArmorStandManipulateEvent.getPlayer(), Material.ARMOR_STAND, playerArmorStandManipulateEvent.getRightClicked().getLocation(), false)) {
            playerArmorStandManipulateEvent.setCancelled(true);
        }
    }

    public static boolean preventCommand(String object, Player player) {
        String string;
        MainConfig.Factions.Protection protection = FactionsPlugin.getInstance().conf().factions().protection();
        if (protection.getTerritoryNeutralDenyCommands().isEmpty() && protection.getTerritoryEnemyDenyCommands().isEmpty() && protection.getPermanentFactionMemberDenyCommands().isEmpty() && protection.getWildernessDenyCommands().isEmpty() && protection.getTerritoryAllyDenyCommands().isEmpty() && protection.getTerritoryTruceDenyCommands().isEmpty() && protection.getWarzoneDenyCommands().isEmpty()) {
            return false;
        }
        object = ((String)object).toLowerCase();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (((String)object).startsWith("/")) {
            string = ((String)object).substring(1);
        } else {
            string = object;
            object = "/" + (String)object;
        }
        if (fPlayer.hasFaction() && !fPlayer.isAdminBypassing() && !protection.getPermanentFactionMemberDenyCommands().isEmpty() && fPlayer.getFaction().isPermanent() && FactionsPlayerListener.isCommandInSet((String)object, string, protection.getPermanentFactionMemberDenyCommands())) {
            fPlayer.msg(TL.PLAYER_COMMAND_PERMANENT, object);
            return true;
        }
        Faction faction = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        if (faction.isWilderness() && !protection.getWildernessDenyCommands().isEmpty() && !fPlayer.isAdminBypassing() && FactionsPlayerListener.isCommandInSet((String)object, string, protection.getWildernessDenyCommands())) {
            fPlayer.msg(TL.PLAYER_COMMAND_WILDERNESS, object);
            return true;
        }
        Relation relation = faction.getRelationTo(fPlayer);
        if (faction.isNormal() && relation.isAlly() && !protection.getTerritoryAllyDenyCommands().isEmpty() && !fPlayer.isAdminBypassing() && FactionsPlayerListener.isCommandInSet((String)object, string, protection.getTerritoryAllyDenyCommands())) {
            fPlayer.msg(TL.PLAYER_COMMAND_ALLY, object);
            return true;
        }
        if (faction.isNormal() && relation.isTruce() && !protection.getTerritoryTruceDenyCommands().isEmpty() && !fPlayer.isAdminBypassing() && FactionsPlayerListener.isCommandInSet((String)object, string, protection.getTerritoryTruceDenyCommands())) {
            fPlayer.msg(TL.PLAYER_COMMAND_TRUCE, object);
            return true;
        }
        if (faction.isNormal() && relation.isNeutral() && !protection.getTerritoryNeutralDenyCommands().isEmpty() && !fPlayer.isAdminBypassing() && FactionsPlayerListener.isCommandInSet((String)object, string, protection.getTerritoryNeutralDenyCommands())) {
            fPlayer.msg(TL.PLAYER_COMMAND_NEUTRAL, object);
            return true;
        }
        if (faction.isNormal() && relation.isEnemy() && !protection.getTerritoryEnemyDenyCommands().isEmpty() && !fPlayer.isAdminBypassing() && FactionsPlayerListener.isCommandInSet((String)object, string, protection.getTerritoryEnemyDenyCommands())) {
            fPlayer.msg(TL.PLAYER_COMMAND_ENEMY, object);
            return true;
        }
        if (faction.isWarZone() && !protection.getWarzoneDenyCommands().isEmpty() && !fPlayer.isAdminBypassing() && FactionsPlayerListener.isCommandInSet((String)object, string, protection.getWarzoneDenyCommands())) {
            fPlayer.msg(TL.PLAYER_COMMAND_WARZONE, object);
            return true;
        }
        return false;
    }

    private static boolean isCommandInSet(String string, String string2, Set<String> set) {
        for (String string3 : set) {
            if (string3 == null || !string.startsWith(string3 = string3.toLowerCase()) && !string2.startsWith(string3)) continue;
            return true;
        }
        return false;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteractGUI(InventoryClickEvent inventoryClickEvent) {
        if (!this.plugin.worldUtil().isEnabled(inventoryClickEvent.getWhoClicked().getWorld())) {
            return;
        }
        Inventory inventory = this.getClickedInventory(inventoryClickEvent);
        if (inventory == null) {
            return;
        }
        InventoryHolder inventoryHolder = inventory.getHolder();
        if (inventoryHolder instanceof GUI) {
            GUI gUI = (GUI)inventoryHolder;
            inventoryClickEvent.setCancelled(true);
            gUI.click(inventoryClickEvent.getRawSlot(), inventoryClickEvent.getClick());
        }
    }

    private Inventory getClickedInventory(InventoryClickEvent inventoryClickEvent) {
        int n = inventoryClickEvent.getRawSlot();
        InventoryView inventoryView = inventoryClickEvent.getView();
        if (n < 0 || n >= inventoryView.countSlots()) {
            return null;
        }
        if (n < inventoryView.getTopInventory().getSize()) {
            return inventoryView.getTopInventory();
        }
        return inventoryView.getBottomInventory();
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerMoveGUI(InventoryDragEvent inventoryDragEvent) {
        if (!this.plugin.worldUtil().isEnabled(inventoryDragEvent.getWhoClicked().getWorld())) {
            return;
        }
        if (inventoryDragEvent.getInventory().getHolder() instanceof GUI) {
            inventoryDragEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerKick(PlayerKickEvent playerKickEvent) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(playerKickEvent.getPlayer());
        if (fPlayer == null) {
            return;
        }
        if (FactionsPlugin.getInstance().conf().factions().other().isRemovePlayerDataWhenBanned() && playerKickEvent.getReason().equals("Banned by admin.")) {
            if (fPlayer.getRole() == Role.ADMIN) {
                fPlayer.getFaction().promoteNewLeader();
            }
            fPlayer.leave(false);
            fPlayer.remove();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public final void onFactionJoin(FPlayerJoinEvent fPlayerJoinEvent) {
        FTeamWrapper.applyUpdatesLater(fPlayerJoinEvent.getFaction());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onFactionLeave(FPlayerLeaveEvent fPlayerLeaveEvent) {
        FTeamWrapper.applyUpdatesLater(fPlayerLeaveEvent.getFaction());
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        if (!this.plugin.worldUtil().isEnabled(playerCommandPreprocessEvent.getPlayer().getWorld())) {
            return;
        }
        String string = playerCommandPreprocessEvent.getMessage().split(" ")[0];
        if (FactionsPlugin.getInstance().conf().factions().chat().isTriggerPublicChat(string.startsWith("/") ? string.substring(1) : string)) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(playerCommandPreprocessEvent.getPlayer());
            fPlayer.setChatMode(ChatMode.PUBLIC);
            fPlayer.msg(TL.COMMAND_CHAT_MODE_PUBLIC, new Object[0]);
        }
        if (FactionsPlayerListener.preventCommand(playerCommandPreprocessEvent.getMessage(), playerCommandPreprocessEvent.getPlayer())) {
            if (this.plugin.logPlayerCommands()) {
                this.plugin.getLogger().info("[PLAYER_COMMAND] " + playerCommandPreprocessEvent.getPlayer().getName() + ": " + playerCommandPreprocessEvent.getMessage());
            }
            playerCommandPreprocessEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerPreLogin(PlayerLoginEvent playerLoginEvent) {
        FPlayers.getInstance().getByPlayer(playerLoginEvent.getPlayer());
    }

    private static class InteractAttemptSpam {
        private int attempts = 0;
        private long lastAttempt = System.currentTimeMillis();

        private InteractAttemptSpam() {
        }

        public int increment() {
            long l = System.currentTimeMillis();
            this.attempts = l > this.lastAttempt + 2000L ? 1 : ++this.attempts;
            this.lastAttempt = l;
            return this.attempts;
        }
    }
}

