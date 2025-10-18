/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.landraidcontrol.PowerControl;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.lang3.time.DurationFormatUtils;

public enum FactionTag implements Tag
{
    INTERNAL_ID("faction-internal-id", faction -> String.valueOf(faction.getIntId())),
    HOME_X("x", faction -> faction.hasHome() ? String.valueOf(faction.getHome().getBlockX()) : (Tag.isMinimalShow() ? null : "{ig}")),
    HOME_Y("y", faction -> faction.hasHome() ? String.valueOf(faction.getHome().getBlockY()) : (Tag.isMinimalShow() ? null : "{ig}")),
    HOME_Z("z", faction -> faction.hasHome() ? String.valueOf(faction.getHome().getBlockZ()) : (Tag.isMinimalShow() ? null : "{ig}")),
    CHUNKS("chunks", faction -> String.valueOf(faction.getLandRounded())),
    WARPS("warps", faction -> String.valueOf(faction.getWarps().size())),
    HEADER("header", (faction, fPlayer) -> FactionsPlugin.getInstance().txt().titleize(faction.getTag((FPlayer)fPlayer))),
    POWER("power", faction -> String.valueOf(faction.getPowerRounded())),
    MAX_POWER("maxPower", faction -> String.valueOf(faction.getPowerMaxRounded())),
    POWER_BOOST("power-boost", faction -> {
        double d = faction.getPowerBoost();
        return d == 0.0 ? "" : (d > 0.0 ? TL.COMMAND_SHOW_BONUS.toString() : TL.COMMAND_SHOW_PENALTY.toString() + d + ")");
    }),
    LEADER("leader", faction -> {
        FPlayer fPlayer = faction.getFPlayerAdmin();
        return fPlayer == null ? TL.TAG_LEADER_OWNERLESS.toString() : fPlayer.getName().substring(0, fPlayer.getName().length() > 14 ? 13 : fPlayer.getName().length());
    }),
    JOINING("joining", faction -> faction.getOpen() ? TL.COMMAND_SHOW_UNINVITED.toString() : TL.COMMAND_SHOW_INVITATION.toString()),
    FACTION("faction", faction -> faction.getTag()),
    FACTION_RELATION_COLOR("faction-relation-color", (faction, fPlayer) -> fPlayer == null ? "" : fPlayer.getColorStringTo((RelationParticipator)faction)),
    HOME_WORLD("world", faction -> faction.hasHome() ? faction.getHome().getWorld().getName() : (Tag.isMinimalShow() ? null : "{ig}")),
    RAIDABLE("raidable", faction -> {
        boolean bl = FactionsPlugin.getInstance().getLandRaidControl().isRaidable((Faction)faction);
        return bl ? TL.RAIDABLE_TRUE.toString() : TL.RAIDABLE_FALSE.toString();
    }),
    DTR("dtr", faction -> {
        if (FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl) {
            int n = faction.getLandRounded() >= faction.getPowerRounded() ? 0 : (int)Math.ceil((double)(faction.getPowerRounded() - faction.getLandRounded()) / FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getLossPerDeath());
            return TL.COMMAND_SHOW_DEATHS_TIL_RAIDABLE.format(n);
        }
        return DTRControl.round(faction.getDTR());
    }),
    MAX_DTR("max-dtr", faction -> {
        if (FactionsPlugin.getInstance().getLandRaidControl() instanceof DTRControl) {
            return DTRControl.round(((DTRControl)FactionsPlugin.getInstance().getLandRaidControl()).getMaxDTR((Faction)faction));
        }
        return Tag.isMinimalShow() ? null : "{ig}";
    }),
    DTR_FROZEN("dtr-frozen-status", faction -> TL.DTR_FROZEN_STATUS_MESSAGE.format(faction.isFrozenDTR() ? TL.DTR_FROZEN_STATUS_TRUE.toString() : TL.DTR_FROZEN_STATUS_FALSE.toString())),
    DTR_FROZEN_TIME("dtr-frozen-time", faction -> TL.DTR_FROZEN_TIME_MESSAGE.format(faction.isFrozenDTR() ? DurationFormatUtils.formatDuration(faction.getFrozenDTRUntilTime() - System.currentTimeMillis(), FactionsPlugin.getInstance().conf().factions().landRaidControl().dtr().getFreezeTimeFormat()) : TL.DTR_FROZEN_TIME_NOTFROZEN.toString())),
    MAX_CHUNKS("max-chunks", faction -> String.valueOf(FactionsPlugin.getInstance().getLandRaidControl().getLandLimit((Faction)faction))),
    PEACEFUL("peaceful", faction -> faction.isPeaceful() ? String.valueOf(FactionsPlugin.getInstance().conf().colors().relations().getPeaceful()) + TL.COMMAND_SHOW_PEACEFUL.toString() : ""),
    PERMANENT("permanent", faction -> faction.isPermanent() ? "permanent" : "{notPermanent}"),
    LAND_VALUE("land-value", faction -> Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandValue(faction.getLandRounded())) : (Tag.isMinimalShow() ? null : TL.ECON_OFF.format("value"))),
    DESCRIPTION("description", Faction::getDescription),
    CREATE_DATE("create-date", faction -> TL.sdf.format(faction.getFoundedDate())),
    LAND_REFUND("land-refund", faction -> Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandRefund(faction.getLandRounded())) : (Tag.isMinimalShow() ? null : TL.ECON_OFF.format("refund"))),
    BANK_BALANCE("faction-balance", faction -> {
        if (Econ.shouldBeUsed()) {
            return FactionsPlugin.getInstance().conf().economy().isBankEnabled() ? Econ.moneyString(Econ.getBalance(faction)) : (Tag.isMinimalShow() ? null : TL.ECON_OFF.format("balance"));
        }
        return Tag.isMinimalShow() ? null : TL.ECON_OFF.format("balance");
    }),
    TNT_BALANCE("tnt-balance", faction -> {
        if (FactionsPlugin.getInstance().conf().commands().tnt().isEnable()) {
            return String.valueOf(faction.getTNTBank());
        }
        return Tag.isMinimalShow() ? null : "";
    }),
    TNT_MAX("tnt-max-balance", faction -> {
        if (FactionsPlugin.getInstance().conf().commands().tnt().isEnable()) {
            return String.valueOf(FactionsPlugin.getInstance().conf().commands().tnt().getMaxStorage());
        }
        return Tag.isMinimalShow() ? null : "";
    }),
    ALLIES_COUNT("allies", faction -> String.valueOf(faction.getRelationCount(Relation.ALLY))),
    ENEMIES_COUNT("enemies", faction -> String.valueOf(faction.getRelationCount(Relation.ENEMY))),
    TRUCES_COUNT("truces", faction -> String.valueOf(faction.getRelationCount(Relation.TRUCE))),
    ONLINE_COUNT("online", (faction, fPlayer) -> {
        if (fPlayer != null && fPlayer.isOnline()) {
            return String.valueOf(faction.getFPlayersWhereOnline(true, (FPlayer)fPlayer).size());
        }
        return String.valueOf(faction.getFPlayersWhereOnline(true).size());
    }),
    OFFLINE_COUNT("offline", (faction, fPlayer) -> {
        if (fPlayer != null && fPlayer.isOnline()) {
            return String.valueOf(faction.getFPlayers().size() - faction.getFPlayersWhereOnline(true, (FPlayer)fPlayer).size());
        }
        return String.valueOf(faction.getFPlayersWhereOnline(false).size());
    }),
    FACTION_SIZE("members", faction -> String.valueOf(faction.getFPlayers().size())),
    FACTION_KILLS("faction-kills", faction -> String.valueOf(faction.getKills())),
    FACTION_DEATHS("faction-deaths", faction -> String.valueOf(faction.getDeaths())),
    FACTION_BANCOUNT("faction-bancount", faction -> String.valueOf(faction.getBannedPlayers().size())),
    FACTION_LINK("faction-link", faction -> faction.getLink());

    private final String tag;
    private final BiFunction<Faction, FPlayer, String> biFunction;
    private final Function<Faction, String> function;

    public static String parse(String string, Faction faction, FPlayer fPlayer) {
        for (FactionTag factionTag : FactionTag.values()) {
            string = factionTag.replace(string, faction, fPlayer);
        }
        return string;
    }

    public static String parse(String string, Faction faction) {
        for (FactionTag factionTag : FactionTag.values()) {
            string = factionTag.replace(string, faction);
        }
        return string;
    }

    private FactionTag(String string2, BiFunction<Faction, FPlayer, String> biFunction) {
        this(string2, null, biFunction);
    }

    private FactionTag(String string2, Function<Faction, String> function) {
        this(string2, function, null);
    }

    private FactionTag(String string2, Function<Faction, String> function, BiFunction<Faction, FPlayer, String> biFunction) {
        this.tag = string2.equalsIgnoreCase("permanent") ? string2 : "{" + string2 + "}";
        this.biFunction = biFunction;
        this.function = function;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean foundInString(String string) {
        return string != null && string.contains(this.tag);
    }

    public String replace(String string, Faction faction, FPlayer fPlayer) {
        if (!this.foundInString(string)) {
            return string;
        }
        String string2 = this.function == null ? this.biFunction.apply(faction, fPlayer) : this.function.apply(faction);
        return string2 == null ? null : string.replace(this.tag, string2);
    }

    public String replace(String string, Faction faction) {
        return this.replace(string, faction, null);
    }
}

