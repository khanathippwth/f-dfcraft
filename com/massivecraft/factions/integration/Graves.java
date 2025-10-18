/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.ranull.graves.Graves
 *  com.ranull.graves.event.GraveCreateEvent
 *  org.bukkit.block.Block
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.ranull.graves.event.GraveCreateEvent;
import java.util.logging.Level;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Graves {
    private static com.ranull.graves.Graves plugin;

    public static boolean init(Plugin plugin) {
        Graves.plugin = (com.ranull.graves.Graves)plugin;
        FactionsPlugin factionsPlugin = FactionsPlugin.getInstance();
        factionsPlugin.getLogger().info("Found Graves plugin");
        MainConfig.Plugins.Graves graves = factionsPlugin.conf().plugins().graves();
        if (graves.isAllowAnyoneToOpenGraves()) {
            factionsPlugin.getLogger().info("Configured to allow anyone to open graves regardless of permissions.");
        }
        if (graves.isPreventGravesInSafezone() || graves.isPreventGravesInWarzone()) {
            String string = graves.isPreventGravesInSafezone() && !graves.isPreventGravesInWarzone() ? "safezone." : (!graves.isPreventGravesInSafezone() ? "warzone." : "safezone and warzone.");
            factionsPlugin.getLogger().info("Configured to prevent graves in " + string);
            FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents((Listener)new GraveListener(), (Plugin)FactionsPlugin.getInstance());
        }
        return true;
    }

    public static boolean allowAnyway(Block block) {
        try {
            if (plugin != null && FactionsPlugin.getInstance().conf().plugins().graves().isAllowAnyoneToOpenGraves()) {
                return plugin.getBlockManager().getGraveFromBlock(block) != null;
            }
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().log(Level.WARNING, "A Grave(s) error occurred!", exception);
        }
        return false;
    }

    private static class GraveListener
    implements Listener {
        private GraveListener() {
        }

        @EventHandler
        public void graveCreate(GraveCreateEvent graveCreateEvent) {
            boolean bl = FactionsPlugin.getInstance().conf().plugins().graves().isPreventGravesInSafezone();
            boolean bl2 = FactionsPlugin.getInstance().conf().plugins().graves().isPreventGravesInWarzone();
            if (!bl && !bl2) {
                return;
            }
            if (!FactionsPlugin.getInstance().worldUtil().isEnabled(graveCreateEvent.getGrave().getLocationDeath().getWorld())) {
                return;
            }
            Faction faction = Board.getInstance().getFactionAt(new FLocation(graveCreateEvent.getGrave().getLocationDeath()));
            if (bl && faction.isSafeZone() || bl2 && faction.isWarZone()) {
                graveCreateEvent.setCancelled(true);
                plugin.debugMessage("Grave not created for " + plugin.getEntityManager().getEntityName(graveCreateEvent.getEntity()) + " because FactionsUUID (which is sending this debug message) blocked it (" + (faction.isSafeZone() ? "safezone" : "warzone") + " protected)", 2);
            }
        }
    }
}

