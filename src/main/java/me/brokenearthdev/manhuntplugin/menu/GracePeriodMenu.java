package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.DerivativeMenu;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicIntOption;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicOption;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.*;

/**
 * A derivative menu of {@link StartMenu} which allows players to modify grace periods
 */
public class GracePeriodMenu extends DerivativeMenu {
    
    // The grace period (in seconds)
    private int gracePeriod;
    
    // Whether the grace period is disabled or not
    private boolean disabled = false;
    
    // The modifiers
    private final DynamicOption[] modifiers = new DynamicOption[6];
    
    // The grace period item (displays the set amount of grace period)
    private final DynamicOption gracePeriodItem = new DynamicOption(this, 20) {
        @Override
        public ItemStack refreshItem() {
            return GRACE_PERIOD_MENU_ITEMS.gracePeriodItem(gracePeriod, disabled);
        }
    };
    
    private boolean first = true;
    
    // Option to enable/disable grace period
    private final DynamicOption gracePeriodManip = new DynamicOption(this, 24) {
        @Override
        public ItemStack refreshItem() {
            return GRACE_PERIOD_MENU_ITEMS.gracePeriodManipItem(disabled);
        }
    };
    
    public GracePeriodMenu(StartMenu fallback, int init) {
        super("Set your grace period", 6, fallback, new Button(49, COMMON_ITEMS.backItem()));
        gracePeriod = init;
        
        createModifiers();
        gracePeriodManip.addAction(action -> {
            disabled = !disabled;
            for (DynamicOption mod : modifiers)
                mod.getButton().setItem(mod.refreshItem());
            gracePeriodItem.getButton().setItem(gracePeriodItem.refreshItem());
            display(action.getWhoClicked());
        });
    }
    
    @Override
    public void display(HumanEntity entity) {
        if (first) {
            gracePeriodItem.getButton().setItem(gracePeriodItem.refreshItem());
            first = false;
        }
        super.display(entity);
    }
    
    /**
     * Processes and returns the amount of grace period
     *
      * @return The grace period (in sec)
     */
    public int getGracePeriod() {
        return disabled ? 0 : gracePeriod;
    }
    
    /**
     * Creates modifiers that allows users to modify grace period by the
     * click of a button
     */
    private void createModifiers() {
        int[] slots = {10, 11, 12, 28, 29, 30};
        int[] inc = {1, 5, 60, -1, -5, -60};
        for (int i = 0; i < modifiers.length; i++) {
            int finalI = i;
            modifiers[i] = new DynamicOption(this, slots[finalI]) {
                @Override
                public ItemStack refreshItem() {
                    return GRACE_PERIOD_MENU_ITEMS.gracePeriodModItem(inc[finalI], disabled);
                }
            };
            modifiers[i].addAction(action -> {
                if (!disabled) {
                    if (gracePeriod + inc[finalI] < 0)
                        gracePeriod = 0;
                    else gracePeriod += inc[finalI];
                }
                gracePeriodItem.getButton().setItem(gracePeriodItem.refreshItem());
                display(action.getWhoClicked());
            });
        }
    }
}