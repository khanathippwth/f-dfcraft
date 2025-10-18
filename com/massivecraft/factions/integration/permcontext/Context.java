/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.integration.permcontext;

import java.util.Set;
import org.bukkit.entity.Player;

public interface Context {
    public String getName();

    public String getNamespace();

    default public String getNamespacedName() {
        return this.getNamespace() + ":" + this.getName();
    }

    public Set<String> getPossibleValues();

    public Set<String> getValues(Player var1);
}

