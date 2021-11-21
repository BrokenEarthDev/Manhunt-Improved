package me.brokenearthdev.manhuntplugin.commands;

import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Useful command in case the hunter doesn't get
 * his tracker for any reason(s)
 */
@AutoRegisterCommand
public class GiveTrackerCommand extends ManhuntCommand {
    
    public GiveTrackerCommand() {
        super("mhtracker", Message.ERROR_PREFIX("Invalid usage! Use /mhtracker"), 0, Integer.MAX_VALUE,
                false);
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
        // todo add a check to see if a hunter has the tracker
        Player player = (Player) sender;
        if (ManhuntGame.getManhuntGame() == null) {
            return CommandResponse.BAD_RESPONSE(sender).queueMessage(Message.ERROR("There are no games running!")).execResponse(this);
        }
        if (ManhuntGame.getManhuntGame().duringGracePeriod()) {
            return CommandResponse.BAD_RESPONSE(sender).queueMessage(Message.ERROR("The game is during grace period, so you can't receive a tracker")).execResponse(this);
        }
        if (ManhuntGame.getManhuntGame().isHunter(player)) {
            Hunter hunter = ManhuntGame.getManhuntGame().getHunter(player);
            boolean given = hunter.getTracker().giveTracker();
            if (!given) {
                Message.INFO(ChatColor.YELLOW + "Please clear your inventory first. It is full!").send(player);
            } else {
                Message.GOOD("Tracker was successfully added in your inventory").send(player);
            }
        } else {
            Message.ERROR("You are not a hunter in a game").send(player);
        }
        return CommandResponse.OK_RESPONSE(sender).execResponse(this);
    }
    
    
}
