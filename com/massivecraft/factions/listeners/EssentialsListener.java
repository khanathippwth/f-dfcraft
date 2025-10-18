/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.earth2me.essentials.IEssentials
 *  com.earth2me.essentials.User
 *  org.bukkit.Location
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package com.massivecraft.factions.listeners;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsListener
implements Listener {
    private final IEssentials ess;

    public EssentialsListener(IEssentials iEssentials) {
        this.ess = iEssentials;
    }

    @EventHandler
    public void onLeave(FPlayerLeaveEvent fPlayerLeaveEvent) {
        Faction faction = fPlayerLeaveEvent.getFaction();
        User user = this.ess.getUser(UUID.fromString(fPlayerLeaveEvent.getfPlayer().getId()));
        if (user == null) {
            FactionsPlugin.getInstance().log(Level.WARNING, "Attempted to remove Essentials homes for " + fPlayerLeaveEvent.getfPlayer().getName() + " but no Essentials data at all was found for this user. This may be a bug in Essentials, or may be that the player only played prior to adding Essentials to the server");
            return;
        }
        List list = user.getHomes();
        if (list == null || list.isEmpty()) {
            return;
        }
        for (String string : user.getHomes()) {
            Location location = user.getHome(string);
            if (location == null) {
                FactionsPlugin.getInstance().getLogger().warning("Tried to check on home \"" + string + "\" for user \"" + fPlayerLeaveEvent.getfPlayer().getName() + "\" but Essentials could not load that home (invalid world?). Skipping it.");
                continue;
            }
            FLocation fLocation = new FLocation(location);
            Faction faction2 = Board.getInstance().getFactionAt(fLocation);
            if (!faction2.equals(faction) || !faction2.isNormal()) continue;
            user.delHome(string);
            FactionsPlugin.getInstance().log(Level.INFO, "FactionLeaveEvent: Removing home %s, player %s, in territory of %s", string, fPlayerLeaveEvent.getfPlayer().getName(), faction.getTag());
        }
    }
}

