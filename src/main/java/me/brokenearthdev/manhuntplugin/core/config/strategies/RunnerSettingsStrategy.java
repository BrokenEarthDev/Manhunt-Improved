package me.brokenearthdev.manhuntplugin.core.config.strategies;

import me.brokenearthdev.manhuntplugin.core.config.ConfigStrategy;
import me.brokenearthdev.manhuntplugin.game.players.SpeedrunnerSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class RunnerSettingsStrategy extends ConfigStrategy<SpeedrunnerSettings> {
    /**
     * Loads {@link SpeedrunnerSettings} from the value found in the path
     *
     * @param def  The default value (in case none is found)
     * @param from The {@link YamlConfiguration} to load from
     * @param path The path where {@link SpeedrunnerSettings} is found
     * @return The loaded {@link SpeedrunnerSettings}
     */
    @Override
    public SpeedrunnerSettings load(SpeedrunnerSettings def, YamlConfiguration from, String path) {
        ConfigurationSection section = from.getConfigurationSection(path);
        if (section == null) return def;
        return new SpeedrunnerSettings.Builder()
                .setExtraHearts(section.getInt("extra_hearts"))
                .setExtraDamage(section.getInt("extra_damage"))
                .setSpeedBoost(section.getInt("speed_boost"))
                .setSpeedBoostDuration(section.getInt("speed_boost_duration"))
                .setAlertProximityRadius(section.getInt("alert_proximity_rad"))
                .setAutoSmeltProbability(section.getInt("auto_smelt_probability"))
                .build();
    }
    
    /**
     * Writes {@link SpeedrunnerSettings} to the value in the configuration
     *
     * @param to   The destination
     * @param path The path to write
     * @param val  The value to set in the path
     */
    @Override
    public void write(YamlConfiguration to, String path, SpeedrunnerSettings val) {
        ConfigurationSection destination = to.getConfigurationSection(path);
        if (destination == null)
            destination = to.createSection(path);
        destination.set("extra_hearts", val.extraHearts);
        destination.set("extra_damage", val.extraDamage);
        destination.set("speed_boost", val.speedboost);
        destination.set("speed_boost_duration", val.speedboostDuration);
        destination.set("alert_proximity_rad", val.alertProximityRadius);
        destination.set("auto_smelt_probability", val.autoSmeltProbability);
    }
}
