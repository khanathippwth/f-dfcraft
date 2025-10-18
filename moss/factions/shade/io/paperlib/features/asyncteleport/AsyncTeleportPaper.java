/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 */
package moss.factions.shade.io.paperlib.features.asyncteleport;

import java.util.concurrent.CompletableFuture;
import moss.factions.shade.io.paperlib.PaperLib;
import moss.factions.shade.io.paperlib.features.asyncteleport.AsyncTeleport;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

public class AsyncTeleportPaper
implements AsyncTeleport {
    @Override
    public CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        int n = location.getBlockX() >> 4;
        int n2 = location.getBlockZ() >> 4;
        return PaperLib.getChunkAtAsyncUrgently(location.getWorld(), n, n2, true).thenApply(chunk -> entity.teleport(location, teleportCause));
    }
}

