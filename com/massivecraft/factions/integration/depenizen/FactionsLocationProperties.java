/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.denizenscript.denizen.objects.LocationTag
 *  com.denizenscript.denizencore.objects.Mechanism
 *  com.denizenscript.denizencore.objects.ObjectTag
 *  com.denizenscript.denizencore.objects.properties.Property
 *  com.denizenscript.denizencore.tags.Attribute
 *  org.bukkit.Location
 */
package com.massivecraft.factions.integration.depenizen;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.integration.depenizen.FactionTag;
import org.bukkit.Location;

public class FactionsLocationProperties
implements Property {
    public static final String[] handledTags = new String[]{"faction"};
    public static final String[] handledMechs = new String[0];
    LocationTag location;

    public String getPropertyString() {
        return null;
    }

    public String getPropertyId() {
        return "FactionsLocation";
    }

    public void adjust(Mechanism mechanism) {
    }

    public static boolean describes(ObjectTag objectTag) {
        return objectTag instanceof LocationTag;
    }

    public static FactionsLocationProperties getFrom(ObjectTag objectTag) {
        if (!FactionsLocationProperties.describes(objectTag)) {
            return null;
        }
        return new FactionsLocationProperties((LocationTag)objectTag);
    }

    private FactionsLocationProperties(LocationTag locationTag) {
        this.location = locationTag;
    }

    public ObjectTag getObjectAttribute(Attribute attribute) {
        if (attribute.startsWith("faction")) {
            return new FactionTag(Board.getInstance().getFactionAt(new FLocation((Location)this.location))).getObjectAttribute(attribute.fulfill(1));
        }
        return null;
    }
}

