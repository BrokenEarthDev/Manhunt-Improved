package me.brokenearthdev.manhuntplugin.core.gui.menu;

import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A derivative menu is any menu that returns back to a {@link me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu}
 */
public class DerivativeMenu extends GameMenu {
    
    private final List<Consumer<InventoryClickEvent>> onBack = new ArrayList<>();
    private final GameMenu parent;
    private final Button backItem;
    
    /**
     * Creates a new menu
     *
     * @param title Menu title
     * @param rows  Menu rows
     */
    public DerivativeMenu(String title, int rows, GameMenu parent, Button backItem) {
        super(title, rows);
        this.parent = parent;
        this.backItem = backItem;
        this.setButton(backItem);
        backItem.getOnClick().add(e -> onBack.forEach(backClick -> backClick.accept(e)));
        backItem.getOnClick().add(e -> parent.display(e.getWhoClicked()));
    }
    
    /**
     * @return The consumers that are called when returning to the parent GUI
     */
    public List<Consumer<InventoryClickEvent>> getOnReturn() {
        return onBack;
    }
    
    /**
     * @return The back item
     */
    public Button getBackItem() {
        return backItem;
    }
    
    /**
     * @return The parent menu
     */
    public GameMenu getParentMenu() {
        return parent;
    }
    
}
