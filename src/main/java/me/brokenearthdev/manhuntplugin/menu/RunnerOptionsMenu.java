package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.DerivativeMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.PlayerSelectorMenu;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicIntOption;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicOption;
import me.brokenearthdev.manhuntplugin.game.players.SpeedrunnerSettings;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.kits.KitSelector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.COMMON_ITEMS;

public class RunnerOptionsMenu extends DerivativeMenu {

    private DynamicIntOption extraHearts, extraDamage, speedboostAmp,
                                speedboostTime, apr, autoSmelt;
    
    // Button for kit selector
    private DynamicOption kit;
    
    private Kit selected;
    private final KitSelector selector = new KitSelector();
    private final SpeedrunnerSettings settings;
    private final SpeedrunnerSettings.Builder builder;
    
    // Modifiable integer options
    public SpeedrunnerSettings getSettings() {
        return builder.build();
    }
    
    public RunnerOptionsMenu(StartMenu fallback, SpeedrunnerSettings settings) {
        super("Speedrunner options", 6, fallback, new Button(49, COMMON_ITEMS.backItem()));
        this.settings = settings;
        builder = new SpeedrunnerSettings.Builder();
        selector.addOnItemClick((k, e) -> {
            selected = k;
            kit.getButton().setItem(kit.refreshItem());
            display(e.getWhoClicked());
        });
        initOptions();
    }
    
    public Kit getSelectedKit() {
        return selected;
    }
    
    private void initOptions() {
        extraHearts = new DynamicIntOption(this, 10, 19, 28, settings.extraHearts) {
            @Override
            public ItemStack refreshItem(int value) {
                builder.setExtraHearts(value);
                return ItemFactory.create(Material.APPLE).setName(ChatColor.GOLD + "Extra hearts " + ChatColor.GREEN + "(+" + value +")")
                        .emptyLoreLine().addLoreLine(ChatColor.BLUE + "All runners will get ").addLoreLine(ChatColor.GREEN.toString() + value + ChatColor.BLUE +
                                " extra heart(s)").create();
            }
        };
        extraDamage = new DynamicIntOption(this, 11, 20, 29, settings.extraDamage) {
            @Override
            public ItemStack refreshItem(int value) {
                builder.setExtraDamage(value);
                return ItemFactory.create(Material.GOLDEN_SWORD).setName(ChatColor.GOLD + "Extra damage " + ChatColor.GREEN + "(+ Strength " + value +")")
                        .emptyLoreLine().addLoreLine(ChatColor.BLUE + "All runners will get").addLoreLine(ChatColor.BLUE + "strength " +
                                ChatColor.GREEN + value).create();
            }
        };
        speedboostAmp = new DynamicIntOption(this, 12, 21, 30, settings.speedboost) {
            @Override
            public ItemStack refreshItem(int value) {
                builder.setSpeedBoost(value);
                return ItemFactory.create(Material.SUGAR).setName(ChatColor.GOLD + "Extra speed " + ChatColor.GREEN + "(+ Speed " + value +")")
                        .emptyLoreLine().addLoreLine(ChatColor.BLUE + "All runners will get").addLoreLine(ChatColor.BLUE + "speed " +
                                ChatColor.GREEN + value).create();
            }
        };
        speedboostTime = new DynamicIntOption(this, 13, 22, 31, settings.speedboostDuration) {
            @Override
            public ItemStack refreshItem(int value) {
                builder.setSpeedBoostDuration(value);
                return ItemFactory.create(Material.CLOCK).setName(ChatColor.GOLD + "Speedboost time " + ChatColor.GREEN + "(" + value + "s)")
                        .emptyLoreLine().addLoreLine(ChatColor.BLUE + "The speed duration is").addLoreLine(ChatColor.BLUE + "set to " +
                                ChatColor.GREEN + value + " seconds").create();
            }
        };
        apr = new DynamicIntOption(this, 14, 23, 32, settings.alertProximityRadius) {
            @Override
            public ItemStack refreshItem(int value) {
                builder.setAlertProximityRadius(value);
                return ItemFactory.create(Material.BELL).setName(ChatColor.GOLD + "Alert proximity radius " + ChatColor.GREEN + "(" + value + "" +
                        " blocks)").emptyLoreLine().addLoreLine(ChatColor.BLUE + "Runners will get alerted").addLoreLine(ChatColor.BLUE + "if a " +
                        "hunter is " + ChatColor.GREEN + value + ChatColor.BLUE + " blocks away").create();
            }
        };
        autoSmelt = new DynamicIntOption(this,15, 24, 33, settings.autoSmeltProbability) {
            @Override
            public ItemStack refreshItem(int value) {
                builder.setAutoSmeltProbability(value);
                return ItemFactory.create(Material.FURNACE).setName(ChatColor.GOLD + "Auto smelt probability " + ChatColor.GREEN + "(" + value + "%)")
                        .emptyLoreLine().addLoreLine(ChatColor.BLUE + "There is a " + ChatColor.GREEN + value + "%" + ChatColor.BLUE + " probability")
                        .addLoreLine(ChatColor.BLUE + "that an ore gets smelted").addLoreLine(ChatColor.BLUE + "while mining").create();
            }
        };
        kit = new DynamicOption(this, 25) {
            @Override
            public ItemStack refreshItem() {
                String name = (selected == null) ? ChatColor.RED + "none" : ChatColor.GREEN + selected.getName();
                return ItemFactory.create(Material.GOLDEN_SWORD)
                        .setName(ChatColor.GOLD + "Select kit")
                        .emptyLoreLine().addLoreLine(ChatColor.BLUE + "Selected kit - " + name)
                        .create();
            }
        };
        kit.getButton().getOnClick().clear();
        kit.addAction(event -> selector.display(event.getWhoClicked()));
    }
    
}
