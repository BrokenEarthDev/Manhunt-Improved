package me.brokenearthdev.manhuntplugin.core.config;

import me.brokenearthdev.manhuntplugin.core.config.strategies.DefaultStrategy;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Represents a configuration entry, which can be recaptured.
 *
 * @param <T> Type of the value
 */
public class ConfigurationEntry<T> {

    private T value;
    private final String path;
    private final YamlConfiguration config;
    private final ConfigStrategy<T> strategy;
    
    public ConfigurationEntry(YamlConfiguration config, String path, T def, ConfigStrategy<T> strat) {
        this.path = path;
        this.config = config;
        this.strategy = strat;
        this.value = load(def); // ensure load called last
    }
    
    public ConfigurationEntry(YamlConfiguration config, String path, T def) {
        this(config, path, def, new DefaultStrategy<>());
    }
    
    /**
     * Attempts to recapture the value found in the path. If the
     * recapture fails, the value will not be changed.
     */
    public void recapture() {
        this.value = load(value);
    }
    
    /**
     * Writes the key-value pair to configuration
     */
    public void writeToConfig() {
        strategy.write(config, path, value);
    }
    
    public T get() {
        return value;
    }
    
    public String getPath() {
        return path;
    }
    
    public YamlConfiguration getConfig() {
        return config;
    }
    
    private T load(T def) {
        return strategy.load(def, getConfig(), getPath());
    }

}
