/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  it.unimi.dsi.fastutil.longs.Long2IntMap
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.LWC;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ComponentBuilder;
import moss.factions.shade.net.kyori.adventure.text.TextComponent;
import moss.factions.shade.net.kyori.adventure.text.event.HoverEvent;
import moss.factions.shade.net.kyori.adventure.text.format.NamedTextColor;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class MemoryBoard
extends Board {
    private final char[] mapKeyChrs = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();
    protected final Object2ObjectMap<String, WorldTracker> worldTrackers = new Object2ObjectOpenHashMap();
    private final Cache<FPlayer, List<Component>> mapCache = CacheBuilder.newBuilder().expireAfterWrite(500L, TimeUnit.MILLISECONDS).build();

    protected WorldTracker getAndCreate(String string) {
        return (WorldTracker)this.worldTrackers.computeIfAbsent((Object)string, object -> new WorldTracker(string));
    }

    @Override
    public int getIntIdAt(FLocation fLocation) {
        WorldTracker worldTracker = (WorldTracker)this.worldTrackers.get((Object)fLocation.getWorldName());
        if (worldTracker != null) {
            int n = worldTracker.getFactionIdAt(fLocation);
            return n == Integer.MIN_VALUE ? 0 : n;
        }
        return 0;
    }

    @Override
    public String getIdAt(FLocation fLocation) {
        return String.valueOf(this.getIntIdAt(fLocation));
    }

    @Override
    public Faction getFactionAt(FLocation fLocation) {
        return Factions.getInstance().getFactionById(this.getIntIdAt(fLocation));
    }

    @Override
    public void setIdAt(String string, FLocation fLocation) {
        this.setIdAt(Integer.parseInt(string), fLocation);
    }

    public void setIdAt(int n, FLocation fLocation) {
        this.removeAt(fLocation);
        this.getAndCreate(fLocation.getWorldName()).addClaim(n, fLocation);
    }

    @Override
    public void setFactionAt(Faction faction, FLocation fLocation) {
        this.setIdAt(faction.getIntId(), fLocation);
    }

    @Override
    public void removeAt(FLocation fLocation) {
        Faction faction = this.getFactionAt(fLocation);
        faction.getWarps().values().removeIf(lazyLocation -> fLocation.isInChunk(lazyLocation.getLocation()));
        for (Entity entity : fLocation.getChunk().getEntities()) {
            if (!(entity instanceof Player)) continue;
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player)entity);
            if (!fPlayer.isAdminBypassing() && fPlayer.isFlying()) {
                fPlayer.setFlying(false);
            }
            if (!fPlayer.isWarmingUp()) continue;
            fPlayer.clearWarmup();
            fPlayer.msg(TL.WARMUPS_CANCELLED, new Object[0]);
        }
        this.clearOwnershipAt(fLocation);
        WorldTracker worldTracker = (WorldTracker)this.worldTrackers.get((Object)fLocation.getWorldName());
        if (worldTracker != null) {
            worldTracker.removeClaim(fLocation);
        }
    }

    @Override
    public Set<FLocation> getAllClaims(String string) {
        return this.getAllClaims(Integer.parseInt(string));
    }

    public Set<FLocation> getAllClaims(int n) {
        return this.worldTrackers.values().stream().flatMap(worldTracker -> worldTracker.getAllClaims(n).stream()).collect(Collectors.toSet());
    }

    @Override
    public Set<FLocation> getAllClaims(Faction faction) {
        return this.getAllClaims(faction.getIntId());
    }

    public Int2ObjectMap<LongList> getAllClaimsForDynmap(World world) {
        WorldTracker worldTracker = (WorldTracker)this.worldTrackers.get((Object)world.getName());
        return worldTracker == null ? new Int2ObjectOpenHashMap() : worldTracker.getAllClaimsForDynmap();
    }

    @Override
    public void clearOwnershipAt(FLocation fLocation) {
        Faction faction = this.getFactionAt(fLocation);
        if (faction != null && faction.isNormal()) {
            faction.clearClaimOwnership(fLocation);
        }
    }

    @Override
    public void unclaimAll(Faction faction) {
        this.unclaimAll(faction.getIntId());
    }

    @Override
    public void unclaimAll(String string) {
        this.unclaimAll(Integer.parseInt(string));
    }

    public void unclaimAll(int n) {
        Faction faction = Factions.getInstance().getFactionById(n);
        if (faction != null && faction.isNormal()) {
            faction.clearAllClaimOwnership();
            faction.clearWarps();
        }
        this.clean(n);
    }

    @Override
    public void unclaimAllInWorld(Faction faction, World world) {
        this.unclaimAllInWorld(faction.getIntId(), world);
    }

    @Override
    public void unclaimAllInWorld(String string, World world) {
        this.unclaimAllInWorld(Integer.parseInt(string), world);
    }

    public void unclaimAllInWorld(int n, World world) {
        WorldTracker worldTracker = (WorldTracker)this.worldTrackers.get((Object)world.getName());
        if (worldTracker != null) {
            worldTracker.getAllClaims(n).forEach(this::removeAt);
        }
    }

    public void clean(int n) {
        List list = this.worldTrackers.values().stream().flatMap(worldTracker -> worldTracker.getAllClaims(n).stream()).toList();
        if (LWC.getEnabled() && FactionsPlugin.getInstance().conf().lwc().isResetLocksOnUnclaim()) {
            list.forEach(LWC::clearAllLocks);
        }
        for (FPlayer fPlayer : FPlayers.getInstance().getOnlinePlayers()) {
            if (!list.contains(fPlayer.getLastStoodAt())) continue;
            if (FactionsPlugin.getInstance().conf().commands().fly().isEnable() && !fPlayer.isAdminBypassing() && fPlayer.isFlying()) {
                fPlayer.setFlying(false);
            }
            if (!fPlayer.isWarmingUp()) continue;
            fPlayer.clearWarmup();
            fPlayer.msg(TL.WARMUPS_CANCELLED, new Object[0]);
        }
        this.worldTrackers.values().forEach(worldTracker -> worldTracker.removeAllClaims(n));
    }

    @Override
    public boolean isBorderLocation(FLocation fLocation) {
        Faction faction = this.getFactionAt(fLocation);
        FLocation fLocation2 = fLocation.getRelative(1, 0);
        FLocation fLocation3 = fLocation.getRelative(-1, 0);
        FLocation fLocation4 = fLocation.getRelative(0, 1);
        FLocation fLocation5 = fLocation.getRelative(0, -1);
        return faction != this.getFactionAt(fLocation2) || faction != this.getFactionAt(fLocation3) || faction != this.getFactionAt(fLocation4) || faction != this.getFactionAt(fLocation5);
    }

    @Override
    public boolean isConnectedLocation(FLocation fLocation, Faction faction) {
        FLocation fLocation2 = fLocation.getRelative(1, 0);
        FLocation fLocation3 = fLocation.getRelative(-1, 0);
        FLocation fLocation4 = fLocation.getRelative(0, 1);
        FLocation fLocation5 = fLocation.getRelative(0, -1);
        return faction == this.getFactionAt(fLocation2) || faction == this.getFactionAt(fLocation3) || faction == this.getFactionAt(fLocation4) || faction == this.getFactionAt(fLocation5);
    }

    @Override
    public boolean hasFactionWithin(FLocation fLocation, Faction faction, int n) {
        for (int i = -n; i <= n; ++i) {
            for (int j = -n; j <= n; ++j) {
                FLocation fLocation2;
                Faction faction2;
                if (i == 0 && j == 0 || !(faction2 = this.getFactionAt(fLocation2 = fLocation.getRelative(i, j))).isNormal() || faction2 == faction) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void clean() {
        boolean bl = LWC.getEnabled() && FactionsPlugin.getInstance().conf().lwc().isResetLocksOnUnclaim();
        for (WorldTracker worldTracker2 : this.worldTrackers.values()) {
            IntListIterator intListIterator = worldTracker2.getFactionIds().iterator();
            while (intListIterator.hasNext()) {
                int n = (Integer)intListIterator.next();
                if (Factions.getInstance().isValidFactionId(n)) continue;
                this.worldTrackers.values().stream().flatMap(worldTracker -> worldTracker.getAllClaims(n).stream()).forEach(fLocation -> {
                    if (bl) {
                        LWC.clearAllLocks(fLocation);
                    }
                    FactionsPlugin.getInstance().log("Board cleaner removed id " + n + " from " + String.valueOf(fLocation));
                });
                this.worldTrackers.values().forEach(worldTracker -> worldTracker.removeAllClaims(n));
            }
        }
    }

    @Override
    public int getFactionCoordCount(String string) {
        return this.getFactionCoordCount(Integer.parseInt(string));
    }

    public int getFactionCoordCount(int n) {
        return this.worldTrackers.values().stream().mapToInt(worldTracker -> worldTracker.countFactionClaims(n)).sum();
    }

    @Override
    public int getFactionCoordCount(Faction faction) {
        return this.getFactionCoordCount(faction.getIntId());
    }

    @Override
    public int getFactionCoordCountInWorld(Faction faction, String string) {
        WorldTracker worldTracker = (WorldTracker)this.worldTrackers.get((Object)string);
        return worldTracker == null ? 0 : worldTracker.countFactionClaims(faction.getIntId());
    }

    public int getTotalCount() {
        return this.worldTrackers.values().stream().mapToInt(WorldTracker::countFactionClaims).sum();
    }

    @Override
    public List<Component> getMap(FPlayer fPlayer, FLocation fLocation, double d) {
        Object object;
        Faction faction = fPlayer.getFaction();
        ArrayList<Component> arrayList = new ArrayList<Component>();
        Faction faction2 = this.getFactionAt(fLocation);
        arrayList.add(TextUtil.titleizeC("(" + fLocation.getCoordString() + ") " + faction2.getTag(fPlayer)));
        List<Component> list = AsciiCompass.getAsciiCompass(d, "<red>", "<gold>");
        int n = FactionsPlugin.getInstance().conf().map().getWidth() / 2;
        int n2 = fPlayer.getMapHeight() / 2;
        FLocation fLocation2 = fLocation.getRelative(-n, -n2);
        int n3 = n * 2 + 1;
        int n4 = n2 * 2 + 1;
        if (FactionsPlugin.getInstance().conf().map().isShowFactionKey()) {
            --n4;
        }
        HashMap<String, String> hashMap = new HashMap<String, String>();
        int n5 = 0;
        for (int i = 0; i < n4; ++i) {
            int n6;
            TextComponent.Builder builder = Component.text();
            if (i < 3) {
                builder.append(list.get(i));
            }
            int n7 = n6 = i < 3 ? 6 : 3;
            while (n6 < n3) {
                if (n6 == n && i == n2) {
                    builder.append((ComponentBuilder<?, ?>)((TextComponent.Builder)Component.text().content("+").color(FactionsPlugin.getInstance().conf().map().getSelfColor())).hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(FactionsPlugin.getInstance().txt().parse(TL.CLAIM_YOUAREHERE.toString())))));
                } else {
                    object = fLocation2.getRelative(n6, i);
                    Faction faction3 = this.getFactionAt((FLocation)object);
                    Relation relation = fPlayer.getRelationTo(faction3);
                    if (faction3.isWilderness()) {
                        builder.append((ComponentBuilder<?, ?>)Component.text().content("-").color(FactionsPlugin.getInstance().conf().colors().factions().getWilderness()));
                    } else if (faction3.isSafeZone()) {
                        builder.append((ComponentBuilder<?, ?>)Component.text().content("+").color(FactionsPlugin.getInstance().conf().colors().factions().getSafezone()));
                    } else if (faction3.isWarZone()) {
                        builder.append((ComponentBuilder<?, ?>)Component.text().content("+").color(FactionsPlugin.getInstance().conf().colors().factions().getWarzone()));
                    } else if (faction3 == faction || faction3 == faction2 || relation.isAtLeast(Relation.ALLY) || FactionsPlugin.getInstance().conf().map().isShowNeutralFactionsOnMap() && relation.equals(Relation.NEUTRAL) || FactionsPlugin.getInstance().conf().map().isShowEnemyFactions() && relation.equals(Relation.ENEMY) || FactionsPlugin.getInstance().conf().map().isShowTruceFactions() && relation.equals(Relation.TRUCE)) {
                        if (!hashMap.containsKey(faction3.getTag())) {
                            hashMap.put(faction3.getTag(), String.valueOf(this.mapKeyChrs[Math.min(n5++, this.mapKeyChrs.length - 1)]));
                        }
                        String string = (String)hashMap.get(faction3.getTag());
                        builder.append((ComponentBuilder<?, ?>)Component.text().content(string).color(faction3.getTextColorTo(faction)));
                    } else {
                        builder.append((ComponentBuilder<?, ?>)Component.text().content("-").color(NamedTextColor.GRAY));
                    }
                }
                ++n6;
            }
            arrayList.add((Component)builder.build());
        }
        if (FactionsPlugin.getInstance().conf().map().isShowFactionKey()) {
            TextComponent.Builder builder = Component.text();
            for (String string : hashMap.keySet()) {
                object = fPlayer.getRelationTo(Factions.getInstance().getByTag(string));
                builder.append((ComponentBuilder<?, ?>)Component.text().content(String.format("%s: %s ", hashMap.get(string), string)).color(((Relation)object).getTextColor()));
            }
            arrayList.add((Component)builder.build());
        }
        return arrayList;
    }

    public List<Component> getScoreboardMap(FPlayer fPlayer) {
        try {
            return new ArrayList<Component>(this.mapCache.get(fPlayer, () -> this.makeScoreboardMap(fPlayer)));
        } catch (ExecutionException executionException) {
            return new ArrayList<Component>();
        }
    }

    private List<Component> makeScoreboardMap(FPlayer fPlayer) {
        FLocation fLocation = fPlayer.getLastStoodAt();
        Faction faction = fPlayer.getFaction();
        ArrayList<Component> arrayList = new ArrayList<Component>();
        Faction faction2 = this.getFactionAt(fLocation);
        int n = FactionsPlugin.getInstance().conf().map().getScoreboardWidth() / 2;
        int n2 = FactionsPlugin.getInstance().conf().map().getScoreboardHeight() / 2;
        int n3 = n * 2 + 1;
        int n4 = n2 * 2 + 1;
        double d = (fPlayer.getPlayer().getLocation().getYaw() - 180.0f) % 360.0f;
        if (d < 0.0) {
            d += 360.0;
        }
        Dir dir = d < 45.0 || d >= 315.0 ? Dir.N : (d < 135.0 ? Dir.E : (d < 225.0 ? Dir.S : Dir.W));
        FLocation fLocation2 = switch (dir.ordinal()) {
            case 0 -> fLocation.getRelative(-n, -n2);
            case 2 -> fLocation.getRelative(n, n2);
            case 1 -> fLocation.getRelative(n2, n);
            default -> fLocation.getRelative(-n2, -n);
        };
        for (int i = 0; i < n4; ++i) {
            TextComponent.Builder builder = Component.text();
            for (int j = 0; j < n3; ++j) {
                if (j == n && i == n2) {
                    builder.append((ComponentBuilder<?, ?>)Component.text().content("\u2b1b").color(FactionsPlugin.getInstance().conf().map().getSelfColor()));
                    continue;
                }
                FLocation fLocation3 = switch (dir.ordinal()) {
                    case 0 -> fLocation2.getRelative(j, i);
                    case 2 -> fLocation2.getRelative(-j, -i);
                    case 1 -> fLocation2.getRelative(-i, -(n3 - j - 1));
                    default -> fLocation2.getRelative(i, n3 - j - 1);
                };
                Faction faction3 = this.getFactionAt(fLocation3);
                Relation relation = fPlayer.getRelationTo(faction3);
                if (faction3.isWilderness()) {
                    builder.append((ComponentBuilder<?, ?>)Component.text().content("\u2b1b").color(FactionsPlugin.getInstance().conf().colors().factions().getWilderness()));
                    continue;
                }
                if (faction3.isSafeZone()) {
                    builder.append((ComponentBuilder<?, ?>)Component.text().content("\u2b1b").color(FactionsPlugin.getInstance().conf().colors().factions().getSafezone()));
                    continue;
                }
                if (faction3.isWarZone()) {
                    builder.append((ComponentBuilder<?, ?>)Component.text().content("\u2b1b").color(FactionsPlugin.getInstance().conf().colors().factions().getWarzone()));
                    continue;
                }
                if (faction3 == faction || faction3 == faction2 || relation.isAtLeast(Relation.ALLY) || FactionsPlugin.getInstance().conf().map().isShowNeutralFactionsOnMap() && relation.equals(Relation.NEUTRAL) || FactionsPlugin.getInstance().conf().map().isShowEnemyFactions() && relation.equals(Relation.ENEMY) || FactionsPlugin.getInstance().conf().map().isShowTruceFactions() && relation.equals(Relation.TRUCE)) {
                    builder.append((ComponentBuilder<?, ?>)Component.text().content("\u2b1b").color(faction3.getTextColorTo(faction)));
                    continue;
                }
                builder.append((ComponentBuilder<?, ?>)Component.text().content("\u2b1b").color(NamedTextColor.GRAY));
            }
            arrayList.add((Component)builder.build());
        }
        return arrayList;
    }

    public abstract void convertFrom(MemoryBoard var1);

    protected static class WorldTracker {
        public static final int NO_FACTION = Integer.MIN_VALUE;
        private final String worldName;
        private final Long2IntMap chunkToFaction = new Long2IntOpenHashMap();
        private final Int2ObjectMap<LongSet> factionToChunk = new Int2ObjectOpenHashMap();

        private WorldTracker(String string) {
            this.chunkToFaction.defaultReturnValue(Integer.MIN_VALUE);
            this.worldName = string;
        }

        private LongSet getOrCreate(int n2) {
            return (LongSet)this.factionToChunk.computeIfAbsent(n2, n -> new LongOpenHashSet());
        }

        public void addClaim(int n, FLocation fLocation) {
            long l = Morton.get(fLocation);
            this.removeClaim(fLocation);
            if (n != 0) {
                this.chunkToFaction.put(l, n);
                this.getOrCreate(n).add(l);
            }
        }

        public void addClaimOnLoad(int n, int n2, int n3) {
            long l = Morton.get(n2, n3);
            this.chunkToFaction.put(l, n);
            this.getOrCreate(n).add(l);
        }

        public void removeClaim(FLocation fLocation) {
            LongSet longSet;
            long l = Morton.get(fLocation);
            int n = this.chunkToFaction.remove(l);
            if (n != Integer.MIN_VALUE && (longSet = (LongSet)this.factionToChunk.get(n)) != null) {
                longSet.remove(l);
            }
        }

        public void removeAllClaims(int n) {
            LongSet longSet = (LongSet)this.factionToChunk.remove(n);
            if (longSet != null) {
                longSet.forEach(arg_0 -> ((Long2IntMap)this.chunkToFaction).remove(arg_0));
            }
        }

        public List<FLocation> getAllClaims(int n) {
            LongSet longSet = (LongSet)this.factionToChunk.get(n);
            if (longSet == null) {
                return List.of();
            }
            return longSet.longStream().mapToObj(l -> new FLocation(this.worldName, Morton.getX(l), Morton.getZ(l))).toList();
        }

        public Long2IntMap getChunkToFactionForSave() {
            return this.chunkToFaction;
        }

        public Int2ObjectMap<LongList> getAllClaimsForDynmap() {
            Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
            this.factionToChunk.int2ObjectEntrySet().forEach(arg_0 -> WorldTracker.lambda$getAllClaimsForDynmap$2((Int2ObjectMap)int2ObjectOpenHashMap, arg_0));
            return int2ObjectOpenHashMap;
        }

        public int getFactionIdAt(FLocation fLocation) {
            return this.chunkToFaction.get(Morton.get(fLocation));
        }

        public int countFactionClaims(int n) {
            return ((LongSet)this.factionToChunk.getOrDefault(n, (Object)LongSet.of())).size();
        }

        public int countFactionClaims() {
            return this.chunkToFaction.size();
        }

        public IntList getFactionIds() {
            return new IntArrayList((IntCollection)this.factionToChunk.keySet());
        }

        private static /* synthetic */ void lambda$getAllClaimsForDynmap$2(Int2ObjectMap int2ObjectMap, Int2ObjectMap.Entry entry) {
            int2ObjectMap.put(entry.getIntKey(), (Object)new LongArrayList((LongCollection)entry.getValue()));
        }
    }

    static enum Dir {
        N,
        E,
        S,
        W;

    }

    public static final class Morton {
        public static long get(FLocation fLocation) {
            return Morton.get((int)fLocation.getX(), (int)fLocation.getZ());
        }

        public static long get(int n, int n2) {
            return (Morton.spreadOut(n2) << 1) + Morton.spreadOut(n);
        }

        public static int getX(long l) {
            return Morton.comeTogether(l);
        }

        public static int getZ(long l) {
            return Morton.comeTogether(l >> 1);
        }

        private static long spreadOut(long l) {
            l &= 0xFFFFFFFFL;
            l = (l | l << 16) & 0xFFFF0000FFFFL;
            l = (l | l << 8) & 0xFF00FF00FF00FFL;
            l = (l | l << 4) & 0xF0F0F0F0F0F0F0FL;
            l = (l | l << 2) & 0x3333333333333333L;
            l = (l | l << 1) & 0x5555555555555555L;
            return l;
        }

        private static int comeTogether(long l) {
            l &= 0x5555555555555555L;
            l = (l | l >> 1) & 0x3333333333333333L;
            l = (l | l >> 2) & 0xF0F0F0F0F0F0F0FL;
            l = (l | l >> 4) & 0xFF00FF00FF00FFL;
            l = (l | l >> 8) & 0xFFFF0000FFFFL;
            l = (l | l >> 16) & 0xFFFFFFFFL;
            return (int)l;
        }
    }
}

