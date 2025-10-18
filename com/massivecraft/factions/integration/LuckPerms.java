/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  net.luckperms.api.LuckPerms
 *  net.luckperms.api.LuckPermsProvider
 *  net.luckperms.api.context.ContextCalculator
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.permcontext.LuckpermsContextCalculator;
import java.util.logging.Level;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;

public class LuckPerms {
    private static LuckpermsContextCalculator calculator;

    public static boolean init(FactionsPlugin factionsPlugin) {
        calculator = new LuckpermsContextCalculator();
        try {
            net.luckperms.api.LuckPerms luckPerms = LuckPermsProvider.get();
            luckPerms.getContextManager().registerCalculator((ContextCalculator)calculator);
        } catch (Exception exception) {
            factionsPlugin.getLogger().log(Level.SEVERE, "Failed to connect to LuckPerms!", exception);
            return false;
        }
        factionsPlugin.log("Successfully hooked into LuckPerms for permission contexts!");
        return true;
    }

    public static void shutdown(FactionsPlugin factionsPlugin) {
        if (calculator != null) {
            try {
                net.luckperms.api.LuckPerms luckPerms = LuckPermsProvider.get();
                luckPerms.getContextManager().unregisterCalculator((ContextCalculator)calculator);
            } catch (Exception exception) {
                factionsPlugin.getLogger().log(Level.SEVERE, "Failed to unregister contexts with LuckPerms!", exception);
            }
        }
    }
}

