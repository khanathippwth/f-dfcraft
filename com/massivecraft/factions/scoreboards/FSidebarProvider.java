/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;
import java.util.List;

public abstract class FSidebarProvider {
    public abstract String getTitle(FPlayer var1);

    public abstract List<String> getLines(FPlayer var1);

    public String replaceTags(FPlayer fPlayer, String string) {
        string = Tag.parsePlaceholders(fPlayer.getPlayer(), string);
        return this.qualityAssure(Tag.parsePlain(fPlayer, string));
    }

    public String replaceTags(Faction faction, FPlayer fPlayer, String string) {
        string = Tag.parsePlaceholders(fPlayer.getPlayer(), string);
        return this.qualityAssure(Tag.parsePlain(faction, fPlayer, string));
    }

    private String qualityAssure(String string) {
        if (string.contains("{notFrozen}") || string.contains("{notPermanent}")) {
            return "n/a";
        }
        if (string.contains("{ig}")) {
            return TL.COMMAND_SHOW_NOHOME.toString();
        }
        return FactionsPlugin.getInstance().txt().parse(string);
    }
}

