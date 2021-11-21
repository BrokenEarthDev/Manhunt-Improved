package me.brokenearthdev.manhuntplugin.commands.game;

import me.brokenearthdev.manhuntplugin.core.CounterTask;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.menu.StartMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AutoRegisterCommand
public class GameStartCommand extends ManhuntCommand {
    
    public GameStartCommand() {
        // todo require permissions
        super ("mhstart", Message.ERROR_PREFIX("Invalid usage! Use /mhstart"), 0, 0, false);
        register();
    }
    
    @Override
    public CommandResponse.CompletedResponse processCommand(CommandSender sender, String[] arguments) {
        StartMenu menu = new StartMenu();
        menu.display((Player) sender);
        return CommandResponse.OK_RESPONSE(sender).execResponse(this);
    }
}
