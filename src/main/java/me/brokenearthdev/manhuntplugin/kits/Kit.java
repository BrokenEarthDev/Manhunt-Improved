package me.brokenearthdev.manhuntplugin.kits;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class Kit {
    
    /**
     * Material that will be displayed in the kit GUI
     */
    protected Material symbolizer;
    protected String name;
    protected String displayName;
    
    // the kit items
    protected List<ItemStack> kitItems;
    
    // Kit trials
    protected Button cancelButton =
            new Button(45, ItemFactory.create(Material.RED_CONCRETE).setName(ChatColor.RED + "Back").create())
                    .addAction(e -> e.getWhoClicked().closeInventory());
    protected Button tryButton = new Button(53, ItemFactory.create(Material.GREEN_CONCRETE)
            .setName(ChatColor.GREEN + "Try kit").create()).addAction(e -> {
        e.getWhoClicked().closeInventory();tryKit((Player) e.getWhoClicked());
    });
    private static final List<Button> redGlass = Button.createButtons(ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create(),
            0, 9, 18, 27, 36);
    private static final List<Button> greenGlass = Button.createButtons(ItemFactory.create(Material.GREEN_STAINED_GLASS_PANE).setName(" ").create(),
            8, 17, 26, 35, 44);
    private final GameMenu trialMenu;
    
    protected List<String> containsName = new ArrayList<>();
    
    public Kit(Material symbolizer, String name, List<ItemStack> items) {
        this.symbolizer = symbolizer;
        this.name = name;
        this.kitItems = items;
        this.displayName = ChatColor.GREEN + name;
        this.trialMenu = constructTrialMenu();
        items.forEach(i -> containsName.add(i.getType().toString().toLowerCase().replace("_", " ")));
    }
    
    
    public Kit(String name, List<ItemStack> items) {
        this(Material.WOODEN_SWORD, name, items);
    }
    
    public List<String> getContainsGUIFriendly() {
        return new LinkedList<>(containsName);
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Constructs the entry for the kit
     *
     * @return The kit entry
     */
    public ItemStack constructKitEntry() {
        List<String> lores = Arrays.asList(ChatColor.BLUE + "Contains:");
        kitItems.forEach(item -> {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) lores.add(ChatColor.YELLOW + "- " + meta.getDisplayName());
            else lores.add(ChatColor.YELLOW + "- " + item.getType().toString().toLowerCase().replace("_", " "));
        });
        lores.add(" ");
        lores.add(ChatColor.BLUE + "LEFT CLICK to select");
        lores.add(ChatColor.BLUE + "RIGHT CLICK to try kit");
        return ItemFactory.create(symbolizer).setName(displayName).setLore(lores).create();
    }
    
    public GameMenu constructTrialMenu() {
        GameMenu menu = new GameMenu("Try \"" + displayName + "\"" + ChatColor.RESET, 6);
        menu.setButton(cancelButton);
        menu.setButton(tryButton);
        redGlass.forEach(menu::setButton);
        greenGlass.forEach(menu::setButton);
        int index = 0;
        for (int i = 0; i < 54; i++) {
            if ((i + 1) % 9 != 0 && (i) % 9 != 0) {
                // item not on edge
                int finalI = i;
                if (index >= getItems().size()) break;
                ItemStack k = getItems().get(index);
                menu.setButton(new Button(finalI, k).addAction(e -> e.getWhoClicked().getInventory().addItem(k)));
                index++;
            }
        }
        menu.setScenery(ItemFactory.create(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(" ").create());
        return menu;
    }
    
    public void displayTrialMenu(Player player) {
        trialMenu.display(player);
    }
    
    public void giveKit(Player player, boolean clear) {
        if (clear) player.getInventory().clear();
        kitItems.forEach(item -> player.getInventory().addItem(item));
    }
    
    public void tryKit(Player player) {
        Message.NORMAL_PREFIX(ChatColor.YELLOW + "Now trying " + displayName).send(player);
        giveKit(player, true);
    }
    
    public List<ItemStack> getItems() {
        return new ArrayList<>(kitItems);
    }
    
    public Material getSymbolizer() {
        return symbolizer;
    }
    
}
