/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.integration.permcontext;

import com.google.common.collect.ImmutableSet;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.integration.permcontext.Context;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public enum Contexts implements Context
{
    FACTION_ID(player -> {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player)player);
        return ImmutableSet.of(fPlayer.hasFaction() ? String.valueOf(fPlayer.getFactionIntId()) : "0");
    }, ImmutableSet.of("0")),
    IS_PEACEFUL(player -> {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player)player);
        return ImmutableSet.of(fPlayer.hasFaction() && fPlayer.getFaction().isPeaceful() ? "true" : "false");
    }, ImmutableSet.of("true", "false")),
    IS_PERMANENT(player -> {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player)player);
        return ImmutableSet.of(fPlayer.hasFaction() && fPlayer.getFaction().isPermanent() ? "true" : "false");
    }, ImmutableSet.of("true", "false")),
    TERRITORY_RELATION(player -> FPlayers.getInstance().getByPlayer((Player)player).getRelationTo(Board.getInstance().getFactionAt(new FLocation(player.getLocation()))).getNameInASet(), Arrays.stream(Relation.values()).map(relation -> relation.name().toLowerCase()).collect(Collectors.toSet())),
    ROLE_AT_LEAST(player -> {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player)player);
        return fPlayer.hasFaction() ? fPlayer.getRole().getRoleNamesAtOrBelow() : Collections.emptySet();
    }, Arrays.stream(Role.values()).map(role -> role.name().toLowerCase()).collect(Collectors.toSet())),
    ROLE_AT_MOST(player -> {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player)player);
        return fPlayer.hasFaction() ? fPlayer.getRole().getRoleNamesAtOrAbove() : Collections.emptySet();
    }, Arrays.stream(Role.values()).map(role -> role.name().toLowerCase()).collect(Collectors.toSet()));

    public static final String FACTIONSUUID_NAMESPACE = "factionsuuid";
    private final Function<Player, Set<String>> function;
    private final String name;
    private final Set<String> possibilities;

    private Contexts(Function<Player, Set<String>> function, Set<String> set) {
        this.function = function;
        this.name = this.name().toLowerCase().replace('_', '-');
        this.possibilities = Collections.unmodifiableSet(set);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getNamespace() {
        return FACTIONSUUID_NAMESPACE;
    }

    @Override
    public Set<String> getPossibleValues() {
        return this.possibilities;
    }

    @Override
    public Set<String> getValues(Player player) {
        return this.function.apply(player);
    }
}

