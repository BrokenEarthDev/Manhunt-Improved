package me.brokenearthdev.manhuntplugin.game.players;

import org.bukkit.entity.Player;

/**
 * Arbitrary interface used to represent either a {@link Speedrunner} or
 * {@link Hunter}
 */
public interface GamePlayer {
    
    Player getPlayer();
    boolean isParticipant();
    void setParticipant(boolean participant);
    
    default Team getType() {
        return (this instanceof Speedrunner) ? Team.SPEEDRUNNER : Team.HUNTER;
    }
    
//    @Override
//    default boolean equals(Object o2) {
//        if (o2 instanceof GamePlayer) {
//            GamePlayer p2 = (GamePlayer) o2;
//            return p2.getPlayer().equals(getPlayer()) &&
//                    p2.getType().equals(getType());
//        }
//        return false;
//    }
    
}
