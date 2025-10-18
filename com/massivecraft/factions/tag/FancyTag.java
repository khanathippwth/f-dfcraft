/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.QuadFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.TextComponent;
import moss.factions.shade.net.kyori.adventure.text.event.HoverEvent;
import moss.factions.shade.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

public enum FancyTag implements Tag
{
    ALLIES_LIST("allies-list", (faction, fPlayer, string, map) -> FancyTag.processRelation(string, faction, fPlayer, Relation.ALLY)),
    ENEMIES_LIST("enemies-list", (faction, fPlayer, string, map) -> FancyTag.processRelation(string, faction, fPlayer, Relation.ENEMY)),
    TRUCES_LIST("truces-list", (faction, fPlayer, string, map) -> FancyTag.processRelation(string, faction, fPlayer, Relation.TRUCE)),
    ONLINE_LIST("online-list", (faction, fPlayer, string, map) -> {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        TextComponent.Builder builder = (TextComponent.Builder)Component.text().append((Component)LegacyComponentSerializer.legacySection().deserialize(FactionsPlugin.getInstance().txt().parse((String)string)));
        boolean bl = true;
        for (FPlayer fPlayer2 : MiscUtil.rankOrder(faction.getFPlayersWhereOnline(true, (FPlayer)fPlayer))) {
            if (fPlayer.getPlayer() != null && !fPlayer.getPlayer().canSee(fPlayer2.getPlayer())) continue;
            if (!bl) {
                builder.append((Component)Component.text(", "));
            }
            Component component = FancyTag.tip(FancyTag.tipPlayer(fPlayer2, map));
            builder.append((Component)((TextComponent)LegacyComponentSerializer.legacySection().deserialize(fPlayer2.getNameAndTitle()).color(fPlayer.getTextColorTo(fPlayer2))).hoverEvent(HoverEvent.showText(component)));
            bl = false;
            Object object = builder.build();
            if (((String)GsonComponentSerializer.gson().serialize(object)).length() <= 20000) continue;
            arrayList.add(object);
            builder = Component.text();
        }
        arrayList.add(builder.build());
        return bl && Tag.isMinimalShow() ? null : arrayList;
    }),
    OFFLINE_LIST("offline-list", (faction, fPlayer, string, map) -> {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        TextComponent.Builder builder = (TextComponent.Builder)Component.text().append((Component)LegacyComponentSerializer.legacySection().deserialize(FactionsPlugin.getInstance().txt().parse((String)string)));
        boolean bl = true;
        for (FPlayer fPlayer2 : MiscUtil.rankOrder(faction.getFPlayers())) {
            if (fPlayer2.isOnline() && (fPlayer.getPlayer() == null || !fPlayer2.isOnline() || fPlayer.getPlayer().canSee(fPlayer2.getPlayer()))) continue;
            if (!bl) {
                builder.append((Component)Component.text(", "));
            }
            Component component = FancyTag.tip(FancyTag.tipPlayer(fPlayer2, map));
            builder.append((Component)((TextComponent)LegacyComponentSerializer.legacySection().deserialize(fPlayer2.getNameAndTitle()).color(fPlayer.getTextColorTo(fPlayer2))).hoverEvent(HoverEvent.showText(component)));
            bl = false;
            Object object = builder.build();
            if (((String)GsonComponentSerializer.gson().serialize(object)).length() <= 20000) continue;
            arrayList.add(object);
            builder = Component.text();
        }
        arrayList.add(builder.build());
        return bl && Tag.isMinimalShow() ? null : arrayList;
    });

    private final String tag;
    private final QuadFunction<Faction, FPlayer, String, Map<UUID, String>, List<Component>> function;

    private static List<Component> processRelation(String string, Faction faction, FPlayer fPlayer, Relation relation) {
        ArrayList<Component> arrayList = new ArrayList<Component>();
        TextComponent.Builder builder = (TextComponent.Builder)Component.text().append((Component)LegacyComponentSerializer.legacySection().deserialize(FactionsPlugin.getInstance().txt().parse(string)));
        boolean bl = true;
        for (Faction faction2 : Factions.getInstance().getAllFactions()) {
            if (faction2 == faction || faction2.getRelationTo(faction) != relation) continue;
            if (!bl) {
                builder.append((Component)Component.text(", "));
            }
            Component component = FancyTag.tip(FancyTag.tipFaction(faction2, fPlayer));
            builder.append((Component)((TextComponent)LegacyComponentSerializer.legacySection().deserialize(faction2.getTag(fPlayer)).color(fPlayer.getTextColorTo(faction2))).hoverEvent(HoverEvent.showText(component)));
            bl = false;
            Object object = builder.build();
            if (((String)GsonComponentSerializer.gson().serialize(object)).length() <= 20000) continue;
            arrayList.add((Component)object);
            builder = Component.text();
        }
        arrayList.add((Component)builder.build());
        return bl && Tag.isMinimalShow() ? null : arrayList;
    }

    private static Component tip(List<String> list) {
        TextComponent.Builder builder = Component.text();
        boolean bl = false;
        for (String string : list) {
            if (bl) {
                builder.appendNewline();
            }
            bl = true;
            builder.append((Component)LegacyComponentSerializer.legacySection().deserialize(string));
        }
        return builder.build();
    }

    public static List<Component> parse(String string, Faction faction, FPlayer fPlayer, Map<UUID, String> map) {
        for (FancyTag fancyTag : FancyTag.values()) {
            if (!fancyTag.foundInString(string)) continue;
            return fancyTag.getMessage(string, faction, fPlayer, map);
        }
        return Collections.emptyList();
    }

    public static boolean anyMatch(String string) {
        return FancyTag.getMatch(string) != null;
    }

    public static FancyTag getMatch(String string) {
        for (FancyTag fancyTag : FancyTag.values()) {
            if (!fancyTag.foundInString(string)) continue;
            return fancyTag;
        }
        return null;
    }

    private static List<String> tipFaction(Faction faction, FPlayer fPlayer) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : FactionsPlugin.getInstance().conf().commands().toolTips().getFaction()) {
            String string2 = Tag.parsePlain(faction, fPlayer, string);
            if (string2 == null) continue;
            arrayList.add(ChatColor.translateAlternateColorCodes((char)'&', (String)string2));
        }
        return arrayList;
    }

    private static List<String> tipPlayer(FPlayer fPlayer, Map<UUID, String> map) {
        ArrayList<String> arrayList = new ArrayList<String>();
        Iterator<String> iterator = FactionsPlugin.getInstance().conf().commands().toolTips().getPlayer().iterator();
        while (iterator.hasNext()) {
            String string;
            String string2;
            String string3 = string2 = iterator.next();
            if (string2.contains("{group}")) {
                if (map == null || (string = map.getOrDefault(UUID.fromString(fPlayer.getId()), "")).trim().isEmpty()) continue;
                string3 = string3.replace("{group}", string);
            }
            if ((string = Tag.parsePlain(fPlayer, string3)) == null) continue;
            arrayList.add(ChatColor.translateAlternateColorCodes((char)'&', (String)string));
        }
        return arrayList;
    }

    private FancyTag(String string2, QuadFunction<Faction, FPlayer, String, Map<UUID, String>, List<Component>> quadFunction) {
        this.tag = "{" + string2 + "}";
        this.function = quadFunction;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean foundInString(String string) {
        return string != null && string.contains(this.tag);
    }

    public List<Component> getMessage(String string, Faction faction, FPlayer fPlayer, Map<UUID, String> map) {
        if (!this.foundInString(string)) {
            return Collections.emptyList();
        }
        return this.function.apply(faction, fPlayer, string.replace(this.getTag(), ""), map);
    }
}

