package me.brokenearthdev.manhuntplugin.tracker;

import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Represents the simplistic tracker.
 * <p>
 * In a simplistic tracker, the only information from the tracker
 * will be from the compass lodestone.
 */
public final class Tracker1 extends Tracker {
    
    public Tracker1(Player player) {
        super(player, TrackerType.SIMPLISTIC);
    }
    
    /**
     * Triggers whenever a registered {@link Hunter} interacts
     * with the tracker, triggered by Bukkit's {@link InventoryInteractEvent}
     * and {@link PlayerInteractEvent}
     *
     * @param hunter The hunter who interacted with the tracker
     */
    @Override
    public void trackerInteract(Hunter hunter) {}
}
