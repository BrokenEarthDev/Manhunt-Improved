package me.brokenearthdev.manhuntplugin;

import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class EntityEventListeners implements Listener {
    
    public static final SpeedrunnerListener speedrunner = new SpeedrunnerListener();
    
    /**
     * Listener for speed runners
     */
    @AutoRegisterCommand
    public static class SpeedrunnerListener implements Listener {
    
        @EventHandler
        public void warnRadCheck(PlayerMoveEvent event) {
            ManhuntGame game = ManhuntGame.getManhuntGame();
            Player player = event.getPlayer();
            if (game != null && game.getRunnerSettings().alertProximityRadius > 0 && game.isParticipant(player)) {
                game.getAllPlayers().forEach(p -> {
                    if (game.isParticipant(p)) {
                        if (game.isRunner(player) && game.isHunter(p)) {
                            game.sendInRadiiMsg(game.getRunner(player), game.getHunter(p));
                        }
                        if (game.isRunner(p) && game.isHunter(player)) {
                            game.sendInRadiiMsg(game.getRunner(p), game.getHunter(player));
                        }
                    }
                });
            }
        }
    
        @EventHandler
        public void onMine(BlockBreakEvent event) {
            Player player = event.getPlayer();
            ManhuntGame game = ManhuntGame.getManhuntGame();
            Material smelted = findSmeltedOre(event.getBlock());
            if (game != null && game.isRunner(player) && game.isParticipant(player) && smelted != null) {
                int random = ThreadLocalRandom.current().nextInt(0, 100);
                if (random < game.getRunnerSettings().autoSmeltProbability) {
                    event.setDropItems(false);
                    Location loc = event.getBlock().getLocation();
                    event.getBlock().getWorld().dropItem(loc, new ItemStack(smelted));
                }
            }
        }
    
        private Material findSmeltedOre(Block block) {
            Material mat = null;
            switch (block.getBlockData().getMaterial()) {
                case IRON_ORE:
                    mat = Material.IRON_INGOT;
                    break;
                case GOLD_ORE:
                    mat = Material.GOLD_INGOT;
                    break;
            }
            return mat;
        }
    
        
    }
    
}
