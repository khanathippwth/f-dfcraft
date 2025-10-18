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
import moss.factions.shade.io.paperlib.features.asyncteleport.AsyncTeleport;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

public class AsyncTeleportSync
implements AsyncTeleport {
    @Override
    public CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return CompletableFuture.completedFuture(entity.teleport(location, teleportCause));
    }
}

