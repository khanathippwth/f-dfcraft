/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonPrimitive
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.ServicePriority
 */
package com.massivecraft.factions.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.massivecraft.factions.FactionsPlugin;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

public class Metrics {
    public static final int B_STATS_VERSION = 1;
    private static final String URL = "https://bStats.org/submitData/bukkit";
    private final boolean enabled;
    private static boolean logFailedRequests;
    private static boolean logSentData;
    private static boolean logResponseStatusText;
    private static String serverUUID;
    private final Plugin plugin;
    private final int pluginId;
    private final List<CustomChart> charts = new ArrayList<CustomChart>();

    public Metrics(FactionsPlugin factionsPlugin) {
        if (factionsPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null!");
        }
        this.plugin = factionsPlugin;
        this.pluginId = 5125;
        File file = new File(factionsPlugin.getDataFolder().getParentFile(), "bStats");
        File file2 = new File(file, "config.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)file2);
        if (!yamlConfiguration.isSet("serverUuid")) {
            yamlConfiguration.addDefault("enabled", (Object)true);
            yamlConfiguration.addDefault("serverUuid", (Object)UUID.randomUUID().toString());
            yamlConfiguration.addDefault("logFailedRequests", (Object)false);
            yamlConfiguration.addDefault("logSentData", (Object)false);
            yamlConfiguration.addDefault("logResponseStatusText", (Object)false);
            yamlConfiguration.options().setHeader(List.of((Object)"bStats collects some data for plugin authors like how many servers are using their plugins.", (Object)"To honor their work, you should not disable it.", (Object)"This has nearly no effect on the server performance!", (Object)"Check out https://bStats.org/ to learn more :)")).copyDefaults(true);
            try {
                yamlConfiguration.save(file2);
            } catch (IOException iOException) {
                // empty catch block
            }
        }
        this.enabled = yamlConfiguration.getBoolean("enabled", true);
        serverUUID = yamlConfiguration.getString("serverUuid");
        logFailedRequests = yamlConfiguration.getBoolean("logFailedRequests", false);
        logSentData = yamlConfiguration.getBoolean("logSentData", false);
        logResponseStatusText = yamlConfiguration.getBoolean("logResponseStatusText", false);
        if (this.enabled) {
            boolean bl = false;
            for (Class clazz : Bukkit.getServicesManager().getKnownServices()) {
                try {
                    clazz.getField("B_STATS_VERSION");
                    bl = true;
                    break;
                } catch (NoSuchFieldException noSuchFieldException) {
                }
            }
            Bukkit.getServicesManager().register(Metrics.class, (Object)this, (Plugin)factionsPlugin, ServicePriority.Normal);
            if (!bl) {
                this.startSubmitting();
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void addCustomChart(CustomChart customChart) {
        if (customChart == null) {
            throw new IllegalArgumentException("Chart cannot be null!");
        }
        this.charts.add(customChart);
    }

    private void startSubmitting() {
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                if (!Metrics.this.plugin.isEnabled()) {
                    timer.cancel();
                    return;
                }
                Bukkit.getScheduler().runTask(Metrics.this.plugin, () -> Metrics.this.submitData());
            }
        }, 300000L, 1800000L);
    }

    public JsonObject getPluginData() {
        JsonObject jsonObject = new JsonObject();
        String string = this.plugin.getDescription().getName();
        String string2 = this.plugin.getDescription().getVersion();
        string = "FactionsUUID";
        string2 = this.plugin.getDescription().getVersion().replace("${build.number}", "selfbuilt");
        jsonObject.addProperty("pluginName", string);
        jsonObject.addProperty("id", (Number)this.pluginId);
        jsonObject.addProperty("pluginVersion", string2);
        JsonArray jsonArray = new JsonArray();
        for (CustomChart customChart : this.charts) {
            JsonObject jsonObject2 = customChart.getRequestJsonObject();
            if (jsonObject2 == null) continue;
            jsonArray.add((JsonElement)jsonObject2);
        }
        jsonObject.add("customCharts", (JsonElement)jsonArray);
        return jsonObject;
    }

    private JsonObject getServerData() {
        int n;
        try {
            Method method = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers", new Class[0]);
            n = method.getReturnType().equals(Collection.class) ? ((Collection)method.invoke(Bukkit.getServer(), new Object[0])).size() : ((Player[])method.invoke(Bukkit.getServer(), new Object[0])).length;
        } catch (Exception exception) {
            n = Bukkit.getOnlinePlayers().size();
        }
        int n2 = Bukkit.getOnlineMode() ? 1 : 0;
        String string = Bukkit.getVersion();
        String string2 = Bukkit.getName();
        String string3 = System.getProperty("java.version");
        String string4 = System.getProperty("os.name");
        String string5 = System.getProperty("os.arch");
        String string6 = System.getProperty("os.version");
        int n3 = Runtime.getRuntime().availableProcessors();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("serverUUID", serverUUID);
        jsonObject.addProperty("playerAmount", (Number)n);
        jsonObject.addProperty("onlineMode", (Number)n2);
        jsonObject.addProperty("bukkitVersion", string);
        jsonObject.addProperty("bukkitName", string2);
        jsonObject.addProperty("javaVersion", string3);
        jsonObject.addProperty("osName", string4);
        jsonObject.addProperty("osArch", string5);
        jsonObject.addProperty("osVersion", string6);
        jsonObject.addProperty("coreCount", (Number)n3);
        return jsonObject;
    }

    private void submitData() {
        JsonObject jsonObject = this.getServerData();
        JsonArray jsonArray = new JsonArray();
        for (Class clazz : Bukkit.getServicesManager().getKnownServices()) {
            try {
                clazz.getField("B_STATS_VERSION");
                for (RegisteredServiceProvider registeredServiceProvider : Bukkit.getServicesManager().getRegistrations(clazz)) {
                    try {
                        Object object = registeredServiceProvider.getService().getMethod("getPluginData", new Class[0]).invoke(registeredServiceProvider.getProvider(), new Object[0]);
                        if (object instanceof JsonObject) {
                            jsonArray.add((JsonElement)((JsonObject)object));
                            continue;
                        }
                        try {
                            Class<?> clazz2 = Class.forName("org.json.simple.JSONObject");
                            if (!object.getClass().isAssignableFrom(clazz2)) continue;
                            Method method = clazz2.getDeclaredMethod("toJSONString", new Class[0]);
                            method.setAccessible(true);
                            String string = (String)method.invoke(object, new Object[0]);
                            JsonObject jsonObject2 = JsonParser.parseString((String)string).getAsJsonObject();
                            jsonArray.add((JsonElement)jsonObject2);
                        } catch (ClassNotFoundException classNotFoundException) {
                            if (!logFailedRequests) continue;
                            this.plugin.getLogger().log(Level.SEVERE, "Encountered unexpected exception", classNotFoundException);
                        }
                    } catch (IllegalAccessException | NoSuchMethodException | NullPointerException | InvocationTargetException exception) {}
                }
            } catch (NoSuchFieldException noSuchFieldException) {
            }
        }
        jsonObject.add("plugins", (JsonElement)jsonArray);
        new Thread(() -> {
            block2: {
                try {
                    Metrics.sendData(this.plugin, jsonObject);
                } catch (Exception exception) {
                    if (!logFailedRequests) break block2;
                    this.plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + this.plugin.getName(), exception);
                }
            }
        }).start();
    }

    private static void sendData(Plugin plugin, JsonObject jsonObject) {
        if (jsonObject == null) {
            throw new IllegalArgumentException("Data cannot be null!");
        }
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalAccessException("This method must not be called from the main thread!");
        }
        if (logSentData) {
            plugin.getLogger().info("Sending data to bStats: " + String.valueOf(jsonObject));
        }
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection)new URI(URL).toURL().openConnection();
        byte[] byArray = Metrics.compress(jsonObject.toString());
        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.addRequestProperty("Accept", "application/json");
        httpsURLConnection.addRequestProperty("Connection", "close");
        httpsURLConnection.addRequestProperty("Content-Encoding", "gzip");
        httpsURLConnection.addRequestProperty("Content-Length", String.valueOf(byArray.length));
        httpsURLConnection.setRequestProperty("Content-Type", "application/json");
        httpsURLConnection.setRequestProperty("User-Agent", "MC-Server/1");
        httpsURLConnection.setDoOutput(true);
        try (Object object = new DataOutputStream(httpsURLConnection.getOutputStream());){
            ((FilterOutputStream)object).write(byArray);
        }
        object = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));){
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                ((StringBuilder)object).append(string);
            }
        }
        if (logResponseStatusText) {
            plugin.getLogger().info("Sent data to bStats and received response: " + String.valueOf(object));
        }
    }

    private static byte[] compress(String string) {
        if (string == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);){
            gZIPOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
        }
        return byteArrayOutputStream.toByteArray();
    }

    static {
        if (System.getProperty("bstats.relocatecheck") == null || !System.getProperty("bstats.relocatecheck").equals("false")) {
            String string = new String(new byte[]{111, 114, 103, 46, 98, 115, 116, 97, 116, 115, 46, 98, 117, 107, 107, 105, 116});
            String string2 = new String(new byte[]{121, 111, 117, 114, 46, 112, 97, 99, 107, 97, 103, 101});
            if (Metrics.class.getPackage().getName().equals(string) || Metrics.class.getPackage().getName().equals(string2)) {
                throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
            }
        }
    }

    public static abstract class CustomChart {
        final String chartId;

        CustomChart(String string) {
            if (string == null || string.isEmpty()) {
                throw new IllegalArgumentException("ChartId cannot be null or empty!");
            }
            this.chartId = string;
        }

        private JsonObject getRequestJsonObject() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("chartId", this.chartId);
            try {
                JsonObject jsonObject2 = this.getChartData();
                if (jsonObject2 == null) {
                    return null;
                }
                jsonObject.add("data", (JsonElement)jsonObject2);
            } catch (Throwable throwable) {
                if (logFailedRequests) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to get data for custom chart with id " + this.chartId, throwable);
                }
                return null;
            }
            return jsonObject;
        }

        protected abstract JsonObject getChartData();
    }

    public static class AdvancedBarChart
    extends CustomChart {
        private final Callable<Map<String, int[]>> callable;

        public AdvancedBarChart(String string, Callable<Map<String, int[]>> callable) {
            super(string);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            Map<String, int[]> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            boolean bl = true;
            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                if (entry.getValue().length == 0) continue;
                bl = false;
                JsonArray jsonArray = new JsonArray();
                for (int n : entry.getValue()) {
                    jsonArray.add((JsonElement)new JsonPrimitive((Number)n));
                }
                jsonObject2.add(entry.getKey(), (JsonElement)jsonArray);
            }
            if (bl) {
                return null;
            }
            jsonObject.add("values", (JsonElement)jsonObject2);
            return jsonObject;
        }
    }

    public static class SimpleBarChart
    extends CustomChart {
        private final Callable<Map<String, Integer>> callable;

        public SimpleBarChart(String string, Callable<Map<String, Integer>> callable) {
            super(string);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            Map<String, Integer> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                JsonArray jsonArray = new JsonArray();
                jsonArray.add((JsonElement)new JsonPrimitive((Number)entry.getValue()));
                jsonObject2.add(entry.getKey(), (JsonElement)jsonArray);
            }
            jsonObject.add("values", (JsonElement)jsonObject2);
            return jsonObject;
        }
    }

    public static class MultiLineChart
    extends CustomChart {
        private final Callable<Map<String, Integer>> callable;

        public MultiLineChart(String string, Callable<Map<String, Integer>> callable) {
            super(string);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            Map<String, Integer> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            boolean bl = true;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 0) continue;
                bl = false;
                jsonObject2.addProperty(entry.getKey(), (Number)entry.getValue());
            }
            if (bl) {
                return null;
            }
            jsonObject.add("values", (JsonElement)jsonObject2);
            return jsonObject;
        }
    }

    public static class SingleLineChart
    extends CustomChart {
        private final Callable<Integer> callable;

        public SingleLineChart(String string, Callable<Integer> callable) {
            super(string);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() {
            JsonObject jsonObject = new JsonObject();
            int n = this.callable.call();
            if (n == 0) {
                return null;
            }
            jsonObject.addProperty("value", (Number)n);
            return jsonObject;
        }
    }

    public static class DrilldownPie
    extends CustomChart {
        private final Callable<Map<String, Map<String, Integer>>> callable;

        public DrilldownPie(String string, Callable<Map<String, Map<String, Integer>>> callable) {
            super(string);
            this.callable = callable;
        }

        @Override
        public JsonObject getChartData() {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            Map<String, Map<String, Integer>> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            boolean bl = true;
            for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
                JsonObject jsonObject3 = new JsonObject();
                boolean bl2 = true;
                for (Map.Entry<String, Integer> entry2 : map.get(entry.getKey()).entrySet()) {
                    jsonObject3.addProperty(entry2.getKey(), (Number)1);
                    bl2 = false;
                }
                if (bl2) continue;
                bl = false;
                jsonObject2.add(entry.getKey(), (JsonElement)jsonObject3);
            }
            if (bl) {
                return null;
            }
            jsonObject.add("values", (JsonElement)jsonObject2);
            return jsonObject;
        }
    }

    public static class AdvancedPie
    extends CustomChart {
        private final Callable<Map<String, Integer>> callable;

        public AdvancedPie(String string, Callable<Map<String, Integer>> callable) {
            super(string);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            Map<String, Integer> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            boolean bl = true;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 0) continue;
                bl = false;
                jsonObject2.addProperty(entry.getKey(), (Number)entry.getValue());
            }
            if (bl) {
                return null;
            }
            jsonObject.add("values", (JsonElement)jsonObject2);
            return jsonObject;
        }
    }

    public static class SimplePie
    extends CustomChart {
        private final Callable<String> callable;

        public SimplePie(String string, Callable<String> callable) {
            super(string);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() {
            JsonObject jsonObject = new JsonObject();
            String string = this.callable.call();
            if (string == null || string.isEmpty()) {
                return null;
            }
            jsonObject.addProperty("value", string);
            return jsonObject;
        }
    }
}

