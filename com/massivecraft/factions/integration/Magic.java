/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.elmakers.mine.bukkit.api.entity.TeamProvider
 *  com.elmakers.mine.bukkit.api.magic.MagicAPI
 *  com.elmakers.mine.bukkit.api.magic.MagicProvider
 *  com.elmakers.mine.bukkit.api.protection.BlockBreakManager
 *  com.elmakers.mine.bukkit.api.protection.BlockBuildManager
 *  com.elmakers.mine.bukkit.api.protection.EntityTargetingManager
 *  com.elmakers.mine.bukkit.api.protection.PVPManager
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.integration;

import com.elmakers.mine.bukkit.api.entity.TeamProvider;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.magic.MagicProvider;
import com.elmakers.mine.bukkit.api.protection.BlockBreakManager;
import com.elmakers.mine.bukkit.api.protection.BlockBuildManager;
import com.elmakers.mine.bukkit.api.protection.EntityTargetingManager;
import com.elmakers.mine.bukkit.api.protection.PVPManager;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Magic
implements BlockBuildManager,
BlockBreakManager,
PVPManager,
TeamProvider,
EntityTargetingManager,
Listener {
    public static boolean init(Plugin plugin) {
        if (plugin instanceof MagicAPI) {
            try {
                int n = Integer.parseInt(plugin.getDescription().getVersion().split("\\.")[0]);
                if (n < 8) {
                    FactionsPlugin.getInstance().getLogger().info("Found Magic, but only supporting version 8+");
                    return false;
                }
            } catch (NumberFormatException numberFormatException) {
                FactionsPlugin.getInstance().getLogger().info("Found Magic, but could not determine version");
                return false;
            }
            FactionsPlugin.getInstance().getLogger().info("Integrating with Magic!");
            ((MagicAPI)plugin).getController().register((MagicProvider)new Magic());
            return true;
        }
        return false;
    }

    public boolean hasBuildPermission(Player player, Block block) {
        if (block == null) {
            return true;
        }
        if (player == null) {
            return Board.getInstance().getFactionAt(new FLocation(block)).isWilderness();
        }
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), PermissibleActions.BUILD, true);
    }

    public boolean hasBreakPermission(Player player, Block block) {
        if (block == null) {
            return true;
        }
        if (player == null) {
            return Board.getInstance().getFactionAt(new FLocation(block)).isWilderness();
        }
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), PermissibleActions.DESTROY, true);
    }

    public boolean isPVPAllowed(Player player, Location location) {
        EconomyParticipator economyParticipator;
        if (player == null && !FactionsPlugin.getInstance().conf().magicPlugin().isUsePVPSettingForMagicMobs()) {
            return true;
        }
        MainConfig.Factions factions = FactionsPlugin.getInstance().conf().factions();
        if (factions.pvp().getWorldsIgnorePvP().contains(location.getWorld().getName())) {
            return true;
        }
        if (player != null && factions.protection().getPlayersWhoBypassAllProtection().contains(player.getName())) {
            return true;
        }
        Faction faction = Board.getInstance().getFactionAt(new FLocation(location));
        if (faction.noPvPInTerritory()) {
            return false;
        }
        if (player != null) {
            economyParticipator = FPlayers.getInstance().getByPlayer(player);
            if (economyParticipator.hasLoginPvpDisabled()) {
                return false;
            }
            Faction faction2 = Board.getInstance().getFactionAt(new FLocation((FPlayer)economyParticipator));
            if (faction2.noPvPInTerritory() || faction2.isSafeZone()) {
                return false;
            }
        }
        if ((economyParticipator = Board.getInstance().getFactionAt(new FLocation(location))).noPvPInTerritory()) {
            return false;
        }
        return !economyParticipator.isSafeZone();
    }

    public boolean isFriendly(Entity entity, Entity entity2) {
        if (!(entity instanceof Player) || !(entity2 instanceof Player)) {
            return false;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player)entity);
        FPlayer fPlayer2 = FPlayers.getInstance().getByPlayer((Player)entity2);
        if (fPlayer.getFaction().isWilderness() || fPlayer2.getFaction().isWilderness()) {
            return false;
        }
        return fPlayer.getRelationTo(fPlayer2).isAtLeast(Relation.TRUCE);
    }

    public boolean canTarget(Entity entity, Entity entity2) {
        return FactionsEntityListener.canDamage(entity, entity2, false);
    }
}

