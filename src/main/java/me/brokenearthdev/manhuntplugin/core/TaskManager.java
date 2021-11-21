package me.brokenearthdev.manhuntplugin.core;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Manages sub-tasks using one {@link BukkitTask} that runs once
 * per second.
 */
public class TaskManager {
    
    /**
     * A list of repeated runnables
     */
    private List<RepeatedRunnable> list = new ArrayList<>();
    
    // Total time elapsed since the execution of the task
    // began.
    private int elapsed = 0;
    
    private final BukkitTask task = new BukkitRunnable() {
        // 90 30
        // 60 30
        @Override
        public void run() {
            elapsed += 1;
            List<RepeatedRunnable> copy = new ArrayList<>(list);
            Iterator<RepeatedRunnable> iterator = copy.iterator();
            RepeatedRunnable r;
            while (iterator.hasNext()) {
                r = iterator.next();
                if (r.timeSinceExec == r.getPeriod() && !r.isPaused()) {
                    r.run();
                    r.timeSinceExec = 0;
                    if (ManhuntGame.getManhuntGame() != null)
                        ManhuntGame.getManhuntGame().gameLogger.high("Task " + r.toString() + " executed after " + r.getPeriod() + " ticks");
                } else if (!r.isPaused())
                    r.timeSinceExec++;
            }
            list = copy;
        }
    }.runTaskTimer(Manhunt.getInstance(), 0, 1);
    
    /**
     * Adds a sub-task that would run every given period
     *
     * @param runnable The runnable
     */
    public void setTask(RepeatedRunnable runnable) {
        this.list.add(runnable);
    }
    
    /**
     * Removes a sub-tasks
     *
     * @param runnable The runnable
     */
    public void removeTask(RepeatedRunnable runnable) {
        this.list.remove(runnable);
    }
    
    public List<RepeatedRunnable> getRepeatedRunnables() {
        return list;
    }
    
    public RepeatedRunnable getRunnable(String name, UUID uuid) {
        for (RepeatedRunnable runnable : list) {
            if (runnable.uuid.equals(uuid) && runnable.name.equals(name))
                return runnable;
        }
        return null;
    }
    
}
