package me.brokenearthdev.manhuntplugin.commands;

import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.utils.StaticImports;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.configManager;

@AutoRegisterCommand
public class SaveConfigCommand extends ManhuntCommand {
    
    public SaveConfigCommand() {
        super("mhsaveconfig", Message.ERROR_PREFIX("Invalid usage! Use /mhsaveconfig"), 0, 0, true);
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
        sender.sendMessage(Message.PREFIX + ChatColor.GREEN + " Saving configuration...");
        boolean saved = configManager().saveConfigs();
        if (saved)
            sender.sendMessage(Message.PREFIX + ChatColor.GREEN + " Configuration successfully saved!");
        else sender.sendMessage(Message.PREFIX + ChatColor.RED + " Can't save configuration");
        return null;
    }
}
