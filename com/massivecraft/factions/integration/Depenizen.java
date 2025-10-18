/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.denizenscript.denizen.objects.LocationTag
 *  com.denizenscript.denizen.objects.NPCTag
 *  com.denizenscript.denizen.objects.PlayerTag
 *  com.denizenscript.denizencore.objects.ObjectFetcher
 *  com.denizenscript.denizencore.objects.ObjectTag
 *  com.denizenscript.denizencore.objects.core.ListTag
 *  com.denizenscript.denizencore.objects.properties.PropertyParser
 *  com.denizenscript.denizencore.tags.Attribute
 *  com.denizenscript.denizencore.tags.ReplaceableTagEvent
 *  com.denizenscript.denizencore.tags.TagManager
 *  com.denizenscript.denizencore.tags.TagRunnable$RootForm
 *  com.denizenscript.depenizen.bukkit.Bridge
 *  com.denizenscript.depenizen.bukkit.Depenizen
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.integration;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.depenizen.FactionTag;
import com.massivecraft.factions.integration.depenizen.FactionsLocationProperties;
import com.massivecraft.factions.integration.depenizen.FactionsNPCProperties;
import com.massivecraft.factions.integration.depenizen.FactionsPlayerProperties;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public class Depenizen
extends Bridge {
    public static boolean init(Plugin plugin) {
        try {
            if (plugin instanceof com.denizenscript.depenizen.bukkit.Depenizen) {
                ((com.denizenscript.depenizen.bukkit.Depenizen)plugin).registerBridge("Factions", Depenizen::new);
            }
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().log(Level.WARNING, "Could not load Depenizen integration", exception);
            return false;
        }
        FactionsPlugin.getInstance().getLogger().info("Loaded Depenizen integration!");
        FactionsPlugin.getInstance().getLogger().info("");
        FactionsPlugin.getInstance().getLogger().info("You may safely ignore the Depenizen message warning you about compatibility, as we run our own integration.");
        FactionsPlugin.getInstance().getLogger().info("");
        return true;
    }

    public void init() {
        ObjectFetcher.registerWithObjectFetcher(FactionTag.class);
        PropertyParser.registerProperty(FactionsNPCProperties.class, NPCTag.class);
        PropertyParser.registerProperty(FactionsPlayerProperties.class, PlayerTag.class);
        PropertyParser.registerProperty(FactionsLocationProperties.class, LocationTag.class);
        TagManager.registerTagHandler((TagRunnable.RootForm)new TagRunnable.RootForm(){

            public void run(ReplaceableTagEvent replaceableTagEvent) {
                Depenizen.this.factionTagEvent(replaceableTagEvent);
            }
        }, (String[])new String[]{"faction"});
        TagManager.registerTagHandler((TagRunnable.RootForm)new TagRunnable.RootForm(){

            public void run(ReplaceableTagEvent replaceableTagEvent) {
                Depenizen.this.tagEvent(replaceableTagEvent);
            }
        }, (String[])new String[]{"factions"});
    }

    public void factionTagEvent(ReplaceableTagEvent replaceableTagEvent) {
        Attribute attribute = replaceableTagEvent.getAttributes().fulfill(1);
        String string = attribute.getParam();
        Faction faction = Factions.getInstance().getByTag(string);
        if (faction == null) {
            try {
                faction = Factions.getInstance().getFactionById(Integer.parseInt(string));
            } catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if (faction != null) {
            replaceableTagEvent.setReplacedObject(new FactionTag(faction).getObjectAttribute(attribute.fulfill(1)));
        }
    }

    public void tagEvent(ReplaceableTagEvent replaceableTagEvent) {
        Attribute attribute = replaceableTagEvent.getAttributes().fulfill(1);
        if (attribute.startsWith("list_factions")) {
            ListTag listTag = new ListTag();
            for (Faction faction : Factions.getInstance().getAllFactions()) {
                listTag.addObject((ObjectTag)new FactionTag(faction));
            }
            replaceableTagEvent.setReplacedObject(listTag.getObjectAttribute(attribute.fulfill(1)));
        }
    }
}

