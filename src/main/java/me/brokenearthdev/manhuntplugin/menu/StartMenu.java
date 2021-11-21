package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.GameItems;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicOption;
import me.brokenearthdev.manhuntplugin.game.GameSettings;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.PlayerSelector;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.game.players.SpeedrunnerSettings;
import me.brokenearthdev.manhuntplugin.kits.KitSelector;
import me.brokenearthdev.manhuntplugin.tracker.TrackerType;
import static me.brokenearthdev.manhuntplugin.GameItems.StartMenuItemsGUI.*;
import static me.brokenearthdev.manhuntplugin.utils.StaticImports.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The second version of the start menu (only internal updates).
 */
public class StartMenu extends GameMenu {
    
    private final GameSettings.DefaultOptions defaultSettings = new GameSettings.DefaultOptions();
    private final PlayerSelector selector = new PlayerSelector(new HashSet<>(), new HashSet<>(), new HashSet<>(Bukkit.getOnlinePlayers()));
    
    // Menus
    final RunnerOptionsMenu runnerOptionsMenu = new RunnerOptionsMenu(this, new SpeedrunnerSettings());
    final GameLoggerMenu gameLoggerMenu = new GameLoggerMenu(this);
    final KitSelector hunterKitSelector = new KitSelector();  // Sub menu in hunter options menu
    final KitSelector runnerKitSelector = new KitSelector(); // Sub menu in runner options menu
    final TrackerMenu trackerMenu = new TrackerMenu(this, TrackerType.SIMPLISTIC); // Sub menu in hunter options menu
    final HuntersOptionsMenu huntersOptionsMenu = new HuntersOptionsMenu(this, defaultSettings.defaultKit);
    final GamePlayerSelectorMenu playerSelectorMenu = new GamePlayerSelectorMenu(this, selector, new ArrayList<>(Bukkit.getOnlinePlayers()));
    //final PlayerSelectorOptionsMenu playerSelectorOptionsMenu = new PlayerSelectorOptionsMenu(this, selector);
    final SpawnSettingsMenu spawnSettingsMenu = new SpawnSettingsMenu(this, ThreadLocalRandom.current().nextInt(defaultSettings.randomSpawnDistanceMin, defaultSettings.randomSpawnDistanceMax + 1),
            ThreadLocalRandom.current().nextInt(defaultSettings.runnerSpawnDistanceMin, defaultSettings.runnerSpawnDistanceMax));
    final GracePeriodMenu gracePeriodMenu = new GracePeriodMenu(this, defaultSettings.gracePeriodLength);
    
    // Game creation info
    private GameSettings gameSettings;
    private SpeedrunnerSettings runnerSettings;
    private TrackerType selected;
    
    private StartMenu() {
        super("Game Start Menu", 6);
        hookOptions();
        hookOnReturn();
        addCreationFunctions();
    }
    
    private HashSet<HumanEntity> open = new HashSet<>();
    
    @Override
    public void display(HumanEntity entity) {
        open.add(entity);
        super.display(entity);
    }
    
    /**
     * Hooks an option to its respective GUI
     */
    private void hookOptions() {
        hunterOptions.addAction(e -> huntersOptionsMenu.display(e.getWhoClicked()));
        runnerOptions.addAction(e -> runnerOptionsMenu.display(e.getWhoClicked()));
        playerSelector.addAction(e -> {
//            if (e.isLeftClick())
//                playerSelectorOptionsMenu.display(e.getWhoClicked());
//            if (e.isRightClick())
                playerSelectorMenu.display(e.getWhoClicked());
        });
        loggerOptions.addAction(e -> gameLoggerMenu.display(e.getWhoClicked()));
        spawnSettings.addAction(e -> spawnSettingsMenu.display(e.getWhoClicked()));
        gracePeriodSettings.addAction(e -> gracePeriodMenu.display(e.getWhoClicked()));
    }
    
    private void hookOnReturn() {
        runnerOptionsMenu.getOnReturn().add(action -> runnerOptions.getButton().setItem(runnerOptions.refreshItem()));
        gameLoggerMenu.getOnReturn().add(action -> loggerOptions.getButton().setItem(loggerOptions.refreshItem()));
        // todo include kit selector for runners
        huntersOptionsMenu.getOnReturn().add(action -> hunterOptions.getButton().setItem(hunterOptions.refreshItem()));
        playerSelectorMenu.addOnReturntoGui(action -> playerSelector.getButton().setItem(playerSelector.refreshItem()));
       //playerSelectorOptionsMenu.getOnReturn().add(action -> playerSelector.getButton().setItem(playerSelector.refreshItem()));
        spawnSettingsMenu.getOnReturn().add(action -> spawnSettings.getButton().setItem(spawnSettings.refreshItem()));
        gracePeriodMenu.getOnReturn().add(action -> gracePeriodSettings.getButton().setItem(gracePeriodSettings.refreshItem()));
    }
    
    private void addCreationFunctions() {
        cancel.addAction(e -> {
            e.getWhoClicked().getOpenInventory().close();
            e.getWhoClicked().sendMessage(ChatColor.RED + "Game creation cancelled");
            playCancelledSound((Player) e.getWhoClicked());
        });
        create.addAction(action -> {
            action.getWhoClicked().closeInventory();
            playConfirmedSound((Player) action.getWhoClicked());
            SpeedrunnerSettings settings = runnerOptionsMenu.getSettings();
            Set<GamePlayer> gamePlayers = selector.selectPlayers(settings, huntersOptionsMenu.getSelectedTracker(),
                                                                runnerOptionsMenu.getSelectedKit(),
                                                                huntersOptionsMenu.getSelectedKit());
            ManhuntGame game = new ManhuntGame(constructGameSettings(), runnerOptionsMenu.getSettings(),
                                                filterRunners(gamePlayers),
                                                filterHunters(gamePlayers), Bukkit.getWorld("world"),
                                                Bukkit.getWorld("world_the_end"));
            game.startGame();
        });
    }
    
    private GameSettings constructGameSettings() {
        return new GameSettings(spawnSettingsMenu.getHunterDistance(), spawnSettingsMenu.getHunterDistance(),
                spawnSettingsMenu.getHunterDistance(), spawnSettingsMenu.getHunterDistance(), gracePeriodMenu.getGracePeriod(),
                true, null);
    }
    
    /*
     * Dynamic options
     */
    
    // Hunter options
    private final DynamicOption hunterOptions = new DynamicOption(this, 19) {
        public ItemStack refreshItem() { return hunterOptions(huntersOptionsMenu); }
    };
    
    // Runner options
    private final DynamicOption runnerOptions = new DynamicOption(this, 20) {
        public ItemStack refreshItem() { return runnerOptions(settingsToArray()); }
    };
    
    // Player selector
    private final DynamicOption playerSelector = new DynamicOption(this, 21) {
        public ItemStack refreshItem() { return playerSelector(selector); }
    };
    
    // Logger options
    private final DynamicOption loggerOptions = new DynamicOption(this, 23) {
        public ItemStack refreshItem() { return GameItems.StartMenuItemsGUI.loggerOptions(gameLoggerMenu); }
    };
    
    // Spawn settings
    private final DynamicOption spawnSettings = new DynamicOption(this, 24) {
        public ItemStack refreshItem() { return spawnOptions(); }
    };
    
    // Grace period settings
    private DynamicOption gracePeriodSettings = new DynamicOption(this, 25) {
        public ItemStack refreshItem() { return START_MENU_ITEMS.gracePeriodSettings(gracePeriodMenu.getGracePeriod()); }
    };
    
    // Cancel game creation
    private final DynamicOption cancel = new DynamicOption(this, 46) {
        public ItemStack refreshItem() { return cancel(); }
    };
    
    // Create game
    private DynamicOption create = new DynamicOption(this, 52) {
        public ItemStack refreshItem() { return create(); }
    };
    
    private String[] settingsToArray() {
        SpeedrunnerSettings settings = runnerOptionsMenu.getSettings();
        List<String> list = new LinkedList<>();
        list.add(ChatColor.BLUE.toString() + settings.extraHearts + " extra hearts");
        list.add(ChatColor.BLUE + "Strength " + settings.extraDamage);
        list.add(ChatColor.BLUE + "Speed " + settings.speedboost + " for " + settings.speedboostDuration + "s");
        list.add(ChatColor.BLUE + "Alert " + settings.alertProximityRadius + "m in radius");
        list.add(ChatColor.BLUE.toString() + settings.autoSmeltProbability + "% to obtain smelted ore");
        list.add(ChatColor.BLUE.toString() + "Selected kit is " + (runnerOptionsMenu.getSelectedKit() == null ? "none" : runnerOptionsMenu.getSelectedKit().getName()));
        String[] str = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
            str[i] = list.get(i);
        return str;
    }
    
}
