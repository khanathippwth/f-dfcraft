/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.runtime.SwitchBootstraps
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Enderman
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Monster
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.Silverfish
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.Wither
 *  org.bukkit.entity.minecart.ExplosiveMinecart
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.entity.CreatureSpawnEvent
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 *  org.bukkit.event.entity.EntityChangeBlockEvent
 *  org.bukkit.event.entity.EntityCombustByEntityEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.EntityTargetEvent
 *  org.bukkit.event.entity.PotionSplashEvent
 *  org.bukkit.event.hanging.HangingBreakByEntityEvent
 *  org.bukkit.event.hanging.HangingBreakEvent
 *  org.bukkit.event.hanging.HangingBreakEvent$RemoveCause
 *  org.bukkit.event.hanging.HangingPlaceEvent
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.projectiles.ProjectileSource
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.listeners.AbstractListener;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.util.TL;
import java.lang.runtime.SwitchBootstraps;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class FactionsEntityListener
extends AbstractListener {
    public final FactionsPlugin plugin;
    private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<PotionEffectType>(Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER));

    public FactionsEntityListener(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent entityDeathEvent) {
        if (!this.plugin.worldUtil().isEnabled(entityDeathEvent.getEntity().getWorld())) {
            return;
        }
        LivingEntity livingEntity = entityDeathEvent.getEntity();
        if (livingEntity instanceof Player) {
            FactionsPlugin.getInstance().getLandRaidControl().onDeath((Player)livingEntity);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        Object object;
        if (!this.plugin.worldUtil().isEnabled(entityDamageEvent.getEntity().getWorld())) {
            return;
        }
        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent)entityDamageEvent;
            if (!this.canDamagerHurtDamagee(entityDamageByEntityEvent, true)) {
                entityDamageEvent.setCancelled(true);
            }
        } else if (FactionsPlugin.getInstance().conf().factions().protection().isSafeZonePreventAllDamageToPlayers() && this.isPlayerInSafeZone(entityDamageEvent.getEntity())) {
            entityDamageEvent.setCancelled(true);
        } else if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL && (object = entityDamageEvent.getEntity()) instanceof Player) {
            Player player = (Player)object;
            object = FPlayers.getInstance().getByPlayer(player);
            if (object != null && !object.shouldTakeFallDamage()) {
                entityDamageEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageMonitor(EntityDamageEvent entityDamageEvent) {
        FPlayer fPlayer;
        Entity entity;
        if (!this.plugin.worldUtil().isEnabled(entityDamageEvent.getEntity().getWorld())) {
            return;
        }
        Entity entity2 = entityDamageEvent.getEntity();
        boolean bl = entity2 instanceof Player;
        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            entity = ((EntityDamageByEntityEvent)entityDamageEvent).getDamager();
            if (entity instanceof Projectile && (fPlayer = (Projectile)entity).getShooter() instanceof Entity) {
                entity = (Entity)fPlayer.getShooter();
            }
            if (bl) {
                this.cancelFStuckTeleport((Player)entity2);
                if (entity instanceof Player || this.plugin.conf().commands().fly().isDisableOnHurtByMobs()) {
                    this.cancelFFly((Player)entity2);
                }
            }
            if (entity instanceof Player) {
                this.cancelFStuckTeleport((Player)entity);
                if (bl && this.plugin.conf().commands().fly().isDisableOnHurtingPlayers() || !bl && this.plugin.conf().commands().fly().isDisableOnHurtingMobs()) {
                    this.cancelFFly((Player)entity);
                }
            }
        }
        if (bl) {
            entity = (Player)entity2;
            fPlayer = FPlayers.getInstance().getByPlayer((Player)entity);
            this.cancelFStuckTeleport((Player)entity);
            if (this.plugin.conf().commands().fly().isDisableOnGenericDamage()) {
                this.cancelFFly((Player)entity);
            }
            if (fPlayer.isWarmingUp()) {
                fPlayer.clearWarmup();
                fPlayer.msg(TL.WARMUPS_CANCELLED, new Object[0]);
            }
        }
    }

    private void cancelFFly(Player player) {
        if (player == null) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer.isFlying()) {
            fPlayer.setFlying(false, true);
            if (fPlayer.isAutoFlying()) {
                fPlayer.setAutoFlying(false);
            }
        }
    }

    public void cancelFStuckTeleport(Player player) {
        if (player == null) {
            return;
        }
        UUID uUID = player.getUniqueId();
        if (FactionsPlugin.getInstance().getStuckMap().containsKey(uUID)) {
            FPlayers.getInstance().getByPlayer(player).msg(TL.COMMAND_STUCK_CANCELLED, new Object[0]);
            FactionsPlugin.getInstance().getStuckMap().remove(uUID);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityExplode(EntityExplodeEvent entityExplodeEvent) {
        this.handleExplosion(entityExplodeEvent.getLocation(), entityExplodeEvent.getEntity(), (Cancellable)entityExplodeEvent, entityExplodeEvent.blockList());
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent entityCombustByEntityEvent) {
        if (!this.plugin.worldUtil().isEnabled(entityCombustByEntityEvent.getEntity().getWorld())) {
            return;
        }
        if (!FactionsEntityListener.canDamage(entityCombustByEntityEvent.getCombuster(), entityCombustByEntityEvent.getEntity(), false)) {
            entityCombustByEntityEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPotionSplashEvent(PotionSplashEvent potionSplashEvent) {
        if (!this.plugin.worldUtil().isEnabled(potionSplashEvent.getEntity().getWorld())) {
            return;
        }
        boolean bl = false;
        for (Object object : potionSplashEvent.getPotion().getEffects()) {
            if (!badPotionEffects.contains(object.getType())) continue;
            bl = true;
            break;
        }
        if (!bl) {
            return;
        }
        ProjectileSource projectileSource = potionSplashEvent.getPotion().getShooter();
        if (!(projectileSource instanceof Entity)) {
            return;
        }
        if (projectileSource instanceof Player) {
            Object object;
            object = (Player)projectileSource;
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player)object);
            if (fPlayer.getFaction().isPeaceful()) {
                if (potionSplashEvent.getPotion().getEffects().stream().allMatch(potionEffect -> potionEffect.getType().equals(PotionEffectType.WEAKNESS))) {
                    for (LivingEntity livingEntity : potionSplashEvent.getAffectedEntities()) {
                        if (livingEntity.getType() == EntityType.ZOMBIE_VILLAGER) continue;
                        potionSplashEvent.setIntensity(livingEntity, 0.0);
                    }
                    return;
                }
                potionSplashEvent.setCancelled(true);
                return;
            }
        }
        for (FPlayer fPlayer : potionSplashEvent.getAffectedEntities()) {
            if (FactionsEntityListener.canDamage((Entity)projectileSource, (Entity)fPlayer, true)) continue;
            potionSplashEvent.setIntensity((LivingEntity)fPlayer, 0.0);
        }
    }

    public boolean isPlayerInSafeZone(Entity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        return Board.getInstance().getFactionAt(new FLocation(entity.getLocation())).isSafeZone();
    }

    public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        return this.canDamagerHurtDamagee(entityDamageByEntityEvent, true);
    }

    public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent entityDamageByEntityEvent, boolean bl) {
        return FactionsEntityListener.canDamage(entityDamageByEntityEvent.getDamager(), entityDamageByEntityEvent.getEntity(), bl);
    }

    public static boolean canDamage(Entity entity, Entity entity2, boolean bl) {
        Object object;
        Material material;
        Object object2;
        FLocation fLocation = new FLocation(entity2.getLocation());
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        if (entity instanceof Projectile) {
            object2 = (Projectile)entity;
            if (!(object2.getShooter() instanceof Entity)) {
                return true;
            }
            entity = (Entity)object2.getShooter();
        }
        if ((entity instanceof TNTPrimed || entity instanceof Creeper || entity instanceof ExplosiveMinecart) && ((object2 = entity2.getType()).name().contains("ITEM_FRAME") || object2.name().equals("ARMOR_STAND") || object2 == EntityType.PAINTING) && FactionsEntityListener.explosionDisallowed(entity, new FLocation(entity2.getLocation()))) {
            return false;
        }
        if (entity instanceof Player) {
            object2 = (Player)entity;
            material = null;
            object = entity2.getType();
            if (object.name().contains("ITEM_FRAME")) {
                material = Material.ITEM_FRAME;
            } else if (object.name().equals("ARMOR_STAND")) {
                material = Material.ARMOR_STAND;
            }
            if (material != null && !FactionsEntityListener.canUseBlock((Player)object2, material, entity2.getLocation(), false)) {
                return false;
            }
        }
        if (!(entity2 instanceof Player)) {
            if (FactionsPlugin.getInstance().conf().factions().protection().isSafeZoneBlockAllEntityDamage() && faction.isSafeZone()) {
                if (entity instanceof Player && bl) {
                    FPlayers.getInstance().getByPlayer((Player)entity).msg(TL.PERM_DENIED_SAFEZONE.format(TL.GENERIC_ATTACK.toString()), new Object[0]);
                }
                return false;
            }
            if (FactionsPlugin.getInstance().conf().factions().protection().isPeacefulBlockAllEntityDamage() && faction.isPeaceful()) {
                if (entity instanceof Player && bl) {
                    FPlayers.getInstance().getByPlayer((Player)entity).msg(TL.PERM_DENIED_TERRITORY.format(TL.GENERIC_ATTACK.toString(), faction.getTag(FPlayers.getInstance().getByPlayer((Player)entity))), new Object[0]);
                }
                return false;
            }
            if (FactionsPlugin.getInstance().conf().factions().protection().isTerritoryBlockEntityDamageMatchingPerms() && entity instanceof Player && faction.isNormal() && !faction.hasAccess((Selectable)(object2 = FPlayers.getInstance().getByPlayer((Player)entity)), PermissibleActions.DESTROY, fLocation)) {
                if (bl) {
                    object2.msg(TL.PERM_DENIED_TERRITORY.format(TL.GENERIC_ATTACK.toString(), faction.getTag(FPlayers.getInstance().getByPlayer((Player)entity))), new Object[0]);
                }
                return false;
            }
            return true;
        }
        object2 = FPlayers.getInstance().getByPlayer((Player)entity2);
        if (object2 == null || object2.getPlayer() == null) {
            return true;
        }
        material = object2.getPlayer().getLocation();
        if (entity == entity2) {
            return true;
        }
        if (FactionsPlugin.getInstance().conf().worldGuard().isPVPPriority() && FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().isCustomPVPFlag((Player)entity2)) {
            return true;
        }
        if (faction.noPvPInTerritory()) {
            if (entity instanceof Player) {
                if (bl) {
                    object = FPlayers.getInstance().getByPlayer((Player)entity);
                    object.msg(TL.PLAYER_CANTHURT, faction.isSafeZone() ? TL.REGION_SAFEZONE.toString() : TL.REGION_PEACEFUL.toString());
                }
                return false;
            }
            return !faction.noMonstersInTerritory();
        }
        if (!(entity instanceof Player)) {
            return true;
        }
        object = FPlayers.getInstance().getByPlayer((Player)entity);
        boolean bl2 = bl = bl && ((Player)entity).canSee((Player)entity2);
        if (object == null || object.getPlayer() == null) {
            return true;
        }
        MainConfig.Factions factions = FactionsPlugin.getInstance().conf().factions();
        if (factions.protection().getPlayersWhoBypassAllProtection().contains(object.getName())) {
            return true;
        }
        if (object.hasLoginPvpDisabled()) {
            if (bl) {
                object.msg(TL.PLAYER_PVP_LOGIN, factions.pvp().getNoPVPDamageToOthersForXSecondsAfterLogin());
            }
            return false;
        }
        Faction faction2 = Board.getInstance().getFactionAt(new FLocation((FPlayer)object));
        if (faction2.noPvPInTerritory()) {
            if (bl) {
                object.msg(TL.PLAYER_CANTHURT, faction2.isSafeZone() ? TL.REGION_SAFEZONE.toString() : TL.REGION_PEACEFUL.toString());
            }
            return false;
        }
        if (faction2.isWarZone() && factions.protection().isWarZoneFriendlyFire()) {
            return true;
        }
        if (factions.pvp().getWorldsIgnorePvP().contains(material.getWorld().getName())) {
            return true;
        }
        Faction faction3 = object2.getFaction();
        Faction faction4 = object.getFaction();
        if (faction4.isWilderness() && factions.pvp().isDisablePVPForFactionlessPlayers()) {
            if (bl) {
                object.msg(TL.PLAYER_PVP_REQUIREFACTION, new Object[0]);
            }
            return false;
        }
        if (faction3.isWilderness()) {
            if (faction == faction4 && factions.pvp().isEnablePVPAgainstFactionlessInAttackersLand()) {
                return true;
            }
            if (factions.pvp().isDisablePVPForFactionlessPlayers()) {
                if (bl) {
                    object.msg(TL.PLAYER_PVP_FACTIONLESS, new Object[0]);
                }
                return false;
            }
        }
        if (!faction.isWarZone() || factions.pvp().isDisablePeacefulPVPInWarzone()) {
            if (faction3.isPeaceful()) {
                if (bl) {
                    object.msg(TL.PLAYER_PVP_PEACEFUL, new Object[0]);
                }
                return false;
            }
            if (faction4.isPeaceful()) {
                if (bl) {
                    object.msg(TL.PLAYER_PVP_PEACEFUL, new Object[0]);
                }
                return false;
            }
        }
        Relation relation = faction3.getRelationTo(faction4);
        if (factions.pvp().isDisablePVPBetweenNeutralFactions() && relation.isNeutral()) {
            if (bl) {
                object.msg(TL.PLAYER_PVP_NEUTRAL, new Object[0]);
            }
            return false;
        }
        if (!object2.hasFaction()) {
            return true;
        }
        if (relation.isMember() || relation.isAlly() || relation.isTruce()) {
            if (bl) {
                object.msg(TL.PLAYER_PVP_CANTHURT, object2.describeTo((RelationParticipator)object));
            }
            return false;
        }
        boolean bl3 = object2.isInOwnTerritory();
        if (bl3 && relation.isNeutral()) {
            if (bl) {
                object.msg(TL.PLAYER_PVP_NEUTRALFAIL, object2.describeTo((RelationParticipator)object));
                object2.msg(TL.PLAYER_PVP_TRIED, object.describeTo((RelationParticipator)object2, true));
            }
            return false;
        }
        return true;
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onCreatureSpawn(CreatureSpawnEvent creatureSpawnEvent) {
        if (!this.plugin.worldUtil().isEnabled(creatureSpawnEvent.getEntity().getWorld())) {
            return;
        }
        if (creatureSpawnEvent.getLocation() == null) {
            return;
        }
        Faction faction = Board.getInstance().getFactionAt(new FLocation(creatureSpawnEvent.getLocation()));
        CreatureSpawnEvent.SpawnReason spawnReason = creatureSpawnEvent.getSpawnReason();
        EntityType entityType = creatureSpawnEvent.getEntityType();
        MainConfig.Factions.Spawning spawning = FactionsPlugin.getInstance().conf().factions().spawning();
        if (faction.isNormal()) {
            if (faction.isPeaceful() && FactionsPlugin.getInstance().conf().factions().specialCase().isPeacefulTerritoryDisableMonsters() && creatureSpawnEvent.getEntity() instanceof Monster) {
                creatureSpawnEvent.setCancelled(true);
            }
            if (spawning.getPreventInTerritory().contains(spawnReason) && !spawning.getPreventInTerritoryExceptions().contains(entityType)) {
                creatureSpawnEvent.setCancelled(true);
            }
        } else if (faction.isSafeZone()) {
            if (spawning.getPreventInSafezone().contains(spawnReason) && !spawning.getPreventInSafezoneExceptions().contains(entityType)) {
                creatureSpawnEvent.setCancelled(true);
            }
        } else if (faction.isWarZone()) {
            if (spawning.getPreventInWarzone().contains(spawnReason) && !spawning.getPreventInWarzoneExceptions().contains(entityType)) {
                creatureSpawnEvent.setCancelled(true);
            }
        } else if (faction.isWilderness() && spawning.getPreventInWilderness().contains(spawnReason) && !spawning.getPreventInWildernessExceptions().contains(entityType)) {
            creatureSpawnEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityTarget(EntityTargetEvent entityTargetEvent) {
        if (!this.plugin.worldUtil().isEnabled(entityTargetEvent.getEntity().getWorld())) {
            return;
        }
        Entity entity = entityTargetEvent.getTarget();
        if (entity == null) {
            return;
        }
        if (entityTargetEvent.getEntity() instanceof Monster && Board.getInstance().getFactionAt(new FLocation(entity.getLocation())).noMonstersInTerritory()) {
            entityTargetEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPaintingBreak(HangingBreakEvent hangingBreakEvent) {
        Location location;
        block8: {
            block9: {
                if (!this.plugin.worldUtil().isEnabled(hangingBreakEvent.getEntity().getWorld())) {
                    return;
                }
                if (hangingBreakEvent.getCause() != HangingBreakEvent.RemoveCause.EXPLOSION && (hangingBreakEvent.getCause() != HangingBreakEvent.RemoveCause.ENTITY || !(hangingBreakEvent instanceof HangingBreakByEntityEvent) || !(((HangingBreakByEntityEvent)hangingBreakEvent).getRemover() instanceof Creeper))) break block8;
                location = hangingBreakEvent.getEntity().getLocation();
                Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
                if (faction.noExplosionsInTerritory()) {
                    hangingBreakEvent.setCancelled(true);
                    return;
                }
                boolean bl = faction.hasPlayersOnline();
                MainConfig.Factions.Protection protection = FactionsPlugin.getInstance().conf().factions().protection();
                if (faction.isWilderness() && !protection.getWorldsNoWildernessProtection().contains(location.getWorld().getName()) && (protection.isWildernessBlockCreepers() || protection.isWildernessBlockFireballs() || protection.isWildernessBlockTNT()) || faction.isNormal() && (!bl ? protection.isTerritoryBlockCreepersWhenOffline() || protection.isTerritoryBlockFireballsWhenOffline() || protection.isTerritoryBlockTNTWhenOffline() : protection.isTerritoryBlockCreepers() || protection.isTerritoryBlockFireballs() || protection.isTerritoryBlockTNT())) break block9;
                if ((!faction.isWarZone() || !protection.isWarZoneBlockCreepers() && !protection.isWarZoneBlockFireballs() && !protection.isWarZoneBlockTNT()) && !faction.isSafeZone()) break block8;
            }
            hangingBreakEvent.setCancelled(true);
            return;
        }
        if (!(hangingBreakEvent instanceof HangingBreakByEntityEvent)) {
            return;
        }
        location = ((HangingBreakByEntityEvent)hangingBreakEvent).getRemover();
        if (!(location instanceof Player)) {
            return;
        }
        if (!FactionsBlockListener.playerCanBuildDestroyBlock((Player)location, hangingBreakEvent.getEntity().getLocation(), PermissibleActions.DESTROY, false)) {
            hangingBreakEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPaintingPlace(HangingPlaceEvent hangingPlaceEvent) {
        if (!this.plugin.worldUtil().isEnabled(hangingPlaceEvent.getEntity().getWorld())) {
            return;
        }
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(hangingPlaceEvent.getPlayer(), hangingPlaceEvent.getBlock().getRelative(hangingPlaceEvent.getBlockFace()).getLocation(), PermissibleActions.BUILD, false)) {
            hangingPlaceEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityChangeBlock(EntityChangeBlockEvent entityChangeBlockEvent) {
        if (!this.plugin.worldUtil().isEnabled(entityChangeBlockEvent.getEntity().getWorld())) {
            return;
        }
        Entity entity = entityChangeBlockEvent.getEntity();
        Location location = entityChangeBlockEvent.getBlock().getLocation();
        Entity entity2 = entity;
        Objects.requireNonNull(entity2);
        Entity entity3 = entity2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{Enderman.class, Silverfish.class, Wither.class}, (Object)entity3, (int)n)) {
            case 0: {
                Enderman enderman = (Enderman)entity3;
                if (!this.stopEndermanBlockManipulation(location)) break;
                entityChangeBlockEvent.setCancelled(true);
                break;
            }
            case 1: {
                Silverfish silverfish = (Silverfish)entity3;
                Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
                if (!faction.isSafeZone() && !faction.isWarZone() && !faction.isPeaceful()) break;
                entityChangeBlockEvent.setCancelled(true);
                break;
            }
            case 2: {
                Wither wither = (Wither)entity3;
                Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
                MainConfig.Factions.Protection protection = FactionsPlugin.getInstance().conf().factions().protection();
                if (!(faction.isWilderness() && protection.isWildernessBlockFireballs() && !protection.getWorldsNoWildernessProtection().contains(location.getWorld().getName()) || faction.isNormal() && (!faction.hasPlayersOnline() ? protection.isTerritoryBlockFireballsWhenOffline() : protection.isTerritoryBlockFireballs()))) {
                    if ((!faction.isWarZone() || !protection.isWarZoneBlockFireballs()) && !faction.isSafeZone()) break;
                }
                entityChangeBlockEvent.setCancelled(true);
                break;
            }
        }
    }

    private boolean stopEndermanBlockManipulation(Location location) {
        if (location == null) {
            return false;
        }
        MainConfig.Factions.Protection protection = FactionsPlugin.getInstance().conf().factions().protection();
        if (protection.isWildernessDenyEndermanBlocks() && protection.isTerritoryDenyEndermanBlocks() && protection.isTerritoryDenyEndermanBlocksWhenOffline() && protection.isSafeZoneDenyEndermanBlocks() && protection.isWarZoneDenyEndermanBlocks()) {
            return true;
        }
        FLocation fLocation = new FLocation(location);
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        if (faction.isWilderness()) {
            return protection.isWildernessDenyEndermanBlocks();
        }
        if (faction.isNormal()) {
            return faction.hasPlayersOnline() ? protection.isTerritoryDenyEndermanBlocks() : protection.isTerritoryDenyEndermanBlocksWhenOffline();
        }
        if (faction.isSafeZone()) {
            return protection.isSafeZoneDenyEndermanBlocks();
        }
        if (faction.isWarZone()) {
            return protection.isWarZoneDenyEndermanBlocks();
        }
        return false;
    }
}

