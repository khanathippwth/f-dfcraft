/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.mcmonkey.sentinel.SentinelIntegration
 *  org.mcmonkey.sentinel.SentinelPlugin
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Relation;
import java.util.logging.Level;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.mcmonkey.sentinel.SentinelIntegration;
import org.mcmonkey.sentinel.SentinelPlugin;

public class Sentinel
extends SentinelIntegration {
    public static String TARGET_FACTIONS = "factions";
    public static String TARGET_FACTIONS_ENEMY = "factionsEnemy";
    public static String TARGET_FACTIONS_ALLY = "factionsAlly";

    public static boolean init(Plugin plugin) {
        FactionsPlugin.getInstance().getLogger().info("Attempting to integrate with Sentinel!");
        try {
            ((SentinelPlugin)plugin).registerIntegration((SentinelIntegration)new Sentinel());
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().log(Level.WARNING, "Could not load Sentinel integration", exception);
            return false;
        }
        FactionsPlugin.getInstance().getLogger().info("Loaded Sentinel integration!");
        FactionsPlugin.getInstance().getLogger().info("");
        FactionsPlugin.getInstance().getLogger().info("You may safely ignore the Sentinel message warning you about compatibility, as we run our own integration.");
        FactionsPlugin.getInstance().getLogger().info("");
        return true;
    }

    public String getTargetHelp() {
        return TARGET_FACTIONS + ":FACTION_NAME, " + TARGET_FACTIONS_ENEMY + ":NAME, " + TARGET_FACTIONS_ALLY + ":NAME";
    }

    public String[] getTargetPrefixes() {
        return new String[]{TARGET_FACTIONS, TARGET_FACTIONS_ENEMY, TARGET_FACTIONS_ALLY};
    }

    public boolean isTarget(LivingEntity livingEntity, String string, String string2) {
        if (!(livingEntity instanceof Player)) {
            return false;
        }
        Faction faction = Factions.getInstance().getByTag(string2);
        if (faction == null) {
            return false;
        }
        Faction faction2 = FPlayers.getInstance().getByPlayer((Player)livingEntity).getFaction();
        if (string.equals(TARGET_FACTIONS)) {
            return faction == faction2;
        }
        if (string.equals(TARGET_FACTIONS_ENEMY)) {
            return faction.getRelationTo(faction2).equals(Relation.ENEMY);
        }
        if (string.equals(TARGET_FACTIONS_ALLY)) {
            return faction.getRelationTo(faction2).equals(Relation.ALLY);
        }
        return false;
    }
}

