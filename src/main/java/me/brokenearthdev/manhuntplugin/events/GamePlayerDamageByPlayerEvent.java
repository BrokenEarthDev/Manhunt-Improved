package me.brokenearthdev.manhuntplugin.events;

import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Triggered when a {@link GamePlayer} is damaged by another {@link GamePlayer}
 * in an ongoing {@link ManhuntGame}.
 */
public class GamePlayerDamageByPlayerEvent extends GameEvent {
    
    // Damager and the victim
    private GamePlayer damager, victim;
    
    public GamePlayerDamageByPlayerEvent(ManhuntGame game, GamePlayer damager, GamePlayer victim,
                                         EntityDamageByEntityEvent event) {
        super(game, event);
        this.damager = damager;
        this.victim = victim;
    }
    
    /**
     * @return The victim (damaged player)
     */
    public GamePlayer getVictim() {
        return victim;
    }
    
    /**
     * @return The damager
     */
    public GamePlayer getDamager() {
        return damager;
    }
    
}
