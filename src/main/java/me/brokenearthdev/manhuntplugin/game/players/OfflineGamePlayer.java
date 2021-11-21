package me.brokenearthdev.manhuntplugin.game.players;

import org.bukkit.OfflinePlayer;

/**
 * Represents a {@link GamePlayer} that is offline. There are no retainable information
 * present within any instance of {@link OfflineGamePlayer} except the {@link OfflinePlayer}
 * object.
 * <p>
 * This is because, by definition, a player is a {@link GamePlayer} if and only if the player
 * is in a running manhunt game.
 */
public interface OfflineGamePlayer {
    
    /**
     * @return The offline player
     */
    OfflinePlayer getOfflinePlayer();
    
    default boolean isHunter() {
        return this instanceof OfflineHunter;
    }
    
    default boolean isRunner() {
        return this instanceof OfflineRunner;
    }
    
    
    class OfflineHunter implements OfflineGamePlayer {
    
        private OfflinePlayer player;
        public OfflineHunter(OfflinePlayer player) {
            this.player = player;
        }
        
        /**
         * @return The offline player
         */
        @Override
        public OfflinePlayer getOfflinePlayer() {
            return player;
        }
    }
    
    class OfflineRunner implements OfflineGamePlayer {
    
        private final OfflinePlayer player;
        
        public OfflineRunner(OfflinePlayer player) {
            this.player = player;
        }
        
        /**
         * @return The offline player
         */
        @Override
        public OfflinePlayer getOfflinePlayer() {
            return player;
        }
    }
    
}
