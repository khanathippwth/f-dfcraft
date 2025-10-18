/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.config.transition.oldclass.v0;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.config.transition.oldclass.v0.OldAccessV0;
import com.massivecraft.factions.config.transition.oldclass.v0.OldMemoryFactionV0;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.util.LazyLocation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NewMemoryFaction {
    private String id;
    private boolean peacefulExplosionsEnabled;
    private boolean permanent;
    private String tag;
    private String description;
    private boolean open;
    private boolean peaceful;
    private Integer permanentPower;
    private LazyLocation home;
    private long foundedDate;
    private double powerBoost;
    private Map<String, Relation> relationWish;
    private Map<FLocation, Set<String>> claimOwnership;
    private Set<String> invites;
    private HashMap<String, List<String>> announcements;
    private ConcurrentHashMap<String, LazyLocation> warps;
    private ConcurrentHashMap<String, String> warpPasswords;
    private long lastDeath;
    private int maxVaults;
    private Role defaultRole;
    private Map<String, Map<String, Boolean>> permissions;
    private Map<String, Map<String, Boolean>> permissionsOffline;
    private Set<BanInfo> bans;

    public NewMemoryFaction(OldMemoryFactionV0 oldMemoryFactionV0) {
        this.id = oldMemoryFactionV0.id;
        this.peacefulExplosionsEnabled = oldMemoryFactionV0.peacefulExplosionsEnabled;
        this.permanent = oldMemoryFactionV0.permanent;
        this.tag = oldMemoryFactionV0.tag;
        this.description = oldMemoryFactionV0.description;
        this.open = oldMemoryFactionV0.open;
        this.peaceful = oldMemoryFactionV0.peaceful;
        this.permanentPower = oldMemoryFactionV0.permanentPower;
        this.home = oldMemoryFactionV0.home;
        this.foundedDate = oldMemoryFactionV0.foundedDate;
        this.powerBoost = oldMemoryFactionV0.powerBoost;
        this.relationWish = oldMemoryFactionV0.relationWish;
        this.claimOwnership = oldMemoryFactionV0.claimOwnership;
        this.invites = oldMemoryFactionV0.invites;
        this.announcements = oldMemoryFactionV0.announcements;
        this.warps = oldMemoryFactionV0.warps;
        this.warpPasswords = oldMemoryFactionV0.warpPasswords;
        this.lastDeath = oldMemoryFactionV0.lastDeath;
        this.maxVaults = oldMemoryFactionV0.maxVaults;
        this.defaultRole = oldMemoryFactionV0.defaultRole;
        this.permissions = new HashMap<String, Map<String, Boolean>>();
        this.permissionsOffline = new HashMap<String, Map<String, Boolean>>();
        oldMemoryFactionV0.permissions.forEach((string2, map) -> {
            HashMap hashMap = new HashMap();
            map.forEach((string, oldAccessV0) -> {
                switch (string.toUpperCase()) {
                    case "FROST_WALK": {
                        string = "FROSTWALK";
                        break;
                    }
                    case "PAIN_BUILD": {
                        string = "PAINBUILD";
                        break;
                    }
                    case "WITHDRAW": {
                        string = "ECONOMY";
                    }
                }
                if (oldAccessV0 == OldAccessV0.ALLOW || oldAccessV0 == OldAccessV0.DENY) {
                    hashMap.put(string, oldAccessV0 == OldAccessV0.ALLOW);
                }
            });
            this.permissions.put((String)string2, hashMap);
            this.permissionsOffline.put((String)string2, hashMap);
        });
        this.bans = oldMemoryFactionV0.bans;
    }
}

