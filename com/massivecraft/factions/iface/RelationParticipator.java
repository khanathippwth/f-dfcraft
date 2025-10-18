/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.iface;

import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.RelationUtil;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

public interface RelationParticipator {
    public String describeTo(RelationParticipator var1);

    public String describeTo(RelationParticipator var1, boolean var2);

    public Relation getRelationTo(RelationParticipator var1);

    public Relation getRelationTo(RelationParticipator var1, boolean var2);

    @Deprecated
    public ChatColor getColorTo(RelationParticipator var1);

    default public TextColor getTextColorTo(RelationParticipator to) {
        return RelationUtil.getTextColorOfThatToMe(this, to);
    }

    public String getColorStringTo(RelationParticipator var1);
}

