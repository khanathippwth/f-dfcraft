/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.permission.Permission
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.plugin.RegisteredServiceProvider
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.FactionsPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPerms {
    private Permission perms = null;

    public VaultPerms() {
        try {
            RegisteredServiceProvider registeredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (registeredServiceProvider != null) {
                this.perms = (Permission)registeredServiceProvider.getProvider();
            }
        } catch (NoClassDefFoundError noClassDefFoundError) {
            return;
        }
        if (this.perms != null) {
            FactionsPlugin.getInstance().getLogger().info("Using Vault with permissions plugin " + this.perms.getName());
        }
    }

    public String getName() {
        return this.perms == null ? "nope" : this.perms.getName();
    }

    public Object getPerms() {
        return this.perms;
    }

    public String getPrimaryGroup(OfflinePlayer offlinePlayer) {
        return this.perms == null || !this.perms.hasGroupSupport() ? " " : this.perms.getPrimaryGroup(((World)Bukkit.getWorlds().getFirst()).toString(), offlinePlayer);
    }
}

