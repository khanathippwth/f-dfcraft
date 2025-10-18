/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.denizenscript.denizen.objects.ChunkTag
 *  com.denizenscript.denizen.objects.LocationTag
 *  com.denizenscript.denizen.objects.PlayerTag
 *  com.denizenscript.denizencore.objects.Fetchable
 *  com.denizenscript.denizencore.objects.ObjectTag
 *  com.denizenscript.denizencore.objects.core.ElementTag
 *  com.denizenscript.denizencore.objects.core.ListTag
 *  com.denizenscript.denizencore.tags.Attribute
 *  com.denizenscript.denizencore.tags.TagContext
 *  com.denizenscript.denizencore.utilities.debugging.Debug
 */
package com.massivecraft.factions.integration.depenizen;

import com.denizenscript.denizen.objects.ChunkTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.LazyLocation;
import java.util.UUID;

public class FactionTag
implements ObjectTag {
    Faction faction;
    private String prefix = "Faction";

    public static FactionTag valueOf(String string) {
        return FactionTag.valueOf(string, null);
    }

    @Fetchable(value="faction")
    public static FactionTag valueOf(String string, TagContext tagContext) {
        if (string == null) {
            return null;
        }
        string = string.replace("faction@", "");
        Faction faction = Factions.getInstance().getByTag(string);
        if (faction != null) {
            return new FactionTag(faction);
        }
        return null;
    }

    public static boolean matches(String string) {
        return FactionTag.valueOf(string) != null;
    }

    public FactionTag(Faction faction) {
        if (faction != null) {
            this.faction = faction;
        } else {
            Debug.echoError((String)"Faction referenced is null!");
        }
    }

    public Faction getFaction() {
        return this.faction;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public ObjectTag setPrefix(String string) {
        this.prefix = string;
        return this;
    }

    public boolean isUnique() {
        return true;
    }

    public String identify() {
        return "faction@" + this.faction.getTag();
    }

    public String identifySimple() {
        return this.identify();
    }

    public String toString() {
        return this.identify();
    }

    public ObjectTag getObjectAttribute(Attribute attribute) {
        Object object;
        if (attribute.startsWith("balance")) {
            return new ElementTag(Econ.getBalance(this.faction)).getObjectAttribute(attribute.fulfill(1));
        }
        if (attribute.startsWith("warp") && attribute.hasParam()) {
            object = this.faction.getWarp(attribute.getParam());
            if (object != null) {
                return new LocationTag(((LazyLocation)object).getLocation()).getObjectAttribute(attribute.fulfill(1));
            }
        } else if (attribute.startsWith("home")) {
            if (this.faction.hasHome()) {
                return new LocationTag(this.faction.getHome()).getObjectAttribute(attribute.fulfill(1));
            }
        } else {
            if (attribute.startsWith("id")) {
                return new ElementTag(String.valueOf(this.faction.getIntId())).getObjectAttribute(attribute.fulfill(1));
            }
            if (attribute.startsWith("isopen") || attribute.startsWith("is_open")) {
                return new ElementTag(this.faction.getOpen()).getObjectAttribute(attribute.fulfill(1));
            }
            if (attribute.startsWith("ispeaceful") || attribute.startsWith("is_peaceful")) {
                return new ElementTag(this.faction.isPeaceful()).getObjectAttribute(attribute.fulfill(1));
            }
            if (attribute.startsWith("ispermanent") || attribute.startsWith("is_permanent")) {
                return new ElementTag(this.faction.isPermanent()).getObjectAttribute(attribute.fulfill(1));
            }
            if (attribute.startsWith("leader")) {
                if (this.faction.getFPlayerAdmin() != null) {
                    return new PlayerTag(UUID.fromString(this.faction.getFPlayerAdmin().getId())).getObjectAttribute(attribute.fulfill(1));
                }
            } else {
                if (attribute.startsWith("name")) {
                    return new ElementTag(this.faction.getTag()).getObjectAttribute(attribute.fulfill(1));
                }
                if (attribute.startsWith("playercount") || attribute.startsWith("player_count")) {
                    return new ElementTag(this.faction.getFPlayers().size()).getObjectAttribute(attribute.fulfill(1));
                }
                if (attribute.startsWith("power")) {
                    return new ElementTag(this.faction.getPower()).getObjectAttribute(attribute.fulfill(1));
                }
                if (attribute.startsWith("relation")) {
                    object = FactionTag.valueOf(attribute.getParam());
                    if (object != null) {
                        return new ElementTag(this.faction.getRelationTo(((FactionTag)object).getFaction()).toString()).getObjectAttribute(attribute.fulfill(1));
                    }
                } else if (attribute.startsWith("size")) {
                    return new ElementTag(this.faction.getAllClaims().size()).getObjectAttribute(attribute.fulfill(1));
                }
            }
        }
        if (attribute.startsWith("claimed_chunks")) {
            object = new ListTag();
            for (FLocation fLocation : this.faction.getAllClaims()) {
                object.addObject((ObjectTag)new ChunkTag(fLocation.getChunk()));
            }
            return object.getObjectAttribute(attribute.fulfill(1));
        }
        if (attribute.startsWith("list_players")) {
            object = new ListTag();
            for (FPlayer fPlayer : this.faction.getFPlayers()) {
                object.addObject((ObjectTag)new PlayerTag(UUID.fromString(fPlayer.getId())));
            }
            return object.getObjectAttribute(attribute.fulfill(1));
        }
        return new ElementTag(this.identify()).getObjectAttribute(attribute);
    }
}

