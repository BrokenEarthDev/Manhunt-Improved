package me.brokenearthdev.manhuntplugin.core.config.strategies;

import me.brokenearthdev.manhuntplugin.core.config.ConfigStrategy;
import org.bukkit.configuration.file.YamlConfiguration;

public class DefaultStrategy<T> extends ConfigStrategy<T> {
    public DefaultStrategy() {
        super();
    }
    
    /**
     * Loads {@link T} from the value found in the path
     *
     * @param def  The default value (in case none is found)
     * @param from The {@link YamlConfiguration} to load from
     * @param path The path where {@link T} is found
     * @return The loaded {@link T}
     */
    @Override
    public T load(T def, YamlConfiguration from, String path) {
        Object found = from.get(path, def);
        try {
            T f = (T) found;
            return f == null ? def : f;
        } catch (Exception e) {
            return def;
        }
    }
    
    @Override
    public void write(YamlConfiguration to, String path, T val) {
        to.set(path, val);
    }
}
