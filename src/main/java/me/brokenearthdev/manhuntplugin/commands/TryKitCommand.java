package me.brokenearthdev.manhuntplugin.commands;

import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.kits.Kits;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@AutoRegisterCommand
public class TryKitCommand extends ManhuntCommand {
    
    public TryKitCommand() {
        super("mhtrykit", Message.ERROR_PREFIX("Invalid Usage! Use /mhtrykit [kit name]"), 0,
                1, false);
        register();
    }
    
    @Override
    public CommandResponse.CompletedResponse processCommand(CommandSender sender, String[] arguments) {
        Player player = (Player) sender;
        if (ManhuntGame.getManhuntGame() != null && ManhuntGame.getManhuntGame().isParticipant(player)) {
            return CommandResponse.NO_PERMISSION(sender)
                    .queueMessage(Message.NORMAL_PREFIX(ChatColor.RED + "You are already in a game!"))
                    .execResponse(this);
        }
        
        Message msg = optionsMessage();
        if (arguments.length == 0) {
            msg.addLine(ChatColor.YELLOW + "Please use "+ ChatColor.RED + ChatColor.BOLD.toString() + "/mhtrykit [Kit name]");
        } else {
            Kit kit = Kits.parseKit(arguments[0]);
            if (kit != null) {
                kit.displayTrialMenu(player);
                msg.clear().addLine(ChatColor.GREEN + "Displaying trial menu");
            } else msg.addFirst("Unable to identify kit name");
        }
        return CommandResponse.OK_RESPONSE(sender)
                .queueMessage(msg)
                .execResponse(this);
    }
    
    private Message optionsMessage() {
        Message msg = Message.NORMAL_PREFIX(ChatColor.YELLOW + "Kit names to try include ");
        List<String> names = new ArrayList<>();
        Kits.stringKitMap.forEach((k, v) -> names.add(k));
        for (int i = 0; i < names.size(); i++) {
            msg.append(names.get(i));
            if (i + 1 != names.size())
                msg.append(", ");
        }
        return msg;
    }
}
