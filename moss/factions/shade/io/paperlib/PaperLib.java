/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.plugin.Plugin
 */
package moss.factions.shade.io.paperlib;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import moss.factions.shade.io.paperlib.environments.CraftBukkitEnvironment;
import moss.factions.shade.io.paperlib.environments.Environment;
import moss.factions.shade.io.paperlib.environments.PaperEnvironment;
import moss.factions.shade.io.paperlib.environments.SpigotEnvironment;
import moss.factions.shade.io.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import moss.factions.shade.io.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class PaperLib {
    private static Environment ENVIRONMENT = PaperLib.initialize();

    private PaperLib() {
    }

    private static Environment initialize() {
        if (PaperLib.hasClass("com.destroystokyo.paper.PaperConfig") || PaperLib.hasClass("io.papermc.paper.configuration.Configuration")) {
            return new PaperEnvironment();
        }
        if (PaperLib.hasClass("org.spigotmc.SpigotConfig")) {
            return new SpigotEnvironment();
        }
        return new CraftBukkitEnvironment();
    }

    private static boolean hasClass(String string) {
        try {
            Class.forName(string);
            return true;
        } catch (ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    @Nonnull
    public static Environment getEnvironment() {
        return ENVIRONMENT;
    }

    public static void setCustomEnvironment(@Nonnull Environment environment) {
        ENVIRONMENT = environment;
    }

    @Nonnull
    public static CompletableFuture<Boolean> teleportAsync(@Nonnull Entity entity, @Nonnull Location location) {
        return ENVIRONMENT.teleport(entity, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Nonnull
    public static CompletableFuture<Boolean> teleportAsync(@Nonnull Entity entity, @Nonnull Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return ENVIRONMENT.teleport(entity, location, teleportCause);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull Location location) {
        return PaperLib.getChunkAtAsync(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, true);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull Location location, boolean bl) {
        return PaperLib.getChunkAtAsync(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, bl);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull World world, int n, int n2) {
        return PaperLib.getChunkAtAsync(world, n, n2, true);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull World world, int n, int n2, boolean bl) {
        return ENVIRONMENT.getChunkAtAsync(world, n, n2, bl, false);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsync(@Nonnull World world, int n, int n2, boolean bl, boolean bl2) {
        return ENVIRONMENT.getChunkAtAsync(world, n, n2, bl, bl2);
    }

    @Nonnull
    public static CompletableFuture<Chunk> getChunkAtAsyncUrgently(@Nonnull World world, int n, int n2, boolean bl) {
        return ENVIRONMENT.getChunkAtAsync(world, n, n2, bl, true);
    }

    public static boolean isChunkGenerated(@Nonnull Location location) {
        return PaperLib.isChunkGenerated(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public static boolean isChunkGenerated(@Nonnull World world, int n, int n2) {
        return ENVIRONMENT.isChunkGenerated(world, n, n2);
    }

    @Nonnull
    public static BlockStateSnapshotResult getBlockState(@Nonnull Block block, boolean bl) {
        return ENVIRONMENT.getBlockState(block, bl);
    }

    @Nonnull
    public static InventoryHolderSnapshotResult getHolder(@Nonnull Inventory inventory, boolean bl) {
        return ENVIRONMENT.getHolder(inventory, bl);
    }

    public static CompletableFuture<Location> getBedSpawnLocationAsync(@Nonnull Player player, boolean bl) {
        return ENVIRONMENT.getBedSpawnLocationAsync(player, bl);
    }

    public static boolean isVersion(int n) {
        return ENVIRONMENT.isVersion(n);
    }

    public static boolean isVersion(int n, int n2) {
        return ENVIRONMENT.isVersion(n, n2);
    }

    public static int getMinecraftVersion() {
        return ENVIRONMENT.getMinecraftVersion();
    }

    public static int getMinecraftPatchVersion() {
        return ENVIRONMENT.getMinecraftPatchVersion();
    }

    public static int getMinecraftPreReleaseVersion() {
        return ENVIRONMENT.getMinecraftPreReleaseVersion();
    }

    public static int getMinecraftReleaseCandidateVersion() {
        return ENVIRONMENT.getMinecraftReleaseCandidateVersion();
    }

    public static boolean isSpigot() {
        return ENVIRONMENT.isSpigot();
    }

    public static boolean isPaper() {
        return ENVIRONMENT.isPaper();
    }

    public static void suggestPaper(@Nonnull Plugin plugin) {
        PaperLib.suggestPaper(plugin, Level.INFO);
    }

    public static void suggestPaper(@Nonnull Plugin plugin, @Nonnull Level level) {
        if (PaperLib.isPaper()) {
            return;
        }
        String string = "paperlib.shown-benefits";
        String string2 = plugin.getDescription().getName();
        Logger logger = plugin.getLogger();
        logger.log(level, "====================================================");
        logger.log(level, " " + string2 + " works better if you use Paper ");
        logger.log(level, " as your server software. ");
        if (System.getProperty("paperlib.shown-benefits") == null) {
            System.setProperty("paperlib.shown-benefits", "1");
            logger.log(level, "  ");
            logger.log(level, " Paper offers significant performance improvements,");
            logger.log(level, " bug fixes, security enhancements and optional");
            logger.log(level, " features for server owners to enhance their server.");
            logger.log(level, "  ");
            logger.log(level, " Paper includes Timings v2, which is significantly");
            logger.log(level, " better at diagnosing lag problems over v1.");
            logger.log(level, "  ");
            logger.log(level, " All of your plugins should still work, and the");
            logger.log(level, " Paper community will gladly help you fix any issues.");
            logger.log(level, "  ");
            logger.log(level, " Join the Paper Community @ https://papermc.io");
        }
        logger.log(level, "====================================================");
    }
}

