/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Fireball
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.Wither
 *  org.bukkit.entity.WitherSkull
 *  org.bukkit.entity.minecart.ExplosiveMinecart
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Listener
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

public abstract class AbstractListener
implements Listener {
    public boolean playerCanInteractHere(Player player, Location location) {
        return AbstractListener.canInteractHere(player, location);
    }

    public static boolean canInteractHere(Player player, Location location) {
        String string = player.getName();
        if (FactionsPlugin.getInstance().conf().factions().protection().getPlayersWhoBypassAllProtection().contains(string)) {
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
        MainConfig.Factions.Protection protection = FactionsPlugin.getInstance().conf().factions().protection();
        if (faction.isWilderness()) {
            if (!protection.isWildernessDenyUsage() || protection.getWorldsNoWildernessProtection().contains(location.getWorld().getName())) {
                return true;
            }
            fPlayer.msg(TL.PLAYER_USE_WILDERNESS, "this");
            return false;
        }
        if (faction.isSafeZone()) {
            if (!protection.isSafeZoneDenyUsage() || Permission.MANAGE_SAFE_ZONE.has((CommandSender)player)) {
                return true;
            }
            fPlayer.msg(TL.PLAYER_USE_SAFEZONE, "this");
            return false;
        }
        if (faction.isWarZone()) {
            if (!protection.isWarZoneDenyUsage() || Permission.MANAGE_WAR_ZONE.has((CommandSender)player)) {
                return true;
            }
            fPlayer.msg(TL.PLAYER_USE_WARZONE, "this");
            return false;
        }
        boolean bl = faction.hasAccess(fPlayer, PermissibleActions.ITEM, fLocation);
        if (!bl) {
            fPlayer.msg(TL.PLAYER_USE_TERRITORY, "this", faction.getTag(fPlayer.getFaction()));
            return false;
        }
        if (FactionsPlugin.getInstance().conf().factions().ownedArea().isEnabled() && FactionsPlugin.getInstance().conf().factions().ownedArea().isDenyUsage() && !faction.playerHasOwnershipRights(fPlayer, fLocation)) {
            fPlayer.msg(TL.PLAYER_USE_OWNED, "this", faction.getOwnerListString(fLocation));
            return false;
        }
        return true;
    }

    protected void handleExplosion(Location location, Entity entity, Cancellable cancellable, List<Block> list) {
        Block block2;
        if (!FactionsPlugin.getInstance().worldUtil().isEnabled(location.getWorld())) {
            return;
        }
        if (AbstractListener.explosionDisallowed(entity, new FLocation(location))) {
            cancellable.setCancelled(true);
            return;
        }
        List list2 = list.stream().map(Block::getChunk).distinct().collect(Collectors.toList());
        if (list2.removeIf(chunk -> AbstractListener.explosionDisallowed(entity, new FLocation((Chunk)chunk)))) {
            list.removeIf(block -> !list2.contains(block.getChunk()));
        }
        if ((entity instanceof TNTPrimed || entity instanceof ExplosiveMinecart) && FactionsPlugin.getInstance().conf().exploits().isTntWaterlog() && (block2 = location.getBlock()).isLiquid()) {
            ArrayList<Block> arrayList = new ArrayList<Block>();
            arrayList.add(block2.getRelative(0, 0, 1));
            arrayList.add(block2.getRelative(0, 0, -1));
            arrayList.add(block2.getRelative(0, 1, 0));
            arrayList.add(block2.getRelative(0, -1, 0));
            arrayList.add(block2.getRelative(1, 0, 0));
            arrayList.add(block2.getRelative(-1, 0, 0));
            for (Block block3 : arrayList) {
                Material material = block3.getType();
                if (material == Material.AIR || material == Material.BEDROCK || material == Material.WATER || material == Material.LAVA || material == Material.OBSIDIAN || material == Material.NETHER_PORTAL || material == Material.ENCHANTING_TABLE || material.name().contains("ANVIL") || material == Material.END_PORTAL || material == Material.END_PORTAL_FRAME || material == Material.ENDER_CHEST || AbstractListener.explosionDisallowed(entity, new FLocation(block3.getLocation()))) continue;
                block3.breakNaturally();
            }
        }
    }

    public static boolean explosionDisallowed(Entity entity, FLocation fLocation) {
        block20: {
            block19: {
                MainConfig.Factions.Protection protection;
                boolean bl;
                Faction faction;
                block17: {
                    block18: {
                        block15: {
                            block16: {
                                block13: {
                                    block14: {
                                        faction = Board.getInstance().getFactionAt(fLocation);
                                        bl = faction.hasPlayersOnline();
                                        if (faction.noExplosionsInTerritory() || faction.isPeaceful() && FactionsPlugin.getInstance().conf().factions().specialCase().isPeacefulTerritoryDisableBoom()) {
                                            return true;
                                        }
                                        protection = FactionsPlugin.getInstance().conf().factions().protection();
                                        if (!(entity instanceof Creeper)) break block13;
                                        if (faction.isWilderness() && protection.isWildernessBlockCreepers() && !protection.getWorldsNoWildernessProtection().contains(fLocation.getWorldName()) || faction.isNormal() && (!bl ? protection.isTerritoryBlockCreepersWhenOffline() : protection.isTerritoryBlockCreepers())) break block14;
                                        if ((!faction.isWarZone() || !protection.isWarZoneBlockCreepers()) && !faction.isSafeZone()) break block13;
                                    }
                                    return true;
                                }
                                if (!(entity instanceof Fireball) && !(entity instanceof WitherSkull) && !(entity instanceof Wither)) break block15;
                                if (faction.isWilderness() && protection.isWildernessBlockFireballs() && !protection.getWorldsNoWildernessProtection().contains(fLocation.getWorldName()) || faction.isNormal() && (!bl ? protection.isTerritoryBlockFireballsWhenOffline() : protection.isTerritoryBlockFireballs())) break block16;
                                if ((!faction.isWarZone() || !protection.isWarZoneBlockFireballs()) && !faction.isSafeZone()) break block15;
                            }
                            return true;
                        }
                        if (!(entity instanceof TNTPrimed) && !(entity instanceof ExplosiveMinecart)) break block17;
                        if (faction.isWilderness() && protection.isWildernessBlockTNT() && !protection.getWorldsNoWildernessProtection().contains(fLocation.getWorldName()) || faction.isNormal() && (!bl ? protection.isTerritoryBlockTNTWhenOffline() : protection.isTerritoryBlockTNT())) break block18;
                        if ((!faction.isWarZone() || !protection.isWarZoneBlockTNT()) && (!faction.isSafeZone() || !protection.isSafeZoneBlockTNT())) break block17;
                    }
                    return true;
                }
                if (faction.isWilderness() && protection.isWildernessBlockOtherExplosions() && !protection.getWorldsNoWildernessProtection().contains(fLocation.getWorldName()) || faction.isNormal() && (!bl ? protection.isTerritoryBlockOtherExplosionsWhenOffline() : protection.isTerritoryBlockOtherExplosions())) break block19;
                if ((!faction.isWarZone() || !protection.isWarZoneBlockOtherExplosions()) && (!faction.isSafeZone() || !protection.isSafeZoneBlockOtherExplosions())) break block20;
            }
            return true;
        }
        return false;
    }

    public boolean canPlayerUseBlock(Player player, Material material, Location location, boolean bl) {
        return AbstractListener.canUseBlock(player, material, location, bl);
    }

    public static boolean canUseBlock(Player player, Material material, Location location, boolean bl) {
        Material material2;
        if (FactionsPlugin.getInstance().conf().factions().protection().getPlayersWhoBypassAllProtection().contains(player.getName())) {
            return true;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer.isAdminBypassing()) {
            return true;
        }
        FLocation fLocation = new FLocation(location);
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        String string = material.name();
        if (!faction.isNormal()) {
            if (material == Material.ITEM_FRAME || material == Material.GLOW_ITEM_FRAME || material == Material.ARMOR_STAND) {
                return AbstractListener.canInteractHere(player, location);
            }
            return true;
        }
        if (FactionsPlugin.getInstance().getLandRaidControl().isRaidable(faction)) {
            return true;
        }
        PermissibleActions permissibleActions = null;
        if (material == Material.LEVER) {
            permissibleActions = PermissibleActions.LEVER;
        } else if (string.contains("BUTTON")) {
            permissibleActions = PermissibleActions.BUTTON;
        } else if (string.contains("DOOR") || string.contains("GATE")) {
            permissibleActions = PermissibleActions.DOOR;
        } else if (string.endsWith("_PLATE")) {
            permissibleActions = PermissibleActions.PLATE;
        } else if (string.contains("SIGN")) {
            permissibleActions = PermissibleActions.ITEM;
        } else if (material == Material.CHEST || material == Material.ENDER_CHEST || material == Material.TRAPPED_CHEST || material == Material.BARREL || material == Material.DROPPER || material == Material.DISPENSER || material == Material.HOPPER || string.contains("CAULDRON") || material == Material.CAMPFIRE || material == Material.BREWING_STAND || material == Material.CARTOGRAPHY_TABLE || material == Material.GRINDSTONE || material == Material.SMOKER || material == Material.STONECUTTER || material == Material.LECTERN || material == Material.ITEM_FRAME || material == Material.GLOW_ITEM_FRAME || material == Material.JUKEBOX || material == Material.ARMOR_STAND || material == Material.REPEATER || material == Material.ENCHANTING_TABLE || material == Material.BEACON || material == Material.CHIPPED_ANVIL || material == Material.DAMAGED_ANVIL || material == Material.FLOWER_POT || string.contains("POTTED") || material == Material.BEE_NEST || string.contains("SHULKER") || string.contains("ANVIL") || string.startsWith("POTTED") || string.contains("FURNACE") || FactionsPlugin.getInstance().conf().factions().protection().getCustomContainers().contains(material)) {
            permissibleActions = PermissibleActions.CONTAINER;
        }
        if (permissibleActions == null) {
            return true;
        }
        if (permissibleActions == PermissibleActions.CONTAINER && (FactionsPlugin.getInstance().conf().factions().protection().getContainerExceptions().contains(material) || faction.isNormal() && material == Material.LECTERN && FactionsPlugin.getInstance().conf().factions().protection().isTerritoryAllowLecternReading())) {
            return true;
        }
        if (!faction.hasAccess(fPlayer, permissibleActions, fLocation)) {
            if (permissibleActions != PermissibleActions.PLATE) {
                fPlayer.msg(TL.GENERIC_NOPERMISSION, permissibleActions.getShortDescription());
            }
            return false;
        }
        Faction faction2 = fPlayer.getFaction();
        Relation relation = faction2.getRelationTo(faction);
        if (FactionsPlugin.getInstance().conf().exploits().doPreventDuping() && (!relation.isMember() || !faction.playerHasOwnershipRights(fPlayer, fLocation)) && AbstractListener.isDupeMaterial(material2 = player.getInventory().getItemInMainHand().getType())) {
            return false;
        }
        if (FactionsPlugin.getInstance().conf().factions().ownedArea().isEnabled() && FactionsPlugin.getInstance().conf().factions().ownedArea().isProtectMaterials() && !faction.playerHasOwnershipRights(fPlayer, fLocation)) {
            if (!bl) {
                fPlayer.msg(TL.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), faction.getOwnerListString(fLocation));
            }
            return false;
        }
        return true;
    }

    private static boolean isDupeMaterial(Material material) {
        return material.name().contains("SIGN") || material.name().contains("DOOR") || material.name().contains("CHEST");
    }
}

