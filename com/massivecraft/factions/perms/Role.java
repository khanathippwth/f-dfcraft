/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.perms;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Permissible;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

public enum Role implements Permissible
{
    ADMIN(4, TL.ROLE_ADMIN),
    COLEADER(3, TL.ROLE_COLEADER),
    MODERATOR(2, TL.ROLE_MODERATOR),
    NORMAL(1, TL.ROLE_NORMAL),
    RECRUIT(0, TL.ROLE_RECRUIT);

    public final int value;
    public final String nicename;
    public final TL translation;
    private Set<String> roleNamesAtOrBelow;
    private Set<String> roleNamesAtOrAbove;

    private Role(int n2, TL tL) {
        this.value = n2;
        this.nicename = tL.toString();
        this.translation = tL;
    }

    public boolean isAtLeast(Role role) {
        return this.value >= role.value;
    }

    public boolean isAtMost(Role role) {
        return this.value <= role.value;
    }

    public static Role getRelative(Role role, int n) {
        return Role.getByValue(role.value + n);
    }

    public static Role getByValue(int n) {
        return switch (n) {
            case 0 -> RECRUIT;
            case 1 -> NORMAL;
            case 2 -> MODERATOR;
            case 3 -> COLEADER;
            case 4 -> ADMIN;
            default -> null;
        };
    }

    public static Role fromString(String string) {
        return switch (string.toLowerCase(Locale.ROOT)) {
            case "admin" -> ADMIN;
            case "coleader", "coowner" -> COLEADER;
            case "mod", "moderator" -> MODERATOR;
            case "normal", "member" -> NORMAL;
            case "recruit", "rec" -> RECRUIT;
            default -> null;
        };
    }

    public String toString() {
        return this.nicename;
    }

    @Override
    public String getTranslation() {
        return this.translation.toString();
    }

    public String getPrefix() {
        if (this == ADMIN) {
            return FactionsPlugin.getInstance().conf().factions().prefixes().getAdmin();
        }
        if (this == COLEADER) {
            return FactionsPlugin.getInstance().conf().factions().prefixes().getColeader();
        }
        if (this == MODERATOR) {
            return FactionsPlugin.getInstance().conf().factions().prefixes().getMod();
        }
        if (this == NORMAL) {
            return FactionsPlugin.getInstance().conf().factions().prefixes().getNormal();
        }
        if (this == RECRUIT) {
            return FactionsPlugin.getInstance().conf().factions().prefixes().getRecruit();
        }
        return "";
    }

    @Override
    public ChatColor getColor() {
        return Relation.MEMBER.getColor();
    }

    @Override
    public TextColor getTextColor() {
        return Relation.MEMBER.getTextColor();
    }

    public Set<String> getRoleNamesAtOrAbove() {
        if (this.roleNamesAtOrAbove == null) {
            HashSet<String> hashSet = new HashSet<String>();
            for (Role role : Role.values()) {
                if (!this.isAtMost(role)) continue;
                hashSet.add(role.name().toLowerCase());
            }
            this.roleNamesAtOrAbove = Collections.unmodifiableSet(hashSet);
        }
        return this.roleNamesAtOrAbove;
    }

    public Set<String> getRoleNamesAtOrBelow() {
        if (this.roleNamesAtOrBelow == null) {
            HashSet<String> hashSet = new HashSet<String>();
            for (Role role : Role.values()) {
                if (!this.isAtLeast(role)) continue;
                hashSet.add(role.name().toLowerCase());
            }
            this.roleNamesAtOrBelow = Collections.unmodifiableSet(hashSet);
        }
        return this.roleNamesAtOrBelow;
    }
}

