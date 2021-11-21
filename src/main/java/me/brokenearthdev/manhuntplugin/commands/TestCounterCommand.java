package me.brokenearthdev.manhuntplugin.commands;

import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.createCounter;
import static me.brokenearthdev.manhuntplugin.utils.StaticImports.playBellSound;

@AutoRegisterCommand
public class TestCounterCommand extends ManhuntCommand {
    
    public TestCounterCommand() {
        super("testcounter", Message.ERROR_PREFIX("Invalid usage! Use /testcounter <sec>"),
                1, 1, false);
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
        try {
            int seconds = Integer.parseInt(arguments[0]);
            createCounter("counter_task", seconds, Collections.singletonList((Player) sender), i -> {
                playBellSound((Player) sender);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return CommandResponse.BAD_RESPONSE(sender)
                    .queueMessage(Message.ERROR_PREFIX("Please input a number!"))
                    .execResponse(this);
        }
        return null;
    }
}
