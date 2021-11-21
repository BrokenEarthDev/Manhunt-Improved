package me.brokenearthdev.manhuntplugin;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.LoreCreator;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.NumberDecreaseButton;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.NumberIncreaseButton;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.ListPaginatedMenu;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.OfflineGamePlayer;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.stats.CachedPlayerProfile;
import me.brokenearthdev.manhuntplugin.stats.GameStats;
import me.brokenearthdev.manhuntplugin.stats.PlayerProfileGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;

public class Utils {
    
    public static String twoDigits(double d) {
        DecimalFormat format = new DecimalFormat("#.00");
        return format.format(d);
    }
    
    public static void gameStatsPlayerMenu(String name, List<OfflineGamePlayer> players, HumanEntity entity, GameMenu fallback) {
        ListPaginatedMenu<OfflineGamePlayer> menu = new ListPaginatedMenu<>(name, players, (p -> createPlayerHead(p.getOfflinePlayer(),
                (p.isHunter() ? ChatColor.AQUA : ChatColor.RED) + p.getOfflinePlayer().getName())));
        menu.setReturntoGui(fallback);
        menu.addOnItemClick((player, event) -> {
            CachedPlayerProfile profile = Manhunt.getInstance().getPlayerProfileFor(player.getOfflinePlayer());
            new PlayerProfileGUI(profile).display(entity);
        });
        menu.display(entity);
    }
    
    public static List<String> createListLoreLine(String preceding, String ...elements) {
        List<String> str = new LinkedList<>();
        str.add(preceding);
        for (String e : elements) {
            str.add("- " + e);
        }
        return str;
    }

    public static ItemStack increaseButton(int i) {
        i = Math.max(i, 1);
        return ItemFactory.create(Material.GREEN_STAINED_GLASS_PANE).setName(ChatColor.GREEN + "Increase option")
                .emptyLoreLine().addLoreLine(ChatColor.YELLOW + "LEFT CLICK to increase by 1")
                .addLoreLine(ChatColor.YELLOW + "RIGHT CLICK to increase by 5").setAmount(i).create();
    }
    
    public static ItemStack decreaseButton(int i) {
        i = Math.max(i, 1);
        return ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(ChatColor.RED + "Decrease option")
                .emptyLoreLine().addLoreLine(ChatColor.YELLOW + "LEFT CLICK to decrease by 1")
                .addLoreLine(ChatColor.YELLOW + "RIGHT CLICK to decrease by 5").setAmount(i).create();
    }
    
    public static ItemStack kitRepresentation(Kit kit) {
        String[] elements = new String[kit.getItems().size()];
        for (int i = 0; i < kit.getContainsGUIFriendly().size(); i++) {
            elements[i] = ChatColor.BLUE + kit.getContainsGUIFriendly().get(i);
        }
        return ItemFactory.create(kit.getSymbolizer()).setName(ChatColor.GREEN + kit.getName()).emptyLoreLine()
                .addLoreLines(createListLoreLine(ChatColor.YELLOW + "Contains:", elements)).create();
    }
    
    
    public static ItemStack createPlayerHead(OfflinePlayer owningPlayer, String name) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setOwningPlayer(owningPlayer);
        stack.setItemMeta(meta);
        return stack;
    }
    public static String potentialHunterNames(ManhuntGame game, Player compassOwner) {
        List<Player> hun = new ArrayList<>();
        game.getHunters().forEach(h -> hun.add(h.getPlayer()));
        if (hun.size() == 1) return "none";
        StringBuilder builder = new StringBuilder();
        for (Player player : hun) {
            if (!player.equals(compassOwner))
                builder.append(player.getName()).append(", ");
        }
        String str = builder.toString();
        if (str.length() - 2 < 0) return str;
        String toReturn = str.substring(0, str.length() - 2);
        if (toReturn.length() > 21) toReturn = toReturn.substring(0, 17) + "...";
        return toReturn;
    }
    
    
}
