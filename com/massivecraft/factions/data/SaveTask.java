/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.data;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;

public class SaveTask
implements Runnable {
    private static boolean running = false;
    private final FactionsPlugin plugin;

    public SaveTask(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
    }

    @Override
    public void run() {
        if (!this.plugin.getAutoSave() || running) {
            return;
        }
        running = true;
        Factions.getInstance().forceSave(false);
        FPlayers.getInstance().forceSave(false);
        Board.getInstance().forceSave(false);
        running = false;
    }
}

