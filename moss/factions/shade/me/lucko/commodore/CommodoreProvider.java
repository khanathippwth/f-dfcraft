/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package moss.factions.shade.me.lucko.commodore;

import java.util.Objects;
import java.util.function.Function;
import moss.factions.shade.me.lucko.commodore.BrigadierUnsupportedException;
import moss.factions.shade.me.lucko.commodore.Commodore;
import moss.factions.shade.me.lucko.commodore.PaperCommodore;
import moss.factions.shade.me.lucko.commodore.ReflectionCommodore;
import org.bukkit.plugin.Plugin;

public final class CommodoreProvider {
    private static final Function<Plugin, Commodore> PROVIDER = CommodoreProvider.checkSupported();

    private CommodoreProvider() {
        throw new AssertionError();
    }

    private static Function<Plugin, Commodore> checkSupported() {
        try {
            Class.forName("com.mojang.brigadier.CommandDispatcher");
        } catch (Throwable throwable) {
            CommodoreProvider.printDebugInfo(throwable);
            return null;
        }
        try {
            PaperCommodore.ensureSetup();
            return PaperCommodore::new;
        } catch (Throwable throwable) {
            CommodoreProvider.printDebugInfo(throwable);
            try {
                ReflectionCommodore.ensureSetup();
                return ReflectionCommodore::new;
            } catch (Throwable throwable2) {
                CommodoreProvider.printDebugInfo(throwable2);
                return null;
            }
        }
    }

    private static void printDebugInfo(Throwable throwable) {
        if (System.getProperty("commodore.debug") != null) {
            System.err.println("Exception while initialising commodore:");
            throwable.printStackTrace(System.err);
        }
    }

    public static boolean isSupported() {
        return PROVIDER != null;
    }

    public static Commodore getCommodore(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        if (PROVIDER == null) {
            throw new BrigadierUnsupportedException("Brigadier is not supported by the server. Set -Dcommodore.debug=true for debug info.");
        }
        return PROVIDER.apply(plugin);
    }
}

