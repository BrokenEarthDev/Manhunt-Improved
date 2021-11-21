package me.brokenearthdev.manhuntplugin.commands;

import me.brokenearthdev.manhuntplugin.admin.AdminTools;
import me.brokenearthdev.manhuntplugin.admin.AdminToolsGUI;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AutoRegisterCommand
public class AdminCommand extends ManhuntCommand {
    
    public AdminCommand() {
        super("mhadmin", Message.ERROR_PREFIX("Invalid usage! Use /mhadmin"),
                0, Integer.MAX_VALUE, false); // todo req perms
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
        Player player = (Player) sender;
//        if (ManhuntGame.getManhuntGame() == null) {
//            Message.ERROR("There are no games running").send(player);
//            return CommandResponse.BAD_RESPONSE(sender).execResponse(this);
//        }
        final AdminToolsGUI inv = new AdminToolsGUI(new AdminTools());
        inv.display(player);
        return CommandResponse.OK_RESPONSE(sender).execResponse(this);
    }
}
