package me.brokenearthdev.manhuntplugin.tracker;

import me.brokenearthdev.manhuntplugin.GameItems;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import me.brokenearthdev.manhuntplugin.game.players.Speedrunner;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.currentGame;

/**
 * Represents the simplistic tracker (with special features)
 * <p>
 * The distance/location of the hunter from the runner will be
 * displayed on the actionbar. Whether the distance or location
 * will be selected will be entirely dependent on the {@link Hunter}'s
 * choice.
 */
public final class Tracker2 extends Tracker {
    
    private boolean distanceOrCoords = true;
    
    public Tracker2(Player player) {
        super(player, TrackerType.SIMPLE_W_COORDINATES);
        distance = new Button(11, distanceOrCoords? selectedItem(GameItems.Tracker2ItemsGUI.DISTANCE) : GameItems.Tracker2ItemsGUI.DISTANCE);
        coords = new Button(15, !distanceOrCoords? selectedItem(GameItems.Tracker2ItemsGUI.COORDS) : GameItems.Tracker2ItemsGUI.COORDS);
        menu = new GameMenu("Select tracking information", 3);
        distance.addAction(a -> {
            distanceOrCoords = true;
            distance.setItem(selectedItem(GameItems.Tracker2ItemsGUI.DISTANCE));
            coords.setItem(GameItems.Tracker2ItemsGUI.COORDS);
            menu.display(player);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
        });
        coords.addAction(a -> {
            distanceOrCoords = false;
            distance.setItem(GameItems.Tracker2ItemsGUI.DISTANCE);
            coords.setItem(selectedItem(GameItems.Tracker2ItemsGUI.COORDS));
            menu.display(player);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
        });
        menu.setButton(distance).setButton(coords);
    }
    
    /**
     * Triggers whenever a registered {@link Hunter} interacts
     * with the tracker, triggered by Bukkit's {@link InventoryInteractEvent}
     * and {@link PlayerInteractEvent}
     *
     * @param hunter The hunter who interacted with the tracker
     */
    @Override
    public void trackerInteract(Hunter hunter) {
        menu.display(hunter.getPlayer());
        ManhuntGame.getManhuntGame().gameLogger.medium("Tracker gui opened for " + hunter + " in " + Tracker2.class);
    }
    
    /**
     * Sets the tracked player
     *
     * @param speedrunner The runner
     */
    @Override
    public void updateTracker(Speedrunner speedrunner) {
        super.updateTracker(speedrunner);
        if (!hasTracker())
            return;
        String message;
        if (distanceOrCoords) {
            // distance will be tracked
            // todo add the ability to find distance to N.P.
//            int distance = (int) speedrunner.getPlayer().getLocation().distance(hunter.getPlayer().getLocation());
            int distance = (int) currentGame().calcDistance(currentGame().getHunter(hunter), speedrunner);
            message = ChatColor.GOLD + ChatColor.BOLD.toString() + "Closest runner is " + ChatColor.RED + ChatColor.BOLD.toString() +
                    distance + ChatColor.GOLD + ChatColor.BOLD.toString() + "m away";
        } else {
            Location target = currentGame().calcTargetLoc(currentGame().getHunter(hunter), speedrunner);
            // coordinates will be tracked
            int x = (int) target.getX();
            int y = (int) target.getY();
            int z = (int) target.getZ();
            message = ChatColor.RED + ChatColor.BOLD.toString() + "X: " + x + ChatColor.GREEN +
                      ChatColor.BOLD.toString() + "   Y: " + y + ChatColor.BLUE +
                      ChatColor.BOLD.toString() + "   Z: " + z;
        }
        hunter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
    
    private final GameMenu menu;
    private final Button distance;
    private final Button coords;
    
    private ItemStack selectedItem(ItemStack original) {
        ItemStack clone = original.clone();
        ItemMeta meta = clone.getItemMeta();
        meta.setDisplayName(meta.getDisplayName() + ChatColor.GREEN + " (selected)");
        meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        clone.setItemMeta(meta);
        return clone;
    }
    
}
