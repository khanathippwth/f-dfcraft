/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.util.AutoLeaveProcessFactionTask;
import com.massivecraft.factions.util.AutoLeaveProcessTask;
import java.util.ListIterator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoLeaveTask
implements Runnable {
    private static AutoLeaveProcessor<?> task;
    private final double rate = FactionsPlugin.getInstance().conf().factions().other().getAutoLeaveRoutineRunsEveryXMinutes();
    private final boolean factions = FactionsPlugin.getInstance().conf().factions().other().isAutoLeaveOnlyEntireFactionInactive();

    @Override
    public synchronized void run() {
        if (task != null && !task.isFinished()) {
            return;
        }
        task = this.factions ? new AutoLeaveProcessFactionTask() : new AutoLeaveProcessTask();
        task.runTaskTimer((Plugin)FactionsPlugin.getInstance(), 1L, 1L);
        if (this.rate != FactionsPlugin.getInstance().conf().factions().other().getAutoLeaveRoutineRunsEveryXMinutes() || this.factions != FactionsPlugin.getInstance().conf().factions().other().isAutoLeaveOnlyEntireFactionInactive()) {
            FactionsPlugin.getInstance().startAutoLeaveTask(true);
        }
    }

    public static abstract class AutoLeaveProcessor<T extends Selectable>
    extends BukkitRunnable {
        protected transient boolean readyToGo = true;
        protected transient boolean finished;
        protected transient ListIterator<T> iterator;
        protected final transient double toleranceMillis = FactionsPlugin.getInstance().conf().factions().other().getAutoLeaveAfterDaysOfInactivity() * 24.0 * 60.0 * 60.0 * 1000.0;
        protected long now;

        public void stop() {
            this.readyToGo = false;
            this.finished = true;
            this.cancel();
        }

        public final void run() {
            MainConfig mainConfig = FactionsPlugin.getInstance().conf();
            if (mainConfig.factions().other().getAutoLeaveAfterDaysOfInactivity() <= 0.0 || mainConfig.factions().other().getAutoLeaveRoutineMaxMillisecondsPerTick() <= 0) {
                this.stop();
                return;
            }
            if (!this.readyToGo) {
                return;
            }
            this.readyToGo = false;
            long l = System.currentTimeMillis();
            while (this.iterator.hasNext()) {
                this.now = System.currentTimeMillis();
                if (this.now > l + (long)mainConfig.factions().other().getAutoLeaveRoutineMaxMillisecondsPerTick()) {
                    this.readyToGo = true;
                    return;
                }
                this.go(mainConfig);
            }
            this.stop();
        }

        abstract void go(MainConfig var1);

        public boolean isFinished() {
            return this.finished;
        }
    }
}

