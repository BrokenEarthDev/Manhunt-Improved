package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.DerivativeMenu;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicOption;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.tracker.TrackerType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.HUNTERS_OPTIONS_MENU_ITEMS;

/**
 * A menu to manage and edit hunter options
 */
public final class HuntersOptionsMenu extends DerivativeMenu {

    // Options set
    private Kit selected;
    private TrackerType tracker = TrackerType.SIMPLISTIC;
    
    // The tracker button
    private final DynamicOption compass = new DynamicOption(this, 10) {
        @Override
        public ItemStack refreshItem() {
            return  HUNTERS_OPTIONS_MENU_ITEMS.compassItem(tracker);
        }
    };
    
    // The kit button
    private final DynamicOption kit = new DynamicOption(this, 16) {
        @Override
        public ItemStack refreshItem() {
            return HUNTERS_OPTIONS_MENU_ITEMS.kitItem(selected);
        }
    };
    
    public HuntersOptionsMenu(StartMenu parent, Kit def) {
        super("Hunter options", 3, parent, new Button(13, ItemFactory.create(Material.GREEN_CONCRETE)
                .setName(ChatColor.GREEN + "Back")
                .create()));
        selected = def;
        parent.hunterKitSelector.setReturntoGui(this);
        parent.hunterKitSelector.addOnItemClick((kit1, event) -> {
            selected = kit1;
            kit.getButton().setItem(kit.refreshItem());
            display(event.getWhoClicked());
        });
        parent.trackerMenu.getOnReturn().add(e -> {
            tracker = parent.trackerMenu.getSelected();
            compass.getButton().setItem(compass.refreshItem());
            //display(e.getWhoClicked());
            this.tracker = parent.trackerMenu.getSelected();
        });
        compass.addAction(action -> parent.trackerMenu.display(action.getWhoClicked()));
        kit.addAction(action -> parent.hunterKitSelector.display(action.getWhoClicked()));
    }
    
    public Kit getSelectedKit() {
        return selected;
    }
    public TrackerType getSelectedTracker() {
        return tracker;
    }
    
}
