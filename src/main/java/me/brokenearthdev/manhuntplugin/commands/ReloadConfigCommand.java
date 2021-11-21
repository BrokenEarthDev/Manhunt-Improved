package me.brokenearthdev.manhuntplugin.commands;

import me.brokenearthdev.manhuntplugin.core.CounterTask;
import me.brokenearthdev.manhuntplugin.core.GameMessage;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.utils.StaticImports;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.configManager;

@AutoRegisterCommand
public class ReloadConfigCommand extends ManhuntCommand {
    
    public ReloadConfigCommand() {
        super("mhreloadconfig", Message.ERROR_PREFIX("Invalid usage! Use /mhreloadconfig"), 0, 0,
                true);
        register();
    }

    private final Map<CommandSender, Integer> senders = new HashMap<>();
    private final CounterTask task = new CounterTask((integer -> {
        if (senders.size() == 0) {
            return;
        }
        senders.forEach((k, v) -> {
            if (v + 1 >= 30) {
                k.sendMessage(Message.PREFIX + " " + ChatColor.RED + "Config reload confirmation cancelled.");
                senders.remove(k);
                return;
            }
            senders.put(k, v + 1);
        });
    }), Integer.MAX_VALUE, 0);
    
    {
        task.addCounterUpdater(new CounterTask.CounterUpdater(0, Integer.MAX_VALUE, 20, integer -> {
            task.setTicksLeft(Integer.MAX_VALUE);
            if (senders.size() == 0)
                task.setPaused(true);
        }));
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
        if (senders.containsKey(sender)) {
            // reload config
            configManager().recaptureEntries(false);
            sender.sendMessage(Message.PREFIX + ChatColor.GREEN + " Configuration has been recaptured successfully");
        } else {
            sender.sendMessage(Message.PREFIX + ChatColor.RED + " Using this command may cause data loss! It is advisable to" +
                    " run /mhsaveconfig first if there are any pending data");
            sender.sendMessage(Message.PREFIX + ChatColor.GOLD + " Now that you're aware with the risks, rerun the command to confirm " +
                    "within 30 seconds");
            senders.put(sender, 0);
            task.setPaused(false);
        }
        return CommandResponse.OK_RESPONSE(sender).execResponse(this);
    }
}
