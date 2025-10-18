/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DiscUtil {
    private static final HashMap<String, Lock> locks = new HashMap();

    public static byte[] readBytes(File file) {
        int n = (int)file.length();
        byte[] byArray = new byte[n];
        FileInputStream fileInputStream = new FileInputStream(file);
        for (int i = 0; i < n; i += ((InputStream)fileInputStream).read(byArray, i, n - i)) {
        }
        ((InputStream)fileInputStream).close();
        return byArray;
    }

    public static void writeBytes(File file, byte[] byArray) {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(byArray);
        fileOutputStream.close();
    }

    static void write(File file, String string) {
        DiscUtil.writeBytes(file, string.getBytes(StandardCharsets.UTF_8));
    }

    public static String read(File file) {
        return new String(DiscUtil.readBytes(file), StandardCharsets.UTF_8);
    }

    public static boolean writeCatch(File file, String string, boolean bl) {
        DiscUtil.write(file, () -> string, bl);
        return true;
    }

    public static void write(final File file, final Supplier<String> supplier, boolean bl) {
        final Lock lock = locks.computeIfAbsent(file.getName(), string -> new ReentrantReadWriteLock().writeLock());
        if (bl) {
            DiscUtil.write(lock, file, supplier);
        } else {
            new BukkitRunnable(){

                public void run() {
                    DiscUtil.write(lock, file, supplier);
                }
            }.runTaskAsynchronously((Plugin)FactionsPlugin.getInstance());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void write(Lock lock, File file, Supplier<String> supplier) {
        lock.lock();
        try {
            DiscUtil.write(file, supplier.get());
        } catch (IOException iOException) {
            FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to write file " + file.getAbsolutePath(), iOException);
        } finally {
            lock.unlock();
        }
    }

    public static String readCatch(File file) {
        try {
            return DiscUtil.read(file);
        } catch (IOException iOException) {
            return null;
        }
    }
}

