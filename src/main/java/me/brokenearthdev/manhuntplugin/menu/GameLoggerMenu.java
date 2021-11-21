package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.admin.GameLogger;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.DerivativeMenu;
import me.brokenearthdev.manhuntplugin.core.gui.options.DefaultBooleanOption;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicBooleanOption;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.GAME_LOGGER_MENU_ITEMS;
import static me.brokenearthdev.manhuntplugin.utils.StaticImports.playConfirmedSound;

/**
 * A derivative menu of {@link StartMenu} where how game loggers function is decided
 * by the player.
 */
public class GameLoggerMenu extends DerivativeMenu {
    
    private final DefaultBooleanOption dump = new DefaultBooleanOption(this, GAME_LOGGER_MENU_ITEMS.dumpItem(false), 10, false);
    private final DefaultBooleanOption console = new DefaultBooleanOption(this, GAME_LOGGER_MENU_ITEMS.consoleItem(false), 16, false);
    
    //private BooleanButton dump, console;
    private final DynamicBooleanOption high, med, low;
    
    private boolean dumpData = false;
    private boolean dumpConsole = false;
    private GameLogger.GameLogLevel lvl = GameLogger.GameLogLevel.MEDIUM;
    
    public GameLoggerMenu(StartMenu parent) {
        super("Game logger options", 6, parent, new Button(49, ItemFactory.create(Material.ARROW).setName(ChatColor.WHITE + "Back").create()));
        high = new DynamicBooleanOption(this, 28, false) {
            @Override
            public ItemStack refreshItem(boolean val) {
                return GAME_LOGGER_MENU_ITEMS.levelLogs(GameLogger.GameLogLevel.HIGH, val);
            }
        };
        med = new DynamicBooleanOption(this, 31, false) {
            @Override
            public ItemStack refreshItem(boolean value) {
                return GAME_LOGGER_MENU_ITEMS.levelLogs(GameLogger.GameLogLevel.MEDIUM, value);
            }
        };
        low = new DynamicBooleanOption(this, 34, false) {
            @Override
            public ItemStack refreshItem(boolean value) {
                return GAME_LOGGER_MENU_ITEMS.levelLogs(GameLogger.GameLogLevel.LOW, value);
            }
        };
        addFunctions();
    }
    
    public GameLogger.GameLogLevel selectedLvl() {
        return lvl;
    }
    
    public boolean dumpData() {
        return dumpData;
    }
    
    public boolean isDumpConsole() {
        return dumpConsole;
    }
    
    
    private void addFunctions() {
        Consumer<InventoryClickEvent> eventConsumer = e -> {
            if (!dumpData && !dumpConsole) {
                high.getButton().setItem(high.refreshItem(false));
                med.getButton().setItem(med.refreshItem(false));
                low.getButton().setItem(low.refreshItem(false));
            }
        };
        dump.addAction(e -> dumpData = !dumpData);
        console.addAction(e -> dumpConsole = !dumpConsole);
        dump.addAction(eventConsumer);
        console.addAction(eventConsumer);
        high.addAction(action -> {
           lvl = GameLogger.GameLogLevel.HIGH;
           if (dumpData || dumpConsole) {
               high.getButton().setItem(high.refreshItem(true));
               med.getButton().setItem(med.refreshItem(false));
               low.getButton().setItem(low.refreshItem(false));
               playConfirmedSound((Player) action.getWhoClicked());
               display(action.getWhoClicked());
           }
        });
        med.addAction(action -> {
            lvl = GameLogger.GameLogLevel.MEDIUM;
            if (dumpData || dumpConsole) {
                high.getButton().setItem(high.refreshItem(false));
                med.getButton().setItem(med.refreshItem(true));
                low.getButton().setItem(low.refreshItem(false));
                playConfirmedSound((Player) action.getWhoClicked());
                display(action.getWhoClicked());
            }
        });
        low.addAction(action -> {
            lvl = GameLogger.GameLogLevel.LOW;
            if (dumpData || dumpConsole) {
                high.getButton().setItem(high.refreshItem(false));
                med.getButton().setItem(med.refreshItem(false));
                low.getButton().setItem(low.refreshItem(true));
                playConfirmedSound((Player) action.getWhoClicked());
                display(action.getWhoClicked());
            }
        });
    }
    
}
