package me.brokenearthdev.manhuntplugin.core.gui.options;

import me.brokenearthdev.manhuntplugin.core.gui.buttons.BooleanButton;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a {@link DynamicOption} that has two values (true or false / selected or
 * unselected). Whenever the option is selected, the button is refreshed such that
 * it is adjusted to the new state.
 * <p>
 * The {@link #refreshItem(boolean)} function determines what item to set depending
 * on the state.
 */
public abstract class DynamicBooleanOption extends DynamicOption {
    
    private boolean state;
    
    public DynamicBooleanOption(GameMenu menu, int slot, boolean initial) {
        super(menu, slot);
        super.button = new BooleanButton(slot, initial, (event, aBoolean) -> {
            state = aBoolean;
            refreshItem();
            menu.display(event.getWhoClicked());
        }, refreshItem(true), refreshItem(false));
        menu.setButton(button);
    }
    
    /**
     * @return The current state
     */
    public boolean getState() {
        return state;
    }
    
    /**
     * Refreshes the {@link ItemStack} based on the value (true or false / selected or unselected).
     * This function is called by {@link #refreshItem()}, and therefore there is no need to
     * override {@link #refreshItem()}
     *
     * @param value The new value
     * @return A refreshed item
     */
    public abstract ItemStack refreshItem(boolean value);
    
    /**
     * @return A new {@link ItemStack} based on the option
     * set
     */
    @Override
    public final ItemStack refreshItem() {
        return refreshItem(state);
    }
}
