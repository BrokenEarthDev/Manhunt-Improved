package me.brokenearthdev.manhuntplugin.core.config.strategies;

import me.brokenearthdev.manhuntplugin.core.config.ConfigStrategy;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.kits.Kits;
import org.bukkit.configuration.file.YamlConfiguration;

public class KitStrategy extends ConfigStrategy<Kit> {
    /**
     * Loads {@link Kit} from the value found in the path
     *
     * @param def  The default value (in case none is found)
     * @param from The {@link YamlConfiguration} to load from
     * @param path The path where {@link Kit} is found
     * @return The loaded {@link Kit}
     */
    @Override
    public Kit load(Kit def, YamlConfiguration from, String path) {
        Object found = from.get(path, def);
        if (!(found instanceof String))
            // kit must be parsed from string
            return def;
        String name = (String) found;
        Kit parsed = Kits.parseKit(name);
        return parsed == null ? def : parsed;
    }
    
    @Override
    public void write(YamlConfiguration to, String path, Kit val) {
        // todo save kits
    }
}
