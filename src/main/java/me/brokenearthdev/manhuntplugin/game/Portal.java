package me.brokenearthdev.manhuntplugin.game;

import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.currentGame;
import static me.brokenearthdev.manhuntplugin.utils.StaticImports.worldEqual;

/**
 * Represents a portal a player used to travel through different
 * worlds
 * <p>
 * It is of paramount importance to ensure that the from location is
 * from the main world. If, for instance, a player built a portal to the
 * main world in the nether, the to location should be switched with the
 * from location, and the travelled player should only include players
 * from the nether (or those who have travelled to the nether).
 */
public class Portal {
    
    // The players who travelled through the portal
    private final Set<GamePlayer> travelled = new HashSet<>();
    
    // To and from locations
    private final Location to, from;
    private final World.Environment toEnv;
    
    public Portal(Location to, Location from) {
        if (!from.getWorld().equals(World.Environment.NORMAL))
            throw new IllegalArgumentException(from.getWorld() + " should be " + World.Environment.NORMAL);
        this.to = to;
        this.from = from;
        this.toEnv = to.getWorld().getEnvironment();
    }
    
    /**
     * Measures the distance between a {@link GamePlayer} to a
     * portal.
     *
     * @param from The {@link GamePlayer}
     * @return The distance, which can be {@link Double#NaN} if the
     * player is not in the same world in either of the portal's location.
     */
    public double measurePortalDistance(GamePlayer from) {
        if (from.getPlayer().getWorld().equals(this.from.getWorld()))
            return this.from.distance(from.getPlayer().getLocation());
        else if (from.getPlayer().getWorld().equals(this.to.getWorld()))
            return this.to.distance(from.getPlayer().getLocation());
        else return Double.NaN;
    }
    
    /**
     * @return The players who have travelled through the portal
     */
    public Set<GamePlayer> getTravelled() {
        return travelled;
    }
    
    public Location getFrom() {
        return from;
    }
    
    public Location getTo() {
        return to;
    }
    
    public World.Environment getToEnv() {
        return toEnv;
    }
    
    public boolean equals(Object o2) {
        if (o2 instanceof Portal) {
            Portal p2 = (Portal) o2;
            return p2.from.equals(from) && p2.to.equals(to);
        }
        return false;
    }
    
    @AutoRegisterCommand
    public static class PortalEventListener implements Listener {
        
        // PlayerMoveEvent will be used instead
        // because PlayerPortalEvent appears to be
        // unreliable
        @EventHandler
        public void onPortal(PlayerMoveEvent event) {
            System.out.println("TYYYYYYYYYYYYYYYY");
            if (event.getTo() == null || event.getTo().getWorld() == null || event.getFrom().getWorld() == null)
                return;
            System.out.println("XXXXXXXXXXX");
            if (!worldEqual(event.getTo().getWorld(), event.getFrom().getWorld())) {
                System.out.println("Event triggered");
                if (currentGame() != null && currentGame().isPlayer(event.getPlayer())) {
                    System.out.println("passed check");
                    GamePlayer player = currentGame().getPlayer(event.getPlayer());
                    boolean switchLocations = event.getTo().getWorld().getEnvironment().equals(World.Environment.NORMAL);
                    Location from = switchLocations ? event.getTo() : event.getFrom();
                    Location to = switchLocations ? event.getFrom() : event.getTo();
                    Portal portal = currentGame().getPortal(from, to);
                    if (portal == null) {
                        System.out.println("portal is null");
                        portal = new Portal(from, to);
                        currentGame().recordPortal(portal);
                    }
                    if (!switchLocations)
                        // travelled through
                        portal.getTravelled().add(player);
                    else portal.getTravelled().remove(player);
                }
            }
        }
        
        @EventHandler
        public void on(PlayerMoveEvent e) {
            System.out.println("XXX");
        }
        
    }
    
}
