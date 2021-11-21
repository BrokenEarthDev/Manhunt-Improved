package me.brokenearthdev.manhuntplugin.core.gui.options;

import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Represents an option that updates dynamically (i.e. when the option gets clicked).
 * All {@link DynamicOption} subclasses has to implement {@link #refreshItem()}, which
 * will be called when the button is clicked.
 * <p>
 * This is an alternative to {@link Button}. Unlike {@link Button}, however, a {@link DynamicOption}
 * is dynamic (it updates the inventory upon the option is clicked).
 */
public abstract class DynamicOption {
    
    protected Button button;
    protected GameMenu menu;
    
    public DynamicOption(GameMenu menu,int slot) {
        this.button = new Button(slot, refreshItem());
        this.menu = menu;
        menu.setButton(button);
        button.addAction(action -> {
            button.setItem(refreshItem());
            menu.display(action.getWhoClicked());
        });
    }
    
    /**
     * @return A new {@link ItemStack} based on the option
     * set
     */
    public abstract ItemStack refreshItem();
    
    
    /**
     * @return The button
     */
    public Button getButton() {
        return button;
    }
    
    /**
     * Adds an action to the {@link Button} to be triggered when the
     * button is clicked.
     *
     * @param eventConsumer The consumer
     */
    public void addAction(Consumer<InventoryClickEvent> eventConsumer) {
        button.addAction(eventConsumer);
    }
    
}
