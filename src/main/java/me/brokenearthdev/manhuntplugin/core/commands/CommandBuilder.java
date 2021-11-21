package me.brokenearthdev.manhuntplugin.core.commands;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.Message;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a {@link CommandBuilder} without extending {@link ManhuntCommand}. Optimal for
 * small commands.
 */
public class CommandBuilder {
    
    // command permissions
    private List<String> permissions = new ArrayList<>();
    
    // whether this command is accessible to console command sender or not
    private boolean consoleAccessible = true;
    
    // The maximum argument length before the usage message is sent
    private int maxArgs = Integer.MAX_VALUE;
    
    // The minimal argument length before the usage message is sent
    private int minArgs = 0;
    
    // Usage message.
    private Message usage = Message.ERROR("Invalid usage! ");
    
    // Command name
    private String commandName;
    
    // executed when a command runs
    private ManhuntCommandExecutor exec;
    
    public CommandBuilder addPermRequired(String perm) {
        permissions.add(perm);
        return this;
    }
    
    
    public CommandBuilder addPermsRequired(List<String> perms) {
        permissions.addAll(perms);
        return this;
    }
    
    public CommandBuilder setConsoleAccessible(boolean accessible) {
        this.consoleAccessible = accessible;
        return this;
    }
    
    public CommandBuilder setArgsLen(int max, int min) {
        if (max >= min && max > 0 && min > 0) {
            this.maxArgs = max;
            this.minArgs = min;
        }
        return this;
    }
    
    public CommandBuilder setUsageMsg(Message msg) {
        this.usage = msg;
        return this;
    }
    
    public CommandBuilder setCommandName(String cmd) {
        this.commandName = cmd;
        return this;
    }
    
    public CommandBuilder setOnExec(ManhuntCommandExecutor exec) {
        this.exec = exec;
        return this;
    }
    
    public ManhuntCommand buildCommand() {
        return new ManhuntCommand(commandName, usage, minArgs, maxArgs, consoleAccessible, permissions) {
            @Override
            public CommandResponse.CompletedResponse processCommand(CommandSender sender, String[] arguments) {
                return exec.processCommand(sender, arguments);
            }
        };
    }
    
    public ManhuntCommand buildAndRegisterCommand() {
        ManhuntCommand built = buildCommand();
        Manhunt.getInstance().getCommand(commandName).setExecutor(built);
        return built;
    }
    
}
