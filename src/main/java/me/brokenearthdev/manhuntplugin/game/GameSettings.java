package me.brokenearthdev.manhuntplugin.game;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.config.Entries;
import me.brokenearthdev.manhuntplugin.kits.Kit;

import java.util.HashMap;
import java.util.Map;

/**
 * Settings of the game
 */
public class GameSettings {
    
    private final DefaultOptions options = new DefaultOptions();
    
    public final int runnerSpawnDistanceMin;
    public final int runnerSpawnDistanceMax;
    public final int gracePeriodLength;
    public final int randomSpawnDistanceMin;
    public final int randomSpawnDistanceMax;
    public final boolean recordStats;
    public final Kit kit;
    
    private final Map<String, Object> settings = new HashMap<>();
    
    public Map<String, Object> settingsCopy() {
        return new HashMap<>(settings);
    }
    
    public GameSettings(int runnerSpawnDistanceMin, int runnerSpawnDistanceMax, int randomSpawnDistanceMin,
                        int randomSpawnDistanceMax, int gracePeriodLength, boolean recordStats, Kit kit) {
        if (runnerSpawnDistanceMax >= runnerSpawnDistanceMin) {
            this.runnerSpawnDistanceMin = runnerSpawnDistanceMin;
            this.runnerSpawnDistanceMax = runnerSpawnDistanceMax;
        } else {
            this.runnerSpawnDistanceMax = options.runnerSpawnDistanceMax;
            this.runnerSpawnDistanceMin = options.runnerSpawnDistanceMin;
        }
        if (randomSpawnDistanceMax >= randomSpawnDistanceMin) {
            this.randomSpawnDistanceMax = randomSpawnDistanceMax;
            this.randomSpawnDistanceMin = randomSpawnDistanceMin;
        } else {
            this.randomSpawnDistanceMin = options.randomSpawnDistanceMin;
            this.randomSpawnDistanceMax = options.randomSpawnDistanceMax;
        }
        this.gracePeriodLength = gracePeriodLength;
        this.recordStats = recordStats;
        this.kit = kit;
        createSettingsMap();
    }
    
    private void createSettingsMap() {
        settings.put("runnerSpawnDistanceMin", runnerSpawnDistanceMin);
        settings.put("runnerSpawnDistanceMax", runnerSpawnDistanceMax);
        settings.put("randomSpawnDistanceMin", randomSpawnDistanceMin);
        settings.put("randomSpawnDistanceMax", randomSpawnDistanceMax);
        settings.put("gracePeriodLength", gracePeriodLength);
        settings.put("recordStats", recordStats);
        settings.put("Kit", kit != null ? kit.getName() : "null");
    }
    
    public GameSettings() {
        this.runnerSpawnDistanceMin = options.runnerSpawnDistanceMin;
        this.runnerSpawnDistanceMax = options.runnerSpawnDistanceMax;
        this.gracePeriodLength = options.gracePeriodLength;
        this.randomSpawnDistanceMax = options.randomSpawnDistanceMax;
        this.randomSpawnDistanceMin = options.randomSpawnDistanceMin;
        this.recordStats = options.recordStats;
        this.kit = options.defaultKit;
        createSettingsMap();
    }
    
    // loads the default options if possible
    public static class DefaultOptions {
        
        public final int runnerSpawnDistanceMin;
        public final int runnerSpawnDistanceMax;
        public final int gracePeriodLength;
        public final int randomSpawnDistanceMin;
        public final int randomSpawnDistanceMax;
        public final boolean recordStats;
        public final Kit defaultKit;
        
        public DefaultOptions() {
            Entries lastCaptured = Manhunt.getInstance().getConfigManager().getLastCaptured();
           if (lastCaptured.RUNNER_SPAWN_DIST_MIN.get() > lastCaptured.RUNNER_SPAWN_DIST_MAX.get()) {
                this.runnerSpawnDistanceMin = 32;
                this.runnerSpawnDistanceMax = 64;
            } else {
                this.runnerSpawnDistanceMin = lastCaptured.RUNNER_SPAWN_DIST_MIN.get();
                this.runnerSpawnDistanceMax = lastCaptured.RUNNER_SPAWN_DIST_MAX.get();
            }if (lastCaptured.RANDOM_SPAWN_DIST_MIN.get() > lastCaptured.RANDOM_SPAWN_DIST_MAX.get()) {
                this.randomSpawnDistanceMax = 1024;
                this.randomSpawnDistanceMin = 256;
            } else {
                this.randomSpawnDistanceMax = lastCaptured.RANDOM_SPAWN_DIST_MAX.get();
                this.randomSpawnDistanceMin = lastCaptured.RANDOM_SPAWN_DIST_MIN.get();
            }
           gracePeriodLength = lastCaptured.GRACE_PERIOD_LEN.get();
           recordStats = lastCaptured.RECORD_PLAYER_STATS.get();
           defaultKit = lastCaptured.DEF_KIT.get();
        }
        
        
    }
    
}
