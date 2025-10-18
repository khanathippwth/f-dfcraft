/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.griefcraft.lwc.LWC
 *  com.griefcraft.lwc.LWCPlugin
 *  com.griefcraft.model.Protection
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.integration;

import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

public class LWC {
    private static com.griefcraft.lwc.LWC lwc;

    public static boolean setup(Plugin plugin) {
        if (!(plugin instanceof LWCPlugin)) {
            return false;
        }
        lwc = ((LWCPlugin)plugin).getLWC();
        FactionsPlugin.getInstance().log("Successfully hooked into LWC!" + (FactionsPlugin.getInstance().conf().lwc().isEnabled() ? "" : " Integration is currently disabled (\"lwc.integration\")."));
        return true;
    }

    public static boolean getEnabled() {
        return lwc != null && FactionsPlugin.getInstance().conf().lwc().isEnabled();
    }

    public static Plugin getLWC() {
        return lwc == null ? null : lwc.getPlugin();
    }

    public static void clearOtherLocks(FLocation fLocation, Faction faction) {
        for (Block block : LWC.findBlocks(fLocation)) {
            Protection protection = lwc.findProtection(block);
            if (protection == null || faction.getFPlayers().contains(FPlayers.getInstance().getByOfflinePlayer(Bukkit.getServer().getOfflinePlayer(protection.getOwner())))) continue;
            protection.remove();
        }
    }

    public static void clearAllLocks(FLocation fLocation) {
        for (Block block : LWC.findBlocks(fLocation)) {
            Protection protection = lwc.findProtection(block);
            if (protection == null) continue;
            protection.remove();
        }
    }

    private static List<Block> findBlocks(FLocation fLocation) {
        World world = Bukkit.getWorld((String)fLocation.getWorldName());
        if (world == null) {
            return Collections.emptyList();
        }
        Location location = new Location(world, (double)(fLocation.getX() * 16L), 5.0, (double)(fLocation.getZ() * 16L));
        BlockState[] blockStateArray = location.getChunk().getTileEntities();
        LinkedList<Block> linkedList = new LinkedList<Block>();
        for (BlockState blockState : blockStateArray) {
            if (!lwc.isProtectable(blockState)) continue;
            linkedList.add(blockState.getBlock());
        }
        return linkedList;
    }
}

