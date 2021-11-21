package me.brokenearthdev.manhuntplugin.tracker;

import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.gui.menu.ListPaginatedMenu;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import me.brokenearthdev.manhuntplugin.game.players.Speedrunner;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.*;

public final class Tracker3 extends Tracker {
    
    private final CompassTrackingMenu menu;
    
    public Tracker3(Player player) {
        super(player, TrackerType.COMPLEX);
        menu = new CompassTrackingMenu(this);
    }
    
    boolean distanceOrLoc = true;
    boolean actionbar = true;
    boolean trackingType = true;
    boolean trackSpawn = false;
    
    private boolean init = false;
    /**
     * Sets the tracked player
     *
     * @param speedrunner The runner
     */
    @Override
    public void updateTracker(Speedrunner speedrunner) {
        super.updateTracker(speedrunner);
        World mainWorld = ManhuntGame.getManhuntGame().getMainWorld();
        Location or = (tracked == null) ? mainWorld.getSpawnLocation() : currentGame().calcTargetLoc(currentGame().getHunter(hunter), speedrunner);
        hunter.setCompassTarget(or);
        if (actionbar) {
            if (!trackingType && tracked != null) {
                hunter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                        ChatColor.GREEN + ChatColor.BOLD.toString() + "Player Tracker" + ChatColor.GOLD + ChatColor.BOLD.toString()
                                + " | " + ChatColor.GREEN + ChatColor.BOLD.toString() + tracked.getName() + ": " + ((int) tracked.getHealth())
                ));
                return;
            }
            int x = (int) or.getX();
            int y = (int) or.getY();
            int z = (int) or.getZ();
            String space = worldEqual(or.getWorld(), hunter.getWorld()) ? "     " : "   ";
            String end = worldEqual(or.getWorld(), hunter.getWorld()) ? "" : space + ChatColor.AQUA + ChatColor.BOLD + "World: " + or.getWorld().getName();
            if (distanceOrLoc){
                x = (int) (or.getX() - hunter.getLocation().getX());
                y = (int) (or.getY() - hunter.getLocation().getY());
                z = (int) (or.getZ() - hunter.getLocation().getZ());
            }
            hunter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    ChatColor.GREEN + ChatColor.BOLD.toString() + "Player Tracker" + ChatColor.GOLD +
                            ChatColor.BOLD.toString() + " | " + ChatColor.RED + ChatColor.BOLD.toString()
                            + "X: " + x + space + ChatColor.BLUE + ChatColor.BOLD.toString() + "Y: " + y + space + ChatColor.GREEN
                            + ChatColor.BOLD + "Z: " + z + end
            ));
        }
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
        if (!init) {
            init = true;
            menu.setupMenu();
        }
        menu.display(hunter.getPlayer());
        ManhuntGame.getManhuntGame().gameLogger.medium("Tracker gui opened for " + hunter + " in " + Tracker3.class);
    }
    
    public void openHuntersInterface() {
        ArrayList<Hunter> huntersCopy = new ArrayList<>(ManhuntGame.getManhuntGame().getHunters());
        huntersCopy.remove(ManhuntGame.getManhuntGame().getHunter(hunter));
        ListPaginatedMenu<Hunter> listPaginatedMenu = new ListPaginatedMenu<>("Hunters",
                huntersCopy, player -> createPlayerHead(player.getPlayer(), ChatColor.AQUA + player.getPlayer().getName()));
        listPaginatedMenu.addOnItemClick(((player, event) -> {
            tracked = player.getPlayer();
            hunter.playSound(hunter.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            hunter.sendMessage(ChatColor.AQUA + player.getPlayer().getName() + ChatColor.GREEN + " is now tracked!");
        }));
        listPaginatedMenu.setReturntoGui(menu);
        listPaginatedMenu.display(hunter);
    }
    
    public void openRunnersInterface() {
        ArrayList<Speedrunner> runnersCopy = new ArrayList<>(ManhuntGame.getManhuntGame().getRunners());
        ListPaginatedMenu<Speedrunner> listPaginatedMenu = new ListPaginatedMenu<>("Speedrunners",
                runnersCopy, player -> createPlayerHead(player.getPlayer(), ChatColor.RED + player.getPlayer().getName()));
        listPaginatedMenu.addOnItemClick((player, event) -> {
            if (tracked.equals(player.getPlayer())) {
                Message.ERROR("This player is already tracked").send(hunter);
                return;
            }
            tracked = player.getPlayer();
            hunter.playSound(hunter.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            hunter.sendMessage(ChatColor.RED + player.getPlayer().getName() + ChatColor.GREEN + " is now tracked!");
        });
        listPaginatedMenu.setReturntoGui(menu);
        listPaginatedMenu.display(hunter);
    }
    
    public void openTrackersInterface() {
        List<Tracker> hunterTrackers = new ArrayList<>(ManhuntGame.getManhuntGame().getTrackers());
        hunterTrackers.remove(this);
        List<Player> trackers = new ArrayList<>();
        hunterTrackers.forEach(h -> {
            if (h.tracked.equals(hunter)) trackers.add(h.hunter);
        });
        ListPaginatedMenu<Player> menu = new ListPaginatedMenu<>("Your Trackers",
                trackers, (player) -> createPlayerHead(player, ChatColor.AQUA + player.getName()));
        menu.setReturntoGui(this.menu);
        menu.display(hunter);
    }
    
}
