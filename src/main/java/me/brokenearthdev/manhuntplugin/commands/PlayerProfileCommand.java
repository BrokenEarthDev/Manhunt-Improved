package me.brokenearthdev.manhuntplugin.commands;

import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.stats.CachedPlayerProfile;
import me.brokenearthdev.manhuntplugin.stats.PlayerProfileGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

@AutoRegisterCommand
public class PlayerProfileCommand extends ManhuntCommand {
    
    public PlayerProfileCommand() {
        super("mhprofile", Message.ERROR_PREFIX("Invalid usage! Use /mhprofile or /mhprofile [player]"), 0, 1,
                true);
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
        if (arguments.length == 0 && !(sender instanceof Player)) {
            return CommandResponse.NO_PERMISSION(sender)
                    .queueMessage(Message.ERROR_PREFIX("You can't use this argument because you aren't a player"))
                    .execResponse(this);
        }
        
        OfflinePlayer player = arguments.length == 0 ? (Player) sender : Bukkit.getOfflinePlayer(arguments[0]);
        if (!player.hasPlayedBefore()) {
            return CommandResponse.BAD_RESPONSE(sender)
                    .queueMessage(Message.ERROR_PREFIX("There are no profile entries under \"" + ChatColor.LIGHT_PURPLE + player.getName() + "\""))
                    .execResponse(this);
        }
        CachedPlayerProfile profile = Manhunt.getInstance().getPlayerProfileFor(player);
        
        if (sender instanceof Player) {
            // open a gui
            final PlayerProfileGUI gui = new PlayerProfileGUI(profile);
            gui.display((HumanEntity) sender);
        } else {
            Message message = new Message(Message.DefaultMessageType.NORMAL.getStruct());
            profile.stats().forEach((k, v) -> {
                message.addLine(ChatColor.YELLOW + k + ": " + ChatColor.RED + v);
            });
            return CommandResponse.OK_RESPONSE(sender)
                    .queueMessage(Message.GOOD_PREFIX("The profile for " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " are as follows ---"))
                    .queueMessage(message)
                    .queueMessage(Message.GOOD_PREFIX("---"))
                    .execResponse(this);
        }
        return null;
    }
}
