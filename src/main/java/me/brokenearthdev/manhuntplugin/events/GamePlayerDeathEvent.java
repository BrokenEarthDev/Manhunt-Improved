package me.brokenearthdev.manhuntplugin.events;

import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Fired when a player dies, regardless of whether the killer is a {@link me.brokenearthdev.manhuntplugin.game.players.GamePlayer}
 * or an {@link org.bukkit.entity.Entity} or any death that is not caused by an entity.
 * <p>
 * If the killer is also a {@link me.brokenearthdev.manhuntplugin.game.players.GamePlayer}, then
 * {@link GamePlayerHomicideEvent} will also be triggered
 */
public class GamePlayerDeathEvent extends GameEvent {
    
    // The dead player
    private final GamePlayer dead;
    
    public GamePlayerDeathEvent(ManhuntGame game, PlayerDeathEvent event, GamePlayer dead) {
        super(game, event);
        this.dead = dead;
    }
    
    /**
     * @return The dead player
     */
    public GamePlayer getDead() {
        return dead;
    }
    
}
