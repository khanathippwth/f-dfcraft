/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.material.MaterialDb;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MiscUtil {
    private static final Map<String, EntityType> entityTypeMap = new HashMap<String, EntityType>();
    public static final Function<String, EntityType> ENTITY_TYPE_FUNCTION;
    public static final Function<String, Material> MATERIAL_FUNCTION;
    private static final Map<String, CreatureSpawnEvent.SpawnReason> spawnReasonMap;
    public static final Function<String, CreatureSpawnEvent.SpawnReason> SPAWN_REASON_FUNCTION;
    private String nums = "12213504534";
    @Deprecated
    public static HashSet<String> substanceChars;

    public static <Type> Set<Type> typeSetFromStringSet(Set<String> set, Function<String, Type> function) {
        HashSet<Type> hashSet = new HashSet<Type>();
        for (String string : set) {
            Type Type2;
            if (string == null || (Type2 = function.apply(string)) == null) continue;
            hashSet.add(Type2);
        }
        return Collections.unmodifiableSet(hashSet);
    }

    public static long[] range(long l, long l2) {
        long l3;
        long[] lArray = new long[(int)Math.abs(l2 - l) + 1];
        if (l2 < l) {
            l3 = l;
            l = l2;
            l2 = l3;
        }
        for (l3 = l; l3 <= l2; ++l3) {
            lArray[(int)(l3 - l)] = l3;
        }
        return lArray;
    }

    public static String getComparisonString(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        string = ChatColor.stripColor((String)string);
        string = string.toLowerCase();
        MainConfig.Factions.Other other = FactionsPlugin.getInstance().conf().factions().other();
        for (char c : string.toCharArray()) {
            if (!other.isValidTagCharacter(c)) continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString().toLowerCase();
    }

    /*
     * WARNING - void declaration
     */
    public static ArrayList<String> validateTag(String string) {
        void var3_5;
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String object2 : FactionsPlugin.getInstance().conf().factions().other().getNameBlacklist()) {
            if (!string.toLowerCase().contains(object2.toLowerCase())) continue;
            arrayList.add(FactionsPlugin.getInstance().txt().parse(TL.GENERIC_FACTIONTAG_BLACKLIST.toString()));
            break;
        }
        if (MiscUtil.getComparisonString(string).length() < FactionsPlugin.getInstance().conf().factions().other().getTagLengthMin()) {
            arrayList.add(FactionsPlugin.getInstance().txt().parse(TL.GENERIC_FACTIONTAG_TOOSHORT.toString(), FactionsPlugin.getInstance().conf().factions().other().getTagLengthMin()));
        }
        if (string.length() > FactionsPlugin.getInstance().conf().factions().other().getTagLengthMax()) {
            arrayList.add(FactionsPlugin.getInstance().txt().parse(TL.GENERIC_FACTIONTAG_TOOLONG.toString(), FactionsPlugin.getInstance().conf().factions().other().getTagLengthMax()));
        }
        MainConfig.Factions.Other other = FactionsPlugin.getInstance().conf().factions().other();
        Object var3_4 = null;
        for (char c : string.toCharArray()) {
            if (other.isValidTagCharacter(c)) continue;
            if (var3_5 == null) {
                ArrayList arrayList2 = new ArrayList();
            }
            var3_5.add(Character.toString(c));
        }
        if (var3_5 != null) {
            arrayList.add(FactionsPlugin.getInstance().txt().parse(TL.GENERIC_FACTIONTAG_ALPHANUMERIC.toString(), String.join((CharSequence)"", (Iterable<? extends CharSequence>)var3_5)));
        }
        return arrayList;
    }

    public static Iterable<FPlayer> rankOrder(Iterable<FPlayer> iterable) {
        ArrayList<FPlayer> arrayList = new ArrayList<FPlayer>();
        ArrayList<FPlayer> arrayList2 = new ArrayList<FPlayer>();
        ArrayList<FPlayer> arrayList3 = new ArrayList<FPlayer>();
        ArrayList<FPlayer> arrayList4 = new ArrayList<FPlayer>();
        ArrayList<FPlayer> arrayList5 = new ArrayList<FPlayer>();
        for (FPlayer fPlayer : iterable) {
            if (fPlayer.getRole() == null) {
                fPlayer.setRole(Role.NORMAL);
                FactionsPlugin.getInstance().log(Level.WARNING, String.format("Player %s had null role. Setting them to normal. This isn't good D:", fPlayer.getName()));
            }
            switch (fPlayer.getRole()) {
                case ADMIN: {
                    arrayList.add(fPlayer);
                    break;
                }
                case COLEADER: {
                    arrayList2.add(fPlayer);
                    break;
                }
                case MODERATOR: {
                    arrayList3.add(fPlayer);
                    break;
                }
                case NORMAL: {
                    arrayList4.add(fPlayer);
                    break;
                }
                case RECRUIT: {
                    arrayList5.add(fPlayer);
                }
            }
        }
        ArrayList arrayList6 = new ArrayList();
        arrayList6.addAll(arrayList);
        arrayList6.addAll(arrayList2);
        arrayList6.addAll(arrayList3);
        arrayList6.addAll(arrayList4);
        arrayList6.addAll(arrayList5);
        return arrayList6;
    }

    static {
        for (EntityType entityType : EntityType.values()) {
            entityTypeMap.put(entityType.name(), entityType);
        }
        ENTITY_TYPE_FUNCTION = string -> string == null ? null : entityTypeMap.get(string.toUpperCase());
        MATERIAL_FUNCTION = string -> {
            Material material = null;
            if (string != null) {
                material = MaterialDb.get(string, null);
            }
            return material;
        };
        spawnReasonMap = new HashMap<String, CreatureSpawnEvent.SpawnReason>();
        for (EntityType entityType : CreatureSpawnEvent.SpawnReason.values()) {
            spawnReasonMap.put(entityType.name(), (CreatureSpawnEvent.SpawnReason)entityType);
        }
        SPAWN_REASON_FUNCTION = string -> string == null ? null : spawnReasonMap.get(string.toUpperCase());
        substanceChars = new HashSet<String>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    }
}

