package me.brokenearthdev.manhuntplugin.core.config.strategies;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.config.ConfigStrategy;
import me.brokenearthdev.manhuntplugin.game.GameSettings;
import me.brokenearthdev.manhuntplugin.game.players.*;
import me.brokenearthdev.manhuntplugin.stats.GameStats;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class GameStatsStrategy extends ConfigStrategy<Set<GameStats>> {
    
    private final GameSettingsStrategy gameSettingsStrategy = new GameSettingsStrategy();
    private final RunnerSettingsStrategy runnerSettingsStrategy = new RunnerSettingsStrategy();
    
    /**
     * Loads {@link T} from the value found in the path
     *
     * @param def  The default value (in case none is found)
     * @param from The {@link YamlConfiguration} to load from
     * @param path The path where {@link T} is found
     * @return The loaded {@link T}
     */
    @Override
    public Set<GameStats> load(Set<GameStats> def, YamlConfiguration from, String path) {
        ConfigurationSection section = from.getConfigurationSection(path);
        if (section == null) return def;
        Set<String> keys = section.getKeys(false);
        Set<GameStats> set = new HashSet<>();
        keys.forEach(key -> {
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection gameSection = section.getConfigurationSection(key);
                if (gameSection != null) {
                    GameSettings settings = gameSettingsStrategy.load(new GameSettings(), from, path + "." + key + ".settings");
                    SpeedrunnerSettings rSettings = runnerSettingsStrategy.load(new SpeedrunnerSettings(), from, path + "." + key + ".runner_settings");
                    Map<String, Object> data = gameSection.getValues(true);
                    ConfigurationSection playersSection = section.getConfigurationSection(path + "." + key + ".players");
                    if (playersSection != null) {
                        Set<String> uuids = playersSection.getKeys(false);
                        Set<OfflineGamePlayer> offlinePlayers = new HashSet<>();
                        uuids.forEach(string -> {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(string));
                            String role = playersSection.getString(string);
                            if (role != null)
                                if (role.equals("RUNNER"))
                                    offlinePlayers.add(new OfflineGamePlayer.OfflineRunner(player));
                                else if (role.equals("HUNTER"))
                                    offlinePlayers.add(new OfflineGamePlayer.OfflineHunter(player));
                        });
                        data.put("players", offlinePlayers);
                        data.put("settings", settings);
                        data.put("runner_settings", rSettings);
                        data.put("kills", gameSection.getInt("kills"));
                        data.put("hunters", gameSection.getInt("hunters"));
                        data.put("runners", gameSection.getInt("runners"));
                        data.put("time_elapsed", gameSection.getInt("time_elapsed"));
                        data.put("winner", Team.getTeam(gameSection.getString("winner")));
                        data.put("loser", Team.getTeam(gameSection.getString("loser")));
                        set.add(new GameStats(uuid, data));
                    }
                }
            } catch (Exception e) {
                Manhunt.getInstance().getLogger().warning("Unable to load " + GameStats.class + " under key "
                        + key);
                e.printStackTrace();
            }
        });
        return set;
    }
    
    /**
     * Writes {@link T} to the value in the configuration
     *
     * @param to   The destination
     * @param path The path to write
     * @param val  The value to set in the path
     */
    @Override
    public void write(YamlConfiguration to, String path, Set<GameStats> val) {
        ConfigurationSection section = to.getConfigurationSection(path);
        if (section == null)
            section = to.createSection(path);
        for (GameStats stats : val) {
            ConfigurationSection playerSection = section.getConfigurationSection(stats.getUUID() + ".players");
            if (playerSection == null)
                playerSection = section.createSection(stats.getUUID() + ".players");
            for (OfflineGamePlayer player : stats.getPlayers())
                playerSection.set(player.getOfflinePlayer().getUniqueId().toString(), player.isHunter() ? "HUNTER" : "RUNNER");
            gameSettingsStrategy.write(to, path + stats.getUUID() + ".settings", stats.getSettings());
            runnerSettingsStrategy.write(to, path + stats.getUUID() + ".runner_settings", stats.getRunnerSettings());
            section.set(stats.getUUID() + ".kills", stats.getKills());
            section.set(stats.getUUID() + ".hunters", stats.getHunters());
            section.set(stats.getUUID() + ".runners", stats.getRunners());
            section.set(stats.getUUID() + ".time_elapsed", stats.getTimeElapsed());
            section.set(stats.getUUID() + ".winner", stats.getWinner().toString());
            section.set(stats.getUUID() + ".loser", stats.getLoser().toString());
        }
    }
}
