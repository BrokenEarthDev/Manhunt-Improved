package me.brokenearthdev.manhuntplugin.core.commands;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static me.brokenearthdev.manhuntplugin.core.Message.ERROR_PREFIX;


/**
 * Represents a response that occurs after the execution of a {@link ManhuntCommand}
 */
public class CommandResponse {
    
    /**
     * Arbitrary class that represents a command response that was executed
     */
    public class CompletedResponse {
        private CompletedResponse() {}
        
        public CommandResponse getCommandResponse() {
            return CommandResponse.this;
        }
    }
    
    // queued messages to the command sender
    private final List<Message> messages = new LinkedList<>();
    
    // The command sender
    private final CommandSender sender;
    
    // The exception (defined if printing stack trace is queued)
    private Exception exception;
    
    // The response type
    private ResponseType type;
    
    // Fired after command execution
    private List<Consumer<ManhuntCommand>> afterExecution = new LinkedList<>();
    
    // Whether default messages are queued as well
    private boolean defaultQueued = false;
    private int atIndex = 0;
    
    /**
     * Creates an invalid usage command response
     *
     * @param sender The command sender
     * @return A new command response object
     */
    public static CommandResponse INVALID_USAGE(CommandSender sender) {
        return new CommandResponse(sender, ResponseType.INVALID_USAGE);
    }
    
    /**
     * Creates a no permission command response
     *
     * @param sender The command sender
     * @return A new command response object
     */
    public static CommandResponse NO_PERMISSION(CommandSender sender) {
        return new CommandResponse(sender, ResponseType.NO_PERMISSION);
    }
    
    /**
     * Creates an ok response command response
     *
     * @param sender The command sender
     * @return A new command response object
     */
    public static CommandResponse OK_RESPONSE(CommandSender sender) {
        return new CommandResponse(sender, ResponseType.OK_RESPONSE);
    }
    
    /**
     * Creates an internal error command response
     *
     * @param sender The command sender
     * @return A new command response object
     */
    public static CommandResponse INTERNAL_ERROR(CommandSender sender) {
        return new CommandResponse(sender, ResponseType.INTERNAL_ERROR);
    }
    
    public static CommandResponse BAD_RESPONSE(CommandSender sender) {
        return new CommandResponse(sender, ResponseType.BAD_RESPONSE);
    }
    
    private CommandResponse(CommandSender sender, ResponseType type) {
        this.sender = sender;
        this.type = type;
    }
    
    /**
     * Queues a message to the sender.
     *
     * @param msg The message
     * @return This object
     */
    public CommandResponse queueMessage(Message msg) {
        messages.add(msg);
        return this;
    }
    
    public CommandResponse queueDefaultMessages() {
        this.defaultQueued = true;
        this.atIndex = messages.size();
        return this;
    }
    
    /**
     * Sends an exception to the console command sender
     *
     * @param e The exception
     * @param now Whether the exception should be sent
     *            immediately
     * @return This object
     */
    public CommandResponse sendException(Exception e, boolean now) {
        if (now) {
            Manhunt.getInstance().getLogger().info("An error had occurred:");
            e.printStackTrace();
            return this;
        }
        this.exception = e;
        return this;
    }
    
    /**
     * Fires the consumer as a last part of the execution when invoking
     * {@link #execResponse(ManhuntCommand)}
     *
     * @param consumer A consumer
     * @return This object
     */
    public CommandResponse addAfterExecution(Consumer<ManhuntCommand> consumer) {
        this.afterExecution.add(consumer);
        return this;
    }
    
    /**
     * Changes the response type
     *
     * @param type The new response type
     * @return This object
     */
    public CommandResponse changeType(ResponseType type) {
        if (type == null) this.type  = ResponseType.OK_RESPONSE;
        this.type = type;
        return this;
    }
    
    public CompletedResponse execResponse(ManhuntCommand command) {
        for (int i = 0; i < messages.size(); i++) {
            if (defaultQueued && atIndex == i) {
                sendDefaultMsgs(command);
            }
            if (sender instanceof Entity)
                messages.get(i).send((Entity) sender);
            else sender.sendMessage(messages.get(i).toString());
        }
        if (defaultQueued && atIndex >= messages.size())
            sendDefaultMsgs(command);
        if (exception != null) {
            Manhunt.getInstance().getLogger().info("An error had occurred:");
            exception.printStackTrace();
        }
        return new CompletedResponse();
    }
    
    public CompletedResponse cancelResponse() {
        changeType(null);
        return new CompletedResponse();
    }
    
    private void sendDefaultMsgs(ManhuntCommand command) {
        Message def = defaultMessageFor(type, command);
        if (def != null) {
            if (sender instanceof Entity)
                def.send((Entity) sender);
            else sender.sendMessage(def.toString());
        }
    }
    
    public static Message defaultMessageFor(ResponseType type, ManhuntCommand command) {
        switch (type) {
            case OK_RESPONSE: break;
            case INTERNAL_ERROR: return ERROR_PREFIX("An internal error had occurred. Please report this to the administrator.");
            case INVALID_USAGE: return command.usage;
            case NO_PERMISSION: return ERROR_PREFIX("You do not have permission to execute this" +
                    " command!");
            case BAD_RESPONSE: return ERROR_PREFIX("You cannot use this command at this time!");
        }
        return null;
    }
    
    /**
     * The response type (used internally)
     */
    public enum ResponseType {
        INVALID_USAGE,
        NO_PERMISSION,
        OK_RESPONSE,
        INTERNAL_ERROR,
        BAD_RESPONSE
    }
    
}
