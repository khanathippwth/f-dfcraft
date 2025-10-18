/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 */
package com.massivecraft.factions.iface;

import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.util.TL;
import org.bukkit.OfflinePlayer;

public interface EconomyParticipator
extends RelationParticipator {
    public String getAccountId();

    public OfflinePlayer getOfflinePlayer();

    public void msg(String var1, Object ... var2);

    public void msg(TL var1, Object ... var2);
}

