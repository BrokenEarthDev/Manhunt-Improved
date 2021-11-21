package me.brokenearthdev.manhuntplugin.commands.game;

import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.core.gui.menu.ListPaginatedMenu;
import me.brokenearthdev.manhuntplugin.stats.GameStats;
import me.brokenearthdev.manhuntplugin.stats.GameStatsGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.shortenUUID;

@AutoRegisterCommand
public class GamesCommand extends ManhuntCommand {
    
    public GamesCommand() {
        super("mhgames", Message.ERROR_PREFIX("Invalid usage! Use /mhgames [uuid] or /mhgames"),  0, 1,
                true);
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
        if (sender instanceof Player) {
            // open gui
            if (arguments.length == 1) {
                UUID uuid = UUID.fromString(arguments[0]);
                GameStats stats = Manhunt.getInstance().getGameStatsFor(uuid);
                if (stats == null)
                    return CommandResponse.BAD_RESPONSE(sender).queueMessage(Message.ERROR_PREFIX("No game found under the UUID")).execResponse(this);
                GameStatsGUI gameStatsGUI = new GameStatsGUI(stats);
                gameStatsGUI.display((HumanEntity) sender);
                return CommandResponse.OK_RESPONSE(sender).queueMessage(Message.GOOD("Displaying info about game " + ChatColor.BLUE + "(" + uuid + ")")).execResponse(this);
            }
            ArrayList<GameStats> stats = new ArrayList<>(Manhunt.getInstance().getConfigManager().getLastCaptured().GAME_STATS.get());
            Collections.reverse(stats);
            ListPaginatedMenu<GameStats> menu = new ListPaginatedMenu<>("Displaying all games",
                    new ArrayList<>(Manhunt.getInstance().getConfigManager().getLastCaptured().GAME_STATS.get()),
                    stats1 -> ItemFactory.create(Material.GRASS_BLOCK).setName(ChatColor.GOLD + "Game " +   shortenUUID(stats1.getUUID()))
                        .emptyLoreLine().addLoreLine(ChatColor.YELLOW + "CLICK to view more info about the game").emptyLoreLine().addLoreLine(ChatColor.BLUE + "Full uuid: " + stats1.getUUID()).create());
            menu.addOnItemClick((stats1, event) -> new GameStatsGUI(stats1).display(event.getWhoClicked()));
            menu.display((HumanEntity) sender);
        }
        return null;
    }
}
