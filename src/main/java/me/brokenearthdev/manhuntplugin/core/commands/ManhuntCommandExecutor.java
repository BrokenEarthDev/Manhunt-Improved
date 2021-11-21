package me.brokenearthdev.manhuntplugin.core.commands;

import org.bukkit.command.CommandSender;

public interface ManhuntCommandExecutor  {
    
    /**
     * Process the command sent. Only executed when a command, matching that of this command's
     * name, gets executed.
     *
     * @param sender The command sender
     * @param args The arguments
     * @return A response. Depending on it, a message will be sent.
     */
    CommandResponse.CompletedResponse processCommand(CommandSender sender, String[] args);

}
