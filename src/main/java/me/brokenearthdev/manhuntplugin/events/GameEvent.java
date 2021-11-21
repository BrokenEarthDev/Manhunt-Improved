package me.brokenearthdev.manhuntplugin.events;

import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Triggered when a substantial circumstance occurs in a {@link me.brokenearthdev.manhuntplugin.game.ManhuntGame}.
 * Do not listen for this event.
 */
public abstract class GameEvent extends Event {

    // The handler list
    private static final HandlerList handlerList = new HandlerList();
    
    // Game object that fired this event
    private final ManhuntGame game;
    private final Event triggered;
    
    public GameEvent(ManhuntGame game, Event triggered) {
        this.game = game;
        this.triggered = triggered;
    }
    
    /**
     * @return The current manhunt game
     */
    public final ManhuntGame getGame() {
        return game;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
    
    public Event getFiredBukkitEvent() {
        return triggered;
    }
    
}

