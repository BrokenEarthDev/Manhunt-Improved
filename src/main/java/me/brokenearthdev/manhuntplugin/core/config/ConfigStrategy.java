package me.brokenearthdev.manhuntplugin.core.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;

/**
 * Dictates how objects of type {@link T} should be loaded from a configuration.
 * <p>
 * Useful for loading classes that are not supported by loading directly from
 * {@link YamlConfiguration} like {@link me.brokenearthdev.manhuntplugin.kits.Kit}s, etc.
 *
 * @param <T>
 */
public abstract class ConfigStrategy<T> {
    
    /**
     * Loads {@link T} from the value found in the path
     *
     * @param def The default value (in case none is found)
     * @param from The {@link YamlConfiguration} to load from
     * @param path The path where {@link T} is found
     * @return The loaded {@link T}
     */
    public abstract T load(T def, YamlConfiguration from, String path);
    
    /**
     * Writes {@link T} to the path in the configuration
     *
     * @param to   The destination
     * @param path The path to write
     * @param val  The value to set in the path
     */
    public abstract void write(YamlConfiguration to, String path, T val);
    
}
