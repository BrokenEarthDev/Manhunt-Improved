package me.brokenearthdev.manhuntplugin.main;

import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches and creates an instance of classes tagged with {@link AutoRegisterCommand} to be
 * automatically registered.
 */
public class CommandRegistryManager {
    
    private static final List<ManhuntCommand> initCmds = new ArrayList<>();
    
    public static CommandRegistryManager INST = new CommandRegistryManager();
    
    /**
     * Registers all {@link ManhuntCommand}s tagged
     * with {@link AutoRegisterCommand}.
     *
     * @throws Exception If an error occurred
     */
    void scanAutoRegisteredCommands() throws Exception {
        File[] java = loadJavaFiles();//files(PACK_LOC, new ArrayList<>());
        List<Class<?>> commands = new ArrayList<>();
        List<Class<?>> listeners = new ArrayList<>();
        for (File file : java) {
            // turn to package format
            String name = file.getPath().substring(SRC.getPath().length() + 1);
            System.out.println(name);
            String clazzName = name.replace('\\', '.').replace('/', '.').replace(".java", "");
            Class<?> clazz_enc = Class.forName(clazzName);
            Class<?>[] dec = clazz_enc.getDeclaredClasses();
            Class<?>[] clazzes = new Class<?>[1 + dec.length];
            clazzes[0] = clazz_enc;
            if (dec.length - 1 >= 0) System.arraycopy(dec, 1, clazzes, 1, dec.length - 1);
            for (Class<?> clazz : clazzes) {
                if (clazz != null && clazz.getAnnotation(AutoRegisterCommand.class) != null) {
                    // class is annotated
                    if (clazz.getSuperclass().equals(ManhuntCommand.class)) {
                        // register command class
                        commands.add((Class<? extends ManhuntCommand>) clazz);
                    }
                }
            }
        }
        List<String> cmds = new ArrayList<>();
        commands.forEach(cmd -> cmds.add(cmd.getName()));
        String reg = generateRegistry(commands);
        Files.write(GENERATED_FILE.toPath(), reg.getBytes());
    }
    
    /**
     * Registers auto-registered files
     *
     * @throws Exception If an error occurred
     */
    public void registerAuto() throws Exception {
        Generated$AutoRegisteredCommands autoRegistered = new Generated$AutoRegisteredCommands();
        autoRegistered.register();
    }
    
    /**
     * Manually registers a command
     *
     * @param command A command instance
     * @param reReg Whether the command should be registered (<i>recommended if the class doesn't call
     *              {@link ManhuntCommand#register()}} on initialization)</i>
     */
    public void registerCommand(ManhuntCommand command, boolean reReg) {
        initCmds.add(command);
        if (reReg)
            Bukkit.getPluginCommand(command.commandName).setExecutor(command);
    }

}
