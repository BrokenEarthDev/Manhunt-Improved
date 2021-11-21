package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.DerivativeMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicBooleanOption;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicIntOption;
import me.brokenearthdev.manhuntplugin.game.PlayerSelector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.COMMON_ITEMS;
import static me.brokenearthdev.manhuntplugin.utils.StaticImports.PLAYER_SELECTOR_OPTIONS_MENU_ITEMS;

/**
 * A menu that allows players to change selection options
 * (like max players, max hunters, etc.) that is not involved
 * with manually selecting/unselecting players, where the selection
 * of players in entirely random unlike the manual selection in
 * {@link GamePlayerSelectorMenu}
 *
 * @deprecated There are bugs which couldn't be fixed, so they are
 *             removed.
 */
@Deprecated
public class PlayerSelectorOptionsMenu extends DerivativeMenu {

    private final PlayerSelector selector;
    
    private DynamicIntOption MAX_RUNNERS;
    private DynamicIntOption MAX_HUNTERS;
    private DynamicBooleanOption infinityButton;
    
    private int preInfinity;
    private boolean inf;
    
    public PlayerSelectorOptionsMenu(GameMenu fallback, PlayerSelector selector) {
        super("Player selector options", 6, fallback,
                new Button(49, COMMON_ITEMS.backItem()));
        this.selector = selector;
        inf = selector.getMaxHunters() == Integer.MAX_VALUE;
        preInfinity = inf ? 1 : selector.getMaxHunters();
        registerIntOptions();
        registerButtons();
    }
    
    /**
     * @return Maximum runners
     */
    public DynamicIntOption getMaxRunnersOptions() {
        return MAX_RUNNERS;
    }
    
    /**
     * @return Maximum hunters
     */
    public DynamicIntOption getMaxHuntersOption() {
        return MAX_HUNTERS;
    }
    
    /**
     * @return Option to set the max hunters to infinity
     */
    public DynamicBooleanOption getInfinityButton() {
        return infinityButton;
    }
    
    /**
     * Registers integer options
     */
    private void registerIntOptions() {
        MAX_RUNNERS = new DynamicIntOption(this, 10, 19, 28, selector.getMaxRunners()) {
            @Override
            public ItemStack refreshItem(int value) {
                return PLAYER_SELECTOR_OPTIONS_MENU_ITEMS.maxRunnersItem(value);
            }
        };
        MAX_HUNTERS = new DynamicIntOption(this, 16, 25, 34, selector.getMaxHunters()) {
            @Override
            public ItemStack refreshItem(int value) {
                return PLAYER_SELECTOR_OPTIONS_MENU_ITEMS.maxHuntersItem(value, inf, this);
            }
        };
    }
    
    /**
     * Registers buttons
     */
    private void registerButtons() {
        infinityButton = new DynamicBooleanOption(this, 43, inf) {
            @Override
            public ItemStack refreshItem(boolean value) {
                return PLAYER_SELECTOR_OPTIONS_MENU_ITEMS.infinityItem(inf);
            }
        };
        infinityButton.addAction(action -> {
            if (selector.getMaxHunters() == Integer.MAX_VALUE) {
                inf = false;
                MAX_HUNTERS.setValue(preInfinity, true);
//                MAX_HUNTERS.getButton().setItem(MAX_HUNTERS.refreshItem(preInfinity));
            } else {
                inf = true;
                preInfinity = MAX_HUNTERS.getValue();
                MAX_HUNTERS.setValue(Integer.MAX_VALUE, true);
                //MAX_HUNTERS.getButton().setItem(MAX_HUNTERS.refreshItem(Integer.MAX_VALUE));
            }
            display(action.getWhoClicked());
        });
    }
    
}
