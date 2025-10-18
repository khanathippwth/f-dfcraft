/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.denizenscript.denizen.objects.PlayerTag
 *  com.denizenscript.denizencore.objects.Mechanism
 *  com.denizenscript.denizencore.objects.ObjectTag
 *  com.denizenscript.denizencore.objects.core.ElementTag
 *  com.denizenscript.denizencore.objects.properties.Property
 *  com.denizenscript.denizencore.tags.Attribute
 */
package com.massivecraft.factions.integration.depenizen;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.integration.depenizen.FactionTag;

public class FactionsPlayerProperties
implements Property {
    public static final String[] handledTags = new String[]{"factions", "faction"};
    public static final String[] handledMechs = new String[0];
    PlayerTag player;

    public String getPropertyString() {
        return null;
    }

    public String getPropertyId() {
        return "FactionsPlayer";
    }

    public void adjust(Mechanism mechanism) {
    }

    public static boolean describes(ObjectTag objectTag) {
        return objectTag instanceof PlayerTag;
    }

    public static FactionsPlayerProperties getFrom(ObjectTag objectTag) {
        if (!FactionsPlayerProperties.describes(objectTag)) {
            return null;
        }
        return new FactionsPlayerProperties((PlayerTag)objectTag);
    }

    private FactionsPlayerProperties(PlayerTag playerTag) {
        this.player = playerTag;
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getById(this.player.getUUID().toString());
    }

    public ObjectTag getObjectAttribute(Attribute attribute) {
        if (attribute.startsWith("factions")) {
            if ((attribute = attribute.fulfill(1)).startsWith("power")) {
                return new ElementTag(this.getFPlayer().getPower()).getObjectAttribute(attribute.fulfill(1));
            }
            if (this.getFPlayer().hasFaction()) {
                if (attribute.startsWith("role")) {
                    if (this.getFPlayer().getRole() != null) {
                        return new ElementTag(this.getFPlayer().getRole().toString()).getObjectAttribute(attribute.fulfill(1));
                    }
                } else if (attribute.startsWith("title") && this.getFPlayer().getTitle() != null) {
                    return new ElementTag(this.getFPlayer().getTitle()).getObjectAttribute(attribute.fulfill(1));
                }
            }
        } else if (attribute.startsWith("faction")) {
            return new FactionTag(this.getFPlayer().getFaction()).getObjectAttribute(attribute.fulfill(1));
        }
        return null;
    }
}

