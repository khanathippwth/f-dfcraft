/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.massivecraft.factions.integration.permcontext;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.permcontext.Context;
import com.massivecraft.factions.integration.permcontext.Contexts;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ContextManager
implements Listener {
    private static Multimap<String, Context> registeredContexts;

    public static void init(FactionsPlugin factionsPlugin) {
        registeredContexts = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
        registeredContexts.putAll(factionsPlugin.getName(), Arrays.asList(Contexts.values()));
        factionsPlugin.getServer().getPluginManager().registerEvents((Listener)new ContextManager(), (Plugin)factionsPlugin);
    }

    public static void shutdown() {
        registeredContexts = null;
    }

    public static Set<Context> getContexts() {
        if (registeredContexts == null) {
            return Collections.emptySet();
        }
        return Set.copyOf(registeredContexts.values());
    }

    public static void registerContext(Context context) {
        if (registeredContexts == null) {
            throw new IllegalStateException("Cannot register contexts before FactionsUUID finishes loading!");
        }
        Objects.requireNonNull(context);
        if (context.getNamespacedName().indexOf(58) == -1) {
            throw new IllegalArgumentException("Invalid namespaced name " + context.getNamespacedName());
        }
        if (context.getNamespace().equalsIgnoreCase("factionsuuid") || context.getNamespacedName().split(":")[0].equalsIgnoreCase("factionsuuid")) {
            throw new IllegalArgumentException("Cannot register contexts using namespace 'factionsuuid'");
        }
        registeredContexts.put(JavaPlugin.getProvidingPlugin(context.getClass()).getName(), context);
    }

    private ContextManager() {
    }

    @EventHandler
    public void onDisable(PluginDisableEvent pluginDisableEvent) {
        if (pluginDisableEvent.getPlugin().equals((Object)FactionsPlugin.getInstance())) {
            return;
        }
        if (registeredContexts != null) {
            registeredContexts.removeAll(pluginDisableEvent.getPlugin().getName());
        }
    }
}

