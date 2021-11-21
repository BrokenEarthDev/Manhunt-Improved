package me.brokenearthdev.manhuntplugin.admin;

import me.brokenearthdev.manhuntplugin.core.RepeatedRunnable;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Allows player admins to control the ongoing
 * {@link me.brokenearthdev.manhuntplugin.game.ManhuntGame}
 */
public class AdminTools {
    
    public void viewInventory(Player admin, GamePlayer player) {
        Inventory inv = player.getPlayer().getInventory();
        player.getPlayer().updateInventory();
        admin.openInventory(inv);
    }
    
    public void giveKit(GamePlayer player, Kit kit) {
        kit.giveKit(player.getPlayer(), false);
    }
    
    public void disableRepeatedTask(RepeatedRunnable runnable) {
        runnable.setPaused(true);
    }
    
    public void enableRepeatedTasks(RepeatedRunnable runnable) {
        runnable.setPaused(false);
    }
    
}
