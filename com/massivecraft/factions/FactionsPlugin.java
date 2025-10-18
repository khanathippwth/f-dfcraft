/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.authlib.GameProfile
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredListener
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.massivecraft.factions;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsAPI;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.config.ConfigManager;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.config.file.TranslationsConfig;
import com.massivecraft.factions.data.SaveTask;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.event.FactionEvent;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.event.FactionsPluginRegistrationTimeEvent;
import com.massivecraft.factions.integration.ClipPlaceholderAPIManager;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.IntegrationManager;
import com.massivecraft.factions.integration.LWC;
import com.massivecraft.factions.integration.LuckPerms;
import com.massivecraft.factions.integration.VaultPerms;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.integration.dynmap.EngineDynmap;
import com.massivecraft.factions.integration.permcontext.ContextManager;
import com.massivecraft.factions.landraidcontrol.LandRaidControl;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsChatListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.listeners.FactionsExploitListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.listeners.versionspecific.PortalListener_114;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.PermSelectorRegistry;
import com.massivecraft.factions.perms.PermSelectorTypeAdapter;
import com.massivecraft.factions.perms.PermissibleActionRegistry;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.util.AutoLeaveTask;
import com.massivecraft.factions.util.EnumTypeAdapter;
import com.massivecraft.factions.util.FlightUtil;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.MapFLocToStringSetTypeAdapter;
import com.massivecraft.factions.util.Metrics;
import com.massivecraft.factions.util.MyLocationTypeAdapter;
import com.massivecraft.factions.util.PermUtil;
import com.massivecraft.factions.util.Persist;
import com.massivecraft.factions.util.SeeChunkUtil;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import com.massivecraft.factions.util.WorldUtil;
import com.massivecraft.factions.util.material.MaterialDb;
import com.massivecraft.factions.util.particle.BukkitParticleProvider;
import com.mojang.authlib.GameProfile;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import moss.factions.shade.io.paperlib.PaperLib;
import moss.factions.shade.net.kyori.adventure.audience.Audience;
import moss.factions.shade.net.kyori.adventure.platform.bukkit.BukkitAudiences;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.TextComponent;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FactionsPlugin
extends JavaPlugin
implements FactionsAPI {
    private static FactionsPlugin instance;
    private static int mcVersion;
    private static final int OLDEST_MODERN_SUPPORTED = 2004;
    private static final String OLDEST_MODERN_SUPPORTED_STRING = "1.20.4";
    private ConfigManager configManager;
    private Integer saveTask = null;
    private boolean autoSave = true;
    private boolean loadSuccessful = false;
    private Persist persist;
    private TextUtil txt;
    private WorldUtil worldUtil;
    private PermUtil permUtil;
    private Gson gson;
    private final Map<UUID, Long> timers = new HashMap<UUID, Long>();
    private final Map<UUID, Integer> stuckMap = new HashMap<UUID, Integer>();
    private boolean locked = false;
    private Integer autoLeaveTask = null;
    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
    private boolean mvdwPlaceholderAPIManager = false;
    private final Set<String> pluginsHandlingChat = Collections.newSetFromMap(new ConcurrentHashMap());
    private SeeChunkUtil seeChunkUtil;
    private BukkitParticleProvider particleProvider;
    private Worldguard worldguard;
    private LandRaidControl landRaidControl;
    private boolean luckPermsSetup;
    private IntegrationManager integrationManager;
    private Metrics metrics;
    private final Pattern factionsVersionPattern = Pattern.compile("b(\\d{1,4})");
    private UUID serverUUID;
    private String startupLog = "NOTFINISHED";
    private String startupExceptionLog = "NOTFINISHED";
    private final List<RuntimeException> grumpyExceptions = new ArrayList<RuntimeException>();
    private VaultPerms vaultPerms;
    public final boolean likesCats = Arrays.stream(FactionsPlugin.class.getDeclaredMethods()).anyMatch(method -> method.isSynthetic() && method.getName().startsWith("loadCon") && method.getName().endsWith("0"));
    private Method getOffline;
    private BukkitAudiences adventure;
    private String mcVersionString;
    private String updateCheck;
    private Response updateResponse;
    private final Map<String, String> rawTags = new LinkedHashMap<String, String>();
    private final Set<UUID> told = new HashSet<UUID>();

    public static FactionsPlugin getInstance() {
        return instance;
    }

    public static int getMCVersion() {
        return mcVersion;
    }

    public TextUtil txt() {
        return this.txt;
    }

    public WorldUtil worldUtil() {
        return this.worldUtil;
    }

    public void grumpException(RuntimeException runtimeException) {
        this.grumpyExceptions.add(runtimeException);
    }

    public FactionsPlugin() {
        instance = this;
    }

    public void onLoad() {
        IntegrationManager.onLoad(this);
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            Worldguard.onLoad();
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void onEnable() {
        Faction faction;
        String string;
        FactionsPlugin.loadConfig0();
        this.loadSuccessful = false;
        this.adventure = BukkitAudiences.create((Plugin)this);
        final StringBuilder stringBuilder = new StringBuilder();
        final StringBuilder stringBuilder2 = new StringBuilder();
        final Handler handler = new Handler(this){

            @Override
            public void publish(LogRecord logRecord) {
                if (logRecord.getMessage() != null && logRecord.getMessage().contains("Loaded class {0}")) {
                    return;
                }
                stringBuilder.append('[').append(logRecord.getLevel().getName()).append("] ").append(logRecord.getMessage()).append('\n');
                if (logRecord.getThrown() != null) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    logRecord.getThrown().printStackTrace(printWriter);
                    stringBuilder2.append('[').append(logRecord.getLevel().getName()).append("] ").append(logRecord.getMessage()).append('\n').append(stringWriter).append('\n');
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        this.getLogger().addHandler(handler);
        this.getLogger().info("=== Starting up! ===");
        long l = System.currentTimeMillis();
        if (!this.grumpyExceptions.isEmpty()) {
            this.grumpyExceptions.forEach(runtimeException -> this.getLogger().log(Level.WARNING, "Found issue with plugin touching FactionsUUID before it starts up!", (Throwable)runtimeException));
        }
        UpdateCheck updateCheck = new UpdateCheck("FactionsUUID", this.getDescription().getVersion(), this.getServer().getName(), this.getServer().getVersion());
        updateCheck.meow = this.getClass().getDeclaredMethods().length;
        this.getDataFolder().mkdirs();
        byte[] byArray = Bukkit.getMotd().getBytes(StandardCharsets.UTF_8);
        if (byArray.length == 0) {
            byArray = new byte[]{107, 105, 116, 116, 101, 110};
        }
        updateCheck.spigotId = "2135045";
        int n = this.intOr("2135045", 987654321);
        int n2 = this.intOr("566391800", 1234567890);
        int n3 = 0;
        int n4 = Math.min(Bukkit.getMaxPlayers(), 65535);
        long l2 = 20396L;
        if (n2 != 1234567890) {
            l2 += ((long)n2 & 0xFFFFFFFFL) << 32;
            n3 = 4;
        }
        int n5 = 0;
        while (n3 < 6) {
            if (n5 == byArray.length) {
                n5 = 0;
            }
            l2 += ((long)byArray[n5] & 0xFFL) << 8 + 8 * (6 - n3);
            ++n5;
            ++n3;
        }
        this.serverUUID = new UUID(l2, -5788251421077929984L + ((long)n & 0xFFFFFFFFL) + (((long)n4 & 0xFFFFL) << 32));
        Pattern pattern = Pattern.compile("1\\.(\\d{1,2})(?:\\.(\\d{1,2}))?");
        Matcher matcher = pattern.matcher(this.getServer().getVersion());
        this.getLogger().info("");
        this.getLogger().info("Factions UUID!");
        this.getLogger().info("Version " + this.getDescription().getVersion());
        this.getLogger().info("");
        this.getLogger().info("Need support? https://factions.support/help/");
        this.getLogger().info("");
        Integer n6 = null;
        if (matcher.find()) {
            try {
                int n7 = Integer.parseInt(matcher.group(1));
                string = matcher.group(2);
                int n8 = string == null || string.isEmpty() ? 0 : Integer.parseInt(string);
                n6 = n7 * 100 + n8;
                this.mcVersionString = "1." + n7 + (String)(string == null ? "" : "." + string);
                this.getLogger().info("Detected Minecraft " + matcher.group());
            } catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if (n6 == null) {
            this.getLogger().warning("");
            this.getLogger().warning("Could not identify version. Going with least supported version, 1.20.4.");
            this.getLogger().warning("Please visit our support live chat for help - https://factions.support/help/");
            this.getLogger().warning("");
            n6 = 2004;
            this.mcVersionString = this.getServer().getVersion();
        }
        if ((mcVersion = n6.intValue()) < 2004) {
            this.getLogger().info("");
            this.getLogger().warning("FactionsUUID expects at least 1.20.4 and may not work on your version.");
        }
        this.getLogger().info("");
        this.getLogger().info("Server UUID " + String.valueOf(this.serverUUID));
        this.loadLang();
        this.gson = this.getGsonBuilder(true).create();
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfigs();
        this.gson = this.getGsonBuilder(false).create();
        if (this.conf().data().json().useEfficientStorage()) {
            this.getLogger().info("Using space efficient (less readable) storage.");
        }
        this.landRaidControl = LandRaidControl.getByName(this.conf().factions().landRaidControl().getSystem());
        File file = new File(this.getDataFolder(), "data");
        if (!file.exists()) {
            file.mkdir();
        }
        MaterialDb.load();
        this.permUtil = new PermUtil(this);
        this.persist = new Persist(this);
        this.worldUtil = new WorldUtil(this);
        this.txt = new TextUtil(mcVersion < 1600);
        this.initTXT();
        string = "";
        try {
            Map map = this.getDescription().getCommands();
            if (map != null && !map.isEmpty()) {
                string = (String)map.keySet().toArray()[0];
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (this.saveTask == null && this.conf().factions().other().getSaveToFileEveryXMinutes() > 0.0) {
            long l3 = (long)(1200.0 * this.conf().factions().other().getSaveToFileEveryXMinutes());
            this.saveTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new SaveTask(this), l3, l3);
        }
        int n9 = FPlayers.getInstance().load();
        int n10 = Factions.getInstance().load();
        for (FPlayer object2 : FPlayers.getInstance().getAllFPlayers()) {
            faction = Factions.getInstance().getFactionById(object2.getFactionIntId());
            if (faction == null) {
                this.log("Invalid faction id on " + object2.getName() + ":" + object2.getFactionIntId());
                object2.resetFactionData(false);
                continue;
            }
            faction.addFPlayer(object2);
        }
        int n11 = Board.getInstance().load();
        Board.getInstance().clean();
        FactionsPlugin.getInstance().getLogger().info("Loaded " + n9 + " players in " + n10 + " factions with " + n11 + " claims");
        final FCmdRoot fCmdRoot = new FCmdRoot();
        ContextManager.init(this);
        if (this.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
            this.getLogger().info(" ");
            this.getLogger().warning("Notice: PermissionsEx dead. We suggest using LuckPerms. https://luckperms.net/");
            this.getLogger().info(" ");
        }
        if (this.getServer().getPluginManager().getPlugin("GroupManager") != null) {
            this.getLogger().info(" ");
            this.getLogger().warning("Notice: GroupManager died in 2014. We suggest using LuckPerms instead. https://luckperms.net/");
            this.getLogger().info(" ");
        }
        if ((faction = this.getServer().getPluginManager().getPlugin("LWC")) != null && faction.getDescription().getWebsite() != null && !faction.getDescription().getWebsite().contains("extended")) {
            this.getLogger().info(" ");
            this.getLogger().warning("Notice: LWC Extended is the updated, and best supported, continuation of LWC. https://www.spigotmc.org/resources/lwc-extended.69551/");
            this.getLogger().info(" ");
        }
        this.startAutoLeaveTask(false);
        this.particleProvider = new BukkitParticleProvider();
        if (this.conf().commands().seeChunk().isParticles()) {
            double exception = Math.floor(this.conf().commands().seeChunk().getParticleUpdateTime() * 20.0);
            this.seeChunkUtil = new SeeChunkUtil();
            this.seeChunkUtil.runTaskTimer((Plugin)this, 0L, (long)exception);
        }
        this.getServer().getPluginManager().registerEvents((Listener)new FactionsPlayerListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new FactionsChatListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new FactionsEntityListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new FactionsExploitListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new FactionsBlockListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PortalListener_114(this), (Plugin)this);
        this.getCommand(string).setExecutor((CommandExecutor)fCmdRoot);
        if (this.conf().commands().fly().isEnable()) {
            FlightUtil.start();
        }
        try {
            this.getOffline = this.getServer().getClass().getDeclaredMethod("getOfflinePlayer", GameProfile.class);
        } catch (Exception exception) {
            this.getLogger().log(Level.WARNING, "Faction economy lookups will be slower:", exception);
        }
        if (ChatColor.stripColor((String)TL.NOFACTION_PREFIX.toString()).equals("[4-]")) {
            this.getLogger().warning("Looks like you have an old, mistaken 'nofactions-prefix' in your lang.yml. It currently displays [4-] which is... strange.");
        }
        this.integrationManager = new IntegrationManager(this);
        this.getServer().getPluginManager().registerEvents((Listener)this.integrationManager, (Plugin)this);
        new BukkitRunnable(){

            public void run() {
                FactionsPlugin.this.getServer().getPluginManager().callEvent((Event)new FactionsPluginRegistrationTimeEvent());
                try {
                    Method method = PermissibleActionRegistry.class.getDeclaredMethod("close", new Class[0]);
                    method.setAccessible(true);
                    method.invoke(null, new Object[0]);
                    method = PermSelectorRegistry.class.getDeclaredMethod("close", new Class[0]);
                    method.setAccessible(true);
                    method.invoke(null, new Object[0]);
                } catch (Exception exception) {
                    FactionsPlugin.this.getLogger().log(Level.SEVERE, "Failed to close registries", exception);
                }
                Econ.setup();
                FactionsPlugin.this.vaultPerms = new VaultPerms();
                fCmdRoot.done();
                FactionsPlugin.this.setupMetrics();
                FactionsPlugin.this.getLogger().removeHandler(handler);
                FactionsPlugin.this.startupLog = stringBuilder.toString();
                FactionsPlugin.this.startupExceptionLog = stringBuilder2.toString();
            }
        }.runTask((Plugin)this);
        this.getLogger().info("=== Initial start took " + (System.currentTimeMillis() - l) + "ms! ===");
        this.loadSuccessful = true;
        this.updateCheck = new Gson().toJson((Object)updateCheck);
        if (!this.likesCats) {
            return;
        }
        new BukkitRunnable(){

            public void run() {
                try {
                    URL uRL = new URI("https://update.plugin.party/check").toURL();
                    HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    try (Object object = httpURLConnection.getOutputStream();){
                        ((OutputStream)object).write(FactionsPlugin.this.updateCheck.getBytes(StandardCharsets.UTF_8));
                    }
                    object = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
                    Response response = (Response)new Gson().fromJson((String)object, Response.class);
                    if (response.isSuccess()) {
                        if (response.isUpdateAvailable()) {
                            FactionsPlugin.this.updateResponse = response;
                            if (response.isUrgent()) {
                                FactionsPlugin.this.getServer().getOnlinePlayers().forEach(FactionsPlugin.this::updateNotification);
                            }
                            FactionsPlugin.this.getLogger().warning("Update available: " + response.getLatestVersion() + (String)(response.getMessage() == null ? "" : " - " + response.getMessage()));
                        }
                    } else if (response.getMessage().equals("INVALID")) {
                        this.cancel();
                    } else if (!response.getMessage().equals("TOO_FAST")) {
                        FactionsPlugin.this.getLogger().warning("Failed to check for updates: " + response.getMessage());
                    }
                } catch (Exception exception) {
                    // empty catch block
                }
            }
        }.runTaskTimerAsynchronously((Plugin)this, 1L, 72000L);
    }

    private int intOr(String string, int n) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException numberFormatException) {
            return n;
        }
    }

    private void setupMetrics() {
        Plugin plugin;
        String string;
        String string2;
        this.metrics = new Metrics(this);
        String string3 = this.getDescription().getVersion();
        Pattern pattern = Pattern.compile("1\\.6\\.9\\.5-U(?<version>\\d{1,2}\\.\\d{1,2}\\.\\d{1,2})(?<snap>-SNAPSHOT)?");
        Matcher matcher = pattern.matcher(string3);
        if (matcher.find()) {
            string2 = matcher.group(1);
            string = matcher.group("snap") == null ? (this.likesCats ? "release" : "yarr") : "snapshot";
        } else {
            string2 = "Unknown";
            string = string3;
        }
        this.metricsDrillPie("fuuid_version", () -> {
            HashMap hashMap = new HashMap();
            HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
            hashMap2.put(string, 1);
            hashMap.put(string2, hashMap2);
            return hashMap;
        });
        this.metricsDrillPie("fuuid_version_mc", () -> {
            HashMap hashMap = new HashMap();
            HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
            hashMap2.put(this.mcVersionString, 1);
            hashMap.put(string2, hashMap2);
            return hashMap;
        });
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            plugin = Essentials.getEssentials();
            this.metricsDrillPie("essentials", () -> this.metricsPluginInfo(plugin));
            if (plugin != null) {
                this.metricsSimplePie("essentials_delete_homes", () -> "" + this.conf().factions().other().isDeleteEssentialsHomes());
                this.metricsSimplePie("essentials_home_teleport", () -> "" + this.conf().factions().homes().isTeleportCommandEssentialsIntegration());
            }
        }
        plugin = LWC.getLWC();
        this.metricsDrillPie("lwc", () -> this.metricsPluginInfo(plugin));
        if (plugin != null) {
            boolean bl = this.conf().lwc().isEnabled();
            this.metricsSimplePie("lwc_integration", () -> "" + bl);
            if (bl) {
                this.metricsSimplePie("lwc_reset_locks_unclaim", () -> "" + this.conf().lwc().isResetLocksOnUnclaim());
                this.metricsSimplePie("lwc_reset_locks_capture", () -> "" + this.conf().lwc().isResetLocksOnCapture());
            }
        }
        Plugin plugin2 = Bukkit.getServer().getPluginManager().getPlugin("Vault");
        this.metricsDrillPie("vault", () -> this.metricsPluginInfo(plugin2));
        if (plugin2 != null) {
            this.metricsDrillPie("vault_perms", () -> this.metricsInfo(this.vaultPerms.getPerms(), () -> this.vaultPerms.getName()));
            this.metricsDrillPie("vault_econ", () -> {
                HashMap hashMap = new HashMap();
                HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
                hashMap2.put(Econ.getEcon() == null ? "none" : Econ.getEcon().getName(), 1);
                hashMap.put(this.conf().economy().isEnabled() && Econ.getEcon() != null ? "enabled" : "disabled", hashMap2);
                return hashMap;
            });
        }
        this.metricsSimplePie("luckperms_contexts", () -> "" + this.luckPermsSetup);
        Worldguard worldguard = this.getWorldguard();
        String string4 = worldguard == null ? "nope" : worldguard.getVersion();
        this.metricsDrillPie("worldguard", () -> this.metricsInfo(worldguard, () -> string4));
        String string5 = EngineDynmap.getInstance().getVersion();
        boolean bl = EngineDynmap.getInstance().isRunning();
        this.metricsDrillPie("dynmap", () -> {
            HashMap hashMap = new HashMap();
            HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
            hashMap2.put(string5 == null ? "none" : string5, 1);
            hashMap.put(bl ? "enabled" : "disabled", hashMap2);
            return hashMap;
        });
        Plugin plugin3 = this.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        this.metricsDrillPie("clipplaceholder", () -> this.metricsPluginInfo(plugin3));
        Plugin plugin4 = this.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI");
        this.metricsDrillPie("mvdwplaceholder", () -> this.metricsPluginInfo(plugin4));
        this.metricsLine("factions", () -> Factions.getInstance().getAllFactions().size() - 3);
        this.metricsSimplePie("scoreboard", () -> "" + this.conf().scoreboard().constant().isEnabled());
        this.metricsDrillPie("event_listeners", () -> {
            Set<Plugin> set = this.getPlugins(FactionEvent.getHandlerList(), FactionCreateEvent.getHandlerList(), FactionRelationEvent.getHandlerList());
            HashMap hashMap = new HashMap();
            for (Plugin plugin : set) {
                if (plugin.getName().equalsIgnoreCase("factions")) continue;
                HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
                hashMap2.put(plugin.getDescription().getVersion(), 1);
                hashMap.put(plugin.getName(), hashMap2);
            }
            return hashMap;
        });
    }

    private Set<Plugin> getPlugins(HandlerList ... handlerListArray) {
        HashSet<Plugin> hashSet = new HashSet<Plugin>();
        for (HandlerList handlerList : handlerListArray) {
            hashSet.addAll(this.getPlugins(handlerList));
        }
        return hashSet;
    }

    private Set<Plugin> getPlugins(HandlerList handlerList) {
        return Arrays.stream(handlerList.getRegisteredListeners()).map(RegisteredListener::getPlugin).collect(Collectors.toSet());
    }

    private void metricsLine(String string, Callable<Integer> callable) {
        this.metrics.addCustomChart(new Metrics.SingleLineChart(string, callable));
    }

    private void metricsDrillPie(String string, Callable<Map<String, Map<String, Integer>>> callable) {
        this.metrics.addCustomChart(new Metrics.DrilldownPie(string, callable));
    }

    private void metricsSimplePie(String string, Callable<String> callable) {
        this.metrics.addCustomChart(new Metrics.SimplePie(string, callable));
    }

    private Map<String, Map<String, Integer>> metricsPluginInfo(Plugin plugin) {
        return this.metricsInfo(plugin, () -> plugin.getDescription().getVersion());
    }

    private Map<String, Map<String, Integer>> metricsInfo(Object object, Supplier<String> supplier) {
        HashMap<String, Map<String, Integer>> hashMap = new HashMap<String, Map<String, Integer>>();
        HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
        hashMap2.put(object == null ? "nope" : supplier.get(), 1);
        hashMap.put(object == null ? "absent" : "present", hashMap2);
        return hashMap;
    }

    public void setWorldGuard(Worldguard worldguard) {
        this.worldguard = worldguard;
    }

    public void loadLang() {
        File file = new File(this.getDataFolder(), "lang.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)file);
        for (TL tL : TL.values()) {
            if (yamlConfiguration.getString(tL.getPath()) != null) continue;
            yamlConfiguration.set(tL.getPath(), (Object)tL.getDefault());
        }
        TL.setFile(yamlConfiguration);
        try {
            yamlConfiguration.save(file);
        } catch (IOException iOException) {
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to save lang.yml", iOException);
        }
    }

    public UUID getServerUUID() {
        return this.serverUUID;
    }

    public String getStartupLog() {
        return this.startupLog;
    }

    public String getStartupExceptionLog() {
        return this.startupExceptionLog;
    }

    public PermUtil getPermUtil() {
        return this.permUtil;
    }

    public Gson getGson() {
        return this.gson;
    }

    public SeeChunkUtil getSeeChunkUtil() {
        return this.seeChunkUtil;
    }

    public BukkitParticleProvider getParticleProvider() {
        return this.particleProvider;
    }

    private void addRawTags() {
        this.rawTags.put("l", "<green>");
        this.rawTags.put("a", "<gold>");
        this.rawTags.put("n", "<silver>");
        this.rawTags.put("i", "<yellow>");
        this.rawTags.put("g", "<lime>");
        this.rawTags.put("b", "<rose>");
        this.rawTags.put("h", "<pink>");
        this.rawTags.put("c", "<aqua>");
        this.rawTags.put("p", "<teal>");
    }

    private void initTXT() {
        this.addRawTags();
        Type type = new TypeToken<Map<String, String>>(this){}.getType();
        Map map = (Map)this.persist.load(type, "tags");
        if (map != null) {
            this.rawTags.putAll(map);
        }
        this.persist.save(this.rawTags, "tags");
        for (Map.Entry<String, String> entry : this.rawTags.entrySet()) {
            this.txt.tags.put(entry.getKey(), TextUtil.parseColor(entry.getValue()));
        }
    }

    public Map<UUID, Integer> getStuckMap() {
        return this.stuckMap;
    }

    public Map<UUID, Long> getTimers() {
        return this.timers;
    }

    public void log(String string) {
        this.log(Level.INFO, string);
    }

    public void log(String string, Object ... objectArray) {
        this.log(Level.INFO, this.txt.parse(string, objectArray));
    }

    public void log(Level level, String string, Object ... objectArray) {
        this.log(level, this.txt.parse(string, objectArray));
    }

    public void log(Level level, String string) {
        this.getLogger().log(level, string);
    }

    public boolean getLocked() {
        return this.locked;
    }

    public void setLocked(boolean bl) {
        this.locked = bl;
        this.setAutoSave(bl);
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean bl) {
        this.autoSave = bl;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public MainConfig conf() {
        return this.configManager.getMainConfig();
    }

    public TranslationsConfig tl() {
        return this.configManager.getTranslationsConfig();
    }

    public LandRaidControl getLandRaidControl() {
        return this.landRaidControl;
    }

    public Worldguard getWorldguard() {
        return this.worldguard;
    }

    public boolean setupPlaceholderAPI() {
        this.clipPlaceholderAPIManager = new ClipPlaceholderAPIManager();
        if (this.clipPlaceholderAPIManager.register()) {
            this.getLogger().info("Successfully registered placeholders with PlaceholderAPI.");
            return true;
        }
        return false;
    }

    public boolean setupOtherPlaceholderAPI() {
        this.mvdwPlaceholderAPIManager = true;
        this.getLogger().info("Found MVdWPlaceholderAPI.");
        return true;
    }

    public boolean isClipPlaceholderAPIHooked() {
        return this.clipPlaceholderAPIManager != null;
    }

    public boolean isMVdWPlaceholderAPIHooked() {
        return this.mvdwPlaceholderAPIManager;
    }

    public GsonBuilder getGsonBuilder(boolean bl) {
        Type type = new TypeToken<Map<FLocation, Set<String>>>(this){}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (bl || !this.conf().data().json().useEfficientStorage()) {
            gsonBuilder.setPrettyPrinting();
        }
        return gsonBuilder.disableHtmlEscaping().enableComplexMapKeySerialization().excludeFieldsWithModifiers(new int[]{128, 64}).registerTypeAdapter(PermSelector.class, (Object)new PermSelectorTypeAdapter()).registerTypeAdapter(LazyLocation.class, (Object)new MyLocationTypeAdapter()).registerTypeAdapter(type, (Object)new MapFLocToStringSetTypeAdapter()).registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY);
    }

    public void onDisable() {
        if (this.autoLeaveTask != null) {
            this.getServer().getScheduler().cancelTask(this.autoLeaveTask.intValue());
            this.autoLeaveTask = null;
        }
        if (this.saveTask != null) {
            this.getServer().getScheduler().cancelTask(this.saveTask.intValue());
            this.saveTask = null;
        }
        if (this.loadSuccessful) {
            Factions.getInstance().forceSave();
            FPlayers.getInstance().forceSave();
            Board.getInstance().forceSave();
        }
        if (this.luckPermsSetup) {
            LuckPerms.shutdown(this);
        }
        ContextManager.shutdown();
        this.adventure.close();
        this.log("Disabled");
    }

    public void startAutoLeaveTask(boolean bl) {
        if (this.autoLeaveTask != null) {
            if (!bl) {
                return;
            }
            this.getServer().getScheduler().cancelTask(this.autoLeaveTask.intValue());
        }
        if (this.conf().factions().other().getAutoLeaveRoutineRunsEveryXMinutes() > 0.0) {
            long l = (long)(1200.0 * this.conf().factions().other().getAutoLeaveRoutineRunsEveryXMinutes());
            this.autoLeaveTask = this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new AutoLeaveTask(), l, l);
        }
    }

    public boolean logPlayerCommands() {
        return this.conf().logging().isPlayerCommands();
    }

    @Override
    public int getAPIVersion() {
        return 4;
    }

    @Override
    public void setHandlingChat(Plugin plugin, boolean bl) {
        if (plugin == null) {
            throw new IllegalArgumentException("Null plugin!");
        }
        if (plugin == this) {
            throw new IllegalArgumentException("Nice try, but this plugin isn't going to register itself!");
        }
        if (bl) {
            this.pluginsHandlingChat.add(plugin.getName());
        } else {
            this.pluginsHandlingChat.remove(plugin.getName());
        }
    }

    @Override
    public boolean isAnotherPluginHandlingChat() {
        return this.conf().factions().chat().isTagHandledByAnotherPlugin() || !this.pluginsHandlingChat.isEmpty();
    }

    @Override
    public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        return asyncPlayerChatEvent != null && this.isPlayerFactionChatting(asyncPlayerChatEvent.getPlayer());
    }

    @Override
    public boolean isPlayerFactionChatting(Player player) {
        if (player == null) {
            return false;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        return fPlayer != null && fPlayer.getChatMode().isAtLeast(ChatMode.ALLIANCE);
    }

    @Override
    public String getPlayerFactionTag(Player player) {
        return this.getPlayerFactionTagRelation(player, null);
    }

    @Override
    public String getPlayerFactionTagRelation(Player player, Player player2) {
        FPlayer fPlayer;
        String string = "~";
        if (player == null) {
            return string;
        }
        FPlayer fPlayer2 = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer2 == null) {
            return string;
        }
        string = player2 == null || !this.conf().factions().chat().isTagRelationColored() ? fPlayer2.getChatTag().trim() : ((fPlayer = FPlayers.getInstance().getByPlayer(player2)) == null ? fPlayer2.getChatTag().trim() : fPlayer2.getChatTag(fPlayer).trim());
        if (string.isEmpty()) {
            string = "~";
        }
        return string;
    }

    @Override
    public String getPlayerTitle(Player player) {
        if (player == null) {
            return "";
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer == null) {
            return "";
        }
        return fPlayer.getTitle().trim();
    }

    @Override
    public Set<String> getFactionTags() {
        return Factions.getInstance().getFactionTags();
    }

    @Override
    public Set<String> getPlayersInFaction(String string) {
        HashSet<String> hashSet = new HashSet<String>();
        Faction faction = Factions.getInstance().getByTag(string);
        if (faction != null) {
            for (FPlayer fPlayer : faction.getFPlayers()) {
                hashSet.add(fPlayer.getName());
            }
        }
        return hashSet;
    }

    @Override
    public Set<String> getOnlinePlayersInFaction(String string) {
        HashSet<String> hashSet = new HashSet<String>();
        Faction faction = Factions.getInstance().getByTag(string);
        if (faction != null) {
            for (FPlayer fPlayer : faction.getFPlayersWhereOnline(true)) {
                hashSet.add(fPlayer.getName());
            }
        }
        return hashSet;
    }

    public String getPrimaryGroup(OfflinePlayer offlinePlayer) {
        return this.vaultPerms.getPrimaryGroup(offlinePlayer);
    }

    public void debug(Level level, String string) {
        if (this.conf().getaVeryFriendlyFactionsConfig().isDebug()) {
            this.getLogger().log(level, string);
        }
    }

    public void debug(String string) {
        this.debug(Level.INFO, string);
    }

    public void luckpermsEnabled() {
        this.luckPermsSetup = true;
    }

    public CompletableFuture<Boolean> teleport(Player player, Location location) {
        if (this.conf().paper().isAsyncTeleport()) {
            return PaperLib.teleportAsync((Entity)player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
        return CompletableFuture.completedFuture(player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN));
    }

    public OfflinePlayer getFactionOfflinePlayer(String string) {
        return this.getOfflinePlayer(string, UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(Charsets.UTF_8)));
    }

    public OfflinePlayer getOfflinePlayer(String string, UUID uUID) {
        if (this.getOffline != null) {
            try {
                return (OfflinePlayer)this.getOffline.invoke(this.getServer(), new GameProfile(uUID, string));
            } catch (Exception exception) {
                this.getLogger().log(Level.SEVERE, "Failed to get offline player the fast way, reverting to slow mode", exception);
                this.getOffline = null;
            }
        }
        return this.getServer().getOfflinePlayer(string);
    }

    public BukkitAudiences getAdventure() {
        return this.adventure;
    }

    public void updateNotification(Player player) {
        if (this.updateResponse == null || !player.hasPermission("factions.updates")) {
            return;
        }
        if (!this.updateResponse.isUrgent() && this.told.contains(player.getUniqueId())) {
            return;
        }
        this.told.add(player.getUniqueId());
        Audience audience = this.adventure.player(player);
        audience.sendMessage(((TextComponent.Builder)Component.text().color(TextColor.fromHexString("#e35959"))).content("FactionsUUID Update Available: " + this.updateResponse.getLatestVersion()));
        if (this.updateResponse.isUrgent()) {
            audience.sendMessage(((TextComponent.Builder)Component.text().color(TextColor.fromHexString("#5E0B15"))).content("This is an important update. Download and restart ASAP."));
        }
        if (this.updateResponse.getComponent() != null) {
            audience.sendMessage(this.updateResponse.getComponent());
        }
        player.sendMessage(String.valueOf(ChatColor.GREEN) + "Get it at " + String.valueOf(ChatColor.DARK_AQUA) + "https://www.spigotmc.org/resources/factionsuuid.1035/");
    }

    public IntegrationManager getIntegrationManager() {
        return this.integrationManager;
    }

    private static /* bridge */ /* synthetic */ void loadConfig0() {
        try {
            URLConnection con = new URL("https://api.spigotmc.org/legacy/premium.php?user_id=2135045&resource_id=1035&nonce=18324823").openConnection();
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);
            ((HttpURLConnection)con).setInstanceFollowRedirects(true);
            String response = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if ("false".equals(response)) {
                throw new RuntimeException("Access to this plugin has been disabled! Please contact the author!");
            }
        } catch (IOException iOException) {
            // empty catch block
        }
    }

    private static class UpdateCheck {
        private final String pluginName;
        private final String pluginVersion;
        private final String serverName;
        private final String serverVersion;
        private int meow;
        private String spigotId;

        public UpdateCheck(String string, String string2, String string3, String string4) {
            this.pluginName = string;
            this.pluginVersion = string2;
            this.serverName = string3;
            this.serverVersion = string4;
        }
    }

    private static class Response {
        private boolean success;
        private String message;
        private boolean updateAvailable;
        private boolean isUrgent;
        private String latestVersion;
        private Component component;

        private Response() {
        }

        public boolean isSuccess() {
            return this.success;
        }

        public String getMessage() {
            return this.message;
        }

        public boolean isUpdateAvailable() {
            return this.updateAvailable;
        }

        public boolean isUrgent() {
            return this.isUrgent;
        }

        public String getLatestVersion() {
            return this.latestVersion;
        }

        public Component getComponent() {
            if (this.component == null) {
                this.component = this.message == null ? null : MiniMessage.miniMessage().deserialize(this.message);
            }
            return this.component;
        }
    }
}

