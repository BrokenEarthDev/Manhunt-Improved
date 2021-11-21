package me.brokenearthdev.manhuntplugin.commands.game;

import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import org.bukkit.command.CommandSender;

@AutoRegisterCommand
public class GameStopCommand extends ManhuntCommand {
    
    public GameStopCommand() {
        super("mhstop", Message.ERROR_PREFIX("Invalid usage! Use /mhstop"),
                0, Integer.MAX_VALUE, true); // todo add perms req
        register();
    }
    
    /**
     * Process the command sent. Only executed when a command, matching that of this command's
     * name, gets executed.
     * <p>
     * Argument length checking will be made before processing if maximum and minimum args
     * are defined.
     *
     * @param sender    The command sender
     * @param arguments The arguments
     * @return A response. Depending on it, a message will be sent.
     */
    @Override
    public CommandResponse.CompletedResponse processCommand(CommandSender sender, String[] arguments) {
        ManhuntGame game = ManhuntGame.getManhuntGame();
        if (game != null && (game.getGameState() != ManhuntGame.GameState.ENDED && game.getGameState() != ManhuntGame.GameState.ABORTED)) {
            game.stopGame();
            return CommandResponse.OK_RESPONSE(sender).queueMessage(Message.GOOD_PREFIX("Game stopped successfully")).execResponse(this);
        }
        return CommandResponse.OK_RESPONSE(sender).queueMessage(Message.ERROR_PREFIX("There is no game running in the first place")).execResponse(this);
    }
    
    /**
     * Called when an exception occurs
     *
     * @param sender The sender
     * @param args   The arguments
     * @param e      The exception
     */
    @Override
    public CommandResponse.CompletedResponse onException(CommandSender sender, String[] args, Exception e) {
        return CommandResponse.INTERNAL_ERROR(sender).queueMessage(Message.ERROR_PREFIX("An error had occurred" +
                " while attempting to stop an ongoing game.")).queueMessage(Message.ERROR_PREFIX("Use /mhabort to" +
                " force stop the game if necessary")).sendException(e, false).execResponse(this);
    }
}
