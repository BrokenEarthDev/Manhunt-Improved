package me.brokenearthdev.manhuntplugin.stats;

import me.brokenearthdev.manhuntplugin.game.GameSettings;
import me.brokenearthdev.manhuntplugin.game.players.OfflineGamePlayer;
import me.brokenearthdev.manhuntplugin.game.players.SpeedrunnerSettings;
import me.brokenearthdev.manhuntplugin.game.players.Team;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GameStats {

    // The game stats
    private final Map<String, Object> data;
    private final UUID uuid;
    
    public GameStats(UUID uuid, Map<String, Object> data) {
        this.data = data;
        this.uuid = uuid;
    }
    
    /**
     * Returns the game stats map
     * <p>
     * Possible keys include
     * <ol>
     *     <p>players ({@link List} of {@link OfflineGamePlayer}s)</p>
     *     <p>settings ({@link GameSettings}(</p>
     *     <p>runner_settings</p>
     *     <p>kills (int)</p>
     *     <p>deaths (int)</p>
     *     <p>hunters (int)</p>
     *     <p>runners (int)</p>
     *     <p>time_elapsed (int)</p>
     * </ol>
     *
     * @return A map containing data about the stats
     */
    public Map<String, Object> stats() {
        return data;
    }
    
    public UUID getUUID() {
        return uuid;
    }
    
    public Set<OfflineGamePlayer> getPlayers() {
        return (Set<OfflineGamePlayer>) stats().get("players");
    }
    
    public GameSettings getSettings() {
        return (GameSettings) stats().get("settings");
    }
    
    public SpeedrunnerSettings getRunnerSettings() {
        return (SpeedrunnerSettings) stats().get("runner_settings");
    }
    
    public int getKills() {
        return (int) stats().get("kills");
    }
    
    public int getHunters() {
        return (int) stats().get("hunters");
    }
    
    public int getRunners() {
        return (int) stats().get("runners");
    }
    
    public int getTimeElapsed() {
        return (int) stats().get("time_elapsed");
    }
    
    public Team getWinner() {
        return (Team) stats().get("winner");
    }
    
    public Team getLoser() {
        return (Team) stats().get("loser");
    }
    
    public void setPlayers(Set<OfflineGamePlayer> players) {
        stats().put("players", players);
    }
    
    public void setSettings(GameSettings settings) {
        stats().put("settings", settings);
    }
    
    public void setRunnerSettings(SpeedrunnerSettings settings) {
        stats().put("runner_settings", settings);
    }
    
    public void setKills(int kills) {
        stats().put("kills", kills);
    }
    
    public void setHunters(int hunters) {
        stats().put("hunters", hunters);
    }
    
    public void setRunners(int runners) {
        stats().put("runners", runners);
    }
    
    public void setTimeElapsed(int elapsed) {
        stats().put("time_elapsed", elapsed);
    }
    
    public void setWinner(Team winner) {
        stats().put("winner", winner);
    }
    
    public void setLoser(Team loser) {
        stats().put("loser", loser);
    }
    
}
