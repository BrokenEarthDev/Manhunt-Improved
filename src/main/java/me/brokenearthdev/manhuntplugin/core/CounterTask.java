package me.brokenearthdev.manhuntplugin.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a counter that counts from a specific number.
 */
public final class CounterTask extends RepeatedRunnable {
    
    private int left;
    private CounterTask afterCounter;
    private final Consumer<Integer> update;
    
    // User-friendly timer updates
    private final List<CounterUpdater> updaters = new ArrayList<>();
    
    public CounterTask(String name, Consumer<Integer> update, int ticks, int after) {
        super(name);
        left = ticks;
        this.update = update;
        setPeriod(20);
        if (after > 0) {
            setPaused(true); // initially until
                             // enough ticks passed
            afterCounter = new CounterTask(i -> {
                if (i == 0) {
                    CounterTask.this.setPaused(false);
                    update.accept(ticks);
                    this.updaters.forEach(u -> u.check(left));
                }
            }, after);
        }
    }
    
    public CounterTask(Consumer<Integer> update, int ticks, int after) {
        this("counter_task", update, ticks, after);
    }
    
    public CounterTask(Consumer<Integer> update, int ticks) {
        this(update, ticks, 0);
    }
    
    /**
     * Adds a counter updater
     *
     * @param updater The updater
     * @return This object
     */
    public RepeatedRunnable addCounterUpdater(CounterUpdater updater) {
        updaters.add(updater);
        return this;
    }
    
    /**
     * Removes a counter updater
     *
      * @param updater The updater
     * @return This object
     */
    public RepeatedRunnable removeCounterUpdater(CounterUpdater updater) {
        updaters.remove(updater);
        return this;
    }
    
    /**
     * Sets the task manager of {@code this} counter and the <i>afterCounter</i>
     *
     * @param manager the manager
     * @return This instance
     */
    @Override
    public RepeatedRunnable setTaskManager(TaskManager manager) {
        super.setTaskManager(manager);
        if (afterCounter != null) afterCounter.setTaskManager(manager);
        else {
            update.accept(left);
            this.updaters.forEach(u -> u.check(left));
        }
        return this;
    }
    
    @Override
    public void setPaused(boolean paused) {
        super.setPaused(paused);
    }
    
    /**
     * Sets the period to the value passed in. This will modify
     * the time between executions.
     *
     * @param period The period. Should not be less than 20
     */
    @Override
    public RepeatedRunnable setPeriod(int period) {
        throw new IllegalArgumentException(this + " can't have its period modified");
    }
    
    /**
     * Sets the ticks left
     *
     * @param left The ticks left
     */
    public void setTicksLeft(int left) {
        this.left = left;
    }
    
    /**
     * @return The ticks left
     */
    public int getTicksLeft() {
        return left;
    }
    
    /**
     * Code that will be executed
     */
    @Override
    public void run() {
        left -= 20;
        update.accept(left);
        this.updaters.forEach(u -> u.check(left));
        if (left == 0)
            setPaused(true);
    }
    
    /**
     * A class that fires a consumer every certain amount of ticks between a
     * certain range.
     */
    public static final class CounterUpdater {
        
        public final int from, to;
        public final int update;
        private final Consumer<Integer> updateConsumer;
        
        public CounterUpdater(int from, int to, int update, Consumer<Integer> updateConsumer) {
            // check
            if ((to - from) % update != 0 && to != Integer.MAX_VALUE) {
                throw new IllegalArgumentException("(to - from) % update is NOT 0 (" + (to - from) % update + ")");
            }
            this.from = from;
            this.to = to;
            this.update = update;
            this.updateConsumer = updateConsumer;
        }
    
        @Override
        public boolean equals(Object o2) {
            if (o2 instanceof CounterUpdater) {
                CounterUpdater u2 = (CounterUpdater) o2;
                return u2.from == from && u2.to == to && u2.update == update;
            }
            return false;
        }
        
        /**
         * @param left Time left
         * @return Whether the update consumer is fired
         */
        protected boolean check(int left) {
            if (left <= to && left >= from) {
                // check if left is within range
                if (left % update == 0) {
                    // check if update a multiple of
                    // left
                    updateConsumer.accept(left);
                    return true;
                }
            }
            return false;
        }
        
    }

}

