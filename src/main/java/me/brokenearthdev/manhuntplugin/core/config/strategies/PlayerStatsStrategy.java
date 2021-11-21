package me.brokenearthdev.manhuntplugin.core.config.strategies;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.config.ConfigStrategy;
import me.brokenearthdev.manhuntplugin.stats.CachedPlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class PlayerStatsStrategy extends ConfigStrategy<Set<CachedPlayerProfile>> {
    
    /**
     * Loads {@link CachedPlayerProfile} from the value found in the path
     *
     * @param def  The default value (in case none is found)
     * @param from The {@link YamlConfiguration} to load from
     * @param path The path where {@link CachedPlayerProfile} is found
     * @return The loaded {@link CachedPlayerProfile}
     */
    @Override
    public Set<CachedPlayerProfile> load(Set<CachedPlayerProfile> def, YamlConfiguration from, String path) {
        ConfigurationSection found = from.getConfigurationSection(path);
        if (found == null) return def;
        Set<String> keys = found.getKeys(false);
        TreeSet<CachedPlayerProfile> profiles = new TreeSet<>(Comparator.comparingInt(CachedPlayerProfile::getRating));
        keys.forEach(k -> {
            try {
                ConfigurationSection playerSection = found.getConfigurationSection(k);
                if (playerSection != null) {
                    Map<String, Object> data = playerSection.getValues(true);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(k));
                    profiles.add(new CachedPlayerProfile(player, data));
                }
            } catch (Exception e) {
                Manhunt.getInstance().getLogger().warning("Unable to load " + CachedPlayerProfile.class + " under key "
                    + k);
                e.printStackTrace();
            }
        });
        return profiles;
    }
    
    @Override
    public void write(YamlConfiguration to, String path, Set<CachedPlayerProfile> val) {
        ConfigurationSection section = to.getConfigurationSection(path);
        if (section == null)
            section = to.createSection(path);
        ConfigurationSection finalSection = section;
        val.forEach((k) -> {
            finalSection.set(k.getPlayer().getUniqueId().toString(), k.stats());
        });
        //to.set(path, finalSection);
    }
}
