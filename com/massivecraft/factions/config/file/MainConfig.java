/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 */
package com.massivecraft.factions.config.file;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.config.annotation.Comment;
import com.massivecraft.factions.config.annotation.WipeOnReload;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.material.MaterialDb;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import moss.factions.shade.net.kyori.adventure.text.format.NamedTextColor;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MainConfig {
    @Comment(value="The command base (by default f, making the command /f)")
    private List<String> commandBase = new ArrayList<String>(){
        {
            this.add("f");
        }
    };
    @Comment(value="Support and documentation https://factions.support\nUpdates https://www.spigotmc.org/resources/factionsuuid.1035/\n\nMade with love <3")
    private AVeryFriendlyFactionsConfig aVeryFriendlyFactionsConfig = new AVeryFriendlyFactionsConfig();
    @Comment(value="Colors for relationships and default factions")
    private Colors colors = new Colors(this);
    private Commands commands = new Commands(this);
    private Factions factions = new Factions(this);
    @Comment(value="What should be logged?")
    private Logging logging = new Logging(this);
    @Comment(value="Controls certain exploit preventions")
    private Exploits exploits = new Exploits(this);
    @Comment(value="Economy support requires Vault and a compatible economy plugin\nIf you wish to use economy features, be sure to set 'enabled' in this section to true!")
    private Economy economy = new Economy(this);
    @Comment(value="Control for the default settings of /f map")
    private MapSettings map = new MapSettings(this);
    @Comment(value="Data storage settings")
    private Data data = new Data(this);
    private RestrictWorlds restrictWorlds = new RestrictWorlds(this);
    private Scoreboard scoreboard = new Scoreboard(this);
    @Comment(value="LWC integration\nThis support targets the modern fork of LWC, called LWC Extended.\nYou can find it here: https://www.spigotmc.org/resources/lwc-extended.69551/\nNote: Modern LWC is no longer supported, and its former maintainer now runs LWC Extended")
    private LWC lwc = new LWC(this);
    @Comment(value="Integration with the Magic plugin")
    private MagicPlugin magicPlugin = new MagicPlugin(this);
    @Comment(value="Paper features, when accessible.")
    private Paper paper = new Paper(this);
    @Comment(value="Lists plugin integrations. Some other plugins (PVX, LWC, Magic, WG, WB) are currently\n elsewhere but will migrate here in the future")
    private Plugins plugins = new Plugins(this);
    @Comment(value="PlayerVaults faction vault settings.\nEnable faction-owned vaults!\nhttps://www.spigotmc.org/resources/playervaultsx.51204/")
    private PlayerVaults playerVaults = new PlayerVaults(this);
    @Comment(value="WorldGuard settings.\nNote that flag stuff only works on WG 7")
    private WorldGuard worldGuard = new WorldGuard(this);
    private WorldBorder worldBorder = new WorldBorder(this);

    private static TextColor getColor(String string, TextColor textColor, TextColor textColor2) {
        if (textColor != null) {
            return textColor;
        }
        TextColor textColor3 = string.startsWith("#") ? TextColor.fromHexString(string) : (TextColor)NamedTextColor.NAMES.value(string.toLowerCase());
        return textColor3 == null ? NamedTextColor.WHITE : textColor3;
    }

    public List<String> getCommandBase() {
        return this.commandBase == null ? (this.commandBase = Collections.singletonList("f")) : this.commandBase;
    }

    public AVeryFriendlyFactionsConfig getaVeryFriendlyFactionsConfig() {
        return this.aVeryFriendlyFactionsConfig;
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

    public MagicPlugin magicPlugin() {
        return this.magicPlugin;
    }

    public Paper paper() {
        return this.paper;
    }

    public PlayerVaults playerVaults() {
        return this.playerVaults;
    }

    public Plugins plugins() {
        return this.plugins;
    }

    public WorldGuard worldGuard() {
        return this.worldGuard;
    }

    public LWC lwc() {
        return this.lwc;
    }

    public WorldBorder worldBorder() {
        return this.worldBorder;
    }

    public Data data() {
        return this.data;
    }

    public static class AVeryFriendlyFactionsConfig {
        @Comment(value="This is the config version, used for migrating on plugin updates. Don't change this value yourself, unless you WANT a broken config!")
        private int version = 6;
        @Comment(value="Debug\nTurn this on if you are having issues with something and working on resolving them.\nThis will spam your console with information that is useful if you know how to read the source.\nIt's suggested that you only turn this on at the direction of a developer.")
        private boolean debug = false;

        public boolean isDebug() {
            return this.debug;
        }
    }

    public class Colors {
        private Factions factions = new Factions(this);
        private Relations relations = new Relations(this);

        public Colors(MainConfig mainConfig) {
        }

        public Factions factions() {
            return this.factions;
        }

        public Relations relations() {
            return this.relations;
        }

        public class Factions {
            private String wilderness = "GRAY";
            @WipeOnReload
            private transient TextColor wildernessColor;
            private String safezone = "GOLD";
            @WipeOnReload
            private transient TextColor safezoneColor;
            private String warzone = "DARK_RED";
            @WipeOnReload
            private transient TextColor warzoneColor;

            public Factions(Colors colors) {
            }

            public TextColor getWilderness() {
                this.wildernessColor = MainConfig.getColor(this.wilderness, this.wildernessColor, NamedTextColor.GRAY);
                return this.wildernessColor;
            }

            public TextColor getSafezone() {
                this.safezoneColor = MainConfig.getColor(this.safezone, this.safezoneColor, NamedTextColor.GOLD);
                return this.safezoneColor;
            }

            public TextColor getWarzone() {
                this.warzoneColor = MainConfig.getColor(this.warzone, this.warzoneColor, NamedTextColor.DARK_RED);
                return this.warzoneColor;
            }
        }

        public class Relations {
            private String member = "GREEN";
            @WipeOnReload
            private transient TextColor memberColor;
            private String ally = "LIGHT_PURPLE";
            @WipeOnReload
            private transient TextColor allyColor;
            private String truce = "DARK_PURPLE";
            @WipeOnReload
            private transient TextColor truceColor;
            private String neutral = "WHITE";
            @WipeOnReload
            private transient TextColor neutralColor;
            private String enemy = "RED";
            @WipeOnReload
            private transient TextColor enemyColor;
            private String peaceful = "GOLD";
            @WipeOnReload
            private transient TextColor peacefulColor;

            public Relations(Colors colors) {
            }

            public TextColor getMember() {
                this.memberColor = MainConfig.getColor(this.member, this.memberColor, NamedTextColor.GREEN);
                return this.memberColor;
            }

            public TextColor getAlly() {
                this.allyColor = MainConfig.getColor(this.ally, this.allyColor, NamedTextColor.LIGHT_PURPLE);
                return this.allyColor;
            }

            public TextColor getTruce() {
                this.truceColor = MainConfig.getColor(this.truce, this.truceColor, NamedTextColor.DARK_PURPLE);
                return this.truceColor;
            }

            public TextColor getNeutral() {
                this.neutralColor = MainConfig.getColor(this.neutral, this.neutralColor, NamedTextColor.WHITE);
                return this.neutralColor;
            }

            public TextColor getEnemy() {
                this.enemyColor = MainConfig.getColor(this.enemy, this.enemyColor, NamedTextColor.RED);
                return this.enemyColor;
            }

            public TextColor getPeaceful() {
                this.peacefulColor = MainConfig.getColor(this.peaceful, this.peacefulColor, NamedTextColor.GOLD);
                return this.peacefulColor;
            }
        }
    }

    public class Commands {
        private Description description = new Description(this);
        private Kick kick = new Kick(this);
        private Fly fly = new Fly(this);
        private Help help = new Help(this);
        private Home home = new Home(this);
        private Link link = new Link(this);
        private ListCmd list = new ListCmd(this);
        private MapCmd map = new MapCmd(this);
        private Near near = new Near(this);
        private SeeChunk seeChunk = new SeeChunk(this);
        private Show show = new Show(this);
        private Stuck stuck = new Stuck(this);
        @Comment(value="TNT bank!")
        private TNT tnt = new TNT(this);
        private ToolTips toolTips = new ToolTips(this);
        private Warp warp = new Warp(this);

        public Commands(MainConfig mainConfig) {
        }

        public Description description() {
            return this.description;
        }

        public Kick kick() {
            return this.kick;
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

        public Link link() {
            return this.link;
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

        public TNT tnt() {
            return this.tnt;
        }

        public ToolTips toolTips() {
            return this.toolTips;
        }

        public Warp warp() {
            return this.warp;
        }

        public class Description {
            @Comment(value="If -1, no limit.")
            private int maxLength = -1;

            public Description(Commands commands) {
            }

            public int getMaxLength() {
                return this.maxLength;
            }
        }

        public class Kick {
            @Comment(value="If true, players can be kicked while standing in enemy territory")
            private boolean allowKickInEnemyTerritory = false;

            public Kick(Commands commands) {
            }

            public boolean isAllowKickInEnemyTerritory() {
                return this.allowKickInEnemyTerritory;
            }
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
            @Comment(value="Should flight be disabled if the player has hurt mobs?")
            private boolean disableOnHurtingMobs = true;
            @Comment(value="Should flight be disabled if the player has hurt players?")
            private boolean disableOnHurtingPlayers = true;
            @Comment(value="Should players lose flight status while autoclaiming into territory they cannot fly in?")
            private boolean disableFlightDuringAutoclaim = false;
            @Comment(value="Should flight be disabled if the player is hurt by mobs?")
            private boolean disableOnHurtByMobs = true;
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

            public boolean isDisableOnHurtingMobs() {
                return this.disableOnHurtingMobs;
            }

            public boolean isDisableOnHurtingPlayers() {
                return this.disableOnHurtingPlayers;
            }

            public boolean isDisableFlightDuringAutoclaim() {
                return this.disableFlightDuringAutoclaim;
            }

            public boolean isDisableOnHurtByMobs() {
                return this.disableOnHurtByMobs;
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
                return this.entries != null ? Collections.unmodifiableMap(this.entries) : Collections.emptyMap();
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

        public class Link {
            @Comment(value="Default URL")
            private String defaultURL = "No link set";

            public Link(Commands commands) {
            }

            public String getDefaultURL() {
                return this.defaultURL;
            }
        }

        public class ListCmd {
            @Comment(value="You can only use {pagenumber} and {pagecount} in the header.\nBlank entry results in nothing being displayed.")
            private String header = "&e&m----------&r&e[ &2Faction List &9{pagenumber}&e/&9{pagecount} &e]&m----------";
            @Comment(value="You can only use {pagenumber} and {pagecount} in the footer.\nBlank entry results in nothing being displayed.")
            private String footer = "";
            @Comment(value="You can use any variables here")
            private String factionlessEntry = "<i>Factionless<i> {factionless} online";
            @Comment(value="You can use any variable here")
            private String entry = "<a>{faction-relation-color}{faction} <i>{online} / {members} online, <a>Land / Power / Maxpower: <i>{chunks}/{power}/{maxPower}";

            public ListCmd(Commands commands) {
            }

            public String getHeader() {
                return this.header;
            }

            public String getFooter() {
                return this.footer;
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
                return this.format != null ? Collections.unmodifiableList(this.format) : Collections.emptyList();
            }

            public boolean isMinimal() {
                return this.minimal;
            }

            public List<String> getExempt() {
                return this.exempt != null ? Collections.unmodifiableList(this.exempt) : Collections.emptyList();
            }
        }

        public class Stuck {
            @Comment(value="Warmup seconds before command executes. Set to 0 for no warmup.")
            private int delay = 30;
            @Comment(value="This radius defines how far from where they ran the command the player\nmay travel while waiting to be unstuck. If they leave this radius, the\ncommand will be cancelled.")
            private int radius = 10;
            @Comment(value="Search radius allowed for finding safe chunks.")
            private int searchRadius = 30;

            public Stuck(Commands commands) {
            }

            public int getDelay() {
                return this.delay;
            }

            public int getRadius() {
                return this.radius;
            }

            public int getSearchRadius() {
                return this.searchRadius;
            }
        }

        public class TNT {
            private boolean enable = false;
            @Comment(value="Maximum storage. Set to -1 (or lower) to disable")
            private int maxStorage = -1;
            private int maxRadius = 5;

            public TNT(Commands commands) {
            }

            public int getMaxRadius() {
                return this.maxRadius;
            }

            public int getMaxStorage() {
                return this.maxStorage;
            }

            public boolean isAboveMaxStorage(int n) {
                if (this.maxStorage < 0) {
                    return false;
                }
                return n > this.maxStorage;
            }

            public boolean isEnable() {
                return this.enable;
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
                    this.add("&6Power: &f{player-power}");
                    this.add("&6Rank: &f{group}");
                    this.add("&6Balance: &a${balance}");
                }
            };

            public ToolTips(Commands commands) {
            }

            public List<String> getFaction() {
                return this.faction != null ? Collections.unmodifiableList(this.faction) : Collections.emptyList();
            }

            public List<String> getPlayer() {
                return this.player != null ? Collections.unmodifiableList(this.player) : Collections.emptyList();
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
        @Comment(value="Should we send titles when players enter Factions? Durations are in ticks (20 ticks every second)")
        private EnterTitles enterTitles = new EnterTitles(this);
        @Comment(value="Spawn control.\nException names are entity type names as seen at the below URL.\nNote that any name with an underscore MUST have quotes around it.\nhttps://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html\nSpawn types are those at the below URL:\nhttps://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html")
        private Spawning spawning = new Spawning(this);

        public Factions(MainConfig mainConfig) {
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

        public Spawning spawning() {
            return this.spawning;
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
            @Comment(value="Add items here (comma-separated) for commands to listen to that will auto-return the user to public chat")
            private List<String> triggerPublicChatOnCommand = new ArrayList<String>();
            @WipeOnReload
            private transient List<String> triggerPublicChatLowerCased;

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

            public List<String> getTriggerPublicChatOnCommand() {
                if (this.triggerPublicChatLowerCased == null) {
                    this.triggerPublicChatLowerCased = new ArrayList<String>();
                    if (this.triggerPublicChatOnCommand != null) {
                        this.triggerPublicChatOnCommand.forEach(string -> this.triggerPublicChatLowerCased.add(string.toLowerCase()));
                    }
                }
                return this.triggerPublicChatLowerCased;
            }

            public boolean isTriggerPublicChat(String string) {
                return this.getTriggerPublicChatOnCommand().contains(string.toLowerCase());
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
            private boolean requiredToHaveHomeBeforeSettingWarps = false;

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

            public boolean isRequiredToHaveHomeBeforeSettingWarps() {
                return this.requiredToHaveHomeBeforeSettingWarps;
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
            private boolean disablePeacefulPVPInWarzone = true;
            private int noPVPDamageToOthersForXSecondsAfterLogin = 3;
            private Set<String> worldsIgnorePvP = new HashSet<String>(){
                {
                    this.add("exampleWorldName");
                }
            };

            public PVP(Factions factions) {
            }

            public boolean isDisablePVPBetweenNeutralFactions() {
                return this.disablePVPBetweenNeutralFactions;
            }

            public boolean isDisablePVPForFactionlessPlayers() {
                return this.disablePVPForFactionlessPlayers;
            }

            public boolean isDisablePeacefulPVPInWarzone() {
                return this.disablePeacefulPVPInWarzone;
            }

            public boolean isEnablePVPAgainstFactionlessInAttackersLand() {
                return this.enablePVPAgainstFactionlessInAttackersLand;
            }

            public int getNoPVPDamageToOthersForXSecondsAfterLogin() {
                return this.noPVPDamageToOthersForXSecondsAfterLogin;
            }

            public Set<String> getWorldsIgnorePvP() {
                return this.worldsIgnorePvP == null ? Collections.emptySet() : Collections.unmodifiableSet(this.worldsIgnorePvP);
            }
        }

        public class SpecialCase {
            private boolean peacefulTerritoryDisablePVP = true;
            private boolean peacefulTerritoryDisableMonsters = false;
            private boolean peacefulTerritoryDisableBoom = false;
            private boolean permanentFactionsDisableLeaderPromotion = false;
            @Comment(value="Material names of things whose placement is ignored in faction territory")
            private Set<String> ignoreBuildMaterials = new HashSet<String>(){
                {
                    this.add("exampleMaterial");
                }
            };
            @WipeOnReload
            private transient Set<Material> ignoreBuildMaterialsMat;

            public SpecialCase(Factions factions) {
            }

            public Set<Material> getIgnoreBuildMaterials() {
                if (this.ignoreBuildMaterialsMat == null) {
                    this.ignoreBuildMaterialsMat = new HashSet<Material>();
                    this.ignoreBuildMaterials.forEach(string -> this.ignoreBuildMaterialsMat.add(MaterialDb.get(string)));
                    this.ignoreBuildMaterialsMat.remove(Material.AIR);
                    this.ignoreBuildMaterials = Collections.unmodifiableSet(this.ignoreBuildMaterials);
                }
                return this.ignoreBuildMaterialsMat;
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
            private int fillUnClaimMaxClaims = 25;
            private int fillUnClaimMaxDistance = 5;
            private int fillClaimMaxClaims = 25;
            private int fillClaimMaxDistance = 5;
            @Comment(value="If someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit")
            private int radiusClaimFailureLimit = 9;
            private Set<String> worldsNoClaiming = new HashSet<String>(){
                {
                    this.add("exampleWorldName");
                }
            };
            @Comment(value="Buffer Zone is an chunk area required between claims of different Factions.\nThis is default to 0 and has always been that way. Meaning Factions can have\n  claims that border each other.\nIf this is set to 3, then Factions need to have 3 chunks between their claim\n  and another Faction's claim.\nIt's recommended to keep this pretty low as the radius check could be a\n  heavy operation if set to a large number.\nIf this is set to 0, we won't even bother checking which is how Factions has\n  always been.")
            private int bufferZone = 0;
            @Comment(value="Should we allow Factions to over claim if they are raidable?\nThis has always been true, allowing factions to over claim others.")
            private boolean allowOverClaim = true;
            @Comment(value="If true (and allowOverClaim is true, claiming over another faction's land will ignore buffer zone settings.")
            private boolean allowOverClaimIgnoringBuffer = false;

            public Claims(Factions factions) {
            }

            public boolean isAllowOverClaim() {
                return this.allowOverClaim;
            }

            public boolean isAllowOverClaimAndIgnoringBuffer() {
                return this.allowOverClaim && this.allowOverClaimIgnoringBuffer;
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

            public int getFillUnClaimMaxClaims() {
                return this.fillUnClaimMaxClaims;
            }

            public int getFillUnClaimMaxDistance() {
                return this.fillUnClaimMaxDistance;
            }

            public int getFillClaimMaxClaims() {
                return this.fillClaimMaxClaims;
            }

            public int getFillClaimMaxDistance() {
                return this.fillClaimMaxDistance;
            }

            public int getLineClaimLimit() {
                return this.lineClaimLimit;
            }

            public int getRadiusClaimFailureLimit() {
                return this.radiusClaimFailureLimit;
            }

            public Set<String> getWorldsNoClaiming() {
                return this.worldsNoClaiming == null ? Collections.emptySet() : Collections.unmodifiableSet(this.worldsNoClaiming);
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
            @Comment(value="Teleport joining players (or arriving from a plugin-disabled world) out of\nterritories (such as enemy territory) back to a designated location.")
            private TerritoryTeleport territoryTeleport = new TerritoryTeleport(this);
            @Comment(value="Commands which will be prevented if the player is a member of a permanent faction")
            private Set<String> permanentFactionMemberDenyCommands = new HashSet<String>(){
                {
                    this.add("exampleCommand");
                }
            };
            @Comment(value="Commands which will be prevented when in claimed territory of a neutral faction")
            private Set<String> territoryNeutralDenyCommands = new HashSet<String>(){
                {
                    this.add("exampleCommand");
                }
            };
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
            private Set<String> territoryAllyDenyCommands = new HashSet<String>(){
                {
                    this.add("exampleCommand");
                }
            };
            @Comment(value="Commands which will be prevented when in claimed territory of a truced faction")
            private Set<String> territoryTruceDenyCommands = new HashSet<String>(){
                {
                    this.add("exampleCommand");
                }
            };
            @Comment(value="Commands which will be prevented when in warzone")
            private Set<String> warzoneDenyCommands = new HashSet<String>(){
                {
                    this.add("exampleCommand");
                }
            };
            @Comment(value="Commands which will be prevented when in wilderness")
            private Set<String> wildernessDenyCommands = new HashSet<String>(){
                {
                    this.add("exampleCommand");
                }
            };
            private boolean territoryBlockCreepers = false;
            private boolean territoryBlockCreepersWhenOffline = false;
            private boolean territoryBlockFireballs = false;
            private boolean territoryBlockFireballsWhenOffline = false;
            private boolean territoryBlockTNT = false;
            private boolean territoryBlockTNTWhenOffline = false;
            private boolean territoryBlockOtherExplosions = false;
            private boolean territoryBlockOtherExplosionsWhenOffline = false;
            private boolean territoryDenyEndermanBlocks = true;
            private boolean territoryDenyEndermanBlocksWhenOffline = true;
            private boolean territoryBlockEntityDamageMatchingPerms = false;
            @Comment(value="If true, lecterns can be interacted with, but taking the book will still be protected by CONTAINER perm")
            private boolean territoryAllowLecternReading = false;
            private boolean territoryDenyIceFormation = false;
            private boolean safeZoneDenyBuild = true;
            private boolean safeZoneDenyUsage = true;
            private boolean safeZoneBlockTNT = true;
            private boolean safeZoneBlockOtherExplosions = true;
            private boolean safeZonePreventAllDamageToPlayers = false;
            private boolean safeZonePreventLiquidFlowIn = true;
            private boolean safeZoneDenyEndermanBlocks = true;
            private boolean safeZoneBlockAllEntityDamage = false;
            private boolean peacefulBlockAllEntityDamage = false;
            private boolean warZoneDenyBuild = true;
            private boolean warZoneDenyUsage = true;
            private boolean warZoneBlockCreepers = true;
            private boolean warZoneBlockFireballs = true;
            private boolean warZoneBlockTNT = true;
            private boolean warZoneBlockOtherExplosions = true;
            private boolean warZoneFriendlyFire = false;
            private boolean warZonePreventLiquidFlowIn = true;
            private boolean warZoneDenyEndermanBlocks = true;
            private boolean wildernessDenyBuild = false;
            private boolean wildernessDenyUsage = false;
            private boolean wildernessBlockCreepers = false;
            private boolean wildernessBlockFireballs = false;
            private boolean wildernessBlockTNT = false;
            private boolean wildernessBlockOtherExplosions = false;
            private boolean wildernessDenyEndermanBlocks = false;
            private boolean pistonProtectionThroughDenyBuild = true;
            private Set<String> territoryDenyUsageMaterials = new HashSet<String>();
            private Set<String> territoryDenyUsageMaterialsWhenOffline = new HashSet<String>();
            @WipeOnReload
            private transient Set<Material> territoryDenyUsageMaterialsMat;
            @WipeOnReload
            private transient Set<Material> territoryDenyUsageMaterialsWhenOfflineMat;
            @Comment(value="Exceptions to consideration for container perms.\nFor example, putting \"TRAPPED_CHEST\" into here would allow anyone to open trapped chests anywhere.")
            private Set<String> containerExceptions = new HashSet<String>();
            @WipeOnReload
            private transient Set<Material> containerExceptionsMat;
            @Comment(value="Exceptions to consideration for breaking perms. Can always be broken.")
            private Set<String> breakExceptions = new HashSet<String>();
            @WipeOnReload
            private transient Set<Material> breakExceptionsMat;
            @Comment(value="Exceptions for protections of interacting with entities, such as mounting horses")
            private Set<String> entityInteractExceptions = new HashSet<String>();
            @Comment(value="Mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections.")
            private Set<String> playersWhoBypassAllProtection = new HashSet<String>(){
                {
                    this.add("example-player-name");
                }
            };
            private Set<String> worldsNoWildernessProtection = new HashSet<String>(){
                {
                    this.add("exampleWorld");
                }
            };
            @Comment(value="Add material names here that you wish to see treated as containers for interaction.")
            private Set<String> customContainers = new HashSet<String>();
            @WipeOnReload
            private transient Set<Material> customContainersMat;

            private Protection(Factions factions) {
                this.protectUsage("FIRE_CHARGE");
                this.protectUsage("FLINT_AND_STEEL");
                this.protectUsage("BUCKET");
                this.protectUsage("WATER_BUCKET");
                this.protectUsage("LAVA_BUCKET");
                this.protectUsage("PUFFERFISH_BUCKET");
                this.protectUsage("SALMON_BUCKET");
                this.protectUsage("COD_BUCKET");
                this.protectUsage("TROPICAL_FISH_BUCKET");
                this.protectUsage("AXOLOTL_BUCKET");
                this.protectUsage("TADPOLE_BUCKET");
            }

            private void protectUsage(String string) {
                this.territoryDenyUsageMaterials.add(string);
                this.territoryDenyUsageMaterialsWhenOffline.add(string);
            }

            public TerritoryTeleport territoryTeleport() {
                return this.territoryTeleport;
            }

            public Set<String> getPermanentFactionMemberDenyCommands() {
                return this.permanentFactionMemberDenyCommands == null ? Collections.emptySet() : Collections.unmodifiableSet(this.permanentFactionMemberDenyCommands);
            }

            public Set<String> getTerritoryNeutralDenyCommands() {
                return this.territoryNeutralDenyCommands == null ? Collections.emptySet() : Collections.unmodifiableSet(this.territoryNeutralDenyCommands);
            }

            public Set<String> getTerritoryEnemyDenyCommands() {
                return this.territoryEnemyDenyCommands == null ? Collections.emptySet() : Collections.unmodifiableSet(this.territoryEnemyDenyCommands);
            }

            public Set<String> getTerritoryAllyDenyCommands() {
                return this.territoryAllyDenyCommands == null ? Collections.emptySet() : Collections.unmodifiableSet(this.territoryAllyDenyCommands);
            }

            public Set<String> getTerritoryTruceDenyCommands() {
                return this.territoryTruceDenyCommands == null ? Collections.emptySet() : Collections.unmodifiableSet(this.territoryTruceDenyCommands);
            }

            public Set<String> getWarzoneDenyCommands() {
                return this.warzoneDenyCommands == null ? Collections.emptySet() : Collections.unmodifiableSet(this.warzoneDenyCommands);
            }

            public Set<String> getWildernessDenyCommands() {
                return this.wildernessDenyCommands == null ? Collections.emptySet() : Collections.unmodifiableSet(this.wildernessDenyCommands);
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

            public boolean isTerritoryBlockEntityDamageMatchingPerms() {
                return this.territoryBlockEntityDamageMatchingPerms;
            }

            public boolean isTerritoryAllowLecternReading() {
                return this.territoryAllowLecternReading;
            }

            public boolean isTerritoryDenyIceFormation() {
                return this.territoryDenyIceFormation;
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

            public boolean isSafeZonePreventLiquidFlowIn() {
                return this.safeZonePreventLiquidFlowIn;
            }

            public boolean isSafeZoneDenyEndermanBlocks() {
                return this.safeZoneDenyEndermanBlocks;
            }

            public boolean isSafeZoneBlockAllEntityDamage() {
                return this.safeZoneBlockAllEntityDamage;
            }

            public boolean isPeacefulBlockAllEntityDamage() {
                return this.peacefulBlockAllEntityDamage;
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

            public boolean isWarZonePreventLiquidFlowIn() {
                return this.warZonePreventLiquidFlowIn;
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

            public boolean isTerritoryBlockOtherExplosions() {
                return this.territoryBlockOtherExplosions;
            }

            public boolean isTerritoryBlockOtherExplosionsWhenOffline() {
                return this.territoryBlockOtherExplosionsWhenOffline;
            }

            public boolean isSafeZoneBlockOtherExplosions() {
                return this.safeZoneBlockOtherExplosions;
            }

            public boolean isWarZoneBlockOtherExplosions() {
                return this.warZoneBlockOtherExplosions;
            }

            public boolean isWildernessBlockOtherExplosions() {
                return this.wildernessBlockOtherExplosions;
            }

            public Set<Material> getTerritoryDenyUsageMaterials() {
                if (this.territoryDenyUsageMaterialsMat == null) {
                    this.territoryDenyUsageMaterialsMat = new HashSet<Material>();
                    this.territoryDenyUsageMaterials.forEach(string -> this.territoryDenyUsageMaterialsMat.add(MaterialDb.get(string)));
                    this.territoryDenyUsageMaterialsMat.remove(Material.AIR);
                    this.territoryDenyUsageMaterialsMat = Collections.unmodifiableSet(this.territoryDenyUsageMaterialsMat);
                }
                return this.territoryDenyUsageMaterialsMat;
            }

            public Set<Material> getTerritoryDenyUsageMaterialsWhenOffline() {
                if (this.territoryDenyUsageMaterialsWhenOfflineMat == null) {
                    this.territoryDenyUsageMaterialsWhenOfflineMat = new HashSet<Material>();
                    this.territoryDenyUsageMaterialsWhenOffline.forEach(string -> this.territoryDenyUsageMaterialsWhenOfflineMat.add(MaterialDb.get(string)));
                    this.territoryDenyUsageMaterialsWhenOfflineMat.remove(Material.AIR);
                    this.territoryDenyUsageMaterialsWhenOfflineMat = Collections.unmodifiableSet(this.territoryDenyUsageMaterialsWhenOfflineMat);
                }
                return this.territoryDenyUsageMaterialsWhenOfflineMat;
            }

            public Set<Material> getContainerExceptions() {
                if (this.containerExceptionsMat == null) {
                    this.containerExceptionsMat = new HashSet<Material>();
                    this.containerExceptions.forEach(string -> this.containerExceptionsMat.add(MaterialDb.get(string)));
                    this.containerExceptionsMat.remove(Material.AIR);
                    this.containerExceptionsMat = Collections.unmodifiableSet(this.containerExceptionsMat);
                }
                return this.containerExceptionsMat;
            }

            public Set<Material> getBreakExceptions() {
                if (this.breakExceptionsMat == null) {
                    this.breakExceptionsMat = new HashSet<Material>();
                    this.breakExceptions.forEach(string -> this.breakExceptionsMat.add(MaterialDb.get(string)));
                    this.breakExceptionsMat.remove(Material.AIR);
                    this.breakExceptionsMat = Collections.unmodifiableSet(this.breakExceptionsMat);
                }
                return this.breakExceptionsMat;
            }

            public Set<String> getEntityInteractExceptions() {
                return Collections.unmodifiableSet(this.entityInteractExceptions);
            }

            public Set<String> getPlayersWhoBypassAllProtection() {
                return this.playersWhoBypassAllProtection == null ? Collections.emptySet() : Collections.unmodifiableSet(this.playersWhoBypassAllProtection);
            }

            public Set<String> getWorldsNoWildernessProtection() {
                return this.worldsNoWildernessProtection == null ? Collections.emptySet() : Collections.unmodifiableSet(this.worldsNoWildernessProtection);
            }

            public Set<Material> getCustomContainers() {
                if (this.customContainersMat == null) {
                    this.customContainersMat = new HashSet<Material>();
                    this.customContainers.forEach(string -> this.customContainersMat.add(MaterialDb.get(string)));
                    this.customContainersMat.remove(Material.AIR);
                    this.customContainersMat = Collections.unmodifiableSet(this.customContainersMat);
                }
                return this.customContainersMat;
            }

            public class TerritoryTeleport {
                private boolean enable = false;
                @Comment(value="Time, in seconds, since last on the server to trigger this feature.")
                private long timeSinceLastSignedIn = 300L;
                @Comment(value="Destination options. Order them, separated by commas, for priority.\nFor example, if a faction home does not exist then the next option is chosen.\nAbsolute fallback is the spawn of the first world loaded\nOptions:\n  home: Faction home\n  bed: Bed")
                private String destination = "home, bed, spawn";
                @Comment(value="The world in which the spawn exists")
                private String destinationSpawnWorld = "world";
                @Comment(value="Options: MEMBER, ALLY, TRUCE, NEUTRAL, ENEMY\nIncorrectly spelled entries default to NEUTRAL")
                private Set<String> relationsToTeleportOut = new HashSet<String>(){
                    {
                        this.add("ENEMY");
                        this.add("NEUTRAL");
                        this.add("TRUCE");
                    }
                };
                @Comment(value="Should wilderness count if NEUTRAL is listed as a relation?")
                private boolean includeWildernessInNeutral = false;
                @Comment(value="Should safezone count if NEUTRAL is listed as a relation?")
                private boolean includeSafezoneInNeutral = false;
                @Comment(value="Should warzone count if NEUTRAL is listed as a relation?")
                private boolean includeWarzoneInNeutral = false;
                @WipeOnReload
                private transient Set<Relation> relations = null;

                public TerritoryTeleport(Protection protection) {
                }

                public boolean isEnabled() {
                    return this.enable;
                }

                public long getTimeSinceLastSignedIn() {
                    return this.timeSinceLastSignedIn;
                }

                public String getDestination() {
                    return this.destination;
                }

                public String getDestinationSpawnWorld() {
                    return this.destinationSpawnWorld;
                }

                public Set<String> getRelationsToTeleportOut() {
                    return this.relationsToTeleportOut;
                }

                public boolean isRelationToTeleportOut(Relation relation, Faction faction) {
                    if (!faction.isNormal() && (relation != Relation.NEUTRAL || faction.isWilderness() && !this.includeWildernessInNeutral || faction.isSafeZone() && !this.includeSafezoneInNeutral || faction.isWarZone() && !this.includeWarzoneInNeutral)) {
                        return false;
                    }
                    if (this.relations == null) {
                        this.relations = new HashSet<Relation>();
                        for (String string : this.relationsToTeleportOut) {
                            Relation relation2 = Relation.fromString(string);
                            if (relation2 == null) continue;
                            this.relations.add(relation2);
                        }
                    }
                    return this.relations.contains(relation);
                }

                @Deprecated
                public boolean isRelationToTeleportOut(Relation relation) {
                    if (this.relations == null) {
                        this.relations = new HashSet<Relation>();
                        for (String string : this.relationsToTeleportOut) {
                            Relation relation2 = Relation.fromString(string);
                            if (relation2 == null) continue;
                            this.relations.add(relation2);
                        }
                    }
                    return this.relations.contains(relation);
                }
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
            @Comment(value="\nSets the mode of land/raid control")
            private String system = "power";
            @Comment(value="Controls the DTR system of land/raid control\nSet the 'system' value to 'dtr' to use this system")
            private DTR dtr = new DTR(this);
            @Comment(value="Controls the power system of land/raid control\nSet the 'system' value to 'power' to use this system")
            private Power power = new Power(this);
            @Comment(value="Announce in chat if a faction becomes raidable?")
            private boolean announceRaidable = false;
            @Comment(value="Announce in chat if a faction becomes no longer raidable?")
            private boolean announceNotRaidable = false;
            @Comment(value="If either announce on raidable or not raidable is true, only send to the faction and its enemies.\nIf false, sends to all players.")
            private boolean announceToEnemyOnly = false;

            public LandRaidControl(Factions factions) {
            }

            public String getSystem() {
                return this.system;
            }

            public DTR dtr() {
                return this.dtr;
            }

            public Power power() {
                return this.power;
            }

            public boolean isAnnounceRaidable() {
                return this.announceRaidable;
            }

            public boolean isAnnounceNotRaidable() {
                return this.announceNotRaidable;
            }

            public boolean isAnnounceToEnemyOnly() {
                return this.announceToEnemyOnly;
            }

            public class DTR {
                private double startingDTR = 2.0;
                private double maxDTR = 10.0;
                private double minDTR = -3.0;
                private double perPlayer = 1.0;
                private double regainPerMinutePerPlayer = 0.05;
                private double regainPerMinuteMaxRate = 0.1;
                private double lossPerDeath = 1.0;
                @Comment(value="Time, in seconds, to freeze DTR regeneration after a faction member dies")
                private int freezeTime = 0;
                private boolean freezePreventsJoin = true;
                private boolean freezePreventsLeave = true;
                private boolean freezePreventsDisband = true;
                private double freezeKickPenalty = 0.5;
                private String freezeTimeFormat = "H:mm:ss";
                @Comment(value="Additional claims allowed for each player in the faction")
                private int landPerPlayer = 3;
                @Comment(value="Claims the faction starts with.\nNote: A faction of one player has this many PLUS the perPlayer amount.")
                private int landStarting = 6;
                private int decimalDigits = 2;
                private Map<String, Number> worldDeathModifiers = new HashMap<String, Number>(){
                    {
                        this.put("world_nether", 0.5);
                        this.put("world_the_end", 0.25);
                    }
                };
                @Comment(value="DTR stealing. 0 to disable, 1 to give the killing player's faction all of the target player's faction's lost DTR,\n0.5 to give the killing faction half of what was lost, etc.\nNegative values will give the incredibly wild option of taking DTR from the killer's faction too.")
                private double vampirism = 0.0;

                public DTR(LandRaidControl landRaidControl) {
                }

                public int getDecimalDigits() {
                    return this.decimalDigits;
                }

                public int getLandPerPlayer() {
                    return this.landPerPlayer;
                }

                public int getLandStarting() {
                    return this.landStarting;
                }

                public int getFreezeTime() {
                    return this.freezeTime;
                }

                public String getFreezeTimeFormat() {
                    return this.freezeTimeFormat;
                }

                public boolean isFreezePreventsJoin() {
                    return this.freezePreventsJoin;
                }

                public boolean isFreezePreventsLeave() {
                    return this.freezePreventsLeave;
                }

                public boolean isFreezePreventsDisband() {
                    return this.freezePreventsDisband;
                }

                public double getFreezeKickPenalty() {
                    return this.freezeKickPenalty;
                }

                public double getMinDTR() {
                    return this.minDTR;
                }

                public double getPerPlayer() {
                    return this.perPlayer;
                }

                public double getRegainPerMinutePerPlayer() {
                    return this.regainPerMinutePerPlayer;
                }

                public double getRegainPerMinuteMaxRate() {
                    return this.regainPerMinuteMaxRate;
                }

                public double getMaxDTR() {
                    return this.maxDTR;
                }

                public double getStartingDTR() {
                    return this.startingDTR;
                }

                public double getLossPerDeathBase() {
                    return this.lossPerDeath;
                }

                public double getLossPerDeath(World world) {
                    if (this.worldDeathModifiers == null) {
                        this.worldDeathModifiers = new HashMap<String, Number>();
                    }
                    return this.lossPerDeath * this.worldDeathModifiers.getOrDefault(world.getName(), 1.0).doubleValue();
                }

                public double getVampirism() {
                    return this.vampirism;
                }
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
                private Set<String> worldsNoPowerLoss = new HashSet<String>(){
                    {
                        this.add("exampleWorld");
                    }
                };
                private boolean peacefulMembersDisablePowerLoss = true;
                private boolean warZonePowerLoss = true;
                private boolean wildernessPowerLoss = true;
                @Comment(value="Disallow joining/leaving/kicking while power is negative")
                private boolean canLeaveWithNegativePower = true;
                @Comment(value="Allow a faction to be raided if they have more land than power.\nThis will make claimed territory lose all protections\n  allowing factions to open chests, break blocks, etc. if they\n  have more claimed chunks (land) than power. (See raidabilityOnEqualLandAndPower)")
                private boolean raidability = false;
                @Comment(value="Determines if the requirement for raidability is land>=power (true) or\nland>power (false)")
                private boolean raidabilityOnEqualLandAndPower = true;
                @Comment(value="After a player dies, how long should the faction not be able to regen power?\nThis resets on each death but does not accumulate.\nSet to 0 for no freeze. Time is in seconds.")
                private int powerFreeze = 0;
                @Comment(value="Power stealing. 0 to disable, 1 to give the killing player all of the target player's lost power,\n0.5 to give the killing player half of what was lost, etc.\nNegative values will give the incredibly wild option of taking power from the killer too.")
                private double vampirism = 0.0;

                public Power(LandRaidControl landRaidControl) {
                }

                public boolean isRaidability() {
                    return this.raidability;
                }

                public boolean isRaidabilityOnEqualLandAndPower() {
                    return this.raidabilityOnEqualLandAndPower;
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
                    return this.worldsNoPowerLoss == null ? Collections.emptySet() : Collections.unmodifiableSet(this.worldsNoPowerLoss);
                }

                public boolean isPeacefulMembersDisablePowerLoss() {
                    return this.peacefulMembersDisablePowerLoss;
                }

                public double getVampirism() {
                    return this.vampirism;
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
            private String tagValidCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            private transient List<Character> tagValidCharactersList;
            private boolean newFactionsDefaultOpen = false;
            private boolean newFactionsDefaultPeaceful = false;
            @Comment(value="When faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit")
            private int factionMemberLimit = 0;
            @Comment(value="What faction ID to start new players in when they first join the server; default is 0, \"no faction\"")
            private int newPlayerStartingFactionID = 0;
            private double saveToFileEveryXMinutes = 30.0;
            @Comment(value="If anything greater than 0 (can be decimal values like 0.1), players will automatically leave their faction\nafter being inactive for this many days.")
            private double autoLeaveAfterDaysOfInactivity = 10.0;
            @Comment(value="If true, autoleave only processes on players if all faction members meet the inactivity criteria.")
            private boolean autoLeaveOnlyEntireFactionInactive = false;
            private double autoLeaveRoutineRunsEveryXMinutes = 5.0;
            private int autoLeaveRoutineMaxMillisecondsPerTick = 5;
            private boolean removePlayerDataWhenBanned = true;
            private boolean autoLeaveDeleteFPlayerData = true;
            private double considerFactionsReallyOfflineAfterXMinutes = 0.0;
            private int actionDeniedPainAmount = 1;
            @Comment(value="Should we delete player homes that they set via Essentials when they leave a Faction\nif they have homes set in that Faction's territory?")
            private boolean deleteEssentialsHomes = true;
            @Comment(value="Default Relation allows you to change the default relation for Factions.\nExample usage would be so people can't leave then make a new Faction while Raiding\n  in order to be able to execute commands if the default relation is neutral.")
            private String defaultRelation = "neutral";
            @Comment(value="Default role of a player when joining a faction. Can be customized by faction leader\nwith /f defaultrole\nOptions: coleader, moderator, member, recruit\nDefaults to member if set incorrectly")
            private String defaultRole = "member";
            @WipeOnReload
            private transient Role defaultRoleRole;
            @Comment(value="If true, disables pistons entirely within faction territory.\nPrevents flying piston machines in faction territory.")
            private boolean disablePistonsInTerritory = false;
            @Comment(value="Any faction names CONTAINING any of these items will be disallowed")
            private List<String> nameBlacklist = new ArrayList<String>(){
                {
                    this.add("blockedwordhere");
                    this.add("anotherblockedthinghere");
                }
            };
            @Comment(value="Minimum time, in seconds, to display in last seen placeholder (such as in the tooltip).")
            private int minimumLastSeenTime = 3600;

            public Other(Factions factions) {
            }

            public int getMinimumLastSeenTime() {
                return this.minimumLastSeenTime;
            }

            public List<String> getNameBlacklist() {
                return this.nameBlacklist == null ? Collections.emptyList() : Collections.unmodifiableList(this.nameBlacklist);
            }

            public List<Character> getTagValidCharacters() {
                if (this.tagValidCharactersList == null) {
                    ArrayList<Character> arrayList = new ArrayList<Character>();
                    for (char c : this.tagValidCharacters.toCharArray()) {
                        arrayList.add(Character.valueOf(c));
                    }
                    this.tagValidCharactersList = Collections.unmodifiableList(arrayList);
                }
                return this.tagValidCharactersList;
            }

            public boolean isValidTagCharacter(char c) {
                return this.tagValidCharacters.indexOf(c) > -1;
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

            public Role getDefaultRole() {
                if (this.defaultRoleRole == null && (this.defaultRole == null || (this.defaultRoleRole = Role.fromString(this.defaultRole)) == null)) {
                    this.defaultRoleRole = Role.NORMAL;
                }
                return this.defaultRoleRole;
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

            public boolean isNewFactionsDefaultPeaceful() {
                return this.newFactionsDefaultPeaceful;
            }

            public int getFactionMemberLimit() {
                return this.factionMemberLimit;
            }

            public int getNewPlayerStartingFactionID() {
                return this.newPlayerStartingFactionID;
            }

            public double getSaveToFileEveryXMinutes() {
                return this.saveToFileEveryXMinutes;
            }

            public double getAutoLeaveAfterDaysOfInactivity() {
                return this.autoLeaveAfterDaysOfInactivity;
            }

            public boolean isAutoLeaveOnlyEntireFactionInactive() {
                return this.autoLeaveOnlyEntireFactionInactive;
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
            private String title = "{faction-relation-color}{faction}";
            private String subtitle = "&7{description}";

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

            public String getTitle() {
                return this.title;
            }

            public String getSubtitle() {
                return this.subtitle;
            }
        }

        public class Spawning {
            private Set<String> preventSpawningInSafezone = new HashSet<String>(){
                {
                    this.add("BREEDING");
                    this.add("BUILD_IRONGOLEM");
                    this.add("BUILD_SNOWMAN");
                    this.add("BUILD_WITHER");
                    this.add("CURED");
                    this.add("DEFAULT");
                    this.add("DISPENSE_EGG");
                    this.add("DROWNED");
                    this.add("EGG");
                    this.add("ENDER_PEARL");
                    this.add("EXPLOSION");
                    this.add("INFECTION");
                    this.add("LIGHTNING");
                    this.add("MOUNT");
                    this.add("NATURAL");
                    this.add("NETHER_PORTAL");
                    this.add("OCELOT_BABY");
                    this.add("PATROL");
                    this.add("RAID");
                    this.add("REINFORCEMENTS");
                    this.add("SILVERFISH_BLOCK");
                    this.add("SLIME_SPLIT");
                    this.add("SPAWNER");
                    this.add("SPAWNER_EGG");
                    this.add("TRAP");
                    this.add("VILLAGE_DEFENSE");
                    this.add("VILLAGE_INVASION");
                }
            };
            @WipeOnReload
            private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInSafezoneReason;
            private Set<String> preventSpawningInSafezoneExceptions = new HashSet<String>(){
                {
                    this.add("AXOLOTL");
                    this.add("BAT");
                    this.add("CAT");
                    this.add("CHICKEN");
                    this.add("COD");
                    this.add("COW");
                    this.add("DOLPHIN");
                    this.add("DONKEY");
                    this.add("FOX");
                    this.add("HORSE");
                    this.add("IRON_GOLEM");
                    this.add("GLOW_SQUID");
                    this.add("LLAMA");
                    this.add("MULE");
                    this.add("MUSHROOM_COW");
                    this.add("OCELOT");
                    this.add("PANDA");
                    this.add("PARROT");
                    this.add("PIG");
                    this.add("POLAR_BEAR");
                    this.add("PUFFERFISH");
                    this.add("RABBIT");
                    this.add("SALMON");
                    this.add("SHEEP");
                    this.add("STRIDER");
                    this.add("SQUID");
                    this.add("TRADER_LLAMA");
                    this.add("TROPICAL_FISH");
                    this.add("TURTLE");
                    this.add("VILLAGER");
                    this.add("WANDERING_TRADER");
                    this.add("WOLF");
                }
            };
            @WipeOnReload
            private transient Set<EntityType> preventSpawningInSafezoneExceptionsType;
            private Set<String> preventSpawningInWarzone = new HashSet<String>();
            @WipeOnReload
            private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInWarzoneReason;
            private Set<String> preventSpawningInWarzoneExceptions = new HashSet<String>();
            @WipeOnReload
            private transient Set<EntityType> preventSpawningInWarzoneExceptionsType;
            private Set<String> preventSpawningInWilderness = new HashSet<String>();
            @WipeOnReload
            private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInWildernessReason;
            private Set<String> preventSpawningInWildernessExceptions = new HashSet<String>();
            @WipeOnReload
            private transient Set<EntityType> preventSpawningInWildernessExceptionsType;
            private Set<String> preventSpawningInTerritory = new HashSet<String>();
            @WipeOnReload
            private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInTerritoryReason;
            private Set<String> preventSpawningInTerritoryExceptions = new HashSet<String>();
            @WipeOnReload
            private transient Set<EntityType> preventSpawningInTerritoryExceptionsType;
            @Comment(value="If true, FactionsUUID will automatically add in its new defaults such as\nadding new friendly mobs to the safe zone exception list")
            private boolean updateAutomatically = true;

            public Spawning(Factions factions) {
            }

            public boolean isUpdateAutomatically() {
                return this.updateAutomatically;
            }

            public Set<CreatureSpawnEvent.SpawnReason> getPreventInSafezone() {
                if (this.preventSpawningInSafezoneReason == null) {
                    this.preventSpawningInSafezoneReason = MiscUtil.typeSetFromStringSet(this.preventSpawningInSafezone, MiscUtil.SPAWN_REASON_FUNCTION);
                }
                return this.preventSpawningInSafezoneReason;
            }

            public Set<EntityType> getPreventInSafezoneExceptions() {
                if (this.preventSpawningInSafezoneExceptionsType == null) {
                    this.preventSpawningInSafezoneExceptionsType = MiscUtil.typeSetFromStringSet(this.preventSpawningInSafezoneExceptions, MiscUtil.ENTITY_TYPE_FUNCTION);
                }
                return this.preventSpawningInSafezoneExceptionsType;
            }

            public Set<CreatureSpawnEvent.SpawnReason> getPreventInTerritory() {
                if (this.preventSpawningInTerritoryReason == null) {
                    this.preventSpawningInTerritoryReason = MiscUtil.typeSetFromStringSet(this.preventSpawningInTerritory, MiscUtil.SPAWN_REASON_FUNCTION);
                }
                return this.preventSpawningInTerritoryReason;
            }

            public Set<EntityType> getPreventInTerritoryExceptions() {
                if (this.preventSpawningInTerritoryExceptionsType == null) {
                    this.preventSpawningInTerritoryExceptionsType = MiscUtil.typeSetFromStringSet(this.preventSpawningInTerritoryExceptions, MiscUtil.ENTITY_TYPE_FUNCTION);
                }
                return this.preventSpawningInTerritoryExceptionsType;
            }

            public Set<CreatureSpawnEvent.SpawnReason> getPreventInWarzone() {
                if (this.preventSpawningInWarzoneReason == null) {
                    this.preventSpawningInWarzoneReason = MiscUtil.typeSetFromStringSet(this.preventSpawningInWarzone, MiscUtil.SPAWN_REASON_FUNCTION);
                }
                return this.preventSpawningInWarzoneReason;
            }

            public Set<EntityType> getPreventInWarzoneExceptions() {
                if (this.preventSpawningInWarzoneExceptionsType == null) {
                    this.preventSpawningInWarzoneExceptionsType = MiscUtil.typeSetFromStringSet(this.preventSpawningInWarzoneExceptions, MiscUtil.ENTITY_TYPE_FUNCTION);
                }
                return this.preventSpawningInWarzoneExceptionsType;
            }

            public Set<CreatureSpawnEvent.SpawnReason> getPreventInWilderness() {
                if (this.preventSpawningInWildernessReason == null) {
                    this.preventSpawningInWildernessReason = MiscUtil.typeSetFromStringSet(this.preventSpawningInWilderness, MiscUtil.SPAWN_REASON_FUNCTION);
                }
                return this.preventSpawningInWildernessReason;
            }

            public Set<EntityType> getPreventInWildernessExceptions() {
                if (this.preventSpawningInWildernessExceptionsType == null) {
                    this.preventSpawningInWildernessExceptionsType = MiscUtil.typeSetFromStringSet(this.preventSpawningInWildernessExceptions, MiscUtil.ENTITY_TYPE_FUNCTION);
                }
                return this.preventSpawningInWildernessExceptionsType;
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

        public Logging(MainConfig mainConfig) {
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
        @Comment(value="If true, prevents water flow into claimed territory")
        private boolean liquidFlow = false;
        private boolean preventDuping = true;

        public Exploits(MainConfig mainConfig) {
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
        @Comment(value="\n******************\n\nThe value \"enabled\" must be true for any economy features\nMake sure that you confirm the \"defaultWorld\" setting is a valid world name\n\n******************\n")
        private boolean enabled = false;
        private String universeAccount = "";
        @Comment(value="This setting matters in particular if you have per-world economy.\nThis setting is the world to use for:\n faction banks,\n the universe account (if used),\n transferring money to a player who is presently offline,\n or any other situation where the player's world is unknown.\n\nNote that you should set up your per-world plugin to treat all your Factions worlds as one group/world.")
        private String defaultWorld = "world";
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
        private double costDTR = 0.0;
        private double costOpen = 0.0;
        private double costAlly = 0.0;
        private double costTruce = 0.0;
        private double costEnemy = 0.0;
        private double costNeutral = 0.0;
        private double costNoBoom = 0.0;
        private double costWarp = 0.0;
        private double costSetWarp = 0.0;
        private double costDelWarp = 0.0;
        @Comment(value="Faction banks, to pay for land claiming and other costs instead of individuals paying for them\nThis IS NOT the setting for enabling economy features overall. That setting is just named \"enabled\"")
        private boolean bankEnabled = true;
        @Comment(value="Have to be at least moderator to withdraw or pay money to another faction")
        private boolean bankMembersCanWithdraw = false;
        @Comment(value="The faction pays for faction command costs, such as sethome")
        private boolean bankFactionPaysCosts = true;
        @Comment(value="The faction pays for land claiming costs.")
        private boolean bankFactionPaysLandCosts = true;
        @Comment(value="If true, the bank balance will transfer to a player leaving a permanent faction that is about to have 0 players in it\nSet to false to keep the balance in an empty permanent faction")
        private boolean bankPermanentFactionSendBalanceToLastLeaver = true;

        public Economy(MainConfig mainConfig) {
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public String getDefaultWorld() {
            return this.defaultWorld;
        }

        public double getCostDTR() {
            return this.costDTR;
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

        public boolean isBankPermanentFactionSendBalanceToLastLeaver() {
            return this.bankPermanentFactionSendBalanceToLastLeaver;
        }
    }

    public class MapSettings {
        private int height = 17;
        private int width = 49;
        private int scoreboardHeight = 7;
        private int scoreboardWidth = 7;
        private boolean showFactionKey = true;
        private boolean showNeutralFactionsOnMap = true;
        private boolean showEnemyFactions = true;
        private boolean showTruceFactions = true;
        private String selfColor = "AQUA";
        @WipeOnReload
        private transient TextColor selfColorColor;

        public MapSettings(MainConfig mainConfig) {
        }

        public TextColor getSelfColor() {
            this.selfColorColor = MainConfig.getColor(this.selfColor, this.selfColorColor, NamedTextColor.GREEN);
            return this.selfColorColor;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        public int getScoreboardHeight() {
            return this.scoreboardHeight;
        }

        public int getScoreboardWidth() {
            return this.scoreboardWidth;
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

        public Data(MainConfig mainConfig) {
        }

        public Json json() {
            return this.json;
        }

        public class Json {
            @Comment(value="If true, data files will be stored without extra whitespace and linebreaks.\nThis becomes less readable, but can cut storage use in half.")
            private boolean efficientStorage = false;
            @Comment(value="If true, even players with the default power will be saved")
            private boolean saveAllPlayers = false;

            public Json(Data data) {
            }

            public boolean isSaveAllPlayers() {
                return this.saveAllPlayers;
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
        private Set<String> worldList = new HashSet<String>(){
            {
                this.add("exampleWorld");
            }
        };

        public RestrictWorlds(MainConfig mainConfig) {
        }

        public boolean isRestrictWorlds() {
            return this.restrictWorlds;
        }

        public boolean isWhitelist() {
            return this.whitelist;
        }

        public Set<String> getWorldList() {
            return this.worldList == null ? Collections.emptySet() : Collections.unmodifiableSet(this.worldList);
        }
    }

    public class Scoreboard {
        @Comment(value="Constant scoreboard stays around all the time, displaying status info.\nAlso, if prefixes are enabled while it is enabled, will show prefixes on nametags and tab")
        private Constant constant = new Constant(this);
        @Comment(value="Info scoreboard is displayed when a player walks into a new Faction's territory.\nScoreboard disappears after <expiration> seconds.")
        private Info info = new Info(this);

        public Scoreboard(MainConfig mainConfig) {
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
            @Comment(value="Set the length limit for prefixes.\nIf 0, will use a sane default for your Minecraft version (16 for pre-1.13, 32 for 1.13+).")
            private int prefixLength = 0;
            @Comment(value="Takes {relationcolor}, {faction}, player-specific tags, &-prefixed color codes")
            private String prefixTemplate = "{relationcolor}[{faction}] &r";
            @Comment(value="If true, show suffixes on nametags and in tab list if scoreboard is enabled")
            private boolean suffixes = false;
            @Comment(value="Set the length limit for suffixes.\nIf 0, will use a sane default for your Minecraft version (16 for pre-1.13, 32 for 1.13+).")
            private int suffixLength = 0;
            @Comment(value="Takes {relationcolor}, {faction}, player-specific tags, &-prefixed color codes")
            private String suffixTemplate = " {relationcolor}[{faction}]";
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
            private String factionlessTitle = "Status";

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

            public int getPrefixLength() {
                return this.prefixLength < 1 ? 32 : this.prefixLength;
            }

            public String getPrefixTemplate() {
                return this.prefixTemplate;
            }

            public boolean isSuffixes() {
                return this.suffixes;
            }

            public int getSuffixLength() {
                return this.suffixLength < 1 ? 32 : this.suffixLength;
            }

            public String getSuffixTemplate() {
                return this.suffixTemplate;
            }

            public List<String> getContent() {
                return this.content != null ? Collections.unmodifiableList(this.content) : Collections.emptyList();
            }

            public boolean isFactionlessEnabled() {
                return this.factionlessEnabled;
            }

            public List<String> getFactionlessContent() {
                return this.factionlessContent != null ? Collections.unmodifiableList(this.factionlessContent) : Collections.emptyList();
            }

            public String getFactionlessTitle() {
                return this.factionlessTitle;
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
            private String title = "{faction-relation-color}{faction}";

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
                return this.content != null ? Collections.unmodifiableList(this.content) : Collections.emptyList();
            }

            public String getTitle() {
                return this.title;
            }
        }
    }

    public class LWC {
        private boolean enabled = true;
        private boolean resetLocksOnUnclaim = false;
        private boolean resetLocksOnCapture = false;

        public LWC(MainConfig mainConfig) {
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

    public class MagicPlugin {
        @Comment(value="If true, magic mobs will follow whatever pvp allowed/disallowed setting is present for the territory they're attacking into.")
        private boolean usePVPSettingForMagicMobs = false;

        public MagicPlugin(MainConfig mainConfig) {
        }

        public boolean isUsePVPSettingForMagicMobs() {
            return this.usePVPSettingForMagicMobs;
        }
    }

    public class Paper {
        @Comment(value="Utilize Paper's async teleportation if available (Paper 1.9+).")
        private boolean asyncTeleport = true;

        public Paper(MainConfig mainConfig) {
        }

        public boolean isAsyncTeleport() {
            return this.asyncTeleport;
        }
    }

    public class Plugins {
        @Comment(value="EssentialsX")
        private EssentialsX essentialsX = new EssentialsX(this);
        @Comment(value="Ranull's Graves plugin")
        private Graves graves = new Graves(this);

        public Plugins(MainConfig mainConfig) {
        }

        public EssentialsX essentialsX() {
            return this.essentialsX;
        }

        public Graves graves() {
            return this.graves;
        }

        public class EssentialsX {
            @Comment(value="If true, prevents regeneration of dtr/power while marked as AFK")
            private boolean preventRegenWhileAfk = false;

            public EssentialsX(Plugins plugins) {
            }

            public boolean isPreventRegenWhileAfk() {
                return this.preventRegenWhileAfk;
            }
        }

        public class Graves {
            @Comment(value="If true, will allow any Graves plugin graves to be opened by anyone, regardless of permissions")
            private boolean allowAnyoneToOpenGraves = false;
            private boolean preventGravesInSafezone = false;
            private boolean preventGravesInWarzone = false;

            public Graves(Plugins plugins) {
            }

            public boolean isAllowAnyoneToOpenGraves() {
                return this.allowAnyoneToOpenGraves;
            }

            public boolean isPreventGravesInSafezone() {
                return this.preventGravesInSafezone;
            }

            public boolean isPreventGravesInWarzone() {
                return this.preventGravesInWarzone;
            }
        }
    }

    public class PlayerVaults {
        @Comment(value="The %s is for the faction id")
        private String vaultPrefix = "faction-%s";
        private int defaultMaxVaults = 0;

        public PlayerVaults(MainConfig mainConfig) {
        }

        public String getVaultPrefix() {
            return this.vaultPrefix;
        }

        public int getDefaultMaxVaults() {
            return this.defaultMaxVaults;
        }
    }

    public class WorldGuard {
        @Comment(value="If true, disallows claiming a chunk if any WG region is at least partially inside the chunk")
        private boolean checking = false;
        @Comment(value="If true, disallows claiming a chunk if any WG region with the flag 'fuuid-claim' denied for the claiming player is at least partially inside the chunk")
        private boolean checkingFlag = false;
        @Comment(value="If true, allows building in a WG region where the player has the BUILD flag")
        private boolean buildPriority = false;
        @Comment(value="If true, allows pvp in a WG region where the flag `fuuid-pvp` is allowed")
        private boolean pvpPriority = false;

        public WorldGuard(MainConfig mainConfig) {
        }

        public boolean isChecking() {
            return this.checking;
        }

        public boolean isCheckingFlag() {
            return this.checkingFlag;
        }

        public boolean isCheckingEither() {
            return this.checking || this.checkingFlag;
        }

        public boolean isBuildPriority() {
            return this.buildPriority;
        }

        public boolean isPVPPriority() {
            return this.pvpPriority;
        }
    }

    public class WorldBorder {
        @Comment(value="WorldBorder support\nThis is for Minecraft's built-in command. To get your current border: /minecraft:worldborder get\nA buffer of 0 means faction claims can go right up to the border of the world.\nThe buffer is in chunks, so 1 as a buffer means an entire chunk of buffer between\nthe border of the world and what can be claimed to factions")
        private int buffer = 0;

        public WorldBorder(MainConfig mainConfig) {
        }

        public int getBuffer() {
            return this.buffer;
        }
    }
}

