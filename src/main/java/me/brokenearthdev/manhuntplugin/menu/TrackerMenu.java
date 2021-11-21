package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.DerivativeMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.tracker.TrackerType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.function.Consumer;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.COMMON_ITEMS;

public class TrackerMenu extends DerivativeMenu {
    
    private TrackerType type;
    
    private final Button SIMPLISTIC_TRACKER;
    
    private final Button SIMPLISTIC_W_COORDINATES;
    
    private final Button ADVANCED_TRACKER;
    
    public TrackerMenu(StartMenu fallback, TrackerType type) {
        super("Tracker options", 4, fallback, new Button(31, COMMON_ITEMS.backItem()));
        this.type = type;
        SIMPLISTIC_TRACKER = new Button(10, createSimplisticTracker(type == TrackerType.SIMPLISTIC));
        SIMPLISTIC_W_COORDINATES = new Button(13, createSimplisticWCoordsTracker(type == TrackerType.SIMPLE_W_COORDINATES));
        ADVANCED_TRACKER = new Button(16, createAdvancedTracker(type == TrackerType.COMPLEX));
        addFunctions();
    }
    
    private void addFunctions() {
        setButton(SIMPLISTIC_TRACKER).setButton(SIMPLISTIC_W_COORDINATES).setButton(ADVANCED_TRACKER);
        
        SIMPLISTIC_TRACKER.addAction(e -> select(TrackerType.SIMPLISTIC, e.getWhoClicked()));
        SIMPLISTIC_W_COORDINATES.addAction(e -> select(TrackerType.SIMPLE_W_COORDINATES, e.getWhoClicked()));
        ADVANCED_TRACKER.addAction(e -> select(TrackerType.COMPLEX, e.getWhoClicked()));
    }
    
    private void select(TrackerType type, HumanEntity entity) {
        if (this.type == type) {
            Message.ERROR_PREFIX("This option is already selected").send(entity);
            return;
        }
        switch (this.type) {
            case SIMPLISTIC: SIMPLISTIC_TRACKER.setItem(createSimplisticTracker(false));
            break;
            case SIMPLE_W_COORDINATES: SIMPLISTIC_W_COORDINATES.setItem(createSimplisticWCoordsTracker(false));
            break;
            case COMPLEX: ADVANCED_TRACKER.setItem(createAdvancedTracker(false));
            break;
        }
        this.type = type;
        switch (type) {
            case SIMPLISTIC: SIMPLISTIC_TRACKER.setItem(createSimplisticTracker(true));
            break;
            case SIMPLE_W_COORDINATES: SIMPLISTIC_W_COORDINATES.setItem(createSimplisticWCoordsTracker(true));
            break;
            case COMPLEX: ADVANCED_TRACKER.setItem(createAdvancedTracker(true));
            break;
        }
        Message.GOOD_PREFIX("Selected " + type.toString()).send(entity);
        ((Player) entity).playSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
        display(entity);
    }
    
    private ItemStack createSimplisticTracker(boolean selected) {
        String name = ChatColor.GOLD + "Simplistic tracker";
        if (selected) name += " " + ChatColor.GREEN + "(selected)";
        ItemFactory factory = ItemFactory.create(Material.COMPASS).emptyLoreLine()
                .addLoreLine(ChatColor.YELLOW + "Use the compass to only track the ")
                .addLoreLine(ChatColor.YELLOW + "player's direction");
        if (selected) factory.addEnchant(Enchantment.LUCK, 1).addItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS)).create();
        return factory.setName(name).create();
    }
    
    private ItemStack createSimplisticWCoordsTracker(boolean selected) {
        String name = ChatColor.GOLD + "Simplistic tracker";
        if (selected) name += " " + ChatColor.GREEN + "(selected)";
        ItemFactory factory = ItemFactory.create(Material.COMPASS)
                .addLoreLine(ChatColor.GOLD + "(with extra functions)").emptyLoreLine()
                .addLoreLine(ChatColor.YELLOW + "Simplistic trackers with the ability").addLoreLine(ChatColor.YELLOW + "to display coordinates and distance");
        if (selected) factory.addEnchant(Enchantment.LUCK, 1).addItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS)).create();
        return factory.setName(name).create();
    }
    
    private ItemStack createAdvancedTracker(boolean selected) {
        String name = ChatColor.GOLD + "Advanced tracker";
        if (selected) name += " " + ChatColor.GREEN + "(selected)";
        ItemFactory factory = ItemFactory.create(Material.COMPASS)
                .emptyLoreLine().addLoreLine(ChatColor.YELLOW + "A tracker that comes with its own")
                .addLoreLine(ChatColor.YELLOW + "GUI and allows you to select various").addLoreLine(ChatColor.YELLOW + "options");
        if (selected) factory.addEnchant(Enchantment.LUCK, 1).addItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS)).create();
        return factory.setName(name).create();
    }
    
    public TrackerType getSelected() {
        return type;
    }
    
    
}
