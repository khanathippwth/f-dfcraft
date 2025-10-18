/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.config.transition.oldclass.v0;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.config.transition.oldclass.v0.OldAccessV0;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.util.LazyLocation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OldMemoryFactionV0 {
    String id = null;
    boolean peacefulExplosionsEnabled;
    boolean permanent;
    String tag;
    String description;
    boolean open;
    boolean peaceful;
    Integer permanentPower;
    LazyLocation home;
    long foundedDate;
    double powerBoost;
    Map<String, Relation> relationWish = new HashMap<String, Relation>();
    Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<FLocation, Set<String>>();
    Set<String> invites = new HashSet<String>();
    HashMap<String, List<String>> announcements = new HashMap();
    ConcurrentHashMap<String, LazyLocation> warps = new ConcurrentHashMap();
    ConcurrentHashMap<String, String> warpPasswords = new ConcurrentHashMap();
    long lastDeath;
    int maxVaults;
    Role defaultRole;
    Map<String, Map<String, OldAccessV0>> permissions = new HashMap<String, Map<String, OldAccessV0>>();
    Set<BanInfo> bans = new HashSet<BanInfo>();

    private OldMemoryFactionV0() {
    }
}

