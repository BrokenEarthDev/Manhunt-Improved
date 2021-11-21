package me.brokenearthdev.manhuntplugin;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.LoreCreator;
import me.brokenearthdev.manhuntplugin.game.PlayerSelector;
import me.brokenearthdev.manhuntplugin.menu.GameLoggerMenu;
import me.brokenearthdev.manhuntplugin.menu.HuntersOptionsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class GameItems {
    
    public static class GracePeriodMenuGUI {
        public static ItemStack back() {
            return ItemFactory.create(Material.ARROW)
                    .setName(ChatColor.WHITE + "Back")
                    .create();
        }
    }
    
    public static class SpawnSettingsMenuGUI {
        public static ItemStack back() {
            return ItemFactory.create(Material.GREEN_CONCRETE)
                    .setName(ChatColor.GREEN + "Back")
                    .create();
        }
        
        public static ItemStack distanceFromSpawn(int distFromSpawn) {
            return ItemFactory.create(Material.RAIL)
                    .setName(ChatColor.GOLD + "Distance from world spawn (" + distFromSpawn + "m)")
                    .create();
        }
        
        public static ItemStack distanceFromHunter(int distFromHunter) {
            return ItemFactory.create(Material.POWERED_RAIL)
                    .setName(ChatColor.GOLD + "Distance from hunter spawn (" + distFromHunter + "m)")
                    .create();
        }
        
        public static ItemStack dChange(int by) {
            String name = by < 0 ? ChatColor.RED.toString() + "-" + -by : ChatColor.GREEN + "+" + by;
            return ItemFactory.create(by < 0 ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE)
                    .setName(name)
                    .create();
        }
        
        
    }
    
    public static class StartMenuItemsGUI {
        public static ItemStack loggerOptions(GameLoggerMenu gameLoggerMenu) {
            return ItemFactory.create(Material.PAPER)
                    .setName(ChatColor.GOLD + "Game logger options")
                    .setLore(new LoreCreator().emptyLine()
                            .addEntries(ChatColor.YELLOW + "CLICK to edit game logger options")
                            .emptyLine().addEntries(ChatColor.YELLOW + "Game logger options:").addList()
                            .addEntries("Dump to config: " + gameLoggerMenu.dumpData())
                            .addEntries("Log to console: " + gameLoggerMenu.isDumpConsole())
                            .addEntries("Log level (if applicable): " + gameLoggerMenu.selectedLvl().name())
                            .createList().addEntries(ChatColor.RED + "**The log level will only be considered",
                                    ChatColor.RED + "when either dump to config or log to console",
                                    ChatColor.RED + "is selected").getLoreList()
                    ).create();
        }
        
        public static ItemStack playerSelector(PlayerSelector selector) {
            return ItemFactory.create(Material.COMPARATOR)
                    .setName(ChatColor.GREEN + "Player selector")
                    .setLore(new LoreCreator().emptyLine()
                            .addEntries(ChatColor.YELLOW + "CLICK to manually select players")
                            .emptyLine()
                            .addEntries(ChatColor.YELLOW + "The manual selection system will obey the settings",
                                    ChatColor.YELLOW + "set by the random selection system")
                            .emptyLine()
                            .addEntries(ChatColor.RED + "** By default, there will be only 1 speedrunner",
                                    ChatColor.RED + "and ∞ hunters **")
                            .getLoreList()
                    ).create();
        }
        
        public static ItemStack runnerOptions(String[] settings) {
            return ItemFactory.create(Material.IRON_SWORD)
                    .addLoreLine(ChatColor.YELLOW + "LEFT CLICK to select runners").addLoreLine(ChatColor.YELLOW + "RIGHT CLICK to " +
                    "modify runner settings").setName(ChatColor.RED + "Speedrunners").emptyLoreLine()
                    .addLoreLines(Utils.createListLoreLine(ChatColor.YELLOW + "Selected settings:", settings)).create();
        }
        
        public static ItemStack hunterOptions(HuntersOptionsMenu huntersOptionsMenu) {
            return ItemFactory.create(Material.BOW)
                    .setName(ChatColor.AQUA + "Hunters")
                    .addLoreLines(new LoreCreator().emptyLine()
                            .addEntries(ChatColor.YELLOW + "CLICK to change hunter options").emptyLine()
                            .addEntries(ChatColor.YELLOW + "Selected options:").addList()
                            .addEntries(ChatColor.BLUE + "Selected kit is " + (huntersOptionsMenu.getSelectedKit() == null ? ChatColor.RED + "none" :
                                    ChatColor.BLUE + huntersOptionsMenu.getSelectedKit().getName()))
                            .addEntries(ChatColor.BLUE + "Selected tracker is " + (huntersOptionsMenu.getSelectedTracker() == null ? ChatColor.RED + "none" :
                                    ChatColor.BLUE + huntersOptionsMenu.getSelectedTracker().toString())).createList().getLoreList()
                    ).create();
        }
        
        public static ItemStack spawnOptions() {
            return ItemFactory.create(Material.GHAST_SPAWN_EGG).setName("Spawn settings")
                    .addLoreLines(new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to edit spawn options")
                                    .emptyLine().addList()
                                    .addEntries(ChatColor.BLUE + "The distance from spawn set is " )
                                    .getLoreList()
                    ).create();
        }
        
        public static ItemStack gracePeriodSettings() {
            return ItemFactory.create(Material.REDSTONE_TORCH)
                    .addLoreLine(ChatColor.YELLOW + "CLICK to edit grace period settings")
                    .setName(ChatColor.YELLOW + "Grace period settings").create();
        }
        
        public static ItemStack cancel() {
            return ItemFactory.create(Material.RED_CONCRETE)
                    .setName(ChatColor.RED + "Cancel").create();
        }
        
        public static ItemStack create() {
            return ItemFactory.create(Material.GREEN_CONCRETE)
                    .setName(ChatColor.GREEN + "Start game").create();
        }
    }
    
    public static class Tracker2ItemsGUI {
        public static final ItemStack DISTANCE = ItemFactory.create(Material.RAIL)
                .setName(ChatColor.GOLD + "Distance").setLore(
                        new LoreCreator().emptyLine()
                        .addEntries(ChatColor.YELLOW + "Tracks the distance between you",
                                ChatColor.YELLOW + "closest runner").getLoreList()
                ).addItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS)).create();
    
        public static final ItemStack COORDS = ItemFactory.create(Material.MAP)
                .setName(ChatColor.GOLD + "Coordinates").setLore(
                        new LoreCreator().emptyLine()
                                .addEntries(ChatColor.YELLOW + "Displays the coordinates of the",
                                        ChatColor.YELLOW + "closest runner").getLoreList()
                ).addItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS)).create();
    }
    
    public static class TrackerItems {
        private TrackerItems() {}
        // Tracker items
        public static final ItemStack SIMPLE = ItemFactory.create(Material.COMPASS)
                .setName(ChatColor.GOLD + "Hunter tracker")
                .addLoreLines(
                        new LoreCreator().emptyLine()
                                .addEntries(ChatColor.YELLOW + "Tracks nearest runner")
                                .getLoreList()
                ).create();
    
        public static final ItemStack SIMPLE_W_DIST = ItemFactory.create(Material.COMPASS)
                .setName(ChatColor.GOLD + "Hunter tracker")
                .addLoreLines(
                        new LoreCreator().emptyLine()
                                .addEntries(ChatColor.YELLOW + "Right or left click to select how the",
                                        ChatColor.YELLOW + " nearest speedrunner should be tracked")
                                .getLoreList()
                ).create();
    
        public static final ItemStack ADVANCED = ItemFactory.create(Material.COMPASS)
                .setName(ChatColor.GOLD + "Hunter tracker")
                .addLoreLines(new LoreCreator().emptyLine()
                                .addEntries(ChatColor.YELLOW + "Right or left click to open interface",
                                        ChatColor.YELLOW + " to chose tracker options")
                                .getLoreList()
                ).create();
    }
    
}
