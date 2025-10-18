/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.massivecraft.factions.cmd;

import com.google.gson.Gson;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import moss.factions.shade.net.kyori.adventure.audience.Audience;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ComponentLike;
import moss.factions.shade.net.kyori.adventure.text.TextComponent;
import moss.factions.shade.net.kyori.adventure.text.event.ClickEvent;
import moss.factions.shade.net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;

public class CmdTicketInfo
extends FCommand {
    public CmdTicketInfo() {
        this.aliases.add("ticketinfo");
        this.optionalArgs.put("full", null);
        this.requirements = new CommandRequirements.Builder(Permission.DEBUG).build();
    }

    @Override
    public void perform(final CommandContext commandContext) {
        final FactionsPlugin factionsPlugin = FactionsPlugin.getInstance();
        final TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.uuid = factionsPlugin.getServerUUID();
        ticketInfo.pluginVersion = factionsPlugin.getDescription().getVersion();
        ticketInfo.javaVersion = System.getProperty("java.version");
        ticketInfo.likesCats = factionsPlugin.likesCats;
        ticketInfo.serverName = Bukkit.getName();
        ticketInfo.serverVersion = Bukkit.getVersion();
        ticketInfo.userName = commandContext.sender.getName();
        ticketInfo.userUUID = commandContext.player == null ? null : commandContext.player.getUniqueId();
        try {
            ticketInfo.num = FactionsPlugin.class.getDeclaredMethods().length;
        } catch (Throwable throwable) {
            // empty catch block
        }
        final Audience audience = factionsPlugin.getAdventure().sender(commandContext.sender);
        final boolean bl = commandContext.argAsString(0, "").equalsIgnoreCase("full");
        if (bl) {
            ticketInfo.plugins = new ArrayList<TicketInfo.PluginInfo>();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                ticketInfo.plugins.add(new TicketInfo.PluginInfo(plugin));
            }
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                ticketInfo.permissions = new ArrayList<TicketInfo.PlayerInfo>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ticketInfo.permissions.add(new TicketInfo.PlayerInfo(player));
                }
            }
        }
        new BukkitRunnable(this){

            private String getFile(Path path) {
                try {
                    return Files.readString((Path)path);
                } catch (IOException iOException) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter, true);
                    iOException.printStackTrace(printWriter);
                    return stringWriter.getBuffer().toString();
                }
            }

            public void run() {
                try {
                    Object object2;
                    Path path = FactionsPlugin.getInstance().getDataFolder().toPath();
                    String string = this.getFile(Paths.get("spigot.yml", new String[0]));
                    ticketInfo.online = Boolean.toString(Bukkit.getOnlineMode());
                    if (!Bukkit.getOnlineMode()) {
                        for (Object object2 : string.split("\n")) {
                            if (!((String)object2).contains("bungeecord") || !((String)object2).contains("true")) continue;
                            ticketInfo.online = "Bungee";
                            break;
                        }
                    }
                    if (bl) {
                        ticketInfo.startup = factionsPlugin.getStartupLog();
                        if (!factionsPlugin.getStartupExceptionLog().isEmpty()) {
                            ticketInfo.exceptions = factionsPlugin.getStartupExceptionLog();
                        }
                        ticketInfo.mainconf = this.getFile(path.resolve("config/main.conf"));
                    }
                    Gson gson = new Gson();
                    String string2 = gson.toJson((Object)ticketInfo);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    object2 = new GZIPOutputStream(byteArrayOutputStream);
                    ((FilterOutputStream)object2).write(string2.getBytes(StandardCharsets.UTF_8));
                    ((GZIPOutputStream)object2).finish();
                    byte[] byArray = byteArrayOutputStream.toByteArray();
                    URL uRL = new URI("https://ticket.plugin.party/ticket").toURL();
                    HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.setDoOutput(true);
                    try (Object object3 = httpURLConnection.getOutputStream();){
                        ((OutputStream)object3).write(byArray, 0, byArray.length);
                    }
                    object3 = new StringBuilder();
                    try (Object object4 = httpURLConnection.getInputStream();
                         InputStreamReader inputStreamReader = new InputStreamReader((InputStream)object4, StandardCharsets.UTF_8);
                         BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
                        String string3;
                        while ((string3 = bufferedReader.readLine()) != null) {
                            ((StringBuilder)object3).append(string3);
                        }
                    }
                    object4 = (TicketResponse)gson.fromJson(((StringBuilder)object3).toString(), TicketResponse.class);
                    new BukkitRunnable((TicketResponse)object4){
                        final /* synthetic */ TicketResponse val$response;
                        {
                            this.val$response = ticketResponse;
                        }

                        public void run() {
                            if (this.val$response.success) {
                                String string = this.val$response.message;
                                audience.sendMessage((ComponentLike)((Object)((TextComponent.Builder)Component.text().color(NamedTextColor.YELLOW)).content("Share this URL: " + string).clickEvent(ClickEvent.openUrl(string))));
                                if (commandContext.sender instanceof Player) {
                                    FactionsPlugin.getInstance().getLogger().info("Share this URL: " + string);
                                }
                            } else {
                                audience.sendMessage(((TextComponent.Builder)Component.text().color(NamedTextColor.RED)).content("ERROR! Could not generate ticket info. See console for why."));
                                FactionsPlugin.getInstance().getLogger().warning("Received: " + this.val$response.message);
                            }
                        }
                    }.runTask((Plugin)FactionsPlugin.getInstance());
                } catch (Exception exception) {
                    FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to execute ticketinfo command", exception);
                    new BukkitRunnable(){

                        public void run() {
                            audience.sendMessage(((TextComponent.Builder)Component.text().color(NamedTextColor.RED)).content("ERROR! Could not generate ticket info. See console for why."));
                        }
                    }.runTask((Plugin)FactionsPlugin.getInstance());
                }
            }
        }.runTaskAsynchronously((Plugin)FactionsPlugin.getInstance());
        audience.sendMessage(((TextComponent.Builder)Component.text().color(NamedTextColor.YELLOW)).content("Now running..."));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TICKETINFO_DESCRIPTION;
    }

    private static class TicketInfo {
        private final String type = "FUUID";
        private UUID uuid;
        private String pluginVersion;
        private String javaVersion;
        private String serverVersion;
        private String serverName;
        private String userName;
        private boolean likesCats;
        private UUID userUUID;
        private String online;
        private int num;
        private List<PlayerInfo> permissions;
        private List<PluginInfo> plugins;
        private String startup;
        private String exceptions;
        private String mainconf;

        private TicketInfo() {
        }

        private static class PluginInfo {
            private final String name;
            private final String version;
            private final List<String> authors;
            private final List<String> depend;
            private final List<String> softdepend;
            private final List<String> loadBefore;
            private final boolean enabled;

            PluginInfo(Plugin plugin) {
                this.name = plugin.getName();
                PluginDescriptionFile pluginDescriptionFile = plugin.getDescription();
                this.version = pluginDescriptionFile.getVersion();
                this.authors = pluginDescriptionFile.getAuthors();
                this.depend = pluginDescriptionFile.getDepend().isEmpty() ? null : pluginDescriptionFile.getDepend();
                this.softdepend = pluginDescriptionFile.getSoftDepend().isEmpty() ? null : pluginDescriptionFile.getSoftDepend();
                this.loadBefore = pluginDescriptionFile.getLoadBefore().isEmpty() ? null : pluginDescriptionFile.getLoadBefore();
                this.enabled = plugin.isEnabled();
            }
        }

        private static class PlayerInfo {
            private final String name;
            private final UUID uuid;
            private final List<PermInfo> permissions;

            public PlayerInfo(Player player) {
                this.name = player.getName();
                this.uuid = player.getUniqueId();
                this.permissions = new ArrayList<PermInfo>();
                for (Permission permission : Permission.values()) {
                    this.permissions.add(new PermInfo(permission.toString(), player.hasPermission(permission.toString())));
                }
            }

            private static class PermInfo {
                private final String node;
                private final boolean has;

                public PermInfo(String string, boolean bl) {
                    this.node = string;
                    this.has = bl;
                }
            }
        }
    }

    private static class TicketResponse {
        private boolean success;
        private String message;

        private TicketResponse() {
        }
    }
}

