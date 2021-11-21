package me.brokenearthdev.manhuntplugin.core.gui.options;

import me.brokenearthdev.manhuntplugin.Utils;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.NumberDecreaseButton;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.NumberIncreaseButton;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 * A dynamic int option is a modifiable {@link DynamicOption} that has
 * three buttons registered: {@link NumberIncreaseButton}, {@link NumberDecreaseButton},
 * and {@link Button}. Whenever the viewer clicks on either the {@link NumberIncreaseButton}
 * or the {@link NumberDecreaseButton}, {@link #refreshItem(int)} will be fired with the
 * new value passed in.
 * <p>
 * The {@link Button} (not {@code instanceof NumberIncreaseButton} and not
 * {@code instanceof NumberDecreaseButton}) is used to represent the option. The {@link Button}
 * is the same as those in {@link DynamicOption}.
 */
public abstract class DynamicIntOption extends DynamicOption {
    
    private int value;
    
    private final NumberIncreaseButton niButton;
    private final NumberDecreaseButton ndButton;
    
    public DynamicIntOption(GameMenu menu, int nislot, int slot, int ndslot,
                            int initial) {
        super(menu, slot);
        value = Math.max(initial, 1);
        super.button = new Button(slot, refreshItem());
        niButton = new NumberIncreaseButton(nislot, value, Utils.increaseButton(value));
        ndButton = new NumberDecreaseButton(ndslot, value, Utils.decreaseButton(value));
        menu.setButton(button).setButton(niButton).setButton(ndButton);
        BiConsumer<InventoryClickEvent, Integer> onChange = (event, integer) -> {
            value = integer -1;
            button.setItem(refreshItem());
            menu.display(event.getWhoClicked());
        };
        niButton.register(Utils.increaseButton(value), ndButton, onChange);
        ndButton.register(1, niButton, Utils.increaseButton(value), onChange);
    }
    
    /**
     * Sets the new value of the option to the one passed in. Depending
     * on the {@code boolean}, {@link #refreshItem(int)} may be invoked.
     * <p>
     * It is important to note, however, even if {@link #refreshItem(int)} is
     * called, the parent {@link GameMenu} may fail to update. Make sure to
     * call {@link GameMenu#display(HumanEntity)} to refresh <i>if necessary</i>
     *
     * @param value The new value
     * @param refresh Whether to invoke {@link #refreshItem(int)} with the new value
     */
    public void setValue(int value, boolean refresh) {
        this.value = value;
        if (refresh) {
            refreshItem(value);
        }
    }
    
    /**
     * @return The value of the option
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Refreshes the item based on the value passed to this function, which is
     * manipulated by the viewer using {@link NumberIncreaseButton}s and {@link NumberDecreaseButton}s.
     * Whenever the viewer uses those buttons, this function with the new value
     * is invoked.
     * <p>
     * The {@link #refreshItem()} function is already implemented, hence there is
     * no need to override the function.
     *
     * @param value The new value
     */
    public abstract ItemStack refreshItem(int value);
    
    /**
     * @return A new {@link ItemStack} based on the option
     * set
     */
    @Override
    public final ItemStack refreshItem() {
        if (value < 0)
            value = 0;
        return refreshItem(value);
    }
    
    /**
     * @return The number increase button
     */
    public NumberIncreaseButton getNumberIncreaseButton() {
        return niButton;
    }
    
    /**
     * @return The number decrease button
     */
    public NumberDecreaseButton getNumberDecreaseButton() {
        return ndButton;
    }
    
}
