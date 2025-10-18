/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.AutoLeaveTask;
import java.util.ArrayList;
import java.util.logging.Level;

public class AutoLeaveProcessTask
extends AutoLeaveTask.AutoLeaveProcessor<FPlayer> {
    public AutoLeaveProcessTask() {
        this.iterator = ((ArrayList)FPlayers.getInstance().getAllFPlayers()).listIterator();
    }

    @Override
    public void go(MainConfig mainConfig) {
        FPlayer fPlayer = (FPlayer)this.iterator.next();
        if (fPlayer.isOffline() && (double)(this.now - fPlayer.getLastLoginTime()) > this.toleranceMillis) {
            Faction faction;
            if (!fPlayer.willAutoLeave()) {
                FactionsPlugin.getInstance().debug(Level.INFO, fPlayer.getName() + " was going to be auto-removed but was set not to.");
                return;
            }
            if ((mainConfig.logging().isFactionLeave() || mainConfig.logging().isFactionKick()) && (fPlayer.hasFaction() || mainConfig.factions().other().isAutoLeaveDeleteFPlayerData())) {
                FactionsPlugin.getInstance().log("Player " + fPlayer.getName() + " was auto-removed due to inactivity.");
            }
            if (fPlayer.getRole() == Role.ADMIN && (faction = fPlayer.getFaction()) != null) {
                fPlayer.getFaction().promoteNewLeader();
            }
            fPlayer.leave(false);
            this.iterator.remove();
            if (mainConfig.factions().other().isAutoLeaveDeleteFPlayerData()) {
                fPlayer.remove();
            }
        }
    }
}

