package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.Utils;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.ListPaginatedMenu;
import me.brokenearthdev.manhuntplugin.game.PlayerSelector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.createPlayerHead;

/**
 * A menu that allows you to select what player should
 * participate in the game.
 */
public class GamePlayerSelectorMenu extends ListPaginatedMenu<Player> {
    
    // The player selector
    private final PlayerSelector selector;
    
    public GamePlayerSelectorMenu(GameMenu fallbackMenu, PlayerSelector selector, List<Player> possiblePlayers) {
        super("Select players", possiblePlayers, (player -> ItemFactory.create(createPlayerHead(player,
                nameColor(selector, player) + player.getName()))
                .emptyLoreLine()
                .addLoreLine(ChatColor.YELLOW + "LEFT CLICK to select what")
                .addLoreLine(ChatColor.YELLOW + "to do with player").emptyLoreLine()
                .addLoreLines(playerInfoLore(selector, player))
                .create()));
        this.selector = selector;
        addOnItemClick((player, event) -> constructRoleChooser(player, CURRENT_PAGE.getOrDefault(event.getWhoClicked(), 0)).display(event.getWhoClicked()));
        setReturntoGui(fallbackMenu);
    }
    
    /**
     * Constructs a {@link GameMenu} to select options about
     * the target player
     *
     * @param forPlayer The target player
     * @return A newly-constructed {@link GameMenu}
     */
    public GameMenu constructRoleChooser(Player forPlayer, int fallBack) {
        GameMenu menu = new GameMenu("Choose role", 6);
        
        ItemStack headItem = ItemFactory.create(createPlayerHead(forPlayer, nameColor(selector, forPlayer) + forPlayer.getName()))
                .setLore(playerInfoLore(selector, forPlayer))
                .create();
        Button head = new Button(4, headItem);
        Button hunter = new Button(19, ItemFactory.create(Material.BOW).setName(ChatColor.GOLD + "Select as hunter").create());
        Button incl = new Button(22, ItemFactory.create(Material.LADDER).setName(!selector.isIncluded(forPlayer) ? ChatColor.GOLD + "Select as participant" : ChatColor.GOLD + "Remove all selected options").create());
        Button runner = new Button(25, ItemFactory.create(Material.IRON_SWORD).setName(ChatColor.GOLD + "Select as runner").create());
        Button save = new Button(40, ItemFactory.create(Material.GREEN_WOOL).setName(ChatColor.GREEN + "Save for player").create());
        menu.setButton(head).setButton(hunter).setButton(incl).setButton(runner).setButton(save);
        hunter.addAction(event -> {
            Player player = (Player) event.getWhoClicked();
            if (!selector.isHunter(forPlayer) && selector.getSelectedHunters() + 1 > selector.getMaxHunters()) {
                Message.ERROR_PREFIX("The number of hunters selected is at the maximum set constraint").send(player);
                player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 1);
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
                selector.addHunter(forPlayer);
                constructRoleChooser(forPlayer, fallBack).display(player);
            }
        });
        incl.addAction(event -> {
            Player player = (Player) event.getWhoClicked();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
            if (selector.isIncluded(forPlayer))
                selector.removeParticipant(forPlayer);
            else selector.addPlayer(forPlayer);
            constructRoleChooser(forPlayer, fallBack).display(player);
        });
        runner.addAction(event -> {
            Player player = (Player) event.getWhoClicked();
            if (!selector.isRunner(forPlayer) && selector.getSelectedRunners() + 1 > selector.getMaxRunners()) {
                Message.ERROR_PREFIX("The number of runners selected is at the maximum set constraint").send(player);
                player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 1);
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
                selector.addRunner(forPlayer);
                constructRoleChooser(forPlayer, fallBack).display(player);
            }
        });
        save.addAction(event -> this.display(event.getWhoClicked()));
        return menu;
    }
    
    private static ChatColor nameColor(PlayerSelector selector, Player forPlayer) {
        return selector.isHunter(forPlayer) ? ChatColor.AQUA : selector.isRunner(forPlayer) ? ChatColor.RED : selector.isIncluded(forPlayer) ? ChatColor.GREEN : ChatColor.GRAY;
    }
    
    private static List<String> playerInfoLore(PlayerSelector selector, Player forPlayer) {
        List<String> headItemLore = new ArrayList<>();
        if (selector.isIncluded(forPlayer)) {
            headItemLore.add(ChatColor.YELLOW + "This player is selected to");
            if (selector.isHunter(forPlayer))
                headItemLore.add(ChatColor.YELLOW + "be a " + ChatColor.AQUA + "hunter");
            else if (selector.isRunner(forPlayer))
                headItemLore.add(ChatColor.YELLOW + "be a " + ChatColor.RED + "runner");
            else headItemLore.add(ChatColor.YELLOW + "be a " + ChatColor.GRAY + "participant");
        }
        return headItemLore;
    }
    
}
