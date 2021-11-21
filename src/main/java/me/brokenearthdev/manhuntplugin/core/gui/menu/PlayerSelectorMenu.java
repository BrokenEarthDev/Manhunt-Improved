package me.brokenearthdev.manhuntplugin.core.gui.menu;

import me.brokenearthdev.manhuntplugin.Utils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.createPlayerHead;

public class PlayerSelectorMenu extends ListPaginatedMenu<OfflinePlayer> {
    
    public PlayerSelectorMenu(String title, List<OfflinePlayer> players) {
        super(title, players, (a) -> createPlayerHead(a, ChatColor.YELLOW + a.getName()));
    }
    
    
}
