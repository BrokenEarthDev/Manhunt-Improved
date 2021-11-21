package me.brokenearthdev.manhuntplugin.events;

import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Triggered when a game player kills another player
 */
public class GamePlayerHomicideEvent extends GamePlayerDeathEvent {
    
    // The killer player
    private final GamePlayer killer;
    
    public GamePlayerHomicideEvent(ManhuntGame game, PlayerDeathEvent event, GamePlayer dead, GamePlayer killer) {
        super(game, event, dead);
        this.killer = killer;
    }
    
    /**
     * @return The player who killed the dead player
     */
    public GamePlayer getKiller() {
        return killer;
    }
    
}
