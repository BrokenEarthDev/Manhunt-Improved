package me.brokenearthdev.manhuntplugin.tracker;

import me.brokenearthdev.manhuntplugin.GameItems;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import me.brokenearthdev.manhuntplugin.game.players.Speedrunner;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Represents a tracker
 */
public abstract class Tracker {
    
    private static final Set<Tracker> registeredTrackers = new HashSet<>();
    
    private final TrackerType type;
    private final ItemStack copy;
    
    protected Player hunter;
    protected Player tracked;
    
    public Tracker(Player hunter, TrackerType type) {
        Objects.requireNonNull(type);
        this.type = type;
        this.hunter = hunter;
        if (type == TrackerType.SIMPLISTIC) {
            copy = GameItems.TrackerItems.SIMPLE.clone();
        } else if (type == TrackerType.SIMPLE_W_COORDINATES) {
            copy = GameItems.TrackerItems.SIMPLE_W_DIST.clone();
        } else {
            copy = GameItems.TrackerItems.ADVANCED.clone();
        }
        registeredTrackers.add(this);
    }
   
    
    /**
     * Gives a tracker to the hunter
     *
     * @return Whether or not the tracker has been added to the hunter
     */
    public boolean giveTracker() {
        Inventory inv = hunter.getInventory();
        return !inv.addItem(copy).containsValue(copy);
    }
    
    
    /**
     * Sets the tracked player
     *
     * @param speedrunner The runner
     */
    public void updateTracker(Speedrunner speedrunner) {
        // todo add checks if the target is in the
        // todo nether
        hunter.setCompassTarget(speedrunner.getPlayer().getLocation());
        tracked = speedrunner.getPlayer();
    }
    
    /**
     * Removes the tracker from the hunter
     *
     * @return Whether or not the tracker was removed from the hunter
     */
    public boolean removeTracker() {
        Inventory inv = hunter.getInventory();
        return inv.removeItem(copy).containsValue(copy);
    }
    
    /**
     * @return The tracker item
     */
    public ItemStack getTrackerItem() {
        return copy;
    }
    
    /**
     * @return The tracker type
     */
    public TrackerType getType() {
        return type;
    }
    
    public boolean hasTracker() {
        return hunter.getPlayer().getInventory().contains(copy);
    }
    
     private static Tracker getTrackerFromItem(Hunter hunter, ItemStack item) {
        for (Tracker tracker : registeredTrackers) {
            if (tracker.hunter.equals(hunter.getPlayer()) && tracker.copy.equals(item)) {
                return tracker;
            }
        }
        return null;
    }
    
    /**
     * Creates a new tracker of a given type for a certain
     * player
     *
     * @param type The tracker type
     * @param player The player
     * @return A new tracker object
     */
    public static Tracker newTracker(TrackerType type, Player player) {
        switch (type) {
            case SIMPLISTIC: return new Tracker1(player);
            case SIMPLE_W_COORDINATES: return new Tracker2(player);
            case COMPLEX: return new Tracker3(player);
        }
        return null;
    }
    
    /**
     * @return The hunter (the person who has this compass)
     */
    public Player getTracker() {
        return hunter;
    }
    
    /**
     * @return The tracked player
     */
    public Player getTracked() {
        return tracked;
    }
    
    /**
     * Triggers whenever a registered {@link Hunter} interacts
     * with the tracker, triggered by Bukkit's {@link org.bukkit.event.player.PlayerInteractEvent}
     *
     * @param hunter The hunter who interacted with the tracker
     */
    public abstract void trackerInteract(Hunter hunter);
    
    @AutoRegisterCommand
    public static class EventListener implements Listener {
    
        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (ManhuntGame.getManhuntGame() != null && !ManhuntGame.getManhuntGame().duringGracePeriod()) {
                GamePlayer player = ManhuntGame.getManhuntGame().getPlayer(event.getPlayer());
                if (player != null) {
                    ManhuntGame.getManhuntGame().getHunters().forEach(h -> {
                        Tracker tracker = h.getTracker();
                        Speedrunner speedrunner = ManhuntGame.getManhuntGame().calcClosestRunner(h);
                        if (speedrunner != null) tracker.updateTracker(speedrunner);
                        ManhuntGame.getManhuntGame().gameLogger.high("Tracker updated for " + player.getPlayer().getName()
                            + " in " + tracker.getClass());
                    });
                }
            }
        }
        
        @EventHandler
        public void onTrackerInteract(PlayerInteractEvent event) {
            ItemStack item = event.getItem();
            if (ManhuntGame.getManhuntGame() != null && item != null) {
                Hunter hunter = ManhuntGame.getManhuntGame().getHunter(event.getPlayer());
                if (hunter == null)
                    return;
                Tracker tracker = getTrackerFromItem(hunter, item);
                if (tracker != null) {
                    tracker.trackerInteract(hunter);
                    ManhuntGame.getManhuntGame().gameLogger.high("Tracker " + tracker.getClass() + " was interacted by "
                        + hunter.getPlayer().getName());
                    event.setCancelled(true);
                }
            }
        }
        
        @EventHandler
        public void onThrow(PlayerDropItemEvent event) {
            ItemStack item = event.getItemDrop().getItemStack();
            if (ManhuntGame.getManhuntGame() != null) {
                Hunter hunter = ManhuntGame.getManhuntGame().getHunter(event.getPlayer());
                if (hunter == null)
                    return;
                if (getTrackerFromItem(hunter, item) != null) {
                    event.setCancelled(true);
                }
            }
        }
    
        @EventHandler(priority = EventPriority.LOWEST)
        // Allow it to run first to remove
        // trackers before the drops move
        // to the chest
        public void onDeath(PlayerDeathEvent event) {
            if (ManhuntGame.getManhuntGame() != null) {
                Hunter hunter = ManhuntGame.getManhuntGame().getHunter(event.getEntity());
                if (hunter != null) {
                    Tracker tracker = hunter.getTracker();
                    event.getDrops().remove(tracker.copy);
                }
            }
        }
    
        @EventHandler
        public void onSpawn(PlayerRespawnEvent event) {
            if (ManhuntGame.getManhuntGame() != null && !ManhuntGame.getManhuntGame().duringGracePeriod()) {
                Hunter h = ManhuntGame.getManhuntGame().getHunter(event.getPlayer());
                boolean received = h.getTracker().giveTracker();
                if (!received) {
                    Message.ERROR("It appears that you haven't received your tracker. Please clear your " +
                            "inventory and then use /mhtracker").send(h.getPlayer());
                } else Message.GOOD("You regained your tracker").send(h.getPlayer());
            }
        }
        
    }
    
}
