/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;
import java.util.function.Supplier;
import org.bukkit.Bukkit;

public enum GeneralTag implements Tag
{
    MAX_WARPS("max-warps", () -> String.valueOf(FactionsPlugin.getInstance().conf().commands().warp().getMaxWarps())),
    MAX_ALLIES("max-allies", () -> GeneralTag.getRelation(Relation.ALLY)),
    MAX_ENEMIES("max-enemies", () -> GeneralTag.getRelation(Relation.ENEMY)),
    MAX_TRUCES("max-truces", () -> GeneralTag.getRelation(Relation.TRUCE)),
    FACTIONLESS("factionless", () -> String.valueOf(FPlayers.getInstance().getOnlinePlayers().stream().filter(fPlayer -> !fPlayer.hasFaction()).count())),
    FACTIONLESS_TOTAL("factionless-total", () -> String.valueOf(FPlayers.getInstance().getAllFPlayers().stream().filter(fPlayer -> !fPlayer.hasFaction()).count())),
    TOTAL_ONLINE("total-online", () -> String.valueOf(Bukkit.getOnlinePlayers().size()));

    private final String tag;
    private final Supplier<String> supplier;

    private static String getRelation(Relation relation) {
        if (FactionsPlugin.getInstance().conf().factions().maxRelations().isEnabled()) {
            return String.valueOf(relation.getMax());
        }
        return TL.GENERIC_INFINITY.toString();
    }

    public static String parse(String string) {
        for (GeneralTag generalTag : GeneralTag.values()) {
            string = generalTag.replace(string);
        }
        return string;
    }

    private GeneralTag(String string2, Supplier<String> supplier) {
        this.tag = "{" + string2 + "}";
        this.supplier = supplier;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean foundInString(String string) {
        return string != null && string.contains(this.tag);
    }

    public String replace(String string) {
        if (!this.foundInString(string)) {
            return string;
        }
        String string2 = this.supplier.get();
        return string2 == null ? null : string.replace(this.tag, string2);
    }
}

