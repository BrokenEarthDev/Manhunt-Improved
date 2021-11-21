package me.brokenearthdev.manhuntplugin.main;

import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Sample {
    
    private List<String> commands = new ArrayList<>();
    private List<String> listeners = new ArrayList<>();
    
    public void register() throws Exception {
        for (String cmd : commands) {
            ManhuntCommand command = (ManhuntCommand) Class.forName(cmd).newInstance();
            CommandRegistryManager.INST.registerCommand(command, false);
        }
    }
    
}
