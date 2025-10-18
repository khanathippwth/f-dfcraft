/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.AutoLeaveTask;
import java.util.logging.Level;

public class AutoLeaveProcessFactionTask
extends AutoLeaveTask.AutoLeaveProcessor<Faction> {
    public AutoLeaveProcessFactionTask() {
        this.iterator = Factions.getInstance().getAllFactions().listIterator();
    }

    @Override
    public void go(MainConfig mainConfig) {
        Faction faction = (Faction)this.iterator.next();
        if (faction.isPlayerFreeType()) {
            return;
        }
        if (faction.isWilderness()) {
            if (mainConfig.factions().other().isAutoLeaveDeleteFPlayerData()) {
                for (FPlayer fPlayer : faction.getFPlayers()) {
                    if (!fPlayer.isOffline() || !((double)(this.now - fPlayer.getLastLoginTime()) > this.toleranceMillis)) continue;
                    if (!fPlayer.willAutoLeave()) {
                        FactionsPlugin.getInstance().debug(Level.INFO, fPlayer.getName() + " was going to be auto-removed but was set not to.");
                        continue;
                    }
                    if ((mainConfig.logging().isFactionLeave() || mainConfig.logging().isFactionKick()) && (fPlayer.hasFaction() || mainConfig.factions().other().isAutoLeaveDeleteFPlayerData())) {
                        FactionsPlugin.getInstance().log("Player " + fPlayer.getName() + " was auto-removed due to inactivity.");
                    }
                    fPlayer.remove();
                }
            }
            return;
        }
        for (FPlayer fPlayer : faction.getFPlayers()) {
            if (fPlayer.isOnline() || (double)(this.now - fPlayer.getLastLoginTime()) < this.toleranceMillis) {
                return;
            }
            if (fPlayer.willAutoLeave()) continue;
            FactionsPlugin.getInstance().debug(Level.INFO, fPlayer.getName() + " was going to be auto-removed but was set not to.");
            return;
        }
        Object object = Role.getByValue(0);
        int n = 0;
        while (object != null) {
            for (FPlayer fPlayer : faction.getFPlayersWhereRole((Role)object)) {
                if ((mainConfig.logging().isFactionLeave() || mainConfig.logging().isFactionKick()) && (fPlayer.hasFaction() || mainConfig.factions().other().isAutoLeaveDeleteFPlayerData())) {
                    FactionsPlugin.getInstance().log("Player " + fPlayer.getName() + " was auto-removed due to inactivity.");
                }
                fPlayer.leave(false);
                if (!mainConfig.factions().other().isAutoLeaveDeleteFPlayerData()) continue;
                fPlayer.remove();
            }
            object = Role.getByValue(++n);
        }
    }
}

