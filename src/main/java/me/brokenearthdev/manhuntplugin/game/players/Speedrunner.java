package me.brokenearthdev.manhuntplugin.game.players;

import me.brokenearthdev.manhuntplugin.kits.Kit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Represents a speedrunner in a game
 */
public class Speedrunner implements GamePlayer {
    
    public final SpeedrunnerSettings settings;
    
    private final Player player;
    private final Kit kit;
    
    public Speedrunner(SpeedrunnerSettings settings, Player player, Kit kit) {
        this.settings = settings;
        this.player = player;
        this.kit = kit;
    }
    
    public Speedrunner(SpeedrunnerSettings srs, Player player) {
        this(srs, player, null);
    }
    
    public void giveKit() {
        if (kit == null) return;
        kit.getItems().forEach(i -> player.getInventory().addItem(i));
    }
    
    public void giveBenefits() {
        System.out.println(settings.speedboost);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, settings.speedboostDuration * 20,
                settings.speedboost, false, false));
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0 + (settings.extraHearts * 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
                Integer.MAX_VALUE, settings.extraDamage, false, false));
    }
    
    public Player getPlayer() {
        return player;
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