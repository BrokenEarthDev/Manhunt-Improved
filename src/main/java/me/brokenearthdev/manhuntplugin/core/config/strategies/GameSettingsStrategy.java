package me.brokenearthdev.manhuntplugin.core.config.strategies;

import me.brokenearthdev.manhuntplugin.core.config.ConfigStrategy;
import me.brokenearthdev.manhuntplugin.game.GameSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class GameSettingsStrategy extends ConfigStrategy<GameSettings> {
    
    private final KitStrategy strategy = new KitStrategy();
    
    /**
     * Loads {@link GameSettings} from the value found in the path
     *
     * @param def  The default value (in case none is found)
     * @param from The {@link YamlConfiguration} to load from
     * @param path The path where {@link GameSettings} is found
     * @return The loaded {@link GameSettings}
     */
    @Override
    public GameSettings load(GameSettings def, YamlConfiguration from, String path) {
        ConfigurationSection section = from.getConfigurationSection(path);
        if (section == null) return def;
        return new GameSettings(section.getInt("runner_spawn_dist_min"), section.getInt("runner_spawn_dist_max"),
                section.getInt("random_spawn_dist_min"), section.getInt("random_spawn_dist_max"), section.getInt("grace_period_len"),
                section.getBoolean("record_stats"), strategy.load(null, from, path + ".kit"));
    }
    
    /**
     * Writes {@link T} to the value in the configuration
     *
     * @param to   The destination
     * @param path The path to write
     * @param val  The value to set in the path
     */
    @Override
    public void write(YamlConfiguration to, String path, GameSettings val) {
        ConfigurationSection section = to.getConfigurationSection(path);
        if (section == null)
            section = to.createSection(path);
        section.set("runner_spawn_dist_min", val.runnerSpawnDistanceMin);
        section.set("runner_spawn_dist_max", val.runnerSpawnDistanceMax);
        section.set("random_spawn_dist_min", val.randomSpawnDistanceMin);
        section.set("random_spawn_dist_max", val.randomSpawnDistanceMax);
        section.set("record_stats", val.recordStats);
        section.set("kit", val.kit);
        
    }
}
