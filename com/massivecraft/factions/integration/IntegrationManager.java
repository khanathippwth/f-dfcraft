/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginEnableEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.SimplePluginManager
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.Depenizen;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.Graves;
import com.massivecraft.factions.integration.LWC;
import com.massivecraft.factions.integration.LuckPerms;
import com.massivecraft.factions.integration.Magic;
import com.massivecraft.factions.integration.Sentinel;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.integration.dynmap.EngineDynmap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

public class IntegrationManager
implements Listener {
    private final Set<Integration> integrations = new HashSet<Integration>();

    public static void onLoad(FactionsPlugin factionsPlugin) {
        try {
            Field field = SimplePluginManager.class.getDeclaredField("dependencyGraph");
            field.setAccessible(true);
            Object object = field.get(factionsPlugin.getServer().getPluginManager());
            Method method = object.getClass().getDeclaredMethod("putEdge", Object.class, Object.class);
            method.setAccessible(true);
            for (String string : Integration.STARTUP_MAP.keySet()) {
                method.invoke(object, factionsPlugin.getDescription().getName(), string);
            }
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public IntegrationManager(FactionsPlugin factionsPlugin) {
        for (Integration integration : Integration.values()) {
            Plugin plugin = factionsPlugin.getServer().getPluginManager().getPlugin(integration.pluginName);
            if (plugin == null || !plugin.isEnabled()) continue;
            try {
                if (!integration.startup.apply(plugin).booleanValue()) continue;
                this.integrations.add(integration);
            } catch (Exception exception) {
                factionsPlugin.getLogger().log(Level.WARNING, "Failed to start " + integration.pluginName + " integration", exception);
            }
        }
    }

    @EventHandler
    public void onPluginEnabled(PluginEnableEvent pluginEnableEvent) {
        if (Integration.getStartup(pluginEnableEvent.getPlugin().getName()).apply(pluginEnableEvent.getPlugin()).booleanValue()) {
            this.integrations.add(Integration.INT_MAP.get(pluginEnableEvent.getPlugin().getName()));
        }
    }

    public boolean isEnabled(Integration integration) {
        return this.integrations.contains((Object)integration);
    }

    public static enum Integration {
        DYNMAP("dynmap", EngineDynmap.getInstance()::init),
        ESS("Essentials", plugin -> Essentials.setup(plugin)),
        DEPENIZEN("Depenizen", plugin -> Depenizen.init(plugin)),
        GRAVES("Graves", Graves::init),
        LUCKPERMS("LuckPerms", plugin -> {
            String[] stringArray = plugin.getDescription().getVersion().split("\\.");
            boolean bl = true;
            try {
                int n = Integer.parseInt(stringArray[0]);
                int n2 = Integer.parseInt(stringArray[1]);
                if (n == 5 && n2 > 0 || n > 5) {
                    bl = false;
                }
            } catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            if (bl) {
                FactionsPlugin.getInstance().log("Found an outdated LuckPerms. With LuckPerms 5.1.0 and above, FactionsUUID supports permission contexts!");
            } else if (LuckPerms.init(FactionsPlugin.getInstance())) {
                FactionsPlugin.getInstance().luckpermsEnabled();
            }
            return true;
        }),
        LWC("LWC", LWC::setup),
        MAGIC("Magic", plugin -> Magic.init(plugin)),
        PLACEHOLDERAPI("PlaceholderAPI", plugin -> FactionsPlugin.getInstance().setupPlaceholderAPI()),
        PLACEHOLDERAPI_OTHER("MVdWPlaceholderAPI", plugin -> FactionsPlugin.getInstance().setupOtherPlaceholderAPI()),
        SENTINEL("Sentinel", plugin -> Sentinel.init(plugin)),
        WORLDGUARD("WorldGuard", plugin -> {
            FactionsPlugin factionsPlugin = FactionsPlugin.getInstance();
            String string = plugin.getDescription().getVersion();
            if (string.startsWith("7")) {
                factionsPlugin.setWorldGuard(new Worldguard());
                factionsPlugin.getLogger().info("Found support for WorldGuard version " + string);
                return true;
            }
            factionsPlugin.log(Level.WARNING, "Found WorldGuard but couldn't support this version: " + string);
            return false;
        });

        private static final Map<String, Function<Plugin, Boolean>> STARTUP_MAP;
        private static final Map<String, Integration> INT_MAP;
        private final String pluginName;
        private final Function<Plugin, Boolean> startup;

        static Function<Plugin, Boolean> getStartup(String string) {
            return STARTUP_MAP.getOrDefault(string, Integration::omNomNom);
        }

        private static boolean omNomNom(Plugin plugin) {
            return false;
        }

        private Integration(String string2, Function<Plugin, Boolean> function) {
            this.pluginName = string2;
            this.startup = function;
        }

        static {
            STARTUP_MAP = new HashMap<String, Function<Plugin, Boolean>>();
            INT_MAP = new HashMap<String, Integration>();
            for (Integration integration : Integration.values()) {
                STARTUP_MAP.put(integration.pluginName, integration.startup);
                INT_MAP.put(integration.pluginName, integration);
            }
        }
    }
}

