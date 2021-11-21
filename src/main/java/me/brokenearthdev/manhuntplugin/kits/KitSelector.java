package me.brokenearthdev.manhuntplugin.kits;

import me.brokenearthdev.manhuntplugin.Utils;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.menu.ListPaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class KitSelector extends ListPaginatedMenu<Kit> {
    
    public KitSelector() {
        super("Kit Selector", Kits.getAllKits(), Utils::kitRepresentation);
        setItem(null,ItemFactory.create(Material.BARRIER).setName(ChatColor.RED + "No kit").create());
    }

}
