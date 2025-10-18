/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.config;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.Loader;
import com.massivecraft.factions.config.file.DynmapConfig;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.config.file.PermissionsConfig;
import com.massivecraft.factions.config.file.TranslationsConfig;
import com.massivecraft.factions.config.transition.Transitioner;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private final FactionsPlugin plugin;
    private PermissionsConfig permissionsConfig = null;
    private final MainConfig mainConfig = new MainConfig();
    private final DynmapConfig dynmapConfig = new DynmapConfig();
    private final TranslationsConfig translationsConfig = new TranslationsConfig();

    public ConfigManager(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
        Transitioner.transition(this.plugin);
    }

    public void loadConfigs() {
        if (this.permissionsConfig == null) {
            this.permissionsConfig = new PermissionsConfig();
        }
        this.loadConfig("translations", this.translationsConfig);
        this.loadConfig("permissions", this.permissionsConfig);
        this.loadConfig("main", this.mainConfig);
        this.loadConfig("dynmap", this.dynmapConfig);
    }

    private void loadConfig(String string, Object object) {
        try {
            Loader.loadAndSave(string, object);
        } catch (IOException | IllegalAccessException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not load config '" + string + ".conf'", exception);
        }
    }

    public PermissionsConfig getPermissionsConfig() {
        return this.permissionsConfig;
    }

    public MainConfig getMainConfig() {
        return this.mainConfig;
    }

    public DynmapConfig getDynmapConfig() {
        return this.dynmapConfig;
    }

    public TranslationsConfig getTranslationsConfig() {
        return this.translationsConfig;
    }
}

