/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.perms;

import com.massivecraft.factions.perms.Selectable;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

public interface Permissible
extends Selectable {
    public String name();

    public ChatColor getColor();

    public TextColor getTextColor();

    public String getTranslation();
}

