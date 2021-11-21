package me.brokenearthdev.manhuntplugin.core.config;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.config.strategies.GameStatsStrategy;
import me.brokenearthdev.manhuntplugin.core.config.strategies.KitStrategy;
import me.brokenearthdev.manhuntplugin.core.config.strategies.PlayerStatsStrategy;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.stats.CachedPlayerProfile;
import me.brokenearthdev.manhuntplugin.stats.GameStats;

import java.util.HashSet;
import java.util.Set;


/**
 * Quick access entries
 */
public class Entries {
    
    // GAME SETTINGS
    
    /**
     * Minimum runner spawn distance from hunter
     */
    public final ConfigurationEntry<Integer> RUNNER_SPAWN_DIST_MIN =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "game.defoptions.runnerSpawnDistanceMin", 32);
    
    /**
     * Maximum runner spawn distance from hunter
     */
    public final ConfigurationEntry<Integer> RUNNER_SPAWN_DIST_MAX =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "game.defoptions.runnerSpawnDistanceMax", 64);
    
    /**
     * Minimum random spawn distance from spawn
     */
    public final ConfigurationEntry<Integer> RANDOM_SPAWN_DIST_MIN =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "game.defoptions.randomSpawnDistanceMin", 256);
    
    /**
     * Maximum random spawn distance from spawn
     */
    public final ConfigurationEntry<Integer> RANDOM_SPAWN_DIST_MAX =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "game.defoptions.randomSpawnDistanceMax", 1024);
    
    /**
     * Grace period length
     */
    public final ConfigurationEntry<Integer> GRACE_PERIOD_LEN =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "game.defoptions.gracePeriodLen", 30);
    
    /**
     * Record player stats
     */
    public final ConfigurationEntry<Boolean> RECORD_PLAYER_STATS =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "game.defoptions.recordStats", true);
    
    /**
     * Default kit
     */
    public final ConfigurationEntry<Kit> DEF_KIT =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "game.defoptions.kit", null, new KitStrategy());
    
    
    // SPEEDRUNNER SETTINGS
    public final ConfigurationEntry<Integer> EXTRA_HEARTS =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "runner.defoptions.extraHearts", 0);
    
    public final ConfigurationEntry<Integer> EXTRA_DAMAGE =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "runner.defoptions.extraDamage", 0);
    
    public final ConfigurationEntry<Integer> SPEED_BOOST =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "runner.defoptions.speedBoost.amp", 1);

    public final ConfigurationEntry<Integer> SPEED_BOOST_DURATION =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "runner.defoptions.speedBoost.duration", GRACE_PERIOD_LEN.get());
    
    public final ConfigurationEntry<Integer> ALERT_PROXIMITY_RADIUS =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "runner.defoptions.alertProximityRadius", 32);
    
    public final ConfigurationEntry<Integer> AUTO_SMELT_PROBABILITY =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getSettingsConfig(),
                    "runner.defoptions.autoSmeltProbability", 20);
    
    public final ConfigurationEntry<Set<CachedPlayerProfile>> PLAYER_PROFILES =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getDataConfig(),
                    "playerData", new HashSet<>(), new PlayerStatsStrategy());
    
    
    public final ConfigurationEntry<Set<GameStats>> GAME_STATS =
            new ConfigurationEntry<>(Manhunt.getInstance().getConfigManager().getDataConfig(),
                    "gameData", new HashSet<>(), new GameStatsStrategy());
    
    public void writeAll() {
        RUNNER_SPAWN_DIST_MIN.writeToConfig();
        RUNNER_SPAWN_DIST_MAX.writeToConfig();
        RANDOM_SPAWN_DIST_MIN.writeToConfig();
        RANDOM_SPAWN_DIST_MAX.writeToConfig();
        GRACE_PERIOD_LEN.writeToConfig();
        DEF_KIT.writeToConfig();
        
        EXTRA_HEARTS.writeToConfig();
        EXTRA_DAMAGE.writeToConfig();
        SPEED_BOOST.writeToConfig();
        SPEED_BOOST_DURATION.writeToConfig();
        ALERT_PROXIMITY_RADIUS.writeToConfig();
        AUTO_SMELT_PROBABILITY.writeToConfig();
        
        PLAYER_PROFILES.writeToConfig();
        GAME_STATS.writeToConfig();
    }
    
}
