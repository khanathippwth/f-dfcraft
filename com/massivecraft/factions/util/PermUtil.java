/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.permissions.Permission
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.TL;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class PermUtil {
    public final Map<String, String> permissionDescriptions = new HashMap<String, String>();
    protected final FactionsPlugin plugin;

    public PermUtil(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
        this.setup();
    }

    public String getForbiddenMessage(String string) {
        return this.plugin.txt().parse(TL.GENERIC_NOPERMISSION.toString(), this.getPermissionDescription(string));
    }

    public final void setup() {
        for (Permission permission : this.plugin.getDescription().getPermissions()) {
            this.permissionDescriptions.put(permission.getName(), permission.getDescription());
        }
    }

    public String getPermissionDescription(String string) {
        String string2 = this.permissionDescriptions.get(string);
        if (string2 == null) {
            return TL.GENERIC_DOTHAT.toString();
        }
        return string2;
    }

    public boolean has(CommandSender commandSender, String string, boolean bl) {
        if (commandSender == null) {
            return false;
        }
        if (commandSender.hasPermission(string)) {
            return true;
        }
        if (bl) {
            commandSender.sendMessage(this.getForbiddenMessage(string));
        }
        return false;
    }
}

