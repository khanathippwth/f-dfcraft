/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockDamageEvent
 *  org.bukkit.event.block.BlockDispenseEvent
 *  org.bukkit.event.block.BlockExplodeEvent
 *  org.bukkit.event.block.BlockFormEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockPistonExtendEvent
 *  org.bukkit.event.block.BlockPistonRetractEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.block.EntityBlockFormEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.Directional
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.listeners.AbstractListener;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;

public class FactionsBlockListener
extends AbstractListener {
    public final FactionsPlugin plugin;

    public FactionsBlockListener(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        if (!this.plugin.worldUtil().isEnabled(blockPlaceEvent.getBlock().getWorld())) {
            return;
        }
        if (!blockPlaceEvent.canBuild()) {
            return;
        }
        if (blockPlaceEvent.getBlockPlaced().getType() == Material.FIRE) {
            return;
        }
        Faction faction = Board.getInstance().getFactionAt(new FLocation(blockPlaceEvent.getBlock().getLocation()));
        if (faction.isNormal() && !faction.isPeaceful() && FactionsPlugin.getInstance().conf().factions().specialCase().getIgnoreBuildMaterials().contains(blockPlaceEvent.getBlock().getType())) {
            return;
        }
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(blockPlaceEvent.getPlayer(), blockPlaceEvent.getBlock().getLocation(), PermissibleActions.BUILD, false)) {
            blockPlaceEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDispense(BlockDispenseEvent blockDispenseEvent) {
        Faction faction;
        FLocation fLocation;
        if (!this.plugin.worldUtil().isEnabled(blockDispenseEvent.getBlock().getWorld())) {
            return;
        }
        ItemStack itemStack = blockDispenseEvent.getItem();
        if (itemStack == null) {
            return;
        }
        Material material = itemStack.getType();
        FLocation fLocation2 = new FLocation(blockDispenseEvent.getBlock());
        if (fLocation2.equals(fLocation = new FLocation(blockDispenseEvent.getBlock().getRelative(((Directional)blockDispenseEvent.getBlock().getState().getData()).getFacing())))) {
            return;
        }
        Faction faction2 = Board.getInstance().getFactionAt(fLocation2);
        if (faction2 == (faction = Board.getInstance().getFactionAt(fLocation))) {
            return;
        }
        if (FactionsPlugin.getInstance().getLandRaidControl().isRaidable(faction)) {
            return;
        }
        MainConfig.Factions.Protection protection = FactionsPlugin.getInstance().conf().factions().protection();
        if (faction.hasPlayersOnline() ? !protection.getTerritoryDenyUsageMaterials().contains(material) : !protection.getTerritoryDenyUsageMaterialsWhenOffline().contains(material)) {
            return;
        }
        if (faction.isWilderness() ? !protection.isWildernessDenyUsage() || protection.getWorldsNoWildernessProtection().contains(blockDispenseEvent.getBlock().getLocation().getWorld().getName()) : (faction.isSafeZone() ? !protection.isSafeZoneDenyUsage() : (faction.isWarZone() ? !protection.isWarZoneDenyUsage() : faction.hasAccess(faction2, PermissibleActions.ITEM, fLocation)))) {
            return;
        }
        blockDispenseEvent.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockFromTo(BlockFromToEvent blockFromToEvent) {
        if (!this.plugin.worldUtil().isEnabled(blockFromToEvent.getBlock().getWorld())) {
            return;
        }
        boolean bl = FactionsPlugin.getInstance().conf().exploits().isLiquidFlow();
        boolean bl2 = FactionsPlugin.getInstance().conf().factions().protection().isSafeZonePreventLiquidFlowIn();
        boolean bl3 = FactionsPlugin.getInstance().conf().factions().protection().isWarZonePreventLiquidFlowIn();
        if ((bl || bl2 || bl3) && blockFromToEvent.getBlock().isLiquid() && blockFromToEvent.getToBlock().isEmpty()) {
            Faction faction;
            Faction faction2 = Board.getInstance().getFactionAt(new FLocation(blockFromToEvent.getBlock()));
            if (faction2 == (faction = Board.getInstance().getFactionAt(new FLocation(blockFromToEvent.getToBlock())))) {
                return;
            }
            if (bl && faction.isNormal()) {
                if (faction2.isNormal() && faction2.getRelationTo(faction).isAlly()) {
                    return;
                }
                blockFromToEvent.setCancelled(true);
            }
            if (bl2 && faction.isSafeZone() || bl3 && faction.isWarZone()) {
                blockFromToEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockForm(BlockFormEvent blockFormEvent) {
        if (!this.plugin.worldUtil().isEnabled(blockFormEvent.getBlock().getWorld())) {
            return;
        }
        if (blockFormEvent.getBlock().getType() == Material.ICE && FactionsPlugin.getInstance().conf().factions().protection().isTerritoryDenyIceFormation() && Board.getInstance().getFactionAt(new FLocation(blockFormEvent.getBlock())).isNormal()) {
            blockFormEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        if (!this.plugin.worldUtil().isEnabled(blockBreakEvent.getBlock().getWorld())) {
            return;
        }
        if (FactionsPlugin.getInstance().conf().factions().protection().getBreakExceptions().contains(blockBreakEvent.getBlock().getType()) && Board.getInstance().getFactionAt(new FLocation(blockBreakEvent.getBlock().getLocation())).isNormal()) {
            return;
        }
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(blockBreakEvent.getPlayer(), blockBreakEvent.getBlock().getLocation(), PermissibleActions.DESTROY, false)) {
            blockBreakEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockDamage(BlockDamageEvent blockDamageEvent) {
        if (!this.plugin.worldUtil().isEnabled(blockDamageEvent.getBlock().getWorld())) {
            return;
        }
        if (FactionsPlugin.getInstance().conf().factions().protection().getBreakExceptions().contains(blockDamageEvent.getBlock().getType()) && Board.getInstance().getFactionAt(new FLocation(blockDamageEvent.getBlock().getLocation())).isNormal()) {
            return;
        }
        if (blockDamageEvent.getInstaBreak() && !FactionsBlockListener.playerCanBuildDestroyBlock(blockDamageEvent.getPlayer(), blockDamageEvent.getBlock().getLocation(), PermissibleActions.DESTROY, false)) {
            blockDamageEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockExplode(BlockExplodeEvent blockExplodeEvent) {
        this.handleExplosion(blockExplodeEvent.getBlock().getLocation(), null, (Cancellable)blockExplodeEvent, blockExplodeEvent.blockList());
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPistonExtend(BlockPistonExtendEvent blockPistonExtendEvent) {
        if (!this.plugin.worldUtil().isEnabled(blockPistonExtendEvent.getBlock().getWorld())) {
            return;
        }
        if (!FactionsPlugin.getInstance().conf().factions().protection().isPistonProtectionThroughDenyBuild()) {
            return;
        }
        if (blockPistonExtendEvent.getBlocks().isEmpty()) {
            return;
        }
        Faction faction = Board.getInstance().getFactionAt(new FLocation(blockPistonExtendEvent.getBlock()));
        if (!this.canPistonMoveBlock(faction, blockPistonExtendEvent.getBlocks(), blockPistonExtendEvent.getDirection())) {
            blockPistonExtendEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPistonRetract(BlockPistonRetractEvent blockPistonRetractEvent) {
        if (!this.plugin.worldUtil().isEnabled(blockPistonRetractEvent.getBlock().getWorld())) {
            return;
        }
        if (!blockPistonRetractEvent.isSticky() || !FactionsPlugin.getInstance().conf().factions().protection().isPistonProtectionThroughDenyBuild()) {
            return;
        }
        List list = blockPistonRetractEvent.getBlocks();
        if (list.isEmpty()) {
            return;
        }
        Faction faction = Board.getInstance().getFactionAt(new FLocation(blockPistonRetractEvent.getBlock()));
        if (!this.canPistonMoveBlock(faction, list, null)) {
            blockPistonRetractEvent.setCancelled(true);
        }
    }

    private boolean canPistonMoveBlock(Faction faction, List<Block> list, BlockFace blockFace) {
        String string = ((Block)list.getFirst()).getWorld().getName();
        List list2 = (blockFace == null ? list.stream() : list.stream().map(block -> block.getRelative(blockFace))).map(Block::getLocation).map(FLocation::new).distinct().toList();
        boolean bl = FactionsPlugin.getInstance().conf().factions().other().isDisablePistonsInTerritory();
        for (FLocation fLocation : list2) {
            Faction faction2 = Board.getInstance().getFactionAt(fLocation);
            if (faction == faction2) continue;
            if (bl && faction2.isNormal()) {
                return false;
            }
            if (faction2.isWilderness() && FactionsPlugin.getInstance().conf().factions().protection().isWildernessDenyBuild() && !FactionsPlugin.getInstance().conf().factions().protection().getWorldsNoWildernessProtection().contains(string)) {
                return false;
            }
            if (faction2.isSafeZone() && FactionsPlugin.getInstance().conf().factions().protection().isSafeZoneDenyBuild()) {
                return false;
            }
            if (faction2.isWarZone() && FactionsPlugin.getInstance().conf().factions().protection().isWarZoneDenyBuild()) {
                return false;
            }
            Relation relation = faction.getRelationTo(faction2);
            if (faction2.hasAccess(relation, PermissibleActions.BUILD, fLocation)) continue;
            return false;
        }
        return true;
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onFrostWalker(EntityBlockFormEvent entityBlockFormEvent) {
        boolean bl;
        if (!this.plugin.worldUtil().isEnabled(entityBlockFormEvent.getBlock().getWorld())) {
            return;
        }
        if (entityBlockFormEvent.getEntity() == null || entityBlockFormEvent.getEntity().getType() != EntityType.PLAYER || entityBlockFormEvent.getBlock() == null) {
            return;
        }
        Player player = (Player)entityBlockFormEvent.getEntity();
        Location location = entityBlockFormEvent.getBlock().getLocation();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        boolean bl2 = bl = fPlayer.getLastFrostwalkerMessage() + 10000L > System.currentTimeMillis();
        if (!bl) {
            fPlayer.setLastFrostwalkerMessage();
        }
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(player, location, PermissibleActions.FROSTWALK, bl)) {
            entityBlockFormEvent.setCancelled(true);
        }
    }

    public static boolean playerCanBuildDestroyBlock(Player player, Location location, PermissibleAction permissibleAction, boolean bl) {
        boolean bl2;
        String string = player.getName();
        MainConfig mainConfig = FactionsPlugin.getInstance().conf();
        if (mainConfig.factions().protection().getPlayersWhoBypassAllProtection().contains(string)) {
            return true;
        }
        FPlayer fPlayer = FPlayers.getInstance().getById(player.getUniqueId().toString());
        if (fPlayer.isAdminBypassing()) {
            return true;
        }
        FLocation fLocation = new FLocation(location);
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        if (faction.isWilderness()) {
            if (mainConfig.worldGuard().isBuildPriority() && FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().playerCanBuild(player, location)) {
                return true;
            }
            if (!mainConfig.factions().protection().isWildernessDenyBuild() || mainConfig.factions().protection().getWorldsNoWildernessProtection().contains(location.getWorld().getName())) {
                return true;
            }
            if (!bl) {
                fPlayer.msg(TL.PERM_DENIED_WILDERNESS, permissibleAction.getShortDescription());
            }
            return false;
        }
        if (faction.isSafeZone()) {
            if (mainConfig.worldGuard().isBuildPriority() && FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().playerCanBuild(player, location)) {
                return true;
            }
            if (!mainConfig.factions().protection().isSafeZoneDenyBuild() || Permission.MANAGE_SAFE_ZONE.has((CommandSender)player)) {
                return true;
            }
            if (!bl) {
                fPlayer.msg(TL.PERM_DENIED_SAFEZONE, permissibleAction.getShortDescription());
            }
            return false;
        }
        if (faction.isWarZone()) {
            if (mainConfig.worldGuard().isBuildPriority() && FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().playerCanBuild(player, location)) {
                return true;
            }
            if (!mainConfig.factions().protection().isWarZoneDenyBuild() || Permission.MANAGE_WAR_ZONE.has((CommandSender)player)) {
                return true;
            }
            if (!bl) {
                fPlayer.msg(TL.PERM_DENIED_WARZONE, permissibleAction.getShortDescription());
            }
            return false;
        }
        if (FactionsPlugin.getInstance().getLandRaidControl().isRaidable(faction)) {
            return true;
        }
        Faction faction2 = fPlayer.getFaction();
        boolean bl3 = bl2 = !bl && faction.hasAccess(fPlayer, PermissibleActions.PAINBUILD, fLocation);
        if (!faction.hasAccess(fPlayer, permissibleAction, fLocation)) {
            if (bl2 && permissibleAction != PermissibleActions.FROSTWALK) {
                player.damage((double)mainConfig.factions().other().getActionDeniedPainAmount());
                fPlayer.msg(TL.PERM_DENIED_PAINTERRITORY, permissibleAction.getShortDescription(), faction.getTag(faction2));
                return true;
            }
            if (!bl) {
                fPlayer.msg(TL.PERM_DENIED_TERRITORY, permissibleAction.getShortDescription(), faction.getTag(faction2));
            }
            return false;
        }
        if (mainConfig.factions().ownedArea().isEnabled() && (mainConfig.factions().ownedArea().isDenyBuild() || mainConfig.factions().ownedArea().isPainBuild()) && !faction.playerHasOwnershipRights(fPlayer, fLocation)) {
            if (bl2 && mainConfig.factions().ownedArea().isPainBuild()) {
                player.damage((double)mainConfig.factions().other().getActionDeniedPainAmount());
                if (!mainConfig.factions().ownedArea().isDenyBuild()) {
                    fPlayer.msg(TL.PERM_DENIED_PAINOWNED, permissibleAction.getShortDescription(), faction.getOwnerListString(fLocation));
                }
            }
            if (mainConfig.factions().ownedArea().isDenyBuild()) {
                if (!bl) {
                    fPlayer.msg(TL.PERM_DENIED_OWNED, permissibleAction.getShortDescription(), faction.getOwnerListString(fLocation));
                }
                return false;
            }
        }
        return true;
    }
}

