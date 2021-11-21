package me.brokenearthdev.manhuntplugin.events;

import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.currentGame;

@AutoRegisterCommand
public class GameEventTriggerer implements Listener {
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        ManhuntGame game = ManhuntGame.getManhuntGame();
        if (game != null && game.isParticipant(event.getEntity())) {
            GamePlayer player = game.getPlayer(event.getEntity());
            if (event.getEntity().getKiller() != null) {
                GamePlayer killer = game.getPlayer(event.getEntity().getKiller());
                if (killer != null) {
                    Bukkit.getPluginManager().callEvent(new GamePlayerHomicideEvent(game, event, player, killer));
                    return;
                }
            }
            Bukkit.getPluginManager().callEvent(new GamePlayerDeathEvent(game, event, player));
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (currentGame() == null) return;
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            if (currentGame().isPlayer(victim) && currentGame().isPlayer(damager))
                Bukkit.getPluginManager().callEvent(new GamePlayerDamageByPlayerEvent(currentGame(),
                        currentGame().getPlayer(damager), currentGame().getPlayer(victim),
                        event));
        }
    }
    
}
