/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.inventory.Inventory
 */
package moss.factions.shade.io.paperlib.environments;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import moss.factions.shade.io.paperlib.features.asyncchunks.AsyncChunks;
import moss.factions.shade.io.paperlib.features.asyncchunks.AsyncChunksSync;
import moss.factions.shade.io.paperlib.features.asyncteleport.AsyncTeleport;
import moss.factions.shade.io.paperlib.features.asyncteleport.AsyncTeleportSync;
import moss.factions.shade.io.paperlib.features.bedspawnlocation.BedSpawnLocation;
import moss.factions.shade.io.paperlib.features.bedspawnlocation.BedSpawnLocationSync;
import moss.factions.shade.io.paperlib.features.blockstatesnapshot.BlockStateSnapshot;
import moss.factions.shade.io.paperlib.features.blockstatesnapshot.BlockStateSnapshotBeforeSnapshots;
import moss.factions.shade.io.paperlib.features.blockstatesnapshot.BlockStateSnapshotNoOption;
import moss.factions.shade.io.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import moss.factions.shade.io.paperlib.features.chunkisgenerated.ChunkIsGenerated;
import moss.factions.shade.io.paperlib.features.chunkisgenerated.ChunkIsGeneratedApiExists;
import moss.factions.shade.io.paperlib.features.chunkisgenerated.ChunkIsGeneratedUnknown;
import moss.factions.shade.io.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshot;
import moss.factions.shade.io.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotBeforeSnapshots;
import moss.factions.shade.io.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotNoOption;
import moss.factions.shade.io.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

public abstract class Environment {
    private final int minecraftVersion;
    private final int minecraftPatchVersion;
    private final int minecraftPreReleaseVersion;
    private final int minecraftReleaseCandidateVersion;
    protected AsyncChunks asyncChunksHandler = new AsyncChunksSync();
    protected AsyncTeleport asyncTeleportHandler = new AsyncTeleportSync();
    protected ChunkIsGenerated isGeneratedHandler = new ChunkIsGeneratedUnknown();
    protected BlockStateSnapshot blockStateSnapshotHandler;
    protected InventoryHolderSnapshot inventoryHolderSnapshotHandler;
    protected BedSpawnLocation bedSpawnLocationHandler = new BedSpawnLocationSync();

    public Environment() {
        this(Bukkit.getVersion());
    }

    Environment(String string) {
        Pattern pattern = Pattern.compile("(?i)\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?(?: (Pre-Release|Release Candidate) )?(\\d)?\\)");
        Matcher matcher = pattern.matcher(string);
        int n = 0;
        int n2 = 0;
        int n3 = -1;
        int n4 = -1;
        if (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            try {
                n = Integer.parseInt(matchResult.group(2), 10);
            } catch (Exception exception) {
                // empty catch block
            }
            if (matchResult.groupCount() >= 3) {
                try {
                    n2 = Integer.parseInt(matchResult.group(3), 10);
                } catch (Exception exception) {
                    // empty catch block
                }
            }
            if (matchResult.groupCount() >= 5) {
                try {
                    int n5 = Integer.parseInt(matcher.group(5));
                    if (matcher.group(4).toLowerCase(Locale.ENGLISH).contains("pre")) {
                        n3 = n5;
                    } else {
                        n4 = n5;
                    }
                } catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        this.minecraftVersion = n;
        this.minecraftPatchVersion = n2;
        this.minecraftPreReleaseVersion = n3;
        this.minecraftReleaseCandidateVersion = n4;
        if (this.isVersion(13, 1)) {
            this.isGeneratedHandler = new ChunkIsGeneratedApiExists();
        }
        if (!this.isVersion(12)) {
            this.blockStateSnapshotHandler = new BlockStateSnapshotBeforeSnapshots();
            this.inventoryHolderSnapshotHandler = new InventoryHolderSnapshotBeforeSnapshots();
        } else {
            this.blockStateSnapshotHandler = new BlockStateSnapshotNoOption();
            this.inventoryHolderSnapshotHandler = new InventoryHolderSnapshotNoOption();
        }
    }

    public abstract String getName();

    public CompletableFuture<Chunk> getChunkAtAsync(World world, int n, int n2, boolean bl) {
        return this.asyncChunksHandler.getChunkAtAsync(world, n, n2, bl, false);
    }

    public CompletableFuture<Chunk> getChunkAtAsync(World world, int n, int n2, boolean bl, boolean bl2) {
        return this.asyncChunksHandler.getChunkAtAsync(world, n, n2, bl, bl2);
    }

    public CompletableFuture<Chunk> getChunkAtAsyncUrgently(World world, int n, int n2, boolean bl) {
        return this.asyncChunksHandler.getChunkAtAsync(world, n, n2, bl, true);
    }

    public CompletableFuture<Boolean> teleport(Entity entity, Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return this.asyncTeleportHandler.teleportAsync(entity, location, teleportCause);
    }

    public boolean isChunkGenerated(World world, int n, int n2) {
        return this.isGeneratedHandler.isChunkGenerated(world, n, n2);
    }

    public BlockStateSnapshotResult getBlockState(Block block, boolean bl) {
        return this.blockStateSnapshotHandler.getBlockState(block, bl);
    }

    public InventoryHolderSnapshotResult getHolder(Inventory inventory, boolean bl) {
        return this.inventoryHolderSnapshotHandler.getHolder(inventory, bl);
    }

    public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean bl) {
        return this.bedSpawnLocationHandler.getBedSpawnLocationAsync(player, bl);
    }

    public boolean isVersion(int n) {
        return this.isVersion(n, 0);
    }

    public boolean isVersion(int n, int n2) {
        return this.minecraftVersion > n || this.minecraftVersion >= n && this.minecraftPatchVersion >= n2;
    }

    public int getMinecraftVersion() {
        return this.minecraftVersion;
    }

    public int getMinecraftPatchVersion() {
        return this.minecraftPatchVersion;
    }

    public int getMinecraftPreReleaseVersion() {
        return this.minecraftPreReleaseVersion;
    }

    public int getMinecraftReleaseCandidateVersion() {
        return this.minecraftReleaseCandidateVersion;
    }

    public boolean isSpigot() {
        return false;
    }

    public boolean isPaper() {
        return false;
    }
}

