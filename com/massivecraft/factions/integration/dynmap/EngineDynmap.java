/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.longs.LongListIterator
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.dynmap.DynmapAPI
 *  org.dynmap.markers.AreaMarker
 *  org.dynmap.markers.Marker
 *  org.dynmap.markers.MarkerAPI
 *  org.dynmap.markers.MarkerSet
 *  org.dynmap.markers.PlayerSet
 *  org.dynmap.utils.TileFlags
 */
package com.massivecraft.factions.integration.dynmap;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.DynmapConfig;
import com.massivecraft.factions.data.MemoryBoard;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.dynmap.DynmapStyle;
import com.massivecraft.factions.integration.dynmap.TempAreaMarker;
import com.massivecraft.factions.integration.dynmap.TempMarker;
import com.massivecraft.factions.integration.dynmap.TempMarkerSet;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.tag.FactionTag;
import com.massivecraft.factions.tag.GeneralTag;
import com.massivecraft.factions.util.LazyLocation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PlayerSet;
import org.dynmap.utils.TileFlags;

public class EngineDynmap {
    public static final int BLOCKS_PER_CHUNK = 16;
    public static final String DYNMAP_INTEGRATION = "\u00a7dDynmap Integration: \u00a7e";
    public static final String FACTIONS = "factions";
    public static final String FACTIONS_ = "factions_";
    public static final String FACTIONS_MARKERSET = "factions_markerset";
    public static final String FACTIONS_HOME = "factions_home";
    public static final String FACTIONS_HOME_ = "factions_home_";
    public static final String FACTIONS_WARP = "factions_warp";
    public static final String FACTIONS_WARP_ = "factions_home_";
    public static final String FACTIONS_PLAYERSET = "factions_playerset";
    public static final String FACTIONS_PLAYERSET_ = "factions_playerset_";
    private static final EngineDynmap instance = new EngineDynmap();
    private DynmapConfig dynmapConf;
    public DynmapAPI dynmapApi;
    public MarkerAPI markerApi;
    public MarkerSet markerset;
    private boolean enabled;
    private boolean stillNeedsToRunOnce = true;

    public static EngineDynmap getInstance() {
        return instance;
    }

    public boolean isRunning() {
        return this.enabled;
    }

    public String getVersion() {
        return this.dynmapApi == null ? null : this.dynmapApi.getDynmapVersion();
    }

    public boolean init(Plugin plugin) {
        this.dynmapApi = (DynmapAPI)plugin;
        this.dynmapConf = FactionsPlugin.getInstance().getConfigManager().getDynmapConfig();
        if (!this.dynmapConf.dynmap().isEnabled()) {
            if (this.markerset != null) {
                this.markerset.deleteMarkerSet();
                this.markerset = null;
            }
            return false;
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)FactionsPlugin.getInstance(), () -> {
            if (!this.updateCore()) {
                return;
            }
            Map<String, Set<String>> map = this.createPlayersets();
            this.updatePlayersets(map);
        }, 101L, 100L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)FactionsPlugin.getInstance(), () -> {
            boolean bl = true;
            if (FactionsPlugin.getInstance().getConfigManager().getDynmapConfig().dynmap().isOnlyUpdateWorldOnce()) {
                if (this.stillNeedsToRunOnce) {
                    this.stillNeedsToRunOnce = false;
                } else {
                    bl = false;
                }
            }
            if (!this.updateCore()) {
                return;
            }
            if (!this.updateLayer(this.createLayer())) {
                return;
            }
            if (bl) {
                final Map<String, Int2ObjectMap<LongList>> map = this.createWorldFactionChunks();
                final HashMap hashMap = new HashMap();
                final HashMap hashMap2 = new HashMap();
                final HashMap hashMap3 = new HashMap();
                HashSet hashSet = new HashSet();
                map.values().stream().flatMapToInt(int2ObjectMap -> int2ObjectMap.keySet().intStream()).distinct().forEach(n -> {
                    Faction faction = Factions.getInstance().getFactionById(n);
                    if (faction == null) {
                        FactionsPlugin.getInstance().getLogger().warning("Found invalid faction ID " + n);
                        hashSet.add(n);
                        return;
                    }
                    hashMap.put(n, faction.getTag());
                    hashMap2.put(n, this.getDescription(faction));
                    hashMap3.put(n, this.getStyle(faction));
                });
                if (!hashSet.isEmpty()) {
                    map.values().forEach(int2ObjectMap -> {
                        Iterator iterator = hashSet.iterator();
                        while (iterator.hasNext()) {
                            int n = (Integer)iterator.next();
                            int2ObjectMap.remove(n);
                        }
                    });
                }
                new BukkitRunnable(){

                    public void run() {
                        final Map<String, TempAreaMarker> map2 = EngineDynmap.this.createAreas(map, hashMap, hashMap2, hashMap3);
                        new BukkitRunnable(){

                            public void run() {
                                EngineDynmap.this.updateAreas(map2);
                            }
                        }.runTask((Plugin)FactionsPlugin.getInstance());
                    }
                }.runTaskAsynchronously((Plugin)FactionsPlugin.getInstance());
            }
            this.updateHomesAndWarps(this.createHomes(), this.createWarps());
        }, 100L, (long)Math.max(1, this.dynmapConf.dynmap().getClaimUpdatePeriod()) * 20L);
        this.enabled = true;
        FactionsPlugin.getInstance().getLogger().info("Enabled Dynmap integration");
        return true;
    }

    public boolean updateCore() {
        this.dynmapApi = (DynmapAPI)Bukkit.getPluginManager().getPlugin("dynmap");
        if (this.dynmapApi == null) {
            EngineDynmap.severe("Could not retrieve the DynmapAPI.");
            return false;
        }
        this.markerApi = this.dynmapApi.getMarkerAPI();
        if (this.markerApi == null) {
            EngineDynmap.severe("Could not retrieve the MarkerAPI.");
            return false;
        }
        return true;
    }

    public TempMarkerSet createLayer() {
        TempMarkerSet tempMarkerSet = new TempMarkerSet();
        tempMarkerSet.label = this.dynmapConf.dynmap().getLayerName();
        tempMarkerSet.minimumZoom = this.dynmapConf.dynmap().getLayerMinimumZoom();
        tempMarkerSet.priority = this.dynmapConf.dynmap().getLayerPriority();
        tempMarkerSet.hideByDefault = !this.dynmapConf.dynmap().isLayerVisible();
        return tempMarkerSet;
    }

    public boolean updateLayer(TempMarkerSet tempMarkerSet) {
        this.markerset = this.markerApi.getMarkerSet(FACTIONS_MARKERSET);
        if (this.markerset == null) {
            this.markerset = tempMarkerSet.create(this.markerApi, FACTIONS_MARKERSET);
            if (this.markerset == null) {
                EngineDynmap.severe("Could not create the Faction Markerset/Layer");
                return false;
            }
        } else {
            tempMarkerSet.update(this.markerset);
        }
        return true;
    }

    public Map<String, TempMarker> createHomes() {
        HashMap<String, TempMarker> hashMap = new HashMap<String, TempMarker>();
        if (!FactionsPlugin.getInstance().getConfigManager().getDynmapConfig().dynmap().isShowMarkers()) {
            return hashMap;
        }
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            Location location = faction.getHome();
            if (location == null) continue;
            DynmapStyle dynmapStyle = this.getStyle(faction);
            String string = "factions_home_" + faction.getIntId();
            TempMarker tempMarker = new TempMarker();
            tempMarker.label = ChatColor.stripColor((String)faction.getTag());
            tempMarker.world = location.getWorld().getName();
            tempMarker.x = location.getX();
            tempMarker.y = location.getY();
            tempMarker.z = location.getZ();
            tempMarker.iconName = dynmapStyle.getHomeMarker();
            tempMarker.description = this.getDescription(faction);
            hashMap.put(string, tempMarker);
        }
        return hashMap;
    }

    public void updateHomesAndWarps(Map<String, TempMarker> map, Map<String, TempMarker> map2) {
        HashMap<String, Marker> hashMap = new HashMap<String, Marker>();
        for (Marker marker : this.markerset.getMarkers()) {
            hashMap.put(marker.getMarkerID(), marker);
        }
        Stream.of(map, map2).map(Map::entrySet).flatMap(Collection::stream).forEach(entry -> {
            String string = (String)entry.getKey();
            TempMarker tempMarker = (TempMarker)entry.getValue();
            Marker marker = (Marker)hashMap.remove(string);
            if (marker == null) {
                marker = tempMarker.create(this.markerApi, this.markerset, string);
                if (marker == null) {
                    EngineDynmap.severe("Could not get/create the home marker " + string);
                }
            } else {
                tempMarker.update(this.markerApi, marker);
            }
        });
        for (Marker marker : hashMap.values()) {
            marker.deleteMarker();
        }
    }

    public Map<String, TempMarker> createWarps() {
        HashMap<String, TempMarker> hashMap = new HashMap<String, TempMarker>();
        if (!FactionsPlugin.getInstance().getConfigManager().getDynmapConfig().dynmap().isShowWarpMarkers()) {
            return hashMap;
        }
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            Map<String, LazyLocation> map = faction.getWarps();
            for (String string : map.keySet()) {
                LazyLocation lazyLocation = map.get(string);
                DynmapStyle dynmapStyle = this.getStyle(faction);
                String string2 = "factions_home_" + faction.getIntId() + "_" + string;
                TempMarker tempMarker = new TempMarker();
                tempMarker.label = ChatColor.stripColor((String)faction.getTag());
                tempMarker.world = lazyLocation.getWorldName();
                tempMarker.x = lazyLocation.getX();
                tempMarker.y = lazyLocation.getY();
                tempMarker.z = lazyLocation.getZ();
                tempMarker.iconName = dynmapStyle.getWarpMarker();
                tempMarker.description = this.dynmapConf.dynmap().getWarpDescription().replace("%warpname%", string);
                hashMap.put(string2, tempMarker);
            }
        }
        return hashMap;
    }

    public Map<String, Int2ObjectMap<LongList>> createWorldFactionChunks() {
        HashMap<String, Int2ObjectMap<LongList>> hashMap = new HashMap<String, Int2ObjectMap<LongList>>();
        MemoryBoard memoryBoard = (MemoryBoard)Board.getInstance();
        for (World world : Bukkit.getWorlds()) {
            hashMap.put(world.getName(), memoryBoard.getAllClaimsForDynmap(world));
        }
        return hashMap;
    }

    public Map<String, TempAreaMarker> createAreas(Map<String, Int2ObjectMap<LongList>> map, Map<Integer, String> map2, Map<Integer, String> map3, Map<Integer, DynmapStyle> map4) {
        HashMap<String, TempAreaMarker> hashMap = new HashMap<String, TempAreaMarker>();
        for (Map.Entry<String, Int2ObjectMap<LongList>> entry2 : map.entrySet()) {
            String string = entry2.getKey();
            Int2ObjectMap<LongList> int2ObjectMap = entry2.getValue();
            int2ObjectMap.int2ObjectEntrySet().forEach(entry -> {
                int n = entry.getIntKey();
                LongList longList = (LongList)entry.getValue();
                Map<String, TempAreaMarker> map5 = this.createAreas(string, n, longList, map2, map3, map4);
                hashMap.putAll(map5);
            });
        }
        return hashMap;
    }

    public Map<String, TempAreaMarker> createAreas(String string, int n, LongList longList, Map<Integer, String> map, Map<Integer, String> map2, Map<Integer, DynmapStyle> map3) {
        HashMap<String, TempAreaMarker> hashMap = new HashMap<String, TempAreaMarker>();
        if (!this.isVisible(n, map.get(n), string)) {
            return hashMap;
        }
        if (longList.isEmpty()) {
            return hashMap;
        }
        int n2 = 0;
        String string2 = map2.get(n);
        DynmapStyle dynmapStyle = map3.get(n);
        TileFlags tileFlags = new TileFlags();
        LongArrayList longArrayList = new LongArrayList(longList.size());
        LongListIterator longListIterator = longList.iterator();
        while (longListIterator.hasNext()) {
            long l = (Long)longListIterator.next();
            tileFlags.setFlag(MemoryBoard.Morton.getX(l), MemoryBoard.Morton.getZ(l), true);
            longArrayList.add(l);
        }
        while (longArrayList != null) {
            Object object;
            int n3;
            longListIterator = null;
            LongArrayList longArrayList2 = null;
            int n4 = Integer.MAX_VALUE;
            int n5 = Integer.MAX_VALUE;
            LongListIterator longListIterator2 = longArrayList.iterator();
            while (longListIterator2.hasNext()) {
                long l = (Long)longListIterator2.next();
                n3 = MemoryBoard.Morton.getX(l);
                int n6 = MemoryBoard.Morton.getZ(l);
                if (longListIterator == null && tileFlags.getFlag(n3, n6)) {
                    longListIterator = new TileFlags();
                    this.floodFillTarget(tileFlags, (TileFlags)longListIterator, n3, n6);
                    n4 = n3;
                    n5 = n6;
                    continue;
                }
                if (longListIterator != null && longListIterator.getFlag(n3, n6)) {
                    if (n3 < n4) {
                        n4 = n3;
                        n5 = n6;
                        continue;
                    }
                    if (n3 != n4 || n6 >= n5) continue;
                    n5 = n6;
                    continue;
                }
                if (longArrayList2 == null) {
                    longArrayList2 = new LongArrayList();
                }
                longArrayList2.add(l);
            }
            longArrayList = longArrayList2;
            if (longListIterator == null) continue;
            int n7 = n4;
            int n8 = n5;
            int n9 = n4;
            n3 = n5;
            Direction direction = Direction.XPLUS;
            ArrayList<int[]> arrayList = new ArrayList<int[]>();
            arrayList.add(new int[]{n7, n8});
            while (n9 != n7 || n3 != n8 || direction != Direction.ZMINUS) {
                switch (direction.ordinal()) {
                    case 0: {
                        if (!longListIterator.getFlag(n9 + 1, n3)) {
                            arrayList.add(new int[]{n9 + 1, n3});
                            direction = Direction.ZPLUS;
                            break;
                        }
                        if (!longListIterator.getFlag(n9 + 1, n3 - 1)) {
                            ++n9;
                            break;
                        }
                        arrayList.add(new int[]{n9 + 1, n3--});
                        direction = Direction.ZMINUS;
                        ++n9;
                        break;
                    }
                    case 1: {
                        if (!longListIterator.getFlag(n9, n3 + 1)) {
                            arrayList.add(new int[]{n9 + 1, n3 + 1});
                            direction = Direction.XMINUS;
                            break;
                        }
                        if (!longListIterator.getFlag(n9 + 1, n3 + 1)) {
                            ++n3;
                            break;
                        }
                        arrayList.add(new int[]{n9 + 1, n3 + 1});
                        direction = Direction.XPLUS;
                        ++n9;
                        ++n3;
                        break;
                    }
                    case 2: {
                        if (!longListIterator.getFlag(n9 - 1, n3)) {
                            arrayList.add(new int[]{n9, n3 + 1});
                            direction = Direction.ZMINUS;
                            break;
                        }
                        if (!longListIterator.getFlag(n9 - 1, n3 + 1)) {
                            --n9;
                            break;
                        }
                        arrayList.add(new int[]{n9--, n3 + 1});
                        direction = Direction.ZPLUS;
                        ++n3;
                        break;
                    }
                    case 3: {
                        if (!longListIterator.getFlag(n9, n3 - 1)) {
                            arrayList.add(new int[]{n9, n3});
                            direction = Direction.XPLUS;
                            break;
                        }
                        if (!longListIterator.getFlag(n9 - 1, n3 - 1)) {
                            --n3;
                            break;
                        }
                        arrayList.add(new int[]{n9--, n3--});
                        direction = Direction.XMINUS;
                    }
                }
            }
            int n10 = arrayList.size();
            double[] dArray = new double[n10];
            double[] dArray2 = new double[n10];
            for (int i = 0; i < n10; ++i) {
                object = (int[])arrayList.get(i);
                dArray[i] = (double)object[0] * 16.0;
                dArray2[i] = (double)object[1] * 16.0;
            }
            String string3 = FACTIONS_ + string + "__" + n + "__" + n2;
            object = new TempAreaMarker();
            object.label = map.get(n);
            object.world = string;
            object.x = dArray;
            object.z = dArray2;
            object.description = string2;
            object.lineColor = dynmapStyle.getLineColor();
            object.lineOpacity = dynmapStyle.getLineOpacity();
            object.lineWeight = dynmapStyle.getLineWeight();
            object.fillColor = dynmapStyle.getFillColor();
            object.fillOpacity = dynmapStyle.getFillOpacity();
            object.boost = dynmapStyle.getBoost();
            hashMap.put(string3, (TempAreaMarker)object);
            ++n2;
        }
        return hashMap;
    }

    public void updateAreas(Map<String, TempAreaMarker> map) {
        HashMap<String, AreaMarker> hashMap = new HashMap<String, AreaMarker>();
        for (AreaMarker object : this.markerset.getAreaMarkers()) {
            hashMap.put(object.getMarkerID(), object);
        }
        for (Map.Entry entry : map.entrySet()) {
            String string = (String)entry.getKey();
            TempAreaMarker tempAreaMarker = (TempAreaMarker)entry.getValue();
            AreaMarker areaMarker = (AreaMarker)hashMap.remove(string);
            if (areaMarker == null) {
                areaMarker = tempAreaMarker.create(this.markerset, string);
                if (areaMarker != null) continue;
                EngineDynmap.severe("Could not get/create the area marker " + string);
                continue;
            }
            tempAreaMarker.update(areaMarker);
        }
        for (AreaMarker areaMarker : hashMap.values()) {
            areaMarker.deleteMarker();
        }
    }

    public String createPlayersetId(Faction faction) {
        if (faction == null) {
            return null;
        }
        if (faction.isWilderness()) {
            return null;
        }
        return FACTIONS_PLAYERSET_ + faction.getIntId();
    }

    public Set<String> createPlayerset(Faction faction) {
        if (faction == null) {
            return null;
        }
        if (faction.isWilderness()) {
            return null;
        }
        HashSet<String> hashSet = new HashSet<String>();
        for (FPlayer fPlayer : faction.getFPlayers()) {
            hashSet.add(fPlayer.getId());
            hashSet.add(fPlayer.getName());
        }
        return hashSet;
    }

    public Map<String, Set<String>> createPlayersets() {
        if (!this.dynmapConf.dynmap().isVisibilityByFaction()) {
            return null;
        }
        HashMap<String, Set<String>> hashMap = new HashMap<String, Set<String>>();
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            Set<String> set;
            String string = this.createPlayersetId(faction);
            if (string == null || (set = this.createPlayerset(faction)) == null) continue;
            hashMap.put(string, set);
        }
        return hashMap;
    }

    public void updatePlayersets(Map<String, Set<String>> map) {
        if (map == null) {
            return;
        }
        for (PlayerSet object : this.markerApi.getPlayerSets()) {
            if (!object.getSetID().startsWith(FACTIONS_PLAYERSET_) || map.containsKey(object.getSetID())) continue;
            object.deleteSet();
        }
        for (Map.Entry entry : map.entrySet()) {
            String string = (String)entry.getKey();
            Set set = (Set)entry.getValue();
            PlayerSet playerSet = this.markerApi.getPlayerSet(string);
            if (playerSet == null) {
                playerSet = this.markerApi.createPlayerSet(string, true, set, false);
            }
            if (playerSet == null) {
                EngineDynmap.severe("Could not get/create the player set " + string);
                continue;
            }
            playerSet.setPlayers(set);
        }
    }

    private String getDescription(Faction faction) {
        Object object = "<div class=\"regioninfo\">" + this.dynmapConf.dynmap().getDescription() + "</div>";
        String string = faction.getTag();
        string = ChatColor.stripColor((String)string);
        string = EngineDynmap.escapeHtml(string);
        object = ((String)object).replace("%name%", string);
        String string2 = faction.getDescription();
        string2 = ChatColor.stripColor((String)string2);
        string2 = EngineDynmap.escapeHtml(string2);
        object = ((String)object).replace("%description%", string2);
        String string3 = "unavailable";
        if (FactionsPlugin.getInstance().conf().economy().isBankEnabled() && this.dynmapConf.dynmap().isDescriptionMoney()) {
            string3 = String.format("%.2f", Econ.getBalance(faction));
        }
        object = ((String)object).replace("%money%", string3);
        Set<FPlayer> set = faction.getFPlayers();
        String string4 = String.valueOf(set.size());
        String string5 = EngineDynmap.getHtmlPlayerString(set);
        FPlayer fPlayer = faction.getFPlayerAdmin();
        String string6 = EngineDynmap.getHtmlPlayerName(fPlayer);
        List<FPlayer> list = faction.getFPlayersWhereRole(Role.ADMIN);
        String string7 = String.valueOf(list.size());
        String string8 = EngineDynmap.getHtmlPlayerString(list);
        List<FPlayer> list2 = faction.getFPlayersWhereRole(Role.COLEADER);
        String string9 = String.valueOf(list2.size());
        String string10 = EngineDynmap.getHtmlPlayerString(list2);
        List<FPlayer> list3 = faction.getFPlayersWhereRole(Role.MODERATOR);
        String string11 = String.valueOf(list3.size());
        String string12 = EngineDynmap.getHtmlPlayerString(list3);
        List<FPlayer> list4 = faction.getFPlayersWhereRole(Role.NORMAL);
        String string13 = String.valueOf(list4.size());
        String string14 = EngineDynmap.getHtmlPlayerString(list4);
        List<FPlayer> list5 = faction.getFPlayersWhereRole(Role.RECRUIT);
        String string15 = String.valueOf(list5.size());
        String string16 = EngineDynmap.getHtmlPlayerString(list5);
        object = ((String)object).replace("%players%", string5);
        object = ((String)object).replace("%players.count%", string4);
        object = ((String)object).replace("%players.leader%", string6);
        object = ((String)object).replace("%players.admins%", string8);
        object = ((String)object).replace("%players.admins.count%", string7);
        object = ((String)object).replace("%players.coleaders%", string10);
        object = ((String)object).replace("%players.coleaders.count%", string9);
        object = ((String)object).replace("%players.moderators%", string12);
        object = ((String)object).replace("%players.moderators.count%", string11);
        object = ((String)object).replace("%players.normals%", string14);
        object = ((String)object).replace("%players.normals.count%", string13);
        object = ((String)object).replace("%players.recruits%", string16);
        object = ((String)object).replace("%players.recruits.count%", string15);
        for (FactionTag enum_ : FactionTag.values()) {
            if (!((String)object).contains(enum_.getTag())) continue;
            object = ((String)object).replace(enum_.getTag(), EngineDynmap.escapeHtml(ChatColor.stripColor((String)enum_.replace(enum_.getTag(), faction))));
        }
        for (Enum enum_ : GeneralTag.values()) {
            if (!((String)object).contains(((GeneralTag)enum_).getTag())) continue;
            object = ((String)object).replace(((GeneralTag)enum_).getTag(), EngineDynmap.escapeHtml(ChatColor.stripColor((String)((GeneralTag)enum_).replace(((GeneralTag)enum_).getTag()))));
        }
        return object;
    }

    public static String getHtmlPlayerString(Collection<FPlayer> collection) {
        StringBuilder stringBuilder = new StringBuilder();
        for (FPlayer fPlayer : collection) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(EngineDynmap.getHtmlPlayerName(fPlayer));
        }
        return stringBuilder.toString();
    }

    public static String getHtmlPlayerName(FPlayer fPlayer) {
        if (fPlayer == null) {
            return "none";
        }
        return EngineDynmap.escapeHtml(fPlayer.getName());
    }

    public static String escapeHtml(String string) {
        if (string == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(Math.max(16, string.length()));
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c > '\u007f' || c == '\"' || c == '<' || c == '>' || c == '&') {
                stringBuilder.append("&#");
                stringBuilder.append((int)c);
                stringBuilder.append(';');
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private boolean isVisible(int n, String string, String string2) {
        Set<String> set = this.dynmapConf.dynmap().getVisibleFactions();
        Set<String> set2 = this.dynmapConf.dynmap().getHiddenFactions();
        if (!(set.isEmpty() || set.contains(String.valueOf(n)) || set.contains(string) || set.contains("world:" + string2))) {
            return false;
        }
        return !set2.contains(String.valueOf(n)) && !set2.contains(string) && !set2.contains("world:" + string2);
    }

    public DynmapStyle getStyle(Faction faction) {
        DynmapStyle dynmapStyle = this.dynmapConf.dynmap().getFactionStyles().get(String.valueOf(faction.getIntId()));
        if (dynmapStyle != null) {
            return dynmapStyle;
        }
        dynmapStyle = this.dynmapConf.dynmap().getFactionStyles().get(faction.getTag());
        if (dynmapStyle != null) {
            return dynmapStyle;
        }
        return DynmapStyle.getEmpty();
    }

    static void severe(String string) {
        String string2 = DYNMAP_INTEGRATION + String.valueOf(ChatColor.RED) + string;
        FactionsPlugin.getInstance().getLogger().severe(string2);
    }

    private int floodFillTarget(TileFlags tileFlags, TileFlags tileFlags2, int n, int n2) {
        int n3 = 0;
        ArrayDeque<int[]> arrayDeque = new ArrayDeque<int[]>();
        arrayDeque.push(new int[]{n, n2});
        while (!arrayDeque.isEmpty()) {
            int[] nArray = (int[])arrayDeque.pop();
            n = nArray[0];
            if (!tileFlags.getFlag(n, n2 = nArray[1])) continue;
            tileFlags.setFlag(n, n2, false);
            tileFlags2.setFlag(n, n2, true);
            ++n3;
            if (tileFlags.getFlag(n + 1, n2)) {
                arrayDeque.push(new int[]{n + 1, n2});
            }
            if (tileFlags.getFlag(n - 1, n2)) {
                arrayDeque.push(new int[]{n - 1, n2});
            }
            if (tileFlags.getFlag(n, n2 + 1)) {
                arrayDeque.push(new int[]{n, n2 + 1});
            }
            if (!tileFlags.getFlag(n, n2 - 1)) continue;
            arrayDeque.push(new int[]{n, n2 - 1});
        }
        return n3;
    }

    static enum Direction {
        XPLUS,
        ZPLUS,
        XMINUS,
        ZMINUS;

    }
}

