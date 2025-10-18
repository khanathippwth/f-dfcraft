/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.math.BlockVector3
 *  com.sk89q.worldguard.LocalPlayer
 *  com.sk89q.worldguard.WorldGuard
 *  com.sk89q.worldguard.bukkit.WorldGuardPlugin
 *  com.sk89q.worldguard.protection.ApplicableRegionSet
 *  com.sk89q.worldguard.protection.flags.Flag
 *  com.sk89q.worldguard.protection.flags.StateFlag
 *  com.sk89q.worldguard.protection.flags.StateFlag$State
 *  com.sk89q.worldguard.protection.flags.registry.FlagConflictException
 *  com.sk89q.worldguard.protection.managers.RegionManager
 *  com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion
 *  com.sk89q.worldguard.protection.regions.RegionContainer
 *  com.sk89q.worldguard.protection.regions.RegionQuery
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.FactionsPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Worldguard {
    public static final String FLAG_CLAIM_NAME = "fuuid-claim";
    public static final String FLAG_PVP_NAME = "fuuid-pvp";
    public static final String FLAG_NOLOSS_NAME = "fuuid-noloss";
    private static StateFlag FLAG_CLAIM;
    private static StateFlag FLAG_PVP;
    private static StateFlag FLAG_NOLOSS;

    public static void onLoad() {
        FLAG_CLAIM = Worldguard.registerOrGet(FLAG_CLAIM_NAME);
        Worldguard.status(FLAG_CLAIM != null, FLAG_CLAIM_NAME);
        FLAG_PVP = Worldguard.registerOrGet(FLAG_PVP_NAME);
        Worldguard.status(FLAG_PVP != null, FLAG_PVP_NAME);
        FLAG_NOLOSS = Worldguard.registerOrGet(FLAG_NOLOSS_NAME);
        Worldguard.status(FLAG_NOLOSS != null, FLAG_NOLOSS_NAME);
    }

    private static StateFlag registerOrGet(String string) {
        try {
            StateFlag stateFlag = new StateFlag(string, false);
            WorldGuard.getInstance().getFlagRegistry().register((Flag)stateFlag);
            return stateFlag;
        } catch (FlagConflictException flagConflictException) {
            Flag flag = WorldGuard.getInstance().getFlagRegistry().get(string);
            if (flag instanceof StateFlag) {
                StateFlag stateFlag = (StateFlag)flag;
                return stateFlag;
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    private static void status(boolean bl, String string) {
        FactionsPlugin.getInstance().getLogger().info((bl ? "Registered" : "Failed to register") + " flag '" + string + "' with WorldGuard.");
    }

    public boolean isNoLossFlag(Player player) {
        return this.isFlag(player, FLAG_NOLOSS, "noloss");
    }

    public boolean isCustomPVPFlag(Player player) {
        return this.isFlag(player, FLAG_PVP, "PVP");
    }

    private boolean isFlag(Player player, StateFlag stateFlag, String string) {
        if (stateFlag == null) {
            return false;
        }
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = regionContainer.createQuery();
        boolean bl = regionQuery.testState(localPlayer.getLocation(), localPlayer, new StateFlag[]{stateFlag});
        FactionsPlugin.getInstance().debug("Testing " + string + " flag for player " + player.getName() + ": " + bl);
        return bl;
    }

    public boolean playerCanBuild(Player player, Location location) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = regionContainer.createQuery();
        return regionQuery.testBuild(BukkitAdapter.adapt((Location)location), localPlayer, new StateFlag[0]);
    }

    public boolean checkForRegionsInChunk(Chunk chunk) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt((World)chunk.getWorld()));
        if (regionManager == null) {
            return false;
        }
        World world = chunk.getWorld();
        int n = chunk.getX() << 4;
        int n2 = chunk.getZ() << 4;
        int n3 = n + 15;
        int n4 = n2 + 15;
        int n5 = world.getMaxHeight();
        int n6 = world.getMinHeight();
        BlockVector3 blockVector3 = BlockVector3.at((int)n, (int)n6, (int)n2);
        BlockVector3 blockVector32 = BlockVector3.at((int)n3, (int)n5, (int)n4);
        ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion("wgregionflagcheckforfactions", blockVector3, blockVector32);
        ApplicableRegionSet applicableRegionSet = regionManager.getApplicableRegions((ProtectedRegion)protectedCuboidRegion);
        if (FactionsPlugin.getInstance().conf().worldGuard().isChecking()) {
            return applicableRegionSet.size() > 0;
        }
        if (FLAG_CLAIM == null) {
            return false;
        }
        for (ProtectedRegion protectedRegion : applicableRegionSet.getRegions()) {
            StateFlag.State state = (StateFlag.State)protectedRegion.getFlag((Flag)FLAG_CLAIM);
            if (state != StateFlag.State.DENY) continue;
            return true;
        }
        return false;
    }

    public String getVersion() {
        return WorldGuardPlugin.inst().getDescription().getVersion();
    }
}

