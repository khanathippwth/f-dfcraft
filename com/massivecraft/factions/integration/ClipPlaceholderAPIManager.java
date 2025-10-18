/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.expansion.PlaceholderExpansion
 *  me.clip.placeholderapi.expansion.Relational
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.MemoryBoard;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.tag.FactionTag;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;
import java.util.List;
import java.util.UUID;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClipPlaceholderAPIManager
extends PlaceholderExpansion
implements Relational {
    private static final String mapChars = "0123456789abcdef";

    public String getIdentifier() {
        return "factionsuuid";
    }

    public String getAuthor() {
        return "drtshock";
    }

    public String getVersion() {
        return FactionsPlugin.getInstance().getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, Player player2, String string) {
        if (player == null || player2 == null || string == null) {
            return "";
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        FPlayer fPlayer2 = FPlayers.getInstance().getByPlayer(player2);
        if (fPlayer == null || fPlayer2 == null) {
            return "";
        }
        return switch (string) {
            case "relation" -> {
                String var8_8 = fPlayer.getRelationTo((RelationParticipator)fPlayer2).nicename;
                if (var8_8 != null) {
                    yield var8_8;
                }
                yield "";
            }
            case "relation_color" -> fPlayer.getColorStringTo(fPlayer2);
            default -> null;
        };
    }

    public String onPlaceholderRequest(Player player, String string) {
        if (player == null || string == null) {
            return "";
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = fPlayer.getFaction();
        boolean bl = false;
        if (string.contains("faction_territory")) {
            faction = Board.getInstance().getFactionAt(fPlayer.getLastStoodAt());
            string = string.replace("_territory", "");
            bl = true;
        }
        if (string.startsWith("player_map_")) {
            int n;
            List<Component> list = ((MemoryBoard)Board.getInstance()).getScoreboardMap(fPlayer);
            if (list.isEmpty()) {
                return "";
            }
            try {
                n = Integer.parseInt(string.substring("player_map_".length()));
            } catch (NumberFormatException numberFormatException) {
                return "";
            }
            if (n < 1 || n > list.size()) {
                return "";
            }
            return "\u00a7" + mapChars.substring(--n, n + 1) + LegacyComponentSerializer.legacySection().serialize(list.get(n));
        }
        return switch (string) {
            case "player_name" -> fPlayer.getName();
            case "player_lastseen" -> {
                String var8_11 = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fPlayer.getLastLoginTime(), true, true) + String.valueOf((Object)TL.COMMAND_STATUS_AGOSUFFIX);
                if (fPlayer.isOnline()) {
                    yield String.valueOf(ChatColor.GREEN) + TL.COMMAND_STATUS_ONLINE.toString();
                }
                if (System.currentTimeMillis() - fPlayer.getLastLoginTime() < 432000000L) {
                    yield String.valueOf(ChatColor.YELLOW) + var8_11;
                }
                yield String.valueOf(ChatColor.RED) + var8_11;
            }
            case "player_group" -> FactionsPlugin.getInstance().getPrimaryGroup(Bukkit.getOfflinePlayer((UUID)UUID.fromString(fPlayer.getId())));
            case "player_balance" -> {
                if (Econ.isSetup()) {
                    yield Econ.getFriendlyBalance(fPlayer);
                }
                yield TL.ECON_OFF.format("balance");
            }
            case "player_power" -> String.valueOf(fPlayer.getPowerRounded());
            case "player_maxpower" -> String.valueOf(fPlayer.getPowerMaxRounded());
            case "player_kills" -> String.valueOf(fPlayer.getKills());
            case "player_deaths" -> String.valueOf(fPlayer.getDeaths());
            case "player_role" -> {
                if (fPlayer.hasFaction()) {
                    yield fPlayer.getRole().getPrefix();
                }
                yield "";
            }
            case "player_role_name" -> {
                if (fPlayer.hasFaction()) {
                    yield fPlayer.getRole().getTranslation();
                }
                yield TL.PLACEHOLDER_ROLE_NAME.toString();
            }
            case "player_factionless" -> {
                if (fPlayer.hasFaction()) {
                    yield "";
                }
                yield TL.GENERIC_FACTIONLESS.toString();
            }
            case "faction_name" -> {
                if (fPlayer.hasFaction() || bl) {
                    yield faction.getTag();
                }
                yield TL.NOFACTION_PREFIX.toString();
            }
            case "faction_name_custom" -> {
                if (fPlayer.hasFaction() || bl) {
                    yield Tag.parsePlain(fPlayer, TL.PLACEHOLDER_CUSTOM_FACTION.toString());
                }
                yield "";
            }
            case "faction_only_space" -> {
                if (fPlayer.hasFaction() || bl) {
                    yield " ";
                }
                yield "";
            }
            case "faction_internal_id" -> "" + faction.getIntId();
            case "faction_power" -> String.valueOf(faction.getPowerRounded());
            case "faction_powermax" -> String.valueOf(faction.getPowerMaxRounded());
            case "faction_dtr" -> {
                if (fPlayer.hasFaction() || bl) {
                    yield DTRControl.round(faction.getDTR());
                }
                yield "";
            }
            case "faction_dtrmax" -> {
                if ((fPlayer.hasFaction() || bl) && FactionsPlugin.getInstance().getLandRaidControl() instanceof DTRControl) {
                    yield DTRControl.round(((DTRControl)FactionsPlugin.getInstance().getLandRaidControl()).getMaxDTR(faction));
                }
                yield "";
            }
            case "faction_dtr_frozen" -> {
                if ((fPlayer.hasFaction() || bl) && FactionsPlugin.getInstance().getLandRaidControl() instanceof DTRControl) {
                    yield FactionTag.DTR_FROZEN.replace(FactionTag.DTR_FROZEN.getTag(), faction);
                }
                yield "";
            }
            case "faction_dtr_frozen_time" -> {
                if ((fPlayer.hasFaction() || bl) && FactionsPlugin.getInstance().getLandRaidControl() instanceof DTRControl) {
                    yield FactionTag.DTR_FROZEN_TIME.replace(FactionTag.DTR_FROZEN_TIME.getTag(), faction);
                }
                yield "";
            }
            case "faction_maxclaims" -> {
                if (fPlayer.hasFaction() || bl) {
                    yield String.valueOf(FactionsPlugin.getInstance().getLandRaidControl().getLandLimit(faction));
                }
                yield "";
            }
            case "faction_description" -> faction.getDescription();
            case "faction_claims" -> String.valueOf(faction.getAllClaims().size());
            case "faction_founded" -> TL.sdf.format(faction.getFoundedDate());
            case "faction_joining" -> {
                if (faction.getOpen()) {
                    yield TL.COMMAND_SHOW_UNINVITED.toString();
                }
                yield TL.COMMAND_SHOW_INVITATION.toString();
            }
            case "faction_peaceful" -> {
                if (faction.isPeaceful()) {
                    yield String.valueOf(FactionsPlugin.getInstance().conf().colors().relations().getNeutral()) + TL.COMMAND_SHOW_PEACEFUL.toString();
                }
                yield "";
            }
            case "faction_powerboost" -> {
                double var8_12 = faction.getPowerBoost();
                if (var8_12 == 0.0) {
                    yield "";
                }
                yield (var8_12 > 0.0 ? TL.COMMAND_SHOW_BONUS.toString() : TL.COMMAND_SHOW_PENALTY.toString()) + var8_12 + ")";
            }
            case "faction_leader" -> {
                FPlayer var8_13 = faction.getFPlayerAdmin();
                if (var8_13 == null) {
                    yield "Server";
                }
                yield var8_13.getName().substring(0, var8_13.getName().length() > 14 ? 13 : var8_13.getName().length());
            }
            case "faction_warps" -> String.valueOf(faction.getWarps().size());
            case "faction_raidable" -> {
                boolean var8_14 = FactionsPlugin.getInstance().getLandRaidControl().isRaidable(faction);
                if (var8_14) {
                    yield TL.RAIDABLE_TRUE.toString();
                }
                yield TL.RAIDABLE_FALSE.toString();
            }
            case "faction_home_world" -> {
                if (faction.hasHome()) {
                    yield faction.getHome().getWorld().getName();
                }
                yield "";
            }
            case "faction_home_x" -> {
                if (faction.hasHome()) {
                    yield String.valueOf(faction.getHome().getBlockX());
                }
                yield "";
            }
            case "faction_home_y" -> {
                if (faction.hasHome()) {
                    yield String.valueOf(faction.getHome().getBlockY());
                }
                yield "";
            }
            case "faction_home_z" -> {
                if (faction.hasHome()) {
                    yield String.valueOf(faction.getHome().getBlockZ());
                }
                yield "";
            }
            case "faction_land_value" -> {
                if (Econ.shouldBeUsed()) {
                    yield Econ.moneyString(Econ.calculateTotalLandValue(faction.getLandRounded()));
                }
                yield TL.ECON_OFF.format("value");
            }
            case "faction_land_refund" -> {
                if (Econ.shouldBeUsed()) {
                    yield Econ.moneyString(Econ.calculateTotalLandRefund(faction.getLandRounded()));
                }
                yield TL.ECON_OFF.format("refund");
            }
            case "faction_bank_balance" -> {
                if (Econ.shouldBeUsed()) {
                    yield Econ.moneyString(Econ.getBalance(faction));
                }
                yield TL.ECON_OFF.format("balance");
            }
            case "faction_tnt_balance" -> FactionTag.TNT_BALANCE.replace(FactionTag.TNT_BALANCE.getTag(), faction);
            case "faction_tnt_max_balance" -> FactionTag.TNT_MAX.replace(FactionTag.TNT_MAX.getTag(), faction);
            case "faction_allies" -> String.valueOf(faction.getRelationCount(Relation.ALLY));
            case "faction_allies_players" -> String.valueOf(this.countOn(faction, Relation.ALLY, null, fPlayer));
            case "faction_allies_players_online" -> String.valueOf(this.countOn(faction, Relation.ALLY, true, fPlayer));
            case "faction_allies_players_offline" -> String.valueOf(this.countOn(faction, Relation.ALLY, false, fPlayer));
            case "faction_enemies" -> String.valueOf(faction.getRelationCount(Relation.ENEMY));
            case "faction_enemies_players" -> String.valueOf(this.countOn(faction, Relation.ENEMY, null, fPlayer));
            case "faction_enemies_players_online" -> String.valueOf(this.countOn(faction, Relation.ENEMY, true, fPlayer));
            case "faction_enemies_players_offline" -> String.valueOf(this.countOn(faction, Relation.ENEMY, false, fPlayer));
            case "faction_truces" -> String.valueOf(faction.getRelationCount(Relation.TRUCE));
            case "faction_truces_players" -> String.valueOf(this.countOn(faction, Relation.TRUCE, null, fPlayer));
            case "faction_truces_players_online" -> String.valueOf(this.countOn(faction, Relation.TRUCE, true, fPlayer));
            case "faction_truces_players_offline" -> String.valueOf(this.countOn(faction, Relation.TRUCE, false, fPlayer));
            case "faction_online" -> String.valueOf(faction.getOnlinePlayers().size());
            case "faction_offline" -> String.valueOf(faction.getFPlayers().size() - faction.getOnlinePlayers().size());
            case "faction_size" -> String.valueOf(faction.getFPlayers().size());
            case "faction_kills" -> String.valueOf(faction.getKills());
            case "faction_deaths" -> String.valueOf(faction.getDeaths());
            case "faction_maxvaults" -> String.valueOf(faction.getMaxVaults());
            case "faction_relation_color" -> fPlayer.getColorStringTo(faction);
            default -> null;
        };
    }

    private int countOn(Faction faction, Relation relation, Boolean bl, FPlayer fPlayer) {
        int n = 0;
        for (Faction faction2 : Factions.getInstance().getAllFactions()) {
            if (faction2.getRelationTo(faction) != relation) continue;
            if (bl == null) {
                n += faction2.getFPlayers().size();
                continue;
            }
            if (bl.booleanValue()) {
                n += faction2.getFPlayersWhereOnline(true, fPlayer).size();
                continue;
            }
            n += faction2.getFPlayersWhereOnline(false, fPlayer).size();
        }
        return n;
    }
}

