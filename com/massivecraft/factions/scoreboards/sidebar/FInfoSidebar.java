/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.scoreboards.sidebar;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.scoreboards.FSidebarProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class FInfoSidebar
extends FSidebarProvider {
    private final Faction faction;

    public FInfoSidebar(Faction faction) {
        this.faction = faction;
    }

    @Override
    public String getTitle(FPlayer fPlayer) {
        return this.replaceTags(this.faction, fPlayer, FactionsPlugin.getInstance().conf().scoreboard().info().getTitle());
    }

    @Override
    public List<String> getLines(FPlayer fPlayer) {
        ArrayList<String> arrayList = new ArrayList<String>(FactionsPlugin.getInstance().conf().scoreboard().info().getContent());
        ListIterator<String> listIterator = arrayList.listIterator();
        while (listIterator.hasNext()) {
            String string = (String)listIterator.next();
            if (string == null) {
                listIterator.remove();
                continue;
            }
            String string2 = this.replaceTags(this.faction, fPlayer, string);
            if (string2 == null) {
                listIterator.remove();
                continue;
            }
            listIterator.set(string2);
        }
        return arrayList;
    }
}

