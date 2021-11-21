package me.brokenearthdev.manhuntplugin.events;

import me.brokenearthdev.manhuntplugin.game.ManhuntGame;

/**
 * Triggered when a game state changes
 */
public class GameStateChangeEvent extends GameEvent {
    
    // Old game state
    private final ManhuntGame.GameState oldState;
    private final ManhuntGame.GameState newState;
    
    public GameStateChangeEvent(ManhuntGame game, ManhuntGame.GameState oldState, ManhuntGame.GameState newState) {
        super(game, null);
        this.oldState = oldState;
        this.newState = newState;
    }
    
    /**
     * @return The game state prior to the new state
     */
    public ManhuntGame.GameState getOldState() {
        return oldState;
    }
    
    /**
     * @return The new (ie. current) game state
     */
    public ManhuntGame.GameState newState() {
        return newState;
    }
    
}

