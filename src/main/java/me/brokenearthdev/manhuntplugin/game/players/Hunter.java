package me.brokenearthdev.manhuntplugin.game.players;

import me.brokenearthdev.manhuntplugin.tracker.Tracker;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import org.bukkit.entity.Player;

public class Hunter implements GamePlayer {
    
    private final Player player;
    private final Tracker tracker;
    private final Kit kit;
    
    public Hunter(Player player, Tracker tracker, Kit kit) {
        this.player = player;
        this.tracker = tracker;
        this.kit = kit;
    }
    
    public void giveKit() {
        if (kit != null) {
            kit.getItems().forEach(i -> player.getInventory().addItem(i));
        }
    }
 
    public Player getPlayer() {
        return player;
    }
    
    public Tracker getTracker() {
        return tracker;
    }
    
    @Override
    public boolean isParticipant() {
        return alive;
    }
    
    private boolean alive = true;
    
    @Override
    public void setParticipant(boolean participant) {
        this.alive = participant;
    }
    
}
