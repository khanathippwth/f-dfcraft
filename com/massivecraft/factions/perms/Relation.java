/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.perms;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Permissible;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import java.util.Collections;
import java.util.Set;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

public enum Relation implements Permissible
{
    MEMBER(4, TL.RELATION_MEMBER_SINGULAR.toString()),
    ALLY(3, TL.RELATION_ALLY_SINGULAR.toString()),
    TRUCE(2, TL.RELATION_TRUCE_SINGULAR.toString()),
    NEUTRAL(1, TL.RELATION_NEUTRAL_SINGULAR.toString()),
    ENEMY(0, TL.RELATION_ENEMY_SINGULAR.toString());

    public final int value;
    public final String nicename;
    private final Set<String> justMyNameInASet;

    private Relation(int n2, String string2) {
        this.value = n2;
        this.nicename = string2;
        this.justMyNameInASet = Collections.singleton(this.name().toLowerCase());
    }

    public static Relation fromString(String string) {
        if (string == null) {
            return NEUTRAL;
        }
        if (string.equalsIgnoreCase(Relation.MEMBER.nicename)) {
            return MEMBER;
        }
        if (string.equalsIgnoreCase(Relation.ALLY.nicename)) {
            return ALLY;
        }
        if (string.equalsIgnoreCase(Relation.TRUCE.nicename)) {
            return TRUCE;
        }
        if (string.equalsIgnoreCase(Relation.ENEMY.nicename)) {
            return ENEMY;
        }
        return switch (string.toUpperCase()) {
            case "MEMBER" -> MEMBER;
            case "ALLY" -> ALLY;
            case "TRUCE" -> TRUCE;
            case "ENEMY" -> ENEMY;
            default -> NEUTRAL;
        };
    }

    public String toString() {
        return this.nicename;
    }

    @Override
    public String getTranslation() {
        try {
            return TL.valueOf("RELATION_" + this.name() + "_SINGULAR").toString();
        } catch (IllegalArgumentException illegalArgumentException) {
            return this.toString();
        }
    }

    public String getPluralTranslation() {
        for (TL tL : TL.values()) {
            if (!tL.name().equalsIgnoreCase("RELATION_" + this.name() + "_PLURAL")) continue;
            return tL.toString();
        }
        return this.toString();
    }

    public boolean isMember() {
        return this == MEMBER;
    }

    public boolean isAlly() {
        return this == ALLY;
    }

    public boolean isTruce() {
        return this == TRUCE;
    }

    public boolean isNeutral() {
        return this == NEUTRAL;
    }

    public boolean isEnemy() {
        return this == ENEMY;
    }

    public boolean isAtLeast(Relation relation) {
        return this.value >= relation.value;
    }

    public boolean isAtMost(Relation relation) {
        return this.value <= relation.value;
    }

    @Override
    public ChatColor getColor() {
        return TextUtil.getClosest(this.getTextColor());
    }

    @Override
    public TextColor getTextColor() {
        return switch (this.ordinal()) {
            case 0 -> FactionsPlugin.getInstance().conf().colors().relations().getMember();
            case 1 -> FactionsPlugin.getInstance().conf().colors().relations().getAlly();
            case 3 -> FactionsPlugin.getInstance().conf().colors().relations().getNeutral();
            case 2 -> FactionsPlugin.getInstance().conf().colors().relations().getTruce();
            default -> FactionsPlugin.getInstance().conf().colors().relations().getEnemy();
        };
    }

    public int getMax() {
        return switch (this.ordinal()) {
            case 1 -> FactionsPlugin.getInstance().conf().factions().maxRelations().getAlly();
            case 4 -> FactionsPlugin.getInstance().conf().factions().maxRelations().getEnemy();
            case 2 -> FactionsPlugin.getInstance().conf().factions().maxRelations().getTruce();
            default -> FactionsPlugin.getInstance().conf().factions().maxRelations().getNeutral();
        };
    }

    public double getRelationCost() {
        if (this.isEnemy()) {
            return FactionsPlugin.getInstance().conf().economy().getCostEnemy();
        }
        if (this.isAlly()) {
            return FactionsPlugin.getInstance().conf().economy().getCostAlly();
        }
        if (this.isTruce()) {
            return FactionsPlugin.getInstance().conf().economy().getCostTruce();
        }
        return FactionsPlugin.getInstance().conf().economy().getCostNeutral();
    }

    public Set<String> getNameInASet() {
        return this.justMyNameInASet;
    }
}

