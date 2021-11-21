package me.brokenearthdev.manhuntplugin.utils;

import me.brokenearthdev.manhuntplugin.GameItems;
import me.brokenearthdev.manhuntplugin.admin.GameLogger;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicIntOption;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.tracker.TrackerType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

/**
 * Depending on the class, inventory items may be stored here to conserve
 * space.
 */
public class InventoryItems {
    
    public ItemStack backItem() {
        return ItemFactory.create(Material.ARROW)
                .setName(ChatColor.WHITE + "Back")
                .create();
    }
    
    public static class StartMenuItems {
        public ItemStack gracePeriodSettings(int set) {
            return ItemFactory.create(Material.CLOCK)
                    .setName(ChatColor.GOLD + "Edit grace period")
                    .emptyLoreLine()
                    .addLoreLines(set > 0 ? ChatColor.YELLOW + "The set grace period is " + ChatColor.BLUE + set : ChatColor.RED + "The set grace period is",
                            set > 0 ? ChatColor.YELLOW + "seconds" : ChatColor.RED + "disabled")
                    .create();
        }
    }
    
    /**
     * Menu items for {@link me.brokenearthdev.manhuntplugin.menu.GameLoggerMenu}
     */
    public static class GameLoggerMenuItems {
        public ItemStack levelLogs(GameLogger.GameLogLevel lvl, boolean selected) {
            Material material;
            String name = ChatColor.GOLD + lvl.name();
            switch (lvl) {
                case HIGH: material = Material.RED_CONCRETE;
                    break;
                case MEDIUM: material = Material.YELLOW_CONCRETE;
                    break;
                default: material = Material.GREEN_CONCRETE;
                    break;
            }
            ItemFactory factory = ItemFactory.create(material).setName(name + ": " + (selected ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"))
                    .emptyLoreLine().addLoreLines(ChatColor.YELLOW + "The higher the game log level, the",
                            ChatColor.YELLOW + "closer it is to code-level").emptyLoreLine()
                    .addLoreLines(ChatColor.YELLOW + "You can chose to receive logs from", ChatColor.YELLOW + "admin tools menu")
                    .emptyLoreLine().addLoreLines(ChatColor.YELLOW + "In order to select log levels, you need to ",
                            ChatColor.YELLOW + "selected dump data or dump console first");
            if (selected)
                factory.addEnchant(Enchantment.ARROW_FIRE, 1).addItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
            return factory.create();
        }
    
        public ItemStack dumpItem(boolean on) {
            return ItemFactory.create(Material.PAPER)
                    .setName(ChatColor.GOLD + "Dump logs: " + (on ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"))
                    .create();
        }
    
        public ItemStack consoleItem(boolean on) {
            return ItemFactory.create(Material.BOOK)
                    .setName(ChatColor.GOLD + "Dump to console: " + (on ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"))
                    .create();
        }
    }
    
    /**
     * Menu items for {@link me.brokenearthdev.manhuntplugin.menu.HuntersOptionsMenu}
     */
    public static class HuntersOptionsMenuItems {
        public ItemStack compassItem(TrackerType tracker) {
            return ItemFactory.create(Material.COMPASS)
                    .setName(ChatColor.GOLD + "Select tracker")
                    .emptyLoreLine().addLoreLine(ChatColor.BLUE + "Selected tracker: " + tracker.toString())
                    .create();
        }
        public ItemStack kitItem(Kit selected) {
            return ItemFactory.create(Material.GOLDEN_SWORD)
                    .setName(ChatColor.GOLD + "Select kit")
                    .emptyLoreLine().addLoreLine(ChatColor.BLUE + "Selected kit - " +
                            (selected == null ? ChatColor.RED + "none" : selected.getName()))
                    .create();
        }
    }
    
    /**
     * Menu items for {@link me.brokenearthdev.manhuntplugin.menu.PlayerSelectorOptionsMenu}
     */
    public static class PlayerSelectorOptionsMenuItems {
        public ItemStack maxRunnersItem(int value) {
            return ItemFactory.create(Material.IRON_BOOTS)
                    .setName(ChatColor.GOLD + "Maximum runners " + ChatColor.GREEN + "(" + value + " runners)")
                    .emptyLoreLine().setLore(ChatColor.BLUE + "This is the amount of runners that", ChatColor.BLUE + "will participate in the game").create();
        }
        
        public ItemStack maxHuntersItem(int value, boolean inf, DynamicIntOption option) {
            String name;
            if (inf) {
                option.setValue(Integer.MAX_VALUE, false);
                name = ChatColor.GOLD + "Maximum hunters " + ChatColor.GREEN + "(∞ hunters)";
            }
            else name = ChatColor.GOLD + "Maximum hunters " + ChatColor.GREEN + "(" + value + " hunters)";
            return ItemFactory.create(Material.IRON_SWORD)
                    .setName(name).emptyLoreLine()
                    .setLore(ChatColor.BLUE + "This is the amount of runners that", ChatColor.BLUE + "will participate in the game")
                    .create();
        }
        
        public ItemStack infinityItem(boolean inf) {
            return ItemFactory.create(Material.BARRIER)
                    .setName(ChatColor.GOLD + "Set to " + (inf ? "finite" : "∞")).emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "All players on the server, except the selected runners,")
                    .addLoreLine(ChatColor.BLUE +"will play as hunters if set to infinity").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "The number of hunters will be chosen and may")
                    .addLoreLine(ChatColor.BLUE + "not necessarily include all hunters if set to finite")
                    .create();
        }
    }
    
    /**
     * Menu items for {@link me.brokenearthdev.manhuntplugin.menu.GracePeriodMenu}
     */
    public static class GracePeriodMenuItems {
        public ItemStack gracePeriodManipItem(boolean disable) {
            return ItemFactory.create(disable ? Material.GREEN_CONCRETE : Material.RED_CONCRETE)
                    .setName(disable? ChatColor.GREEN + "Enable grace period" : ChatColor.RED + "Disable grace period")
                    .create();
        }
        
        public ItemStack gracePeriodItem(int gp, boolean disabled) {
            if (!disabled)
            return ItemFactory.create(Material.CLOCK)
                    .setName(ChatColor.GOLD + "Grace period")
                    .addLoreLines(ChatColor.GREEN + "(" + gp + " secs)")
                    .create();
            else return ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create();
        }
        
        public ItemStack gracePeriodModItem(int by, boolean disabled) {
            // similar function to #dChange
            if (!disabled)
                return GameItems.SpawnSettingsMenuGUI.dChange(by);
            else return ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create();
        }
    }
    
}
