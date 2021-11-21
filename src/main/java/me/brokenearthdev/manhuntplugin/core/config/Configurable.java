package me.brokenearthdev.manhuntplugin.core.config;

import org.bukkit.configuration.Configuration;

import java.util.Map;

public abstract class Configurable {
   
    
    public abstract Map<String, Object> data();
    
    /**
     * Writes the following object in the {@link Configuration}
     * provided under the given path.
     *
     * @param configuration The configuration
     */
    public abstract void write(String path, Configuration configuration);

}
