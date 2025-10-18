/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 */
package com.massivecraft.factions.config.transition.oldclass.v1;

import com.massivecraft.factions.config.annotation.Comment;
import com.massivecraft.factions.config.transition.oldclass.v1.OldMainConfigV1;
import com.massivecraft.factions.util.material.MaterialDb;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class TransitionConfigV1 {
    @Comment(value="The command base (by default f, making the command /f)")
    private List<String> commandBase = new ArrayList<String>(){
        {
            this.add("f");
        }
    };
    @Comment(value="FactionsUUID by drtshock\nSupport and documentation https://factions.support\nUpdates https://www.spigotmc.org/resources/factionsuuid.1035/\n\nMade with love <3")
    private AVeryFriendlyFactionsConfig aVeryFriendlyFactionsConfig = new AVeryFriendlyFactionsConfig();
    @Comment(value="Colors for relationships and default factions")
    private Colors colors = new Colors(this);
    private Commands commands = new Commands(this);
    private Factions factions = new Factions(this);
    @Comment(value="What should be logged?")
    private Logging logging = new Logging(this);
    @Comment(value="Controls certain exploit preventions")
    private Exploits exploits = new Exploits(this);
    @Comment(value="Economy support requires Vault and a compatible economy plugin")
    private Economy economy = new Economy(this);
    @Comment(value="Control for the default settings of /f map")
    private MapSettings map = new MapSettings(this);
    @Comment(value="Data storage settings")
    private Data data = new Data(this);
    private RestrictWorlds restrictWorlds = new RestrictWorlds(this);
    private Scoreboard scoreboard = new Scoreboard(this);
    @Comment(value="LWC integration\nThis support targets the modern fork of LWC, called LWC Extended.\nYou can find it here: https://www.spigotmc.org/resources/lwc-extended.69551/\nNote: Modern LWC is no longer supported, and its former maintainer now runs LWC Extended")
    private LWC lwc = new LWC(this);
    @Comment(value="PlayerVaults faction vault settings.\nEnable faction-owned vaults!\nhttps://www.spigotmc.org/resources/playervaultsx.51204/")
    private PlayerVaults playerVaults = new PlayerVaults(this);
    @Comment(value="WorldGuard settings")
    private WorldGuard worldGuard = new WorldGuard(this);
    private WorldBorder worldBorder = new WorldBorder(this);

    public List<String> getCommandBase() {
        return this.commandBase == null ? (this.commandBase = Collections.singletonList("f")) : this.commandBase;
    }

    public Colors colors() {
        return this.colors;
    }

    public Commands commands() {
        return this.commands;
    }

    public Factions factions() {
        return this.factions;
    }

    public Logging logging() {
        return this.logging;
    }

    public Exploits exploits() {
        return this.exploits;
    }

    public Economy economy() {
        return this.economy;
    }

    public MapSettings map() {
        return this.map;
    }

    public RestrictWorlds restrictWorlds() {
        return this.restrictWorlds;
    }

    public Scoreboard scoreboard() {
        return this.scoreboard;
    }

    public PlayerVaults playerVaults() {
        return this.playerVaults;
    }

    public WorldGuard worldGuard() {
        return this.worldGuard;
    }

    public WorldBorder worldBorder() {
        return this.worldBorder;
    }

    public Data data() {
        return this.data;
    }

    public void update(OldMainConfigV1 oldMainConfigV1, FileConfiguration fileConfiguration) {
        this.factions.other.allowMultipleColeaders = oldMainConfigV1.factions.allowMultipleColeaders;
        this.factions.other.tagLengthMin = oldMainConfigV1.factions.tagLengthMin;
        this.factions.other.tagLengthMax = oldMainConfigV1.factions.tagLengthMax;
        this.factions.other.tagForceUpperCase = oldMainConfigV1.factions.tagForceUpperCase;
        this.factions.other.newFactionsDefaultOpen = oldMainConfigV1.factions.newFactionsDefaultOpen;
        this.factions.other.factionMemberLimit = oldMainConfigV1.factions.factionMemberLimit;
        this.factions.other.newPlayerStartingFactionID = oldMainConfigV1.factions.newPlayerStartingFactionID;
        this.factions.other.saveToFileEveryXMinutes = oldMainConfigV1.factions.saveToFileEveryXMinutes;
        this.factions.other.autoLeaveAfterDaysOfInactivity = oldMainConfigV1.factions.autoLeaveAfterDaysOfInactivity;
        this.factions.other.autoLeaveRoutineRunsEveryXMinutes = oldMainConfigV1.factions.autoLeaveRoutineRunsEveryXMinutes;
        this.factions.other.autoLeaveRoutineMaxMillisecondsPerTick = oldMainConfigV1.factions.autoLeaveRoutineMaxMillisecondsPerTick;
        this.factions.other.removePlayerDataWhenBanned = oldMainConfigV1.factions.removePlayerDataWhenBanned;
        this.factions.other.autoLeaveDeleteFPlayerData = oldMainConfigV1.factions.autoLeaveDeleteFPlayerData;
        this.factions.other.considerFactionsReallyOfflineAfterXMinutes = oldMainConfigV1.factions.considerFactionsReallyOfflineAfterXMinutes;
        this.factions.other.actionDeniedPainAmount = oldMainConfigV1.factions.actionDeniedPainAmount;
        this.factions.other.separateOfflinePerms = oldMainConfigV1.factions.separateOfflinePerms;
        this.aVeryFriendlyFactionsConfig.debug = fileConfiguration.getBoolean("debug", false);
        this.commands.fly.particles.amount = fileConfiguration.getInt("f-fly.trails.amount", 20);
        this.commands.fly.particles.spawnRate = fileConfiguration.getDouble("f-fly.trails.spawn-rate", 0.2);
        this.commands.fly.particles.speed = fileConfiguration.getDouble("f-fly.trails.speed", 0.02);
        this.commands.fly.enable = fileConfiguration.getBoolean("f-fly.enable", true);
        this.commands.fly.delay = fileConfiguration.getInt("warmups.f-fly", 0);
        this.commands.fly.fallDamageCooldown = fileConfiguration.getInt("f-fly.falldamage-cooldown", 3);
        this.commands.fly.enemyRadius = fileConfiguration.getInt("f-fly.enemy-radius", 7);
        this.commands.fly.radiusCheck = fileConfiguration.getInt("f-fly.radius-check", 1);
        this.commands.fly.disableOnGenericDamage = fileConfiguration.getBoolean("f-fly.disable-generic-damage", false);
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("help");
        HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();
        for (String string : configurationSection.getKeys(false)) {
            List list = configurationSection.getStringList(string);
            if (list == null) continue;
            hashMap.put(string, configurationSection.getStringList(string));
        }
        if (!hashMap.isEmpty()) {
            this.commands.help.entries = hashMap;
        }
        this.commands.help.useOldHelp = fileConfiguration.getBoolean("use-old-help", true);
        this.commands.home.delay = fileConfiguration.getInt("warmups.f-home", 0);
        this.commands.list.header = fileConfiguration.getString("list.header", "&e&m----------&r&e[ &2Faction List &9{pagenumber}&e/&9{pagecount} &e]&m----------");
        this.commands.list.factionlessEntry = fileConfiguration.getString("list.factionless", "<i>Factionless<i> {factionless} online");
        this.commands.list.entry = fileConfiguration.getString("list.entry", "<a>{faction-relation-color}{faction} <i>{online} / {members} online, <a>Land / Power / Maxpower: <i>{chunks}/{power}/{maxPower}");
        this.commands.map.cooldown = fileConfiguration.getInt("findfactionsexploit", 700);
        this.commands.near.radius = fileConfiguration.getInt("f-near.radius", 20);
        this.commands.seeChunk.particleName = fileConfiguration.getString("see-chunk.particle", "REDSTONE");
        this.commands.seeChunk.particles = fileConfiguration.getBoolean("see-chunk.particles", true);
        this.commands.seeChunk.particleUpdateTime = fileConfiguration.getDouble("see-chunk.particle-update-time", 0.75);
        this.commands.seeChunk.relationalColor = fileConfiguration.getBoolean("see-chunk.relational-color", true);
        this.commands.show.exempt = fileConfiguration.getStringList("show-exempt");
        if (this.commands.show.exempt == null) {
            this.commands.show.exempt = Arrays.asList("Put_faction_tag_here");
        }
        this.commands.show.minimal = fileConfiguration.getBoolean("minimal-show", false);
        if (!fileConfiguration.getStringList("show").isEmpty()) {
            this.commands.show.format = fileConfiguration.getStringList("show");
        }
        this.commands.stuck.delay = fileConfiguration.getInt("hcf.stuck.delay", 30);
        this.commands.stuck.radius = fileConfiguration.getInt("hcf.stuck.radius", 10);
        this.commands.warp.delay = fileConfiguration.getInt("warmups.f-warp", 0);
        this.commands.warp.maxWarps = fileConfiguration.getInt("max-warps", 5);
        if (!fileConfiguration.getStringList("tooltips.list").isEmpty()) {
            this.commands.toolTips.faction = fileConfiguration.getStringList("tooltips.list");
        }
        if (!fileConfiguration.getStringList("tooltips.show").isEmpty()) {
            this.commands.toolTips.player = fileConfiguration.getStringList("tooltips.show");
        }
        this.factions.landRaidControl.power.raidability = fileConfiguration.getBoolean("hcf.raidable", false);
        this.factions.landRaidControl.power.powerFreeze = fileConfiguration.getInt("hcf.powerfreeze", 0);
        this.factions.maxRelations.enabled = fileConfiguration.getBoolean("max-relations.enabled", false);
        this.factions.maxRelations.ally = fileConfiguration.getInt("max-relations.ally", 10);
        this.factions.maxRelations.truce = fileConfiguration.getInt("max-relations.truce", 10);
        this.factions.maxRelations.neutral = fileConfiguration.getInt("max-relations.neutral", -1);
        this.factions.maxRelations.enemy = fileConfiguration.getInt("max-relations.enemy", 10);
        this.factions.portals.limit = fileConfiguration.getBoolean("portals.limit", false);
        this.factions.portals.minimumRelation = fileConfiguration.getString("portals.minimum-relation", "MEMBER");
        this.factions.claims.bufferZone = fileConfiguration.getInt("hcf.buffer-zone", 0);
        this.factions.claims.allowOverClaim = fileConfiguration.getBoolean("hcf.allow-overclaim", true);
        this.factions.other.deleteEssentialsHomes = fileConfiguration.getBoolean("delete-ess-homes", true);
        this.factions.other.defaultRelation = fileConfiguration.getString("default-relation", "neutral");
        this.factions.other.disablePistonsInTerritory = fileConfiguration.getBoolean("disable-pistons-in-territory", false);
        this.factions.enterTitles.enabled = fileConfiguration.getBoolean("enter-titles.enabled", true);
        this.factions.enterTitles.fadeIn = fileConfiguration.getInt("enter-titles.fade-in", 10);
        this.factions.enterTitles.fadeOut = fileConfiguration.getInt("enter-titles.fade-out", 20);
        this.factions.enterTitles.stay = fileConfiguration.getInt("enter-titles.stay", 70);
        this.factions.enterTitles.alsoShowChat = fileConfiguration.getBoolean("enter-titles.also-show-chat", false);
        boolean bl = this.economy.enabled && fileConfiguration.getBoolean("warp-cost.enabled", false);
        this.economy.costWarp = bl ? (double)fileConfiguration.getInt("warp-cost.warp", 5) : 0.0;
        this.economy.costSetWarp = bl ? (double)fileConfiguration.getInt("warp-cost.setwarp", 5) : 0.0;
        this.economy.costDelWarp = bl ? (double)fileConfiguration.getInt("warp-cost.delwarp", 5) : 0.0;
        this.scoreboard.constant.enabled = fileConfiguration.getBoolean("scoreboard.default-enabled", false);
        this.scoreboard.constant.title = fileConfiguration.getString("scoreboard.default-title", "Faction Status");
        this.scoreboard.constant.prefixes = fileConfiguration.getBoolean("scoreboard.default-prefixes", true);
        if (!fileConfiguration.getStringList("scoreboard.default").isEmpty()) {
            this.scoreboard.constant.content = fileConfiguration.getStringList("scoreboard.default");
        }
        this.scoreboard.constant.factionlessEnabled = fileConfiguration.getBoolean("scoreboard.factionless-enabled", false);
        if (!fileConfiguration.getStringList("scoreboard.factionless").isEmpty()) {
            this.scoreboard.constant.factionlessContent = fileConfiguration.getStringList("scoreboard.factionless");
        }
        this.scoreboard.info.enabled = fileConfiguration.getBoolean("scoreboard.finfo-enabled", false);
        this.scoreboard.info.alsoSendChat = fileConfiguration.getBoolean("scoreboard.also-sent-chat", true);
        this.scoreboard.info.expiration = fileConfiguration.getInt("scoreboard.expiration");
        if (!fileConfiguration.getStringList("scoreboard.finfo").isEmpty()) {
            this.scoreboard.info.content = fileConfiguration.getStringList("scoreboard.finfo");
        }
        this.lwc.enabled = fileConfiguration.getBoolean("lwc.integration", false);
        this.lwc.resetLocksOnCapture = fileConfiguration.getBoolean("lwc.reset-locks-unclaim");
        this.lwc.resetLocksOnUnclaim = fileConfiguration.getBoolean("lwc.reset-locks-capture");
        this.worldBorder.buffer = fileConfiguration.getInt("world-border.buffer", 0);
    }

    public static class AVeryFriendlyFactionsConfig {
        @Comment(value="Don't change this value yourself, unless you WANT a broken config!")
        private int version = 2;
        @Comment(value="Debug\nTurn this on if you are having issues with something and working on resolving them.\nThis will spam your console with information that is useful if you know how to read the source.\nIt's suggested that you only turn this on at the direction of a developer.")
        private boolean debug = false;

        public boolean isDebug() {
            return this.debug;
        }
    }

    public class Colors {
        private Factions factions = new Factions();
        private Relations relations = new Relations();

        public Colors(TransitionConfigV1 transitionConfigV1) {
        }

        private ChatColor getColor(String string, ChatColor chatColor, ChatColor chatColor2) {
            ChatColor chatColor3;
            if (chatColor != null) {
                return chatColor;
            }
            try {
                chatColor3 = ChatColor.valueOf((String)string);
            } catch (IllegalArgumentException illegalArgumentException) {
                chatColor3 = chatColor2;
            }
            return chatColor3;
        }

        public Factions factions() {
            return this.factions;
        }

        public Relations relations() {
            return this.relations;
        }

        public class Factions {
            private String wilderness = "GRAY";
            private transient ChatColor wildernessColor;
            private String safezone = "GOLD";
            private transient ChatColor safezoneColor;
            private String warzone = "DARK_RED";
            private transient ChatColor warzoneColor;

            public ChatColor getWilderness() {
                this.wildernessColor = Colors.this.getColor(this.wilderness, this.wildernessColor, ChatColor.GRAY);
                return this.wildernessColor;
            }

            public ChatColor getSafezone() {
                this.safezoneColor = Colors.this.getColor(this.safezone, this.safezoneColor, ChatColor.GOLD);
                return this.safezoneColor;
            }

            public ChatColor getWarzone() {
                this.warzoneColor = Colors.this.getColor(this.warzone, this.warzoneColor, ChatColor.DARK_RED);
                return this.warzoneColor;
            }
        }

        public class Relations {
            private String member = "GREEN";
            private transient ChatColor memberColor;
            private String ally = "LIGHT_PURPLE";
            private transient ChatColor allyColor;
            private String truce = "DARK_PURPLE";
            private transient ChatColor truceColor;
            private String neutral = "WHITE";
            private transient ChatColor neutralColor;
            private String enemy = "RED";
            private transient ChatColor enemyColor;
            private String peaceful = "GOLD";
            private transient ChatColor peacefulColor;

            public ChatColor getMember() {
                this.memberColor = Colors.this.getColor(this.member, this.memberColor, ChatColor.GREEN);
                return this.memberColor;
            }

            public ChatColor getAlly() {
                this.allyColor = Colors.this.getColor(this.ally, this.allyColor, ChatColor.LIGHT_PURPLE);
                return this.allyColor;
            }

            public ChatColor getTruce() {
                this.truceColor = Colors.this.getColor(this.truce, this.truceColor, ChatColor.DARK_PURPLE);
                return this.truceColor;
            }

            public ChatColor getNeutral() {
                this.neutralColor = Colors.this.getColor(this.neutral, this.neutralColor, ChatColor.WHITE);
                return this.neutralColor;
            }

            public ChatColor getEnemy() {
                this.enemyColor = Colors.this.getColor(this.enemy, this.enemyColor, ChatColor.RED);
                return this.enemyColor;
            }

            public ChatColor getPeaceful() {
                this.peacefulColor = Colors.this.getColor(this.peaceful, this.peacefulColor, ChatColor.GOLD);
                return this.peacefulColor;
            }
        }
    }

    public class Commands {
        private Fly fly = new Fly(this);
        private Help help = new Help(this);
        private Home home = new Home(this);
        private ListCmd list = new ListCmd(this);
        private MapCmd map = new MapCmd(this);
        private Near near = new Near(this);
        private SeeChunk seeChunk = new SeeChunk(this);
        private Show show = new Show(this);
        private Stuck stuck = new Stuck(this);
        private ToolTips toolTips = new ToolTips(this);
        private Warp warp = new Warp(this);

        public Commands(TransitionConfigV1 transitionConfigV1) {
        }

        public Fly fly() {
            return this.fly;
        }

        public Help help() {
            return this.help;
        }

        public Home home() {
            return this.home;
        }

        public ListCmd list() {
            return this.list;
        }

        public MapCmd map() {
            return this.map;
        }

        public Near near() {
            return this.near;
        }

        public SeeChunk seeChunk() {
            return this.seeChunk;
        }

        public Show show() {
            return this.show;
        }

        public Stuck stuck() {
            return this.stuck;
        }

        public ToolTips toolTips() {
            return this.toolTips;
        }

        public Warp warp() {
            return this.warp;
        }

        public class Fly {
            @Comment(value="Warmup seconds before command executes. Set to 0 for no warmup.")
            private int delay = 0;
            @Comment(value="True to enable the fly command, false to disable")
            private boolean enable = true;
            @Comment(value="If a player leaves fly (out of territory or took damage)\nhow long (in seconds) should they not take fall damage for?\nSet to 0 to have them always take fall damage.")
            private int fallDamageCooldown = 3;
            @Comment(value="From how far away a player can disable another's flight by being enemy\nSet to 0 if wanted disable\nNote: Will produce lag at higher numbers")
            private int enemyRadius = 7;
            @Comment(value="How frequently to check enemy radius, in seconds. Set to 0 to disable checking.")
            private int radiusCheck = 1;
            @Comment(value="Should we disable flight if the player has suffered generic damage")
            private boolean disableOnGenericDamage = false;
            @Comment(value="Trails show below the players foot when flying, faction.fly.trails\nPlayers can enable them with /f trail on/off\nPlayers can also set which effect to show /f trail effect <particle> only if they have faction.fly.trails.<particle>")
            private Particles particles = new Particles(this);

            public Fly(Commands commands) {
            }

            public int getDelay() {
                return this.delay;
            }

            public boolean isEnable() {
                return this.enable;
            }

            public int getFallDamageCooldown() {
                return this.fallDamageCooldown;
            }

            public int getEnemyRadius() {
                return this.enemyRadius;
            }

            public int getRadiusCheck() {
                return this.radiusCheck;
            }

            public boolean isDisableOnGenericDamage() {
                return this.disableOnGenericDamage;
            }

            public Particles particles() {
                return this.particles;
            }

            public class Particles {
                @Comment(value="Speed of the particles, can be decimal value")
                private double speed = 0.02;
                @Comment(value="Amount spawned")
                private int amount = 20;
                @Comment(value="How often should we spawn these particles?\n0 disables this completely")
                private double spawnRate = 0.2;

                public Particles(Fly fly) {
                }

                public double getSpeed() {
                    return this.speed;
                }

                public int getAmount() {
                    return this.amount;
                }

                public double getSpawnRate() {
                    return this.spawnRate;
                }
            }
        }

        public class Help {
            @Comment(value="You can change the page name to whatever you like\nWe use '1' to preserve default functionality of /f help 1")
            private Map<String, List<String>> entries = new HashMap<String, List<String>>(){
                {
                    this.put("1", Arrays.asList("&e&m----------------------------------------------", "                  &c&lFactions Help               ", "&e&m----------------------------------------------", "&3/f create  &e>>  &7Create your own faction", "&3/f who      &e>>  &7Show factions info", "&3/f tag      &e>>  &7Change faction tag", "&3/f join     &e>>  &7Join faction", "&3/f list      &e>>  &7List all factions", "&e&m--------------&r &2/f help 2 for more &e&m--------------"));
                    this.put("2", Arrays.asList("&e&m------------------&r&c&l Page 2 &e&m--------------------", "&3/f home     &e>>  &7Teleport to faction home", "&3/f sethome &e>>  &7Set your faction home", "&3/f leave    &e>>  &7Leave your faction", "&3/f invite    &e>>  &7Invite a player to your faction", "&3/f deinvite &e>>  &7Revoke invitation to player", "&e&m--------------&r &2/f help 3 for more &e&m--------------"));
                    this.put("3", Arrays.asList("&e&m------------------&r&c&l Page 3 &e&m--------------------", "&3/f claim     &e>>  &7Claim land", "&3/f unclaim  &e>>  &7Unclaim land", "&3/f kick      &e>>  &7Kick player from your faction", "&3/f mod      &e>>  &7Set player role in faction", "&3/f chat     &e>>  &7Switch to faction chat", "&e&m--------------&r &2/f help 4 for more &e&m--------------"));
                    this.put("4", Arrays.asList("&e&m------------------&r&c&l Page 4 &e&m--------------------", "&3/f version &e>>  &7Display version information", "&e&m--------------&r&2 End of /f help &e&m-----------------"));
                }
            };
            @Comment(value="set to true to use legacy factions help")
            private boolean useOldHelp = true;

            public Help(Commands commands) {
            }

            public Map<String, List<String>> getEntries() {
                return this.entries != null ? this.entries : Collections.emptyMap();
            }

            public boolean isUseOldHelp() {
                return this.useOldHelp;
            }
        }

        public class Home {
            @Comment(value="Warmup seconds before command executes. Set to 0 for no warmup.")
            private int delay = 0;

            public Home(Commands commands) {
            }

            public int getDelay() {
                return this.delay;
            }
        }

        public class ListCmd {
            @Comment(value="You can only use {pagenumber} and {pagecount} in the header")
            private String header = "&e&m----------&r&e[ &2Faction List &9{pagenumber}&e/&9{pagecount} &e]&m----------";
            @Comment(value="You can use any variables here")
            private String factionlessEntry = "<i>Factionless<i> {factionless} online";
            @Comment(value="You can use any variable here")
            private String entry = "<a>{faction-relation-color}{faction} <i>{online} / {members} online, <a>Land / Power / Maxpower: <i>{chunks}/{power}/{maxPower}";

            public ListCmd(Commands commands) {
            }

            public String getHeader() {
                return this.header;
            }

            public String getFactionlessEntry() {
                return this.factionlessEntry;
            }

            public String getEntry() {
                return this.entry;
            }
        }

        public class MapCmd {
            @Comment(value="This will help limit how many times a player can be sent a map of factions.\nSet this to the cooldown you want, in milliseconds, for a map to be shown to a player.\nThis can prevent some teleportation-based exploits for finding factions.\nThe old default was 2000, which blocks any movement faster than running.\nThe new default is 700, which should also allow boats and horses.")
            private int cooldown = 700;

            public MapCmd(Commands commands) {
            }

            public int getCooldown() {
                return this.cooldown;
            }
        }

        public class Near {
            @Comment(value="Making this radius larger increases lag, do so at your own risk\nIf on a high radius it is advised to add a cooldown to the command\nAlso using {distance} placeholder in the lang would cause more lag on a bigger radius")
            private int radius = 20;

            public Near(Commands commands) {
            }

            public int getRadius() {
                return this.radius;
            }
        }

        public class SeeChunk {
            private boolean particles = true;
            @Comment(value="Get a list of particle names here: https://factions.support/particles/")
            private String particleName = "REDSTONE";
            @Comment(value="If the chosen particle is compatible with coloring we will color\nit based on the current chunk's faction")
            private boolean relationalColor = true;
            @Comment(value="How often should we update the particles to the current player's location?")
            private double particleUpdateTime = 0.75;

            public SeeChunk(Commands commands) {
            }

            public boolean isParticles() {
                return this.particles;
            }

            public String getParticleName() {
                return this.particleName;
            }

            public boolean isRelationalColor() {
                return this.relationalColor;
            }

            public double getParticleUpdateTime() {
                return this.particleUpdateTime;
            }
        }

        public class Show {
            @Comment(value="You can use any variable here, including fancy messages. Color codes and or tags work fine.\nLines that aren't defined wont be sent (home not set, faction not peaceful / permanent, dtr freeze)\nSupports placeholders.\nFirst line can be {header} for default header, or any string (we recommend &m for smooth lines ;p)\nThe line with 'permanent' in it only appears if the faction is permanent.")
            private List<String> format = new ArrayList<String>(){
                {
                    this.add("{header}");
                    this.add("<a>Description: <i>{description}");
                    this.add("<a>Joining: <i>{joining}    {peaceful}");
                    this.add("<a>Land / Power / Maxpower: <i> {chunks}/{power}/{maxPower}");
                    this.add("<a>Raidable: {raidable}");
                    this.add("<a>Founded: <i>{create-date}");
                    this.add("<a>This faction is permanent, remaining even with no members.'");
                    this.add("<a>Land value: <i>{land-value} {land-refund}");
                    this.add("<a>Balance: <i>{faction-balance}");
                    this.add("<a>Bans: <i>{faction-bancount}");
                    this.add("<a>Allies(<i>{allies}<a>/<i>{max-allies}<a>): {allies-list} ");
                    this.add("<a>Online: (<i>{online}<a>/<i>{members}<a>): {online-list}");
                    this.add("<a>Offline: (<i>{offline}<a>/<i>{members}<a>): {offline-list}");
                }
            };
            @Comment(value="Set true to not display empty fancy messages")
            private boolean minimal = false;
            @Comment(value="Factions that should be exempt from /f show, case sensitive, useful for a\nserverteam faction, since the command shows vanished players otherwise")
            private List<String> exempt = new ArrayList<String>(){
                {
                    this.add("put_faction_tag_here");
                }
            };

            public Show(Commands commands) {
            }

            public List<String> getFormat() {
                return this.format != null ? this.format : Collections.emptyList();
            }

            public boolean isMinimal() {
                return this.minimal;
            }

            public List<String> getExempt() {
                return this.exempt != null ? this.exempt : Collections.emptyList();
            }
        }

        public class Stuck {
            @Comment(value="Warmup seconds before command executes. Set to 0 for no warmup.")
            private int delay = 30;
            @Comment(value="This radius defines how far from where they ran the command the player\nmay travel while waiting to be unstuck. If they leave this radius, the\ncommand will be cancelled.")
            private int radius = 10;

            public Stuck(Commands commands) {
            }

            public int getDelay() {
                return this.delay;
            }

            public int getRadius() {
                return this.radius;
            }
        }

        public class ToolTips {
            @Comment(value="Faction on-hover tooltip information")
            private List<String> faction = new ArrayList<String>(){
                {
                    this.add("&6Leader: &f{leader}");
                    this.add("&6Claimed: &f{chunks}");
                    this.add("&6Raidable: &f{raidable}");
                    this.add("&6Warps: &f{warps}");
                    this.add("&6Power: &f{power}/{maxPower}");
                    this.add("&6Members: &f{online}/{members}");
                }
            };
            @Comment(value="Player on-hover tooltip information")
            private List<String> player = new ArrayList<String>(){
                {
                    this.add("&6Last Seen: &f{lastSeen}");
                    this.add("&6Power: &f{power}");
                    this.add("&6Rank: &f{group}");
                    this.add("&6Balance: &a${balance}");
                }
            };

            public ToolTips(Commands commands) {
            }

            public List<String> getFaction() {
                return this.faction != null ? this.faction : Collections.emptyList();
            }

            public List<String> getPlayer() {
                return this.player != null ? this.player : Collections.emptyList();
            }
        }

        public class Warp {
            @Comment(value="Warmup seconds before command executes. Set to 0 for no warmup.")
            private int delay = 0;
            @Comment(value="What should be the maximum amount of warps that a Faction can set?")
            private int maxWarps = 5;

            public Warp(Commands commands) {
            }

            public int getDelay() {
                return this.delay;
            }

            public int getMaxWarps() {
                return this.maxWarps;
            }
        }
    }

    public class Factions {
        private Chat chat = new Chat(this);
        private Homes homes = new Homes(this);
        @Comment(value="Limits factions to having a max number of each relation.\nSetting to 0 means none allowed. -1 for disabled.\nThis will have no effect on default or existing relations, only when relations are changed.\nIt is advised that you set the default relation to -1 so they can always go back to that.\nOtherwise Factions could be stuck with not being able to unenemy other Factions.")
        private MaxRelations maxRelations = new MaxRelations(this);
        private PVP pvp = new PVP(this);
        private SpecialCase specialCase = new SpecialCase(this);
        private Claims claims = new Claims(this);
        @Comment(value="Do you want to limit portal creation?")
        private Portals portals = new Portals(this);
        private Protection protection = new Protection(this);
        @Comment(value="For claimed areas where further faction-member ownership can be defined")
        private OwnedArea ownedArea = new OwnedArea(this);
        @Comment(value="Displayed prefixes for different roles within a faction")
        private Prefix prefixes = new Prefix(this);
        private LandRaidControl landRaidControl = new LandRaidControl(this);
        @Comment(value="Remaining settings not categorized")
        private Other other = new Other(this);
        @Comment(value="Should we send titles when players enter Factions?")
        private EnterTitles enterTitles = new EnterTitles(this);

        public Factions(TransitionConfigV1 transitionConfigV1) {
        }

        public EnterTitles enterTitles() {
            return this.enterTitles;
        }

        public Chat chat() {
            return this.chat;
        }

        public Homes homes() {
            return this.homes;
        }

        public MaxRelations maxRelations() {
            return this.maxRelations;
        }

        public PVP pvp() {
            return this.pvp;
        }

        public SpecialCase specialCase() {
            return this.specialCase;
        }

        public Claims claims() {
            return this.claims;
        }

        public Portals portals() {
            return this.portals;
        }

        public Protection protection() {
            return this.protection;
        }

        public Other other() {
            return this.other;
        }

        public OwnedArea ownedArea() {
            return this.ownedArea;
        }

        public Prefix prefixes() {
            return this.prefixes;
        }

        public LandRaidControl landRaidControl() {
            return this.landRaidControl;
        }

        public class Chat {
            @Comment(value="Allow for players to chat only within their faction, with allies, etc.\nSet to false to only allow public chats through this plugin.")
            private boolean factionOnlyChat = true;
            @Comment(value="If true, disables adding of faction tag so another plugin can manage this")
            private boolean tagHandledByAnotherPlugin = false;
            private boolean tagRelationColored = true;
            private String tagReplaceString = "[FACTION]";
            private String tagInsertAfterString = "";
            private String tagInsertBeforeString = "";
            private int tagInsertIndex = 0;
            private boolean tagPadBefore = false;
            private boolean tagPadAfter = true;
            private String tagFormat = "%s\u00a7f";
            private boolean alwaysShowChatTag = true;
            private String factionChatFormat = "%s:\u00a7f %s";
            private String allianceChatFormat = "\u00a7d%s:\u00a7f %s";
            private String truceChatFormat = "\u00a75%s:\u00a7f %s";
            private String modChatFormat = "\u00a7c%s:\u00a7f %s";
            private boolean broadcastDescriptionChanges = false;
            private boolean broadcastTagChanges = false;

            public Chat(Factions factions) {
            }

            public boolean isFactionOnlyChat() {
                return this.factionOnlyChat;
            }

            public boolean isTagHandledByAnotherPlugin() {
                return this.tagHandledByAnotherPlugin;
            }

            public boolean isTagRelationColored() {
                return this.tagRelationColored;
            }

            public String getTagReplaceString() {
                return this.tagReplaceString;
            }

            public String getTagInsertAfterString() {
                return this.tagInsertAfterString;
            }

            public String getTagInsertBeforeString() {
                return this.tagInsertBeforeString;
            }

            public int getTagInsertIndex() {
                return this.tagInsertIndex;
            }

            public boolean isTagPadBefore() {
                return this.tagPadBefore;
            }

            public boolean isTagPadAfter() {
                return this.tagPadAfter;
            }

            public String getTagFormat() {
                return this.tagFormat;
            }

            public boolean isAlwaysShowChatTag() {
                return this.alwaysShowChatTag;
            }

            public String getFactionChatFormat() {
                return this.factionChatFormat;
            }

            public String getAllianceChatFormat() {
                return this.allianceChatFormat;
            }

            public String getTruceChatFormat() {
                return this.truceChatFormat;
            }

            public String getModChatFormat() {
                return this.modChatFormat;
            }

            public boolean isBroadcastDescriptionChanges() {
                return this.broadcastDescriptionChanges;
            }

            public boolean isBroadcastTagChanges() {
                return this.broadcastTagChanges;
            }
        }

        public class Homes {
            private boolean enabled = true;
            private boolean mustBeInClaimedTerritory = true;
            private boolean teleportToOnDeath = true;
            private boolean teleportCommandEnabled = true;
            private boolean teleportCommandEssentialsIntegration = true;
            private boolean teleportCommandSmokeEffectEnabled = true;
            private float teleportCommandSmokeEffectThickness = 3.0f;
            private boolean teleportAllowedFromEnemyTerritory = true;
            private boolean teleportAllowedFromDifferentWorld = true;
            private double teleportAllowedEnemyDistance = 32.0;
            private boolean teleportIgnoreEnemiesIfInOwnTerritory = true;

            public Homes(Factions factions) {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public boolean isMustBeInClaimedTerritory() {
                return this.mustBeInClaimedTerritory;
            }

            public boolean isTeleportToOnDeath() {
                return this.teleportToOnDeath;
            }

            public boolean isTeleportCommandEnabled() {
                return this.teleportCommandEnabled;
            }

            public boolean isTeleportCommandEssentialsIntegration() {
                return this.teleportCommandEssentialsIntegration;
            }

            public boolean isTeleportCommandSmokeEffectEnabled() {
                return this.teleportCommandSmokeEffectEnabled;
            }

            public float getTeleportCommandSmokeEffectThickness() {
                return this.teleportCommandSmokeEffectThickness;
            }

            public boolean isTeleportAllowedFromEnemyTerritory() {
                return this.teleportAllowedFromEnemyTerritory;
            }

            public boolean isTeleportAllowedFromDifferentWorld() {
                return this.teleportAllowedFromDifferentWorld;
            }

            public double getTeleportAllowedEnemyDistance() {
                return this.teleportAllowedEnemyDistance;
            }

            public boolean isTeleportIgnoreEnemiesIfInOwnTerritory() {
                return this.teleportIgnoreEnemiesIfInOwnTerritory;
            }
        }

        public class MaxRelations {
            private boolean enabled = false;
            private int ally = 10;
            private int truce = 10;
            private int neutral = -1;
            private int enemy = 10;

            public MaxRelations(Factions factions) {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public int getAlly() {
                return this.ally;
            }

            public int getTruce() {
                return this.truce;
            }

            public int getNeutral() {
                return this.neutral;
            }

            public int getEnemy() {
                return this.enemy;
            }
        }

        public class PVP {
            private boolean disablePVPBetweenNeutralFactions = false;
            private boolean disablePVPForFactionlessPlayers = false;
            private boolean enablePVPAgainstFactionlessInAttackersLand = false;
            private int noPVPDamageToOthersForXSecondsAfterLogin = 3;
            private Set<String> worldsIgnorePvP = new HashSet<String>();

            public PVP(Factions factions) {
            }

            public boolean isDisablePVPBetweenNeutralFactions() {
                return this.disablePVPBetweenNeutralFactions;
            }

            public boolean isDisablePVPForFactionlessPlayers() {
                return this.disablePVPForFactionlessPlayers;
            }

            public boolean isEnablePVPAgainstFactionlessInAttackersLand() {
                return this.enablePVPAgainstFactionlessInAttackersLand;
            }

            public int getNoPVPDamageToOthersForXSecondsAfterLogin() {
                return this.noPVPDamageToOthersForXSecondsAfterLogin;
            }

            public Set<String> getWorldsIgnorePvP() {
                return this.worldsIgnorePvP == null ? Collections.emptySet() : this.worldsIgnorePvP;
            }
        }

        public class SpecialCase {
            private boolean peacefulTerritoryDisablePVP = true;
            private boolean peacefulTerritoryDisableMonsters = false;
            private boolean peacefulTerritoryDisableBoom = false;
            private boolean permanentFactionsDisableLeaderPromotion = false;

            public SpecialCase(Factions factions) {
            }

            public boolean isPeacefulTerritoryDisablePVP() {
                return this.peacefulTerritoryDisablePVP;
            }

            public boolean isPeacefulTerritoryDisableMonsters() {
                return this.peacefulTerritoryDisableMonsters;
            }

            public boolean isPeacefulTerritoryDisableBoom() {
                return this.peacefulTerritoryDisableBoom;
            }

            public boolean isPermanentFactionsDisableLeaderPromotion() {
                return this.permanentFactionsDisableLeaderPromotion;
            }
        }

        public class Claims {
            private boolean mustBeConnected = false;
            private boolean canBeUnconnectedIfOwnedByOtherFaction = true;
            private int requireMinFactionMembers = 1;
            private int landsMax = 0;
            private int lineClaimLimit = 5;
            @Comment(value="If someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit")
            private int radiusClaimFailureLimit = 9;
            private Set<String> worldsNoClaiming = new HashSet<String>();
            @Comment(value="Buffer Zone is an chunk area required between claims of different Factions.\nThis is default to 0 and has always been that way. Meaning Factions can have\n  claims that border each other.\nIf this is set to 3, then Factions need to have 3 chunks between their claim\n  and another Faction's claim.\nIt's recommended to keep this pretty low as the radius check could be a\n  heavy operation if set to a large number.\nIf this is set to 0, we won't even bother checking which is how Factions has\n  always been.")
            private int bufferZone = 0;
            @Comment(value="Should we allow Factions to over claim if they are raidable?\nThis has always been true, allowing factions to over claim others.")
            private boolean allowOverClaim = true;

            public Claims(Factions factions) {
            }

            public boolean isAllowOverClaim() {
                return this.allowOverClaim;
            }

            public int getBufferZone() {
                return this.bufferZone;
            }

            public boolean isMustBeConnected() {
                return this.mustBeConnected;
            }

            public boolean isCanBeUnconnectedIfOwnedByOtherFaction() {
                return this.canBeUnconnectedIfOwnedByOtherFaction;
            }

            public int getRequireMinFactionMembers() {
                return this.requireMinFactionMembers;
            }

            public int getLandsMax() {
                return this.landsMax;
            }

            public int getLineClaimLimit() {
                return this.lineClaimLimit;
            }

            public int getRadiusClaimFailureLimit() {
                return this.radiusClaimFailureLimit;
            }

            public Set<String> getWorldsNoClaiming() {
                return this.worldsNoClaiming == null ? Collections.emptySet() : this.worldsNoClaiming;
            }
        }

        public class Portals {
            @Comment(value="If true, portals will be limited to the minimum relation below")
            private boolean limit = false;
            @Comment(value="What should the minimum relation be to create a portal in territory?\nGoes in the order of: ENEMY, NEUTRAL, ALLY, MEMBER.\nMinimum relation allows that and all listed to the right to create portals.\nExample: put ALLY to allow ALLY and MEMBER to be able to create portals.\nIf typed incorrectly, defaults to NEUTRAL.")
            private String minimumRelation = "MEMBER";

            public Portals(Factions factions) {
            }

            public boolean isLimit() {
                return this.limit;
            }

            public String getMinimumRelation() {
                return this.minimumRelation;
            }
        }

        public class Protection {
            @Comment(value="Commands which will be prevented if the player is a member of a permanent faction")
            private Set<String> permanentFactionMemberDenyCommands = new HashSet<String>();
            @Comment(value="Commands which will be prevented when in claimed territory of a neutral faction")
            private Set<String> territoryNeutralDenyCommands = new HashSet<String>();
            @Comment(value="Commands which will be prevented when in claimed territory of an enemy faction")
            private Set<String> territoryEnemyDenyCommands = new HashSet<String>(){
                {
                    this.add("home");
                    this.add("sethome");
                    this.add("spawn");
                    this.add("tpahere");
                    this.add("tpaccept");
                    this.add("tpa");
                }
            };
            @Comment(value="Commands which will be prevented when in claimed territory of an ally faction")
            private Set<String> territoryAllyDenyCommands = new HashSet<String>();
            @Comment(value="Commands which will be prevented when in warzone")
            private Set<String> warzoneDenyCommands = new HashSet<String>();
            @Comment(value="Commands which will be prevented when in wilderness")
            private Set<String> wildernessDenyCommands = new HashSet<String>();
            private boolean territoryBlockCreepers = false;
            private boolean territoryBlockCreepersWhenOffline = false;
            private boolean territoryBlockFireballs = false;
            private boolean territoryBlockFireballsWhenOffline = false;
            private boolean territoryBlockTNT = false;
            private boolean territoryBlockTNTWhenOffline = false;
            private boolean territoryDenyEndermanBlocks = true;
            private boolean territoryDenyEndermanBlocksWhenOffline = true;
            private boolean safeZoneDenyBuild = true;
            private boolean safeZoneDenyUsage = true;
            private boolean safeZoneBlockTNT = true;
            private boolean safeZonePreventAllDamageToPlayers = false;
            private boolean safeZoneDenyEndermanBlocks = true;
            private boolean warZoneDenyBuild = true;
            private boolean warZoneDenyUsage = true;
            private boolean warZoneBlockCreepers = true;
            private boolean warZoneBlockFireballs = true;
            private boolean warZoneBlockTNT = true;
            private boolean warZoneFriendlyFire = false;
            private boolean warZoneDenyEndermanBlocks = true;
            private boolean wildernessDenyBuild = false;
            private boolean wildernessDenyUsage = false;
            private boolean wildernessBlockCreepers = false;
            private boolean wildernessBlockFireballs = false;
            private boolean wildernessBlockTNT = false;
            private boolean wildernessDenyEndermanBlocks = false;
            private boolean pistonProtectionThroughDenyBuild = true;
            private Set<String> territoryDenyUsageMaterials = new HashSet<String>();
            private Set<String> territoryDenyUsageMaterialsWhenOffline = new HashSet<String>();
            private transient Set<Material> territoryDenyUsageMaterialsMat;
            private transient Set<Material> territoryDenyUsageMaterialsWhenOfflineMat;
            @Comment(value="Mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections")
            private Set<String> playersWhoBypassAllProtection = new HashSet<String>();
            private Set<String> worldsNoWildernessProtection = new HashSet<String>();

            private Protection(Factions factions) {
                this.protectUsage("FIRE_CHARGE");
                this.protectUsage("FLINT_AND_STEEL");
                this.protectUsage("BUCKET");
                this.protectUsage("WATER_BUCKET");
                this.protectUsage("LAVA_BUCKET");
            }

            private void protectUsage(String string) {
                this.territoryDenyUsageMaterials.add(string);
                this.territoryDenyUsageMaterialsWhenOffline.add(string);
            }

            public Set<String> getPermanentFactionMemberDenyCommands() {
                return this.permanentFactionMemberDenyCommands == null ? Collections.emptySet() : this.permanentFactionMemberDenyCommands;
            }

            public Set<String> getTerritoryNeutralDenyCommands() {
                return this.territoryNeutralDenyCommands == null ? Collections.emptySet() : this.territoryNeutralDenyCommands;
            }

            public Set<String> getTerritoryEnemyDenyCommands() {
                return this.territoryEnemyDenyCommands == null ? Collections.emptySet() : this.territoryEnemyDenyCommands;
            }

            public Set<String> getTerritoryAllyDenyCommands() {
                return this.territoryAllyDenyCommands == null ? Collections.emptySet() : this.territoryAllyDenyCommands;
            }

            public Set<String> getWarzoneDenyCommands() {
                return this.warzoneDenyCommands == null ? Collections.emptySet() : this.warzoneDenyCommands;
            }

            public Set<String> getWildernessDenyCommands() {
                return this.wildernessDenyCommands == null ? Collections.emptySet() : this.wildernessDenyCommands;
            }

            public boolean isTerritoryBlockCreepers() {
                return this.territoryBlockCreepers;
            }

            public boolean isTerritoryBlockCreepersWhenOffline() {
                return this.territoryBlockCreepersWhenOffline;
            }

            public boolean isTerritoryBlockFireballs() {
                return this.territoryBlockFireballs;
            }

            public boolean isTerritoryBlockFireballsWhenOffline() {
                return this.territoryBlockFireballsWhenOffline;
            }

            public boolean isTerritoryBlockTNT() {
                return this.territoryBlockTNT;
            }

            public boolean isTerritoryBlockTNTWhenOffline() {
                return this.territoryBlockTNTWhenOffline;
            }

            public boolean isTerritoryDenyEndermanBlocks() {
                return this.territoryDenyEndermanBlocks;
            }

            public boolean isTerritoryDenyEndermanBlocksWhenOffline() {
                return this.territoryDenyEndermanBlocksWhenOffline;
            }

            public boolean isSafeZoneDenyBuild() {
                return this.safeZoneDenyBuild;
            }

            public boolean isSafeZoneDenyUsage() {
                return this.safeZoneDenyUsage;
            }

            public boolean isSafeZoneBlockTNT() {
                return this.safeZoneBlockTNT;
            }

            public boolean isSafeZonePreventAllDamageToPlayers() {
                return this.safeZonePreventAllDamageToPlayers;
            }

            public boolean isSafeZoneDenyEndermanBlocks() {
                return this.safeZoneDenyEndermanBlocks;
            }

            public boolean isWarZoneDenyBuild() {
                return this.warZoneDenyBuild;
            }

            public boolean isWarZoneDenyUsage() {
                return this.warZoneDenyUsage;
            }

            public boolean isWarZoneBlockCreepers() {
                return this.warZoneBlockCreepers;
            }

            public boolean isWarZoneBlockFireballs() {
                return this.warZoneBlockFireballs;
            }

            public boolean isWarZoneBlockTNT() {
                return this.warZoneBlockTNT;
            }

            public boolean isWarZoneFriendlyFire() {
                return this.warZoneFriendlyFire;
            }

            public boolean isWarZoneDenyEndermanBlocks() {
                return this.warZoneDenyEndermanBlocks;
            }

            public boolean isWildernessDenyBuild() {
                return this.wildernessDenyBuild;
            }

            public boolean isWildernessDenyUsage() {
                return this.wildernessDenyUsage;
            }

            public boolean isWildernessBlockCreepers() {
                return this.wildernessBlockCreepers;
            }

            public boolean isWildernessBlockFireballs() {
                return this.wildernessBlockFireballs;
            }

            public boolean isWildernessBlockTNT() {
                return this.wildernessBlockTNT;
            }

            public boolean isWildernessDenyEndermanBlocks() {
                return this.wildernessDenyEndermanBlocks;
            }

            public boolean isPistonProtectionThroughDenyBuild() {
                return this.pistonProtectionThroughDenyBuild;
            }

            public Set<Material> getTerritoryDenyUsageMaterials() {
                if (this.territoryDenyUsageMaterialsMat == null) {
                    this.territoryDenyUsageMaterialsMat = new HashSet<Material>();
                    this.territoryDenyUsageMaterials.forEach(string -> this.territoryDenyUsageMaterialsMat.add(MaterialDb.get(string)));
                    this.territoryDenyUsageMaterialsMat.remove(Material.AIR);
                }
                return this.territoryDenyUsageMaterialsMat;
            }

            public Set<Material> getTerritoryDenyUsageMaterialsWhenOffline() {
                if (this.territoryDenyUsageMaterialsWhenOfflineMat == null) {
                    this.territoryDenyUsageMaterialsWhenOfflineMat = new HashSet<Material>();
                    this.territoryDenyUsageMaterialsWhenOffline.forEach(string -> this.territoryDenyUsageMaterialsWhenOfflineMat.add(MaterialDb.get(string)));
                    this.territoryDenyUsageMaterialsWhenOfflineMat.remove(Material.AIR);
                }
                return this.territoryDenyUsageMaterialsWhenOfflineMat;
            }

            public Set<String> getPlayersWhoBypassAllProtection() {
                return this.playersWhoBypassAllProtection == null ? Collections.emptySet() : this.playersWhoBypassAllProtection;
            }

            public Set<String> getWorldsNoWildernessProtection() {
                return this.worldsNoWildernessProtection == null ? Collections.emptySet() : this.worldsNoWildernessProtection;
            }
        }

        public class OwnedArea {
            private boolean enabled = true;
            private int limitPerFaction = 0;
            private boolean moderatorsBypass = true;
            private boolean denyBuild = true;
            private boolean painBuild = false;
            private boolean protectMaterials = true;
            private boolean denyUsage = true;
            private boolean messageOnBorder = true;
            private boolean messageInsideTerritory = true;
            private boolean messageByChunk = false;

            public OwnedArea(Factions factions) {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public int getLimitPerFaction() {
                return this.limitPerFaction;
            }

            public boolean isModeratorsBypass() {
                return this.moderatorsBypass;
            }

            public boolean isDenyBuild() {
                return this.denyBuild;
            }

            public boolean isPainBuild() {
                return this.painBuild;
            }

            public boolean isProtectMaterials() {
                return this.protectMaterials;
            }

            public boolean isDenyUsage() {
                return this.denyUsage;
            }

            public boolean isMessageOnBorder() {
                return this.messageOnBorder;
            }

            public boolean isMessageInsideTerritory() {
                return this.messageInsideTerritory;
            }

            public boolean isMessageByChunk() {
                return this.messageByChunk;
            }
        }

        public class Prefix {
            private String admin = "***";
            private String coleader = "**";
            private String mod = "*";
            private String normal = "+";
            private String recruit = "-";

            public Prefix(Factions factions) {
            }

            public String getAdmin() {
                return this.admin;
            }

            public String getColeader() {
                return this.coleader;
            }

            public String getMod() {
                return this.mod;
            }

            public String getNormal() {
                return this.normal;
            }

            public String getRecruit() {
                return this.recruit;
            }
        }

        public class LandRaidControl {
            @Comment(value="Sets the mode of land/raid control")
            private String system = "power";
            @Comment(value="Controls the power system of land/raid control\nSet the 'system' value to 'power' to use this system")
            private Power power = new Power(this);

            public LandRaidControl(Factions factions) {
            }

            public String getSystem() {
                return this.system;
            }

            public Power power() {
                return this.power;
            }

            public class Power {
                private double playerMin = -10.0;
                private double playerMax = 10.0;
                private double playerStarting = 0.0;
                @Comment(value="Default health rate of 0.2 takes 5 minutes to recover one power")
                private double powerPerMinute = 0.2;
                @Comment(value="How much is lost on death")
                private double lossPerDeath = 4.0;
                @Comment(value="Does a player regenerate power while offline?")
                private boolean regenOffline = false;
                @Comment(value="A player loses this much per day offline")
                private double offlineLossPerDay = 0.0;
                @Comment(value="A player stops losing power from being offline once they reach this amount")
                private double offlineLossLimit = 0.0;
                @Comment(value="If greater than 0, used as a cap for how much power a faction can have\nAdditional power from players beyond this acts as a \"buffer\" of sorts")
                private double factionMax = 0.0;
                private boolean respawnHomeFromNoPowerLossWorlds = true;
                private Set<String> worldsNoPowerLoss = new HashSet<String>();
                private boolean peacefulMembersDisablePowerLoss = true;
                private boolean warZonePowerLoss = true;
                private boolean wildernessPowerLoss = true;
                @Comment(value="Disallow joining/leaving/kicking while power is negative")
                private boolean canLeaveWithNegativePower = true;
                @Comment(value="Allow a faction to be raided if they have more land than power.\nThis will make claimed territory lose all protections\n  allowing factions to open chests, break blocks, etc. if they\n  have claimed chunks >= power.")
                private boolean raidability = false;
                @Comment(value="After a player dies, how long should the faction not be able to regen power?\nThis resets on each death but does not accumulate.\nSet to 0 for no freeze. Time is in seconds.")
                private int powerFreeze = 0;

                public Power(LandRaidControl landRaidControl) {
                }

                public boolean isRaidability() {
                    return this.raidability;
                }

                public int getPowerFreeze() {
                    return this.powerFreeze;
                }

                public boolean canLeaveWithNegativePower() {
                    return this.canLeaveWithNegativePower;
                }

                public boolean isWarZonePowerLoss() {
                    return this.warZonePowerLoss;
                }

                public boolean isWildernessPowerLoss() {
                    return this.wildernessPowerLoss;
                }

                public double getPlayerMin() {
                    return this.playerMin;
                }

                public double getPlayerMax() {
                    return this.playerMax;
                }

                public double getPlayerStarting() {
                    return this.playerStarting;
                }

                public double getPowerPerMinute() {
                    return this.powerPerMinute;
                }

                public double getLossPerDeath() {
                    return this.lossPerDeath;
                }

                public boolean isRegenOffline() {
                    return this.regenOffline;
                }

                public double getOfflineLossPerDay() {
                    return this.offlineLossPerDay;
                }

                public double getOfflineLossLimit() {
                    return this.offlineLossLimit;
                }

                public double getFactionMax() {
                    return this.factionMax;
                }

                public boolean isRespawnHomeFromNoPowerLossWorlds() {
                    return this.respawnHomeFromNoPowerLossWorlds;
                }

                public Set<String> getWorldsNoPowerLoss() {
                    return this.worldsNoPowerLoss == null ? Collections.emptySet() : this.worldsNoPowerLoss;
                }

                public boolean isPeacefulMembersDisablePowerLoss() {
                    return this.peacefulMembersDisablePowerLoss;
                }
            }
        }

        public class Other {
            private boolean allowMultipleColeaders = false;
            @Comment(value="Minimum faction tag length")
            private int tagLengthMin = 3;
            @Comment(value="Maximum faction tag length")
            private int tagLengthMax = 10;
            private boolean tagForceUpperCase = false;
            private boolean newFactionsDefaultOpen = false;
            @Comment(value="When faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit")
            private int factionMemberLimit = 0;
            @Comment(value="What faction ID to start new players in when they first join the server; default is 0, \"no faction\"")
            private String newPlayerStartingFactionID = "0";
            private double saveToFileEveryXMinutes = 30.0;
            private double autoLeaveAfterDaysOfInactivity = 10.0;
            private double autoLeaveRoutineRunsEveryXMinutes = 5.0;
            private int autoLeaveRoutineMaxMillisecondsPerTick = 5;
            private boolean removePlayerDataWhenBanned = true;
            private boolean autoLeaveDeleteFPlayerData = true;
            private double considerFactionsReallyOfflineAfterXMinutes = 0.0;
            private int actionDeniedPainAmount = 1;
            @Comment(value="If enabled, perms can be managed separately for when the faction is offline")
            private boolean separateOfflinePerms = false;
            @Comment(value="Should we delete player homes that they set via Essentials when they leave a Faction\nif they have homes set in that Faction's territory?")
            private boolean deleteEssentialsHomes = true;
            @Comment(value="Default Relation allows you to change the default relation for Factions.\nExample usage would be so people can't leave then make a new Faction while Raiding\n  in order to be able to execute commands if the default relation is neutral.")
            private String defaultRelation = "neutral";
            @Comment(value="If true, disables pistons entirely within faction territory.\nPrevents flying piston machines in faction territory.")
            private boolean disablePistonsInTerritory = false;

            public Other(Factions factions) {
            }

            public boolean isDisablePistonsInTerritory() {
                return this.disablePistonsInTerritory;
            }

            public boolean isDeleteEssentialsHomes() {
                return this.deleteEssentialsHomes;
            }

            public String getDefaultRelation() {
                return this.defaultRelation;
            }

            public boolean isSeparateOfflinePerms() {
                return this.separateOfflinePerms;
            }

            public boolean isAllowMultipleColeaders() {
                return this.allowMultipleColeaders;
            }

            public int getTagLengthMin() {
                return this.tagLengthMin;
            }

            public int getTagLengthMax() {
                return this.tagLengthMax;
            }

            public boolean isTagForceUpperCase() {
                return this.tagForceUpperCase;
            }

            public boolean isNewFactionsDefaultOpen() {
                return this.newFactionsDefaultOpen;
            }

            public int getFactionMemberLimit() {
                return this.factionMemberLimit;
            }

            public String getNewPlayerStartingFactionID() {
                return this.newPlayerStartingFactionID;
            }

            public double getSaveToFileEveryXMinutes() {
                return this.saveToFileEveryXMinutes;
            }

            public double getAutoLeaveAfterDaysOfInactivity() {
                return this.autoLeaveAfterDaysOfInactivity;
            }

            public double getAutoLeaveRoutineRunsEveryXMinutes() {
                return this.autoLeaveRoutineRunsEveryXMinutes;
            }

            public int getAutoLeaveRoutineMaxMillisecondsPerTick() {
                return this.autoLeaveRoutineMaxMillisecondsPerTick;
            }

            public boolean isRemovePlayerDataWhenBanned() {
                return this.removePlayerDataWhenBanned;
            }

            public boolean isAutoLeaveDeleteFPlayerData() {
                return this.autoLeaveDeleteFPlayerData;
            }

            public double getConsiderFactionsReallyOfflineAfterXMinutes() {
                return this.considerFactionsReallyOfflineAfterXMinutes;
            }

            public int getActionDeniedPainAmount() {
                return this.actionDeniedPainAmount;
            }
        }

        public class EnterTitles {
            private boolean enabled = true;
            private int fadeIn = 10;
            private int stay = 70;
            private int fadeOut = 20;
            private boolean alsoShowChat = false;

            public EnterTitles(Factions factions) {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public int getFadeIn() {
                return this.fadeIn;
            }

            public int getStay() {
                return this.stay;
            }

            public int getFadeOut() {
                return this.fadeOut;
            }

            public boolean isAlsoShowChat() {
                return this.alsoShowChat;
            }
        }
    }

    public class Logging {
        private boolean factionCreate = true;
        private boolean factionDisband = true;
        private boolean factionJoin = true;
        private boolean factionKick = true;
        private boolean factionLeave = true;
        private boolean landClaims = true;
        private boolean landUnclaims = true;
        private boolean moneyTransactions = true;
        private boolean playerCommands = true;

        public Logging(TransitionConfigV1 transitionConfigV1) {
        }

        public boolean isFactionCreate() {
            return this.factionCreate;
        }

        public boolean isFactionDisband() {
            return this.factionDisband;
        }

        public boolean isFactionJoin() {
            return this.factionJoin;
        }

        public boolean isFactionKick() {
            return this.factionKick;
        }

        public boolean isFactionLeave() {
            return this.factionLeave;
        }

        public boolean isLandClaims() {
            return this.landClaims;
        }

        public boolean isLandUnclaims() {
            return this.landUnclaims;
        }

        public boolean isMoneyTransactions() {
            return this.moneyTransactions;
        }

        public boolean isPlayerCommands() {
            return this.playerCommands;
        }
    }

    public class Exploits {
        private boolean obsidianGenerators = true;
        private boolean enderPearlClipping = true;
        private boolean interactionSpam = true;
        private boolean tntWaterlog = false;
        private boolean liquidFlow = false;
        private boolean preventDuping = true;

        public Exploits(TransitionConfigV1 transitionConfigV1) {
        }

        public boolean isObsidianGenerators() {
            return this.obsidianGenerators;
        }

        public boolean isEnderPearlClipping() {
            return this.enderPearlClipping;
        }

        public boolean isInteractionSpam() {
            return this.interactionSpam;
        }

        public boolean isTntWaterlog() {
            return this.tntWaterlog;
        }

        public boolean isLiquidFlow() {
            return this.liquidFlow;
        }

        public boolean doPreventDuping() {
            return this.preventDuping;
        }
    }

    public class Economy {
        @Comment(value="Must be true for any economy features")
        private boolean enabled = false;
        private String universeAccount = "";
        private double costClaimWilderness = 30.0;
        private double costClaimFromFactionBonus = 30.0;
        private double overclaimRewardMultiplier = 0.0;
        private double claimAdditionalMultiplier = 0.5;
        private double claimRefundMultiplier = 0.7;
        private double claimUnconnectedFee = 0.0;
        private double costCreate = 100.0;
        private double costOwner = 15.0;
        private double costSethome = 30.0;
        private double costDelhome = 30.0;
        private double costJoin = 0.0;
        private double costLeave = 0.0;
        private double costKick = 0.0;
        private double costInvite = 0.0;
        private double costHome = 0.0;
        private double costTag = 0.0;
        private double costDesc = 0.0;
        private double costTitle = 0.0;
        private double costList = 0.0;
        private double costMap = 0.0;
        private double costPower = 0.0;
        private double costShow = 0.0;
        private double costStuck = 0.0;
        private double costOpen = 0.0;
        private double costAlly = 0.0;
        private double costTruce = 0.0;
        private double costEnemy = 0.0;
        private double costNeutral = 0.0;
        private double costNoBoom = 0.0;
        private double costWarp = 0.0;
        private double costSetWarp = 0.0;
        private double costDelWarp = 0.0;
        @Comment(value="Faction banks, to pay for land claiming and other costs instead of individuals paying for them")
        private boolean bankEnabled = true;
        @Comment(value="Have to be at least moderator to withdraw or pay money to another faction")
        private boolean bankMembersCanWithdraw = false;
        @Comment(value="The faction pays for faction command costs, such as sethome")
        private boolean bankFactionPaysCosts = true;
        @Comment(value="The faction pays for land claiming costs.")
        private boolean bankFactionPaysLandCosts = true;

        public Economy(TransitionConfigV1 transitionConfigV1) {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public String getUniverseAccount() {
            return this.universeAccount;
        }

        public double getCostClaimWilderness() {
            return this.costClaimWilderness;
        }

        public double getCostClaimFromFactionBonus() {
            return this.costClaimFromFactionBonus;
        }

        public double getOverclaimRewardMultiplier() {
            return this.overclaimRewardMultiplier;
        }

        public double getClaimAdditionalMultiplier() {
            return this.claimAdditionalMultiplier;
        }

        public double getClaimRefundMultiplier() {
            return this.claimRefundMultiplier;
        }

        public double getClaimUnconnectedFee() {
            return this.claimUnconnectedFee;
        }

        public double getCostCreate() {
            return this.costCreate;
        }

        public double getCostOwner() {
            return this.costOwner;
        }

        public double getCostSethome() {
            return this.costSethome;
        }

        public double getCostDelhome() {
            return this.costDelhome;
        }

        public double getCostJoin() {
            return this.costJoin;
        }

        public double getCostLeave() {
            return this.costLeave;
        }

        public double getCostKick() {
            return this.costKick;
        }

        public double getCostInvite() {
            return this.costInvite;
        }

        public double getCostHome() {
            return this.costHome;
        }

        public double getCostTag() {
            return this.costTag;
        }

        public double getCostDesc() {
            return this.costDesc;
        }

        public double getCostTitle() {
            return this.costTitle;
        }

        public double getCostList() {
            return this.costList;
        }

        public double getCostMap() {
            return this.costMap;
        }

        public double getCostPower() {
            return this.costPower;
        }

        public double getCostShow() {
            return this.costShow;
        }

        public double getCostStuck() {
            return this.costStuck;
        }

        public double getCostOpen() {
            return this.costOpen;
        }

        public double getCostAlly() {
            return this.costAlly;
        }

        public double getCostTruce() {
            return this.costTruce;
        }

        public double getCostEnemy() {
            return this.costEnemy;
        }

        public double getCostNeutral() {
            return this.costNeutral;
        }

        public double getCostNoBoom() {
            return this.costNoBoom;
        }

        public double getCostWarp() {
            return this.costWarp;
        }

        public double getCostSetWarp() {
            return this.costSetWarp;
        }

        public double getCostDelWarp() {
            return this.costDelWarp;
        }

        public boolean isBankEnabled() {
            return this.bankEnabled;
        }

        public boolean isBankMembersCanWithdraw() {
            return this.bankMembersCanWithdraw;
        }

        public boolean isBankFactionPaysCosts() {
            return this.bankFactionPaysCosts;
        }

        public boolean isBankFactionPaysLandCosts() {
            return this.bankFactionPaysLandCosts;
        }
    }

    public class MapSettings {
        private int height = 17;
        private int width = 49;
        private boolean showFactionKey = true;
        private boolean showNeutralFactionsOnMap = true;
        private boolean showEnemyFactions = true;
        private boolean showTruceFactions = true;

        public MapSettings(TransitionConfigV1 transitionConfigV1) {
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        public boolean isShowFactionKey() {
            return this.showFactionKey;
        }

        public boolean isShowNeutralFactionsOnMap() {
            return this.showNeutralFactionsOnMap;
        }

        public boolean isShowEnemyFactions() {
            return this.showEnemyFactions;
        }

        public boolean isShowTruceFactions() {
            return this.showTruceFactions;
        }
    }

    public class Data {
        @Comment(value="Presently, the only option is JSON.")
        private String storage = "JSON";
        private Json json = new Json(this);

        public Data(TransitionConfigV1 transitionConfigV1) {
        }

        public Json json() {
            return this.json;
        }

        public class Json {
            @Comment(value="If true, data files will be stored without extra whitespace and linebreaks.\nThis becomes less readable, but can cut storage use in half.")
            private boolean efficientStorage = false;

            public Json(Data data) {
            }

            public boolean useEfficientStorage() {
                return this.efficientStorage;
            }
        }
    }

    public class RestrictWorlds {
        @Comment(value="If true, Factions will only function on certain worlds")
        private boolean restrictWorlds = false;
        @Comment(value="If restrictWorlds is true, this setting determines if the world list below is a whitelist or blacklist.\nTrue for whitelist, false for blacklist.")
        private boolean whitelist = true;
        private Set<String> worldList = new HashSet<String>();

        public RestrictWorlds(TransitionConfigV1 transitionConfigV1) {
        }

        public boolean isRestrictWorlds() {
            return this.restrictWorlds;
        }

        public boolean isWhitelist() {
            return this.whitelist;
        }

        public Set<String> getWorldList() {
            return this.worldList == null ? Collections.emptySet() : this.worldList;
        }
    }

    public class Scoreboard {
        @Comment(value="Constant scoreboard stays around all the time, displaying status info.\nAlso, if prefixes are enabled while it is enabled, will show prefixes on nametags and tab")
        private Constant constant = new Constant(this);
        @Comment(value="Info scoreboard is displayed when a player walks into a new Faction's territory.\nScoreboard disappears after <expiration> seconds.")
        private Info info = new Info(this);

        public Scoreboard(TransitionConfigV1 transitionConfigV1) {
        }

        public Constant constant() {
            return this.constant;
        }

        public Info info() {
            return this.info;
        }

        public class Constant {
            private boolean enabled = false;
            @Comment(value="Can use any placeholders, but does not update once set")
            private String title = "Faction Status";
            @Comment(value="If true, show faction prefixes on nametags and in tab list if scoreboard is enabled")
            private boolean prefixes = true;
            private List<String> content = new ArrayList<String>(){
                {
                    this.add("&6Your Faction");
                    this.add("{faction}");
                    this.add("&3Your Power");
                    this.add("{power}");
                    this.add("&aBalance");
                    this.add("${balance}");
                }
            };
            private boolean factionlessEnabled = false;
            private List<String> factionlessContent = new ArrayList<String>(){
                {
                    this.add("Make a new Faction");
                    this.add("Use /f create");
                }
            };

            public Constant(Scoreboard scoreboard) {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public String getTitle() {
                return this.title;
            }

            public boolean isPrefixes() {
                return this.prefixes;
            }

            public List<String> getContent() {
                return this.content != null ? this.content : Collections.emptyList();
            }

            public boolean isFactionlessEnabled() {
                return this.factionlessEnabled;
            }

            public List<String> getFactionlessContent() {
                return this.factionlessContent != null ? this.content : Collections.emptyList();
            }
        }

        public class Info {
            @Comment(value="send faction change message as well when scoreboard is up?")
            private boolean alsoSendChat = true;
            @Comment(value="How long do we want scoreboards to stay")
            private int expiration = 7;
            private boolean enabled = false;
            @Comment(value="Supports placeholders")
            private List<String> content = new ArrayList<String>(){
                {
                    this.add("&6Power");
                    this.add("{power}");
                    this.add("&3Members");
                    this.add("{online}/{members}");
                    this.add("&4Leader");
                    this.add("{leader}");
                    this.add("&bTerritory");
                    this.add("{chunks}");
                }
            };

            public Info(Scoreboard scoreboard) {
            }

            public boolean isAlsoSendChat() {
                return this.alsoSendChat;
            }

            public int getExpiration() {
                return this.expiration;
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public List<String> getContent() {
                return this.content != null ? this.content : Collections.emptyList();
            }
        }
    }

    public class LWC {
        private boolean enabled = true;
        private boolean resetLocksOnUnclaim = false;
        private boolean resetLocksOnCapture = false;

        public LWC(TransitionConfigV1 transitionConfigV1) {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public boolean isResetLocksOnUnclaim() {
            return this.resetLocksOnUnclaim;
        }

        public boolean isResetLocksOnCapture() {
            return this.resetLocksOnCapture;
        }
    }

    public class PlayerVaults {
        @Comment(value="The %s is for the faction id")
        private String vaultPrefix = "faction-%s";
        private int defaultMaxVaults = 0;

        public PlayerVaults(TransitionConfigV1 transitionConfigV1) {
        }

        public String getVaultPrefix() {
            return this.vaultPrefix;
        }

        public int getDefaultMaxVaults() {
            return this.defaultMaxVaults;
        }
    }

    public class WorldGuard {
        private boolean checking;
        private boolean buildPriority;

        public WorldGuard(TransitionConfigV1 transitionConfigV1) {
        }

        public boolean isChecking() {
            return this.checking;
        }

        public boolean isBuildPriority() {
            return this.buildPriority;
        }
    }

    public class WorldBorder {
        @Comment(value="WorldBorder support\nThis is for Minecraft's built-in command. To get your current border: /minecraft:worldborder get\nA buffer of 0 means faction claims can go right up to the border of the world.\nThe buffer is in chunks, so 1 as a buffer means an entire chunk of buffer between\nthe border of the world and what can be claimed to factions")
        private int buffer = 0;

        public WorldBorder(TransitionConfigV1 transitionConfigV1) {
        }

        public int getBuffer() {
            return this.buffer;
        }
    }
}

