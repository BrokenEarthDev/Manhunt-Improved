package me.brokenearthdev.manhuntplugin.events;

import me.brokenearthdev.manhuntplugin.commands.game.GameStopCommand;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.Team;

/**
 * Triggered when a manhunt game had ended. To check whether the game was running, using
 * {@link #getOldState()} checks is necessary.
 * <p>
 * In some circumstances, the game may end without a clear winner (perhaps an operator
 * executed the {@link GameStopCommand}). Hence,
 * the values may be {@code null}
 */
public class GameEndEvent extends GameStateChangeEvent {
    
    // The winner team
    private final Team winner;
    
    // The loser team
    private final Team loser;
    
    public GameEndEvent(ManhuntGame game, ManhuntGame.GameState oldState, Team winner) {
        super(game, oldState, ManhuntGame.GameState.ENDED);
        this.winner = winner;
        this.loser = (winner == null) ? null :
                (winner == Team.HUNTER) ? Team.SPEEDRUNNER : Team.HUNTER;
    }
    
    /**
     * @return The winner team
     */
    public Team getWinnerTeam() {
        return winner;
    }
    
    /**
     * @return The loser team
     */
    public Team getLoserTeam() {
        return loser;
    }
    
}
