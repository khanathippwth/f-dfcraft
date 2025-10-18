/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public interface FactionsAPI {
    default public int getAPIVersion() {
        return 5;
    }

    public boolean isAnotherPluginHandlingChat();

    public void setHandlingChat(Plugin var1, boolean var2);

    public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent var1);

    public boolean isPlayerFactionChatting(Player var1);

    public String getPlayerFactionTag(Player var1);

    public String getPlayerFactionTagRelation(Player var1, Player var2);

    public String getPlayerTitle(Player var1);

    public Set<String> getFactionTags();

    public Set<String> getPlayersInFaction(String var1);

    public Set<String> getOnlinePlayersInFaction(String var1);
}

