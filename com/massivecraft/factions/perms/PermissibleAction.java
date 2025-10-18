/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.massivecraft.factions.perms;

import com.massivecraft.factions.perms.PermissibleActionRegistry;
import org.bukkit.Material;

public interface PermissibleAction {
    @Deprecated
    public boolean isFactionOnly();

    @Deprecated
    public Material getMaterial();

    public String getName();

    public String getDescription();

    public String getShortDescription();

    @Deprecated
    public static PermissibleAction valueOf(String name) {
        PermissibleAction action = PermissibleActionRegistry.get(name);
        if (action == null) {
            throw new IllegalArgumentException("Invalid name '" + name + "'");
        }
        return action;
    }
}

