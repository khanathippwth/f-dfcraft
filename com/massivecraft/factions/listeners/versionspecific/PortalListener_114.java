/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.PortalCreateEvent
 */
package com.massivecraft.factions.listeners.versionspecific;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TL;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

public class PortalListener_114
implements Listener {
    public final FactionsPlugin plugin;

    public PortalListener_114(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent portalCreateEvent) {
        Player player;
        block7: {
            block6: {
                Entity entity = portalCreateEvent.getEntity();
                if (!FactionsPlugin.getInstance().conf().factions().portals().isLimit()) {
                    return;
                }
                if (!(entity instanceof Player)) break block6;
                player = (Player)entity;
                if (this.plugin.worldUtil().isEnabled(portalCreateEvent.getEntity().getWorld())) break block7;
            }
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        for (BlockState blockState : portalCreateEvent.getBlocks()) {
            FLocation fLocation = new FLocation(blockState.getLocation());
            Faction faction = Board.getInstance().getFactionAt(fLocation);
            if (faction.isWilderness()) continue;
            if (!faction.isNormal() && !player.isOp()) {
                portalCreateEvent.setCancelled(true);
                return;
            }
            String string = FactionsPlugin.getInstance().conf().factions().portals().getMinimumRelation();
            if (fPlayer.getFaction().getRelationTo(faction).isAtLeast(Relation.fromString(string))) continue;
            portalCreateEvent.setCancelled(true);
            player.sendMessage(TL.PLAYER_PORTAL_NOTALLOWED.toString());
            return;
        }
    }
}

