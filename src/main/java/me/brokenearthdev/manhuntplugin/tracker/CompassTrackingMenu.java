package me.brokenearthdev.manhuntplugin.tracker;

import me.brokenearthdev.manhuntplugin.Utils;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.BooleanButton;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.createPlayerHead;

public class CompassTrackingMenu extends GameMenu {
    
    /**
     * Represents a compass interface
     */
    
    private final ItemStack redGlass = ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create();
    private final Tracker3 tracker3;
    private final ButtonFunctions functions = new ButtonFunctions();
    private Button trackingSettingsButton = null;
    private Button trackHuntersButton, trackSpeedrunnersButton, trackSpawnButton,
            trackersInterfaceButton = null;
    private Button includeInfoActionbarButton, trackLocOrHealth, autoCollectButton,
            trackDistanceOrCordsButton = null;
    
    public CompassTrackingMenu(Tracker3 tracker3) {
        super("Tracker Settings", 4);
        this.tracker3 = tracker3;
        //initButtons();
        //addFunctionalities();
        //registerButtons();
    }
    
    public void setupMenu() {
        initButtons();
        addFunctionalities();
        registerButtons();
    }
    
    @Override
    public void display(HumanEntity entity) {
        setItemsToNormalButtons();
        super.display(entity);
    }
    
    private void addFunctionalities() {
        functions.addTrackersInterfaceFunction();
        functions.addTrackHuntersFunction();
        functions.addTrackSpawnFunction();
        functions.addTrackSpeedrunnersFunction();
    }
    
    private void registerButtons() {
        this.setButton(trackingSettingsButton)
                .setButton(trackHuntersButton)
                .setButton(trackSpeedrunnersButton)
                .setButton(trackSpawnButton)
                .setButton(trackersInterfaceButton)
                .setButton(includeInfoActionbarButton)
                .setButton(trackLocOrHealth)
                .setButton(autoCollectButton)
                .setButton(trackDistanceOrCordsButton)
                .setButton(redGlassButton(29))
                .setButton(redGlassButton(30))
                .setButton(redGlassButton(32))
                .setButton(redGlassButton(34));
    }
    
    private Button redGlassButton(int slot) {
        return new Button(slot, redGlass);
    }
    
    /**
     * Initializes buttons
     */
    private void initButtons() {
        Player hunter = tracker3.getTracker();
        //boolean actionBar = tracker3.isIncludeInfoAtActionbar();
        //boolean loc = tracker.getTrackingType();
        //boolean autoCollect = tracker.isAutoCollect();
        //boolean distance = tracker.isTrackingDistance();
        trackingSettingsButton = new Button(4, null);
        trackHuntersButton = new Button(19, null);
        
        trackSpeedrunnersButton = new Button(20, null);
        trackSpawnButton = new Button(21, null);
        includeInfoActionbarButton = new BooleanButton(23, tracker3.actionbar, (event, change) -> {
            tracker3.actionbar = change;
            hunter.sendMessage(change ? ChatColor.GREEN + "You can now see info in the actionbar" :
                    ChatColor.RED + "You can't see info in the actionbar");
            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
        }, ItemFactory.create(Material.REDSTONE_TORCH)
                .setName(ChatColor.GREEN + "Actionbar: ENABLED").addGlowEffect(true).create(), ItemFactory.create(Material.LEVER)
                .setName(ChatColor.RED + "Actionbar: DISABLED").create());
        trackLocOrHealth = new BooleanButton(24, tracker3.trackingType, (event, change) -> {
            tracker3.trackingType = change;
            if (change) hunter.sendMessage(ChatColor.GREEN + "Tracking location");
            else hunter.sendMessage(ChatColor.GREEN + "Tracking health");
            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
        }, ItemFactory.create(Material.ARROW)
                .setName(ChatColor.RED + "Tracked: location").create(), ItemFactory.create(Material.APPLE)
                .setName(ChatColor.GREEN + "Tracked: health").create());
        autoCollectButton = new Button(25, redGlass);
        //autoCollectButton = new BooleanButton(25, autoCollect, (event, aBoolean) -> {
          //  tracker.setAutoCollect(aBoolean);
            //hunter.sendMessage(ChatColor.GREEN + "Auto collect has been " + (aBoolean ? "enabled" : "disabled"));
            //hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
        //}, ItemFactory.create(
          //      Material.DIAMOND_PICKAXE).setName(ChatColor.GREEN + "Auto Collect: ENABLED").addGlowEffect(true).create(),
            //    ItemFactory.create(Material.WOODEN_PICKAXE).setName(ChatColor.RED + "Auto Collect: DISABLED")
              //          .create());
        trackDistanceOrCordsButton = new BooleanButton(28, tracker3.distanceOrLoc, (event, aBoolean) -> {
            tracker3.distanceOrLoc = aBoolean;
            hunter.sendMessage(ChatColor.GREEN + "Tracking " + (aBoolean ? "distance" : "coordinates"));
            hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
        }, ItemFactory.create(Material.RAIL)
                .setName(ChatColor.GREEN + "Location Tracked: DISTANCE").create(), ItemFactory.create(Material.MAP)
                .setName(ChatColor.GREEN + "Location Tracked: COORDINATES").create());
        trackersInterfaceButton = new Button(33, null);
        setItemsToNormalButtons();
    }
    
    /**
     * Sets items to buttons that aren't instances of {@link BooleanButton}, {@link org.github.brokenearthdev.manhunt.gui.buttons.NumberIncreaseButton},
     * and {@link org.github.brokenearthdev.manhunt.gui.buttons.NumberDecreaseButton}.
     * This method is automatically called when invoking {@link #initButtons()}
     */
    private void setItemsToNormalButtons() {
        ManhuntGame game = ManhuntGame.getManhuntGame();
        Player hunter = tracker3.getTracker();
        Player tracked = tracker3.getTracked();
        ItemStack trackingSettings = ItemFactory.create(Material.COMPASS).setName(ChatColor.YELLOW +
                "Tracking Settings").create();
        ItemStack trackHunters = ItemFactory.create(createPlayerHead(new ArrayList<>(game.getHunters()).get(0).getPlayer(),
                ChatColor.GREEN + "Track Hunter(s)")).addLoreLine(ChatColor.GREEN + "Potential Hunter(s) Include(s): "
                + ChatColor.RED + Utils.potentialHunterNames(game, hunter)).create();
        String text2 = tracked != null && game.isRunner(tracked) ? ChatColor.RED + "You're Already Tracking a Speedrunner"
                : ChatColor.GREEN + "Track Speedrunner";
        ItemStack trackSpeedrunners = createPlayerHead(tracked, text2);
        String text3 = tracked == null ? ChatColor.RED + "You're Already Tracking Spawn" : ChatColor.GREEN + "Track Spawn";
        ItemStack trackSpawn = ItemFactory.create(Material.GHAST_SPAWN_EGG).setName(text3).create();
        ItemStack trackersInterface = ItemFactory.create(Material.BOW).setName(ChatColor.GREEN + "Your Trackers").create();
        ItemStack disabled = ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.RED + "This feature is disabled")
                .setLore(ChatColor.YELLOW + "In under certain circumstances,", ChatColor.YELLOW + "this feature may be enabled").create();
        // adds the items
        trackingSettingsButton.setItem(trackingSettings);
        trackHuntersButton.setItem(game.getHunters().size() == 1 ? disabled : trackHunters);
        trackSpeedrunnersButton.setItem(trackSpeedrunners);
        trackSpawnButton.setItem(trackSpawn);
        trackersInterfaceButton.setItem(game.getHunters().size() == 1 ? disabled : trackersInterface);
    }
    
    /**
     * Adds button functions to every buttons that are not instances of
     * {@link BooleanButton}, {@link org.github.brokenearthdev.manhunt.gui.buttons.NumberDecreaseButton},
     * and {@link org.github.brokenearthdev.manhunt.gui.buttons.NumberIncreaseButton}
     */
    private class ButtonFunctions {
        
        private void addTrackHuntersFunction() {
            trackHuntersButton.addAction(action -> {
                ManhuntGame game = ManhuntGame.getManhuntGame();
                if (game.getHunters().size() > 1) {
                    CompassTrackingMenu.this.tracker3.openHuntersInterface();
                }
            });
        }
        
        private void addTrackSpeedrunnersFunction() {
            trackSpeedrunnersButton.addAction(e -> {
                ManhuntGame game = ManhuntGame.getManhuntGame();
                Player hunter = CompassTrackingMenu.this.tracker3.getTracker();
                Player tracked = CompassTrackingMenu.this.tracker3.getTracked();
                if (game.getRunners().size() > 1) {
                    tracker3.openRunnersInterface();
                }
//                if (game.getSpeedrunner().isOnline()) {
//                    if (tracked != null && game.getSpeedrunner().equals(tracked)) {
//                        hunter.sendMessage(ChatColor.RED + "The speedrunner is already being tracked!");
//                        hunter.playSound(hunter.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 100);
//                    } else {
//                        CompassTrackingMenu.this.tracker3.setTracked(game.getSpeedrunner());
//                        hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
//                        e.setCurrentItem(Utils.createPlayerHead(tracked, ChatColor.RED + "You're Already Tracking a Speedrunner"));
//                    }
//                } else {
//                    hunter.sendMessage(ChatColor.RED + "The speedrunner isn't online!");
//                    hunter.playSound(hunter.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 100);
//                }
            });
        }
        
        private void addTrackSpawnFunction() {
            trackSpawnButton.addAction(e -> {
                boolean loc = tracker3.distanceOrLoc;
                Player hunter = CompassTrackingMenu.this.tracker3.getTracker();
                Player tracked = CompassTrackingMenu.this.tracker3.getTracked();
                if (!loc) {
                    hunter.sendMessage(ChatColor.RED + "Please track location first, and not health!");
                    hunter.playSound(hunter.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 100);
                } else {
                    if (tracked == null) {
                        hunter.sendMessage(ChatColor.RED + "You're already tracking spawn!");
                        hunter.playSound(hunter.getLocation(), Sound.ENTITY_SKELETON_DEATH, 100, 100);
                    } else {
                        e.setCurrentItem(ItemFactory.create(Material.GHAST_SPAWN_EGG)
                                .setName(ChatColor.GREEN + "Track Spawn").create());
                        tracker3.trackSpawn = true;
                        hunter.playEffect(hunter.getLocation(), Effect.CLICK2, null);
                    }
                }
            });
        }
        
        private void addTrackersInterfaceFunction() {
            trackersInterfaceButton.addAction(event -> {
                ManhuntGame game = ManhuntGame.getManhuntGame();
                if (game.getHunters().size() > 1) {
                    tracker3.openTrackersInterface();
                }
            });
        }
        
    }
    
    
}
