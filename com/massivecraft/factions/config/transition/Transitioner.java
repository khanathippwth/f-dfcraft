/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  org.bukkit.Material
 */
package com.massivecraft.factions.config.transition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.Loader;
import com.massivecraft.factions.config.transition.oldclass.v0.MaterialAdapter;
import com.massivecraft.factions.config.transition.oldclass.v0.NewMemoryFaction;
import com.massivecraft.factions.config.transition.oldclass.v0.OldConfV0;
import com.massivecraft.factions.config.transition.oldclass.v0.OldMemoryFactionV0;
import com.massivecraft.factions.config.transition.oldclass.v0.TransitionConfigV0;
import com.massivecraft.factions.config.transition.oldclass.v1.OldMainConfigV1;
import com.massivecraft.factions.config.transition.oldclass.v1.TransitionConfigV1;
import com.massivecraft.factions.perms.PermSelectorTypeAdapter;
import com.massivecraft.factions.util.EnumTypeAdapter;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.MapFLocToStringSetTypeAdapter;
import com.massivecraft.factions.util.MyLocationTypeAdapter;
import com.massivecraft.factions.util.TL;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import moss.factions.shade.ninja.leaping.configurate.commented.CommentedConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.bukkit.Material;

public class Transitioner {
    private final FactionsPlugin plugin;
    private Gson gsonV0;
    private final Path pluginFolder;
    private final Path configFolder;
    private final Path oldConfigFolder;

    public static void transition(FactionsPlugin factionsPlugin) {
        Transitioner transitioner = new Transitioner(factionsPlugin);
        transitioner.migrateV0();
        transitioner.migrateV5A();
        Path path = factionsPlugin.getDataFolder().toPath().resolve("config").resolve("main.conf");
        if (!Files.exists(path, new LinkOption[0])) {
            return;
        }
        HoconConfigurationLoader hoconConfigurationLoader = Loader.getLoader("main");
        try {
            Object object;
            CommentedConfigurationNode commentedConfigurationNode;
            int n;
            CommentedConfigurationNode commentedConfigurationNode2 = (CommentedConfigurationNode)hoconConfigurationLoader.load();
            CommentedConfigurationNode commentedConfigurationNode3 = commentedConfigurationNode2.getNode("aVeryFriendlyFactionsConfig").getNode("version");
            if (commentedConfigurationNode3.isVirtual()) {
                transitioner.migrateV1(hoconConfigurationLoader);
                commentedConfigurationNode2 = (CommentedConfigurationNode)hoconConfigurationLoader.load();
                commentedConfigurationNode3 = commentedConfigurationNode2.getNode("aVeryFriendlyFactionsConfig").getNode("version");
                if (commentedConfigurationNode3.isVirtual()) {
                    return;
                }
            }
            if ((n = commentedConfigurationNode2.getNode("aVeryFriendlyFactionsConfig").getNode("version").getInt()) < 3) {
                transitioner.migrateV2(commentedConfigurationNode2);
            }
            if (n < 4) {
                transitioner.migrateV3(commentedConfigurationNode2);
            }
            if (n < 5) {
                transitioner.migrateV4(commentedConfigurationNode2);
            }
            if (n < 6) {
                transitioner.migrateV5B(commentedConfigurationNode2);
            }
            if (!(commentedConfigurationNode = commentedConfigurationNode2.getNode("factions").getNode("other").getNode("newPlayerStartingFactionID")).isVirtual() && (object = commentedConfigurationNode.getValue()) instanceof String) {
                String string = (String)object;
                try {
                    commentedConfigurationNode.setValue(Integer.parseInt(string));
                } catch (NumberFormatException numberFormatException) {
                    factionsPlugin.getLogger().warning("Failed to migrate new player starting faction ID to numeric ID. Found: " + string);
                }
            }
            hoconConfigurationLoader.save(commentedConfigurationNode2);
        } catch (IOException iOException) {
            factionsPlugin.getLogger().log(Level.SEVERE, "Failed to save configuration migration! Data may be lost, requiring restoration from backups.", iOException);
        }
    }

    private Transitioner(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
        this.pluginFolder = this.plugin.getDataFolder().toPath();
        this.configFolder = this.pluginFolder.resolve("config");
        this.oldConfigFolder = this.pluginFolder.resolve("oldConfig");
    }

    private void migrateV0() {
        if (Files.exists(this.configFolder, new LinkOption[0])) {
            return;
        }
        Path path = this.pluginFolder.resolve("conf.json");
        if (!Files.exists(path, new LinkOption[0])) {
            return;
        }
        if (Files.exists(this.oldConfigFolder, new LinkOption[0])) {
            this.plugin.getLogger().warning("Found no 'config' folder, but an 'oldConfig' exists. Not attempting conversion.");
            return;
        }
        this.plugin.getLogger().info("Found no 'config' folder. Starting configuration transition...");
        this.buildV0Gson();
        try {
            OldConfV0 oldConfV0 = (OldConfV0)this.gsonV0.fromJson(Files.readString((Path)path), OldConfV0.class);
            TransitionConfigV0 transitionConfigV0 = new TransitionConfigV0(oldConfV0);
            Loader.loadAndSave("main", (Object)transitionConfigV0);
            Files.createDirectories(this.oldConfigFolder, new FileAttribute[0]);
            Path path2 = this.pluginFolder.resolve("data");
            Files.createDirectories(path2, new FileAttribute[0]);
            Files.move(this.pluginFolder.resolve("board.json"), path2.resolve("board.json"), new CopyOption[0]);
            Files.move(this.pluginFolder.resolve("players.json"), path2.resolve("players.json"), new CopyOption[0]);
            Path path3 = this.pluginFolder.resolve("factions.json");
            Map map = (Map)this.gsonV0.fromJson(Files.readString((Path)path3), new TypeToken<Map<String, OldMemoryFactionV0>>(this){}.getType());
            HashMap hashMap = new HashMap();
            map.forEach((string, oldMemoryFactionV0) -> hashMap.put(string, new NewMemoryFaction((OldMemoryFactionV0)oldMemoryFactionV0)));
            Files.writeString((Path)path2.resolve("factions.json"), (CharSequence)this.plugin.getGson().toJson(hashMap), (OpenOption[])new OpenOption[0]);
            Files.move(path3, this.oldConfigFolder.resolve("factions.json"), new CopyOption[0]);
            Files.move(path, this.oldConfigFolder.resolve("conf.json"), new CopyOption[0]);
            this.plugin.getLogger().info("Transition complete!");
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not convert old conf.json", exception);
        }
    }

    private void buildV0Gson() {
        Type type = new TypeToken<Map<FLocation, Set<String>>>(this){}.getType();
        Type type2 = new TypeToken<Material>(this){}.getType();
        this.gsonV0 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().excludeFieldsWithModifiers(new int[]{128, 64}).registerTypeAdapter(type2, (Object)new MaterialAdapter()).registerTypeAdapter(LazyLocation.class, (Object)new MyLocationTypeAdapter()).registerTypeAdapter(type, (Object)new MapFLocToStringSetTypeAdapter()).registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY).create();
    }

    private void migrateV1(HoconConfigurationLoader hoconConfigurationLoader) {
        Path path = this.plugin.getDataFolder().toPath();
        Path path2 = path.resolve("config.yml");
        Path path3 = path.resolve("oldConfig");
        if (!Files.exists(path2, new LinkOption[0])) {
            this.plugin.getLogger().warning("Found a main.conf from before 0.5.4 but no config.yml was found! Might lose some config information!");
            return;
        }
        try {
            OldMainConfigV1 oldMainConfigV1 = new OldMainConfigV1();
            Loader.load(hoconConfigurationLoader, oldMainConfigV1);
            TransitionConfigV1 transitionConfigV1 = new TransitionConfigV1();
            Loader.load(hoconConfigurationLoader, transitionConfigV1);
            transitionConfigV1.update(oldMainConfigV1, this.plugin.getConfig());
            Loader.loadAndSave(hoconConfigurationLoader, (Object)transitionConfigV1);
            Files.createDirectories(path3, new FileAttribute[0]);
            Files.move(path2, path3.resolve("config.yml"), new CopyOption[0]);
        } catch (Exception exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not migrate configuration", exception);
        }
    }

    private void migrateV2(CommentedConfigurationNode commentedConfigurationNode) {
        commentedConfigurationNode.getNode("factions").getNode("enterTitles").getNode("title").setValue("");
        commentedConfigurationNode.getNode("factions").getNode("enterTitles").getNode("subtitle").setValue("{faction-relation-color}{faction}");
        commentedConfigurationNode.getNode("aVeryFriendlyFactionsConfig").getNode("version").setValue(3);
        commentedConfigurationNode.getNode("scoreboard").getNode("constant").getNode("factionlessTitle").setValue(commentedConfigurationNode.getNode("scoreboard").getNode("constant").getNode("title").getString());
        this.plugin.getLogger().info("Detected a config from before 0.5.7");
        this.plugin.getLogger().info("  Setting default enterTitles settings based on old style. Visit main.conf to edit.");
        this.plugin.getLogger().info("  Setting default constant scoreboard factionlessTitle settings based on normal title. Visit main.conf to edit.");
    }

    private void migrateV3(CommentedConfigurationNode commentedConfigurationNode) {
        commentedConfigurationNode.getNode("scoreboard").getNode("constant").getNode("prefixTemplate").setValue(TL.DEFAULT_PREFIX.toString());
        commentedConfigurationNode.getNode("aVeryFriendlyFactionsConfig").getNode("version").setValue(4);
        this.plugin.getLogger().info("Detected a config from before 0.5.14");
        this.plugin.getLogger().info("  1. Setting default scoreboard prefixTemplate based on lang.yml default-prefix setting.");
        this.plugin.getLogger().info("  2. Be aware that \"warZonePreventMonsterSpawns\" has been removed entirely, and there is");
        this.plugin.getLogger().info("     now a new spawning control system added.");
        this.plugin.getLogger().info("  3. The perms gui hover text can now be customized in config, and the allow/deny/locked text");
        this.plugin.getLogger().info("     can now be customized in lang.yml under GUI->PERMS->ACTION");
    }

    private void migrateV4(CommentedConfigurationNode commentedConfigurationNode) {
        CommentedConfigurationNode commentedConfigurationNode2;
        commentedConfigurationNode.getNode("aVeryFriendlyFactionsConfig").getNode("version").setValue(5);
        boolean bl = commentedConfigurationNode.getNode("factions").getNode("spawning").getNode("updateAutomatically").getBoolean(true);
        if (bl && !(commentedConfigurationNode2 = commentedConfigurationNode.getNode("factions").getNode("spawning").getNode("preventSpawningInSafezoneExceptions")).isVirtual()) {
            ArrayList<String> arrayList = new ArrayList<String>(commentedConfigurationNode2.getList(Object::toString));
            arrayList.add("AXOLOTL");
            arrayList.add("GLOW_SQUID");
            commentedConfigurationNode.getNode("factions").getNode("spawning").getNode("preventSpawningInSafezoneExceptions").setValue(arrayList);
        }
        this.plugin.getLogger().info("Detected a config from before 0.5.24 (which adds 1.17 entities)");
        if (bl) {
            this.plugin.getLogger().info("  Because you had auto updating enabled, added AXOLOTL and GLOW_SQUID to the safe zone spawning exception list.");
        } else {
            this.plugin.getLogger().info("  If you had auto updating enabled, this would have added AXOLOTL and GLOW_SQUID to the safe zone spawning exception list.");
        }
        this.plugin.getLogger().info("  We chose not to add GOAT due to its affection for ramming.");
    }

    private void migrateV5A() {
        Path path = this.configFolder.resolve("default_permissions.conf");
        Path path2 = this.configFolder.resolve("default_permissions_offline.conf");
        boolean bl = Files.exists(path, new LinkOption[0]);
        boolean bl2 = Files.exists(path2, new LinkOption[0]);
        if (bl || bl2) {
            this.plugin.getLogger().info("Detected now-unused default permissions files.");
            if (!Files.exists(this.oldConfigFolder, new LinkOption[0])) {
                try {
                    Files.createDirectories(this.oldConfigFolder, new FileAttribute[0]);
                } catch (IOException iOException) {
                    this.plugin.getLogger().log(Level.WARNING, "Failed to create oldConfig folder!", iOException);
                    return;
                }
            }
        }
        if (bl) {
            try {
                Files.move(path, this.oldConfigFolder.resolve("default_permissions.conf"), new CopyOption[0]);
                this.plugin.getLogger().info("  Moved default_permissions.conf to oldConfig");
            } catch (IOException iOException) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to move old default_permissions.conf to oldConfig folder!", iOException);
            }
        }
        if (bl2) {
            try {
                Files.move(path2, this.oldConfigFolder.resolve("default_permissions_offline.conf"), new CopyOption[0]);
                this.plugin.getLogger().info("  Moved default_permissions_offline.conf to oldConfig");
            } catch (IOException iOException) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to move old default_permissions_offline.conf to oldConfig folder!", iOException);
            }
        }
    }

    private void migrateV5B(CommentedConfigurationNode commentedConfigurationNode) {
        commentedConfigurationNode.getNode("aVeryFriendlyFactionsConfig").getNode("version").setValue(6);
        this.plugin.getLogger().info("");
        this.plugin.getLogger().info("              !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        this.plugin.getLogger().info("");
        this.plugin.getLogger().info("You are upgrading from a version prior to 0.6.0, so please read closely.");
        this.plugin.getLogger().info("  Faction permissions (/f perms) have changed significantly.");
        this.plugin.getLogger().info("  Permissions in already created factions are preserved, but if you modified the");
        this.plugin.getLogger().info("    default_permissions files you will need to set those again.");
        this.plugin.getLogger().info("  Your old default entries in the default_permissions files are not migrated.");
        this.plugin.getLogger().info("    The original defaults have been restored to the new format.");
        this.plugin.getLogger().info("  'Locked' actions have been replaced with 'override' settings. You will need to");
        this.plugin.getLogger().info("    set up new 'override' settings, if you had any locked actions before.");
        this.plugin.getLogger().info("  See the new file config/permissions.conf for more,");
        this.plugin.getLogger().info("    and be sure to read the changelog for this release.");
        this.plugin.getLogger().info("");
        this.plugin.getLogger().info("              !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        this.plugin.getLogger().info("");
        PermSelectorTypeAdapter.setLegacy();
    }
}

