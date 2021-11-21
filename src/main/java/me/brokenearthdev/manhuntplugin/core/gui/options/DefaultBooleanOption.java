package me.brokenearthdev.manhuntplugin.core.gui.options;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

/**
 * An implementation of {@link DynamicBooleanOption} for quick
 * use
 * <p>
 * A default {@link ItemStack} will be passed in. Based on the value,
 * the {@link ItemStack}'s name will have {@code ChatColor.GREEN + " (selected)} or
 * {@code ChatColor.RED + " (not selected)"} added to them. If selected, the item will
 * also have an enchantment effect added to it.
 */
public class DefaultBooleanOption extends DynamicBooleanOption {
    
    private final ItemStack defaultItem;
    private final ItemStack onItem;
    private final ItemStack offItem;
    
    public DefaultBooleanOption(GameMenu menu, ItemStack defaultItem, int slot, boolean initial) {
        super(menu, slot, initial);
        this.defaultItem = defaultItem;
        this.onItem = ItemFactory.create(defaultItem.clone())
                .setName(defaultItem.getItemMeta().getDisplayName() + ChatColor.GREEN + " (selected)")
                .addEnchant(Enchantment.ARROW_FIRE, 1)
                .addItemFlags(Collections.singletonList(ItemFlag.HIDE_ENCHANTS))
                .create();
        this.offItem = ItemFactory.create(defaultItem.clone())
                .setName(defaultItem.getItemMeta().getDisplayName() + ChatColor.RED + " (not selected)")
                .create();
    }
    
    /**
     * @return The default item
     */
    public ItemStack getDefaultItem() {
        return defaultItem;
    }
    
    /**
     * Refreshes the {@link ItemStack} based on the value (true or false / selected or unselected).
     * This function is called by {@link #refreshItem()}, and therefore there is no need to
     * override {@link #refreshItem()}
     *
     * @param value The new value
     * @return A refreshed item
     */
    @Override
    public ItemStack refreshItem(boolean value) {
        return value ? onItem : offItem;
    }
    
}
