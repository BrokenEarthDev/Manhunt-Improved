package me.brokenearthdev.manhuntplugin.core.gui.options;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.NumberIncreaseButton;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.NumberDecreaseButton;

/**
 * A built-in implementation of {@link DynamicIntOption}
 * for quick use.
 * <p>
 * A default {@link ItemStack} is to be passed in. When a
 * {@link NumberIncreaseButton} or a {@link NumberDecreaseButton} is clicked,
 * the default item will have its name appended with {@code ChatColor.GREEN + " (+value)"}
 * (see {@link #refreshItem(int)} in this object).
 */
public class DefaultIntOption extends DynamicIntOption {
    
    private final ItemStack defaultItem;
    
    public DefaultIntOption(GameMenu menu, ItemStack defaultItem,
                            int nislot, int slot, int ndslot, int initial) {
        super(menu, nislot, slot, ndslot, initial);
        this.defaultItem = defaultItem;
    }
    
    /**
     * @return The default item
     */
    public ItemStack getDefaultItem() {
        return defaultItem;
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
    @Override
    public ItemStack refreshItem(int value) {
        return ItemFactory.create(defaultItem.clone())
                .setName(defaultItem.getItemMeta().getDisplayName() + ChatColor.GREEN + " (+" + value + ")")
                .create();
    }
}
