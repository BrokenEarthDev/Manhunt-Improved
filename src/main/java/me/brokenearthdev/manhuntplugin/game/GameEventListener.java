package me.brokenearthdev.manhuntplugin.game;

import me.brokenearthdev.manhuntplugin.core.GameMessage;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.events.GameEndEvent;
import me.brokenearthdev.manhuntplugin.events.GamePlayerDamageByPlayerEvent;
import me.brokenearthdev.manhuntplugin.events.GamePlayerDeathEvent;
import me.brokenearthdev.manhuntplugin.events.GamePlayerHomicideEvent;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import me.brokenearthdev.manhuntplugin.game.players.Speedrunner;
import me.brokenearthdev.manhuntplugin.game.players.Team;
import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.stats.CachedPlayerProfile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.*;

@AutoRegisterCommand
public class GameEventListener implements Listener {

    @EventHandler
    public void newDeathMessage(GamePlayerDeathEvent event) {
        PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent) event.getFiredBukkitEvent();
        String name = (event.getDead() instanceof Speedrunner ? ChatColor.RED : ChatColor.AQUA) + event.getDead().getPlayer().getName();
        if (event instanceof GamePlayerHomicideEvent) {
            GamePlayerHomicideEvent homicideEvent = (GamePlayerHomicideEvent) event;
            String killer = ((homicideEvent.getKiller() instanceof Speedrunner) ? ChatColor.RED : ChatColor.AQUA) + homicideEvent.getKiller().getPlayer().getName();
            playerDeathEvent.setDeathMessage(name + ChatColor.YELLOW + " was killed by " + killer);
        } else {
            if (playerDeathEvent.getDeathMessage() == null) {
                playerDeathEvent.setDeathMessage(name + ChatColor.YELLOW + " died");
                return;
            }
            playerDeathEvent.setDeathMessage(playerDeathEvent.getDeathMessage().replace(event.getDead().getPlayer().getDisplayName(), name + ChatColor.YELLOW.toString()));
        }
    }
    
    @EventHandler
    public void recordKill(GamePlayerDeathEvent event) {
        if (event.getGame().getGameSettings().recordStats) {
            GamePlayer dead = event.getDead();
            CachedPlayerProfile profile = findPlayerProfile(dead.getPlayer());
            profile.setDeaths(profile.getDeaths() + 1);
            
            if (event instanceof GamePlayerHomicideEvent) {
                GamePlayer killer = ((GamePlayerHomicideEvent) event).getKiller();
                CachedPlayerProfile killerProfile = findPlayerProfile(killer.getPlayer());
                killerProfile.setKills(killerProfile.getKills() + 1);
                event.getGame().setKills(event.getGame().getKills() + 1);
            }
        }
    }
    
    @EventHandler
    public void checkTeam(EntityDamageByEntityEvent event) {
        if (ManhuntGame.getManhuntGame() != null) {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                GamePlayer player = currentGame().getPlayer((Player) event.getEntity());
                GamePlayer damager = currentGame().getPlayer((Player) event.getDamager());
                if (player != null && damager != null && player.getType().equals(damager.getType())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void spawnChest(GamePlayerDeathEvent event) {
        Player dead = event.getDead().getPlayer();
        PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent) event.getFiredBukkitEvent();
        event.getGame().registerDeathAt(dead, playerDeathEvent.getDrops());
        playerDeathEvent.getDrops().clear();
        event.getGame().getAllPlayers().forEach(e -> {
            if (!e.getWorld().equals(dead.getLocation().getWorld())) {
                // announce world
                Message.INFO("A player died at " + userFriendlyLoc(dead.getLocation(), true)).send(e);
            } else Message.INFO("A player died at " + userFriendlyLoc(dead.getLocation(), false)).send(e);
        });
    
    }
    
    @EventHandler
    public void onEnd(GameEndEvent event) {
        if (event.getLoserTeam() != null && event.getWinnerTeam() != null && event.getGame().getGameSettings().recordStats) {
            // record profile
            Set<OfflinePlayer> winners = new HashSet<>();
            Set<OfflinePlayer> losers = new HashSet<>();
            if (event.getWinnerTeam().equals(Team.SPEEDRUNNER)) {
                event.getGame().getRunners().forEach(speedrunner -> winners.add(speedrunner.getPlayer()));
                event.getGame().getHunters().forEach(hunter -> losers.add(hunter.getPlayer()));
            } else {
                event.getGame().getHunters().forEach(hunter -> winners.add(hunter.getPlayer()));
                event.getGame().getRunners().forEach(speedrunner -> losers.add(speedrunner.getPlayer()));
            }
            winners.forEach(winner -> {
                CachedPlayerProfile profile = findPlayerProfile(winner);
                profile.setWins(profile.getWins() + 1);
            });
            losers.forEach(loser -> {
                CachedPlayerProfile profile = findPlayerProfile(loser);
                profile.setLosses(profile.getLosses() + 1);
            });
        }
        
    }
    @EventHandler
    public void onDragonKill(EntityDeathEvent event) {
        if (currentGame() != null && event.getEntityType() == EntityType.ENDER_DRAGON) {
            // dragon killed
            currentGame().setWinner(Team.SPEEDRUNNER);
            currentGame().stopGame();
        }
    }
    
    @EventHandler
    public void onRunnerDeath(GamePlayerDeathEvent event) {
        if (event.getDead() instanceof Speedrunner) {
            event.getGame().setLostSpeedrunner((Speedrunner) event.getDead());
            if (event.getGame().getRemainingRunners() == 0) {
                // all dead
                event.getGame().setWinner(Team.HUNTER);
                event.getGame().stopGame();
            }
            event.getGame().gameLogger.low("Runner death at " + event.getDead().getPlayer().getLocation());
        }
    }
    
    @EventHandler
    public void onHunterDeath(GamePlayerDeathEvent event) {
        if (event.getDead() instanceof Hunter) {
            event.getGame().gameLogger.low("Hunter death at " + event.getDead().getPlayer().getLocation());
        }
    }
    
    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        if (currentGame() != null && currentGame().isPlayer(event.getPlayer())) {
            currentGame().gameLogger.low("Spawn by " + event.getPlayer() + " at " + event.getPlayer().getLocation());
        }
    }
    
    @EventHandler
    public void enforceGracePeriodProtection(GamePlayerDamageByPlayerEvent event) {
        if (event.getGame().duringGracePeriod())
            ((EntityDamageByEntityEvent) event.getFiredBukkitEvent()).setCancelled(true);
    }
    
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && currentGame() != null && currentGame().isPlayer(event.getPlayer())
            && event.getClickedBlock() != null && currentGame().getSpawnedChests().containsKey(event.getClickedBlock())) {
            Inventory inventory = currentGame().getSpawnedChests().get(event.getClickedBlock());
            if (inventory != null) {
                // then the player tried to open the chest
                event.setCancelled(true);
                event.getPlayer().closeInventory();
                event.getPlayer().openInventory(inventory);
            }
            
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && currentGame() != null && event.getClickedBlock() != null &&
                currentGame().getSpawnedChests().containsKey(event.getClickedBlock())) {
            Inventory inventory = currentGame().getSpawnedChests().get(event.getClickedBlock());
            if (inventory != null) {
                event.setCancelled(true);
                Message.ERROR_PREFIX("You are not participating in a game and hence you can't access this chest!").send(event.getPlayer());
            }
        }
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (currentGame() != null && currentGame().getSpawnedChests().containsKey(event.getBlock()) && currentGame().isPlayer(event.getPlayer())) {
            Inventory inventory = currentGame().getSpawnedChests().get(event.getBlock());
            if (inventory != null) {
                // then the player broke that block
                event.setDropItems(false);
                ItemStack[] items = inventory.getContents();
                Location dropLoc = event.getBlock().getLocation();
                for (ItemStack stack : items) {
                    event.getBlock().getWorld().dropItemNaturally(dropLoc, stack);
                }
                currentGame().getSpawnedChests().remove(event.getBlock());
            }
        } else if (currentGame() != null && currentGame().getSpawnedChests().containsKey(event.getBlock())) {
            Inventory inventory = currentGame().getSpawnedChests().get(event.getBlock());
            if (inventory != null) {
                event.setCancelled(true);
                Message.ERROR_PREFIX("You are not participating in a game and hence you can't break this chest!");
            }
        }
    }
    
    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if (currentGame() != null && currentGame().getSpawnedChests().containsKey(event.getBlock())) {
            // Stop the block from blowing up
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBurn(BlockBurnEvent event) {
        if (currentGame() != null && currentGame().getSpawnedChests().containsKey(event.getBlock()))
            // Stop the block from burning
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPush(BlockPistonExtendEvent event) {
        List<Block> blocks = event.getBlocks();
        if (currentGame() != null && containsChest(blocks))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onRetract(BlockPistonRetractEvent event) {
        List<Block> blocks = event.getBlocks();
        if (currentGame() != null && containsChest(blocks))
            event.setCancelled(true);
    }
    
    private boolean containsChest(List<Block> blocks) {
        for (Block block : currentGame().getSpawnedChests().keySet()) {
            if (blocks.contains(block))
                return true;
        }
        return false;
    }
    
    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (currentGame() != null && currentGame().isPlayer(event.getPlayer())) {
            currentGame().sendMessage(GameMessage.GOOD(event.getPlayer() + " teleported from a portal at " + userFriendlyLoc(event.getFrom(), true)));
        }
    }
    
}
