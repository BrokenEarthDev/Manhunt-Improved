package me.brokenearthdev.manhuntplugin.core;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class RepeatedRunnable {
    
    private int period = 20;
    private boolean paused = false;
    int timeSinceExec = 0;
    
    public final String name;
    public final UUID uuid = UUID.randomUUID();
    
    public RepeatedRunnable(String name) {
        this.name = name;
    }
    
    /**
     * Code that will be executed
     */
    public abstract void run();
    
    /**
     * Sets the period to the value passed in. This will modify
     * the time between executions.
     *
     * @param period The period. Should not be less than 20
     */
    public RepeatedRunnable setPeriod(int period) {
        this.period = Math.max(period, 20);
        return this;
    }
    
    /**
     * @return Period between executions in ticks. Must be less than
     * 20
     */
    public int getPeriod() {
        return period;
    }
    
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public RepeatedRunnable setTaskManager(TaskManager manager) {
        manager.setTask(this);
        return this;
    }
    
    public int getTimeSinceExec() {
        return timeSinceExec;
    }
    
    public void removeTask(TaskManager manager) {
        manager.removeTask(this);
    }
    
    @Override
    public String toString() {
        return name + "#" + uuid.toString();
    }
    
}
