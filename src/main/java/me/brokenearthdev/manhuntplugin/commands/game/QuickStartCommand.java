package me.brokenearthdev.manhuntplugin.commands.game;

import me.brokenearthdev.manhuntplugin.core.GameMessage;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.game.GameSettings;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.PlayerSelector;
import me.brokenearthdev.manhuntplugin.game.players.*;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.kits.Kits;
import me.brokenearthdev.manhuntplugin.tracker.Tracker1;
import me.brokenearthdev.manhuntplugin.tracker.TrackerType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class QuickStartCommand extends ManhuntCommand {
    
    public QuickStartCommand() {
        super("mhquickstart", Message.ERROR_PREFIX("Invalid usage! Use /mhquickstart or /mhquickstart [hunter kit] [runner kit]"),
                0, 2, true);
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
        if (ManhuntGame.getManhuntGame() != null) {
            return CommandResponse.BAD_RESPONSE(sender).queueMessage(Message.ERROR_PREFIX("Can't start game because a game is already running!")).execResponse(this);
        }
        Kit hunterKit = null;
        Kit runnerKit = null;
        if (arguments.length == 2) {
            hunterKit = Kits.parseKit(arguments[0].toLowerCase());
            runnerKit = Kits.parseKit(arguments[1].toLowerCase());
            if (hunterKit == null || runnerKit == null) {
                return CommandResponse.BAD_RESPONSE(sender)
                        .queueMessage(Message.ERROR_PREFIX("Can't find kit with the names. Use /mhtrykit to get more info about kits"))
                        .execResponse(this);
            }
        } else if (arguments.length == 1)
            return CommandResponse.INVALID_USAGE(sender).queueDefaultMessages().execResponse(this);
        PlayerSelector selector = new PlayerSelector(new HashSet<>(), new HashSet<>(), new HashSet<>(Bukkit.getOnlinePlayers()));
        SpeedrunnerSettings defaultSettings = new SpeedrunnerSettings();
        GameSettings defaultGameSettings = new GameSettings();
        Set<GamePlayer> selected = selector.selectPlayers(defaultSettings, TrackerType.SIMPLISTIC, runnerKit, hunterKit);
        Set<Speedrunner> runners = new HashSet<>();
        Set<Hunter> hunters = new HashSet<>();
        selected.forEach(gp -> {
            if (gp.getType() == Team.SPEEDRUNNER)
                runners.add((Speedrunner) gp);
            else hunters.add((Hunter) gp);
        });
        ManhuntGame game = new ManhuntGame(defaultGameSettings, defaultSettings, runners, hunters, Bukkit.getWorld("world"),
                                            Bukkit.getWorld("world_the_end"));
        return CommandResponse.OK_RESPONSE(sender)
                .queueMessage(Message.GOOD_PREFIX("Attempting to start game..."))
                .addAfterExecution(cmd -> game.startGame())
                .execResponse(this);
    }
    
    
    /**
     * Called when an exception occurs
     *
     * @param sender The sender
     * @param args   The arguments
     * @param e      The exception
     */
    @Override
    public CommandResponse.CompletedResponse onException(CommandSender sender, String[] args, Exception e) {
        GameMessage.ERROR_PREFIX("It seems like we can't start this game due to an error")
                .addLine("Please contact the administrator or try again")
                .addLine("If the error persists, attempt to use /mhadmin and disable all \"repeated tasks\"")
                .send((Entity) sender);
        return super.onException(sender, args, e);
    }
}
