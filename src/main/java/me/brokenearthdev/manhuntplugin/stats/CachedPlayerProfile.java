package me.brokenearthdev.manhuntplugin.stats;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.Set;

/**
 * Allows access to information about a player entailing their kills, deaths,
 * wins, losses, etc. in manhunt games.
 * <p>
 * "Cached" means that the player profile is captured, meaning that updates to
 * the configuration will not affect the values in an instance. It is only when
 * the object reloaded will the data be updated.
 * <p>
 * For this reason, it is unrecommended to designate an instance to a new object
 * except if the object isn't permanent. It is advisable to retrieve the profiles
 * from {@link Manhunt#getPlayerProfileFor(OfflinePlayer)}
 */
public class CachedPlayerProfile {
    
    // the stats
    private final Map<String, Object> stats;
    
    // player obj
    private final OfflinePlayer player;
    
    /**
     * Returns the stats map. Possible keys are:
     * <ol>
     *     <li>kills (int)</li>
     *     <li>deaths (int)</li>
     *     <li>wins (int)</li>
     *     <li>losses (int)</li>
     *     <li>games_speedrunner (int)</li>
     *     <li>games_hunter (int)</li>
     *     <li>avg_runner_survival_sec (double)</li>
     * </ol>
     *
     * @return The stats map
     */
    public Map<String, Object> stats() {
        return stats;
    }
    
    public CachedPlayerProfile(OfflinePlayer player, Map<String, Object> stats) {
        this.player = player;
        this.stats = stats;
    }
    
    /**
     * @return The number of kills in manhunt games
     */
    public int getKills() {
        return (int) stats.get("kills");
    }
    
    /**
     * @return The number of deaths in manhunt games
     */
    public int getDeaths() {
        return (int) stats.get("deaths");
    }
    
    /**
     * @return The number of wins in manhunt games
     */
    public int getWins() {
        return (int) stats.get("wins");
    }
    
    /**
     * @return The number of losses in manhunt games
     */
    public int getLosses() {
        return (int) stats.get("losses");
    }
    
    /**
     * @return The number of games where the client is selected to be a speedrunner
     */
    public int getGamesSpeedrunner() {
        return (int) stats.get("games_speedrunner");
    }
    
    /**
     * @return The number of games where the client is selected to be a hunter
     */
    public int getGamesHunter() {
        return (int) stats.get("games_hunter");
    }
    
    
    /**
     * Sets the number of kills
     *
     * @param kills New no. of kills
     */
    public void setKills(int kills) {
        stats.put("kills", kills);
        save();
    }
    
    /**
     * Sets the number of deaths
     *
     * @param deaths New no. of deaths
     */
    public void setDeaths(int deaths) {
        stats.put("deaths", deaths);
        save();
    }
    
    /**
     * Sets the number of wins
     *
     * @param wins New no. of wins
     */
    public void setWins(int wins) {
        stats.put("wins", wins);
        save();
    }
    
    /**
     * Sets the number of losses
     *
     * @param losses New no. of losses
     */
    public void setLosses(int losses) {
        stats.put("losses", losses);
        save();
    }
    
    /**
     * Sets the number of games spent as a speedrunner
     *
     * @param games New no. of games spent as speedrunner
     */
    public void setGamesSpeedrunner(int games) {
        stats.put("games_speedrunner", games);
    }
    
    /**
     * Sets the number of games spent as a hunter
     *
     * @param games New no. of games spent as hunter
     */
    public void setGamesHunter(int games) {
        stats.put("games_hunter", games);
    }
    
    public int getRating() {
        int wins = getWins() * 9;
        int losses = getLosses() * 5;
        int kills = getKills() * 4;
        int deaths = getDeaths() * 3;
        return (wins - losses) + (kills - deaths);
    }
    
    public int getRanking() {
        Set<CachedPlayerProfile> cachedPlayerProfiles = Manhunt.getInstance().getConfigManager().getLastCaptured().PLAYER_PROFILES.get();
        int index = 1;
        for (CachedPlayerProfile profile : cachedPlayerProfiles) {
            if (profile.equals(this))
                break;
            index++;
        }
        return index;
    }
    
    /**
     * @return The {@link OfflinePlayer}, who holds this profile
     */
    public OfflinePlayer getPlayer() {
        return player;
    }
    
    private void save() {
        Set<CachedPlayerProfile> profiles = Manhunt.getInstance().getConfigManager().getLastCaptured().PLAYER_PROFILES.get();
        profiles.remove(this);
        profiles.add(this);
    }
    
}
