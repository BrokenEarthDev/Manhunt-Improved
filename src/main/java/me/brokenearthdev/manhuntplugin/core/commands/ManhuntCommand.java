package me.brokenearthdev.manhuntplugin.core.commands;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Implement this class to develop commands faster.
 */
public abstract class ManhuntCommand implements ManhuntCommandExecutor, CommandExecutor {
    
    // command permissions
   private final List<String> permissions;
   
   // whether this command is accessible to console command sender or not
   public final boolean consoleAccessible;
   
   // The maximum argument length before the usage message is sent
   public final int maxArgs;
   
   // The minimal argument length before the usage message is sent
   public final int minArgs;
   
   // Usage message.
   public final Message usage;
   
   // Command name
   public final String commandName;
   
   protected ManhuntCommand(String cmdName, Message usage, int minArgs, int maxArgs, boolean consoleAccessible,
                            List<String> perms) {
       this.commandName = cmdName;
       this.usage = usage;
       this.minArgs = minArgs;
       this.maxArgs = maxArgs;
       this.consoleAccessible = consoleAccessible;
       this.permissions = perms;
   }
   
   protected ManhuntCommand(String cmdName, Message usage, int minArgs, int maxArgs, boolean consoleAccessible) {
       this(cmdName, usage, minArgs, maxArgs, consoleAccessible, Collections.emptyList());
   }
   
   protected ManhuntCommand(String cmdName, Message usage, boolean consoleAccessible) {
       this(cmdName, usage, 0, Integer.MAX_VALUE, consoleAccessible);
   }
   
   private ManhuntCommand(String cmdName, boolean consoleAccessible) {
       this(cmdName, Message.ERROR("Invalid Usage! "), consoleAccessible);
   }
   
    /**
     * Process the command sent. Only executed when a command, matching that of this command's
     * name, gets executed.
     * <p>
     * Argument length checking will be made before processing if maximum and minimum args
     * are defined.
     *
     * @param sender The command sender
     * @param arguments The arguments
     * @return A response. Depending on it, a message will be sent.
     */
    @Override
   public abstract CommandResponse.CompletedResponse processCommand(CommandSender sender, String[] arguments);
    
    /**
     * Called when an exception occurs
     *
     * @param sender The sender
     * @param args The arguments
     * @param e The exception
     */
    public CommandResponse.CompletedResponse onException(CommandSender sender, String[] args, Exception e) {
        return CommandResponse.INTERNAL_ERROR(sender).sendException(e, false).queueDefaultMessages()
                .execResponse(this);
    }
    
    public void register() {
        Manhunt.getInstance().getCommand(commandName).setExecutor(this);
    }
   
    /**
     * Checks whether the {@link CommandSender} meets all permissions. This
     * assumes that console command sender has all permissions.
     *
     * @param sender The command sender
     * @return Whether the permissions were met or not
     */
   public boolean meetPermissions(CommandSender sender) {
       // consoles have all permissions assumed
       if (sender instanceof ConsoleCommandSender) return true;
       for (String permission : permissions) {
           if (!sender.hasPermission(permission))
               return false;
       }
       return true;
   }
    
    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       if (!command.getName().equalsIgnoreCase(commandName)) return true;
       if (sender instanceof ConsoleCommandSender && !consoleAccessible) {
           sender.sendMessage(ChatColor.RED + "Sorry, this command was designed for players.");
           return true;
       }
       if (!meetPermissions(sender)) {
           CommandResponse.NO_PERMISSION(sender).queueDefaultMessages().execResponse(this);
           return true;
       }
       if (args.length > maxArgs || args.length < minArgs) {
           CommandResponse.INVALID_USAGE(sender).queueDefaultMessages().execResponse(this);
           return true;
       }
       try {
           processCommand(sender, args);
           if (ManhuntGame.getManhuntGame() != null) {
               ManhuntGame.getManhuntGame().gameLogger.medium(command.getName() + " command was executed");
           }
       } catch (Exception e) {
           try {
               onException(sender, args, e);
           } catch (Exception e1) {
               CommandResponse.INTERNAL_ERROR(sender).queueDefaultMessages().execResponse(this);
               Manhunt.getInstance().getLogger().log(Level.WARNING, "An exception had occurred which the " +
                       "command exception handler can't catch");
               Manhunt.getInstance().getLogger().log(Level.INFO, "Original exception ---");
               e.printStackTrace();
               Manhunt.getInstance().getLogger().log(Level.INFO, "Exception in the cmd exception handler ---");
               e1.printStackTrace();
               Manhunt.getInstance().getLogger().log(Level.INFO, "--- This exception was not expected ---");
    
           }
       }
       return true;
    }
    
    

   
}
