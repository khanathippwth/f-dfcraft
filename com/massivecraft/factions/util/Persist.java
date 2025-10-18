/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.DiscUtil;
import java.io.File;
import java.lang.reflect.Type;
import java.util.logging.Level;

public class Persist {
    private final FactionsPlugin plugin;

    public Persist(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
    }

    public static String getName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    public static String getName(Object object) {
        return Persist.getName(object.getClass());
    }

    public static String getName(Type type) {
        return Persist.getName(type.getClass());
    }

    public File getFile(String string) {
        return new File(this.plugin.getDataFolder(), string + ".json");
    }

    public File getFile(Class<?> clazz) {
        return this.getFile(Persist.getName(clazz));
    }

    public File getFile(Object object) {
        return this.getFile(Persist.getName(object));
    }

    public File getFile(Type type) {
        return this.getFile(Persist.getName(type));
    }

    public <T> T loadOrSaveDefault(T t, Class<T> clazz) {
        return this.loadOrSaveDefault(t, clazz, this.getFile(clazz));
    }

    public <T> T loadOrSaveDefault(T t, Class<T> clazz, String string) {
        return this.loadOrSaveDefault(t, clazz, this.getFile(string));
    }

    public <T> T loadOrSaveDefault(T t, Class<T> clazz, File file) {
        if (!file.exists()) {
            this.plugin.getLogger().info("Creating default: " + String.valueOf(file));
            this.save(t, file);
            return t;
        }
        T t2 = this.load(clazz, file);
        if (t2 == null) {
            this.plugin.log(Level.WARNING, "Using default as I failed to load: " + String.valueOf(file));
            File file2 = new File(file.getPath() + "_bad");
            if (file2.exists()) {
                file2.delete();
            }
            this.plugin.log(Level.WARNING, "Backing up copy of bad file to: " + String.valueOf(file2));
            file.renameTo(file2);
            return t;
        }
        return t2;
    }

    public boolean save(Object object) {
        return this.save(object, this.getFile(object));
    }

    public boolean save(Object object, String string) {
        return this.save(object, this.getFile(string));
    }

    public boolean save(Object object, File file) {
        return DiscUtil.writeCatch(file, this.plugin.getGson().toJson(object), true);
    }

    public <T> T load(Class<T> clazz) {
        return this.load(clazz, this.getFile(clazz));
    }

    public <T> T load(Class<T> clazz, String string) {
        return this.load(clazz, this.getFile(string));
    }

    public <T> T load(Class<T> clazz, File file) {
        String string = DiscUtil.readCatch(file);
        if (string == null) {
            return null;
        }
        try {
            return (T)this.plugin.getGson().fromJson(string, clazz);
        } catch (Exception exception) {
            this.plugin.log(Level.WARNING, exception.getMessage());
            return null;
        }
    }

    public <T> T load(Type type, String string) {
        return this.load(type, this.getFile(string));
    }

    public <T> T load(Type type, File file) {
        String string = DiscUtil.readCatch(file);
        if (string == null) {
            return null;
        }
        try {
            return (T)this.plugin.getGson().fromJson(string, type);
        } catch (Exception exception) {
            this.plugin.log(Level.WARNING, exception.getMessage());
            return null;
        }
    }
}

