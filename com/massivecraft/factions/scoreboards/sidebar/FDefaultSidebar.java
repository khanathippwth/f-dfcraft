/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.scoreboards.sidebar;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.MemoryBoard;
import com.massivecraft.factions.scoreboards.FSidebarProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class FDefaultSidebar
extends FSidebarProvider {
    @Override
    public String getTitle(FPlayer fPlayer) {
        if (FactionsPlugin.getInstance().conf().scoreboard().constant().isFactionlessEnabled() && !fPlayer.hasFaction()) {
            return this.replaceTags(fPlayer, FactionsPlugin.getInstance().conf().scoreboard().constant().getFactionlessTitle());
        }
        return this.replaceTags(fPlayer, FactionsPlugin.getInstance().conf().scoreboard().constant().getTitle());
    }

    @Override
    public List<String> getLines(FPlayer fPlayer) {
        if (FactionsPlugin.getInstance().conf().scoreboard().constant().isFactionlessEnabled() && !fPlayer.hasFaction()) {
            return this.getOutput(fPlayer, FactionsPlugin.getInstance().conf().scoreboard().constant().getFactionlessContent());
        }
        return this.getOutput(fPlayer, FactionsPlugin.getInstance().conf().scoreboard().constant().getContent());
    }

    public List<String> getOutput(FPlayer fPlayer, List<String> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<String>();
        }
        List<Component> list2 = null;
        String string = "0123456789abcdef";
        list = new ArrayList<String>(list);
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Object object;
            String string2 = listIterator.next();
            if (string2 == null) {
                listIterator.remove();
                continue;
            }
            if (string2.contains("{map}")) {
                if (list2 == null) {
                    list2 = ((MemoryBoard)Board.getInstance()).getScoreboardMap(fPlayer);
                }
                Object object2 = object = list2.isEmpty() ? "" : LegacyComponentSerializer.legacySection().serialize((Component)list2.removeFirst());
                if (!((String)object).isEmpty() && !string.isEmpty()) {
                    object = "\u00a7" + string.charAt(0) + "\u00a7r" + (String)object;
                    string = string.substring(1);
                }
                string2 = string2.replace("{map}", (CharSequence)object);
            }
            if ((object = this.replaceTags(fPlayer, string2)) == null) {
                listIterator.remove();
                continue;
            }
            listIterator.set((String)object);
        }
        return list;
    }
}

