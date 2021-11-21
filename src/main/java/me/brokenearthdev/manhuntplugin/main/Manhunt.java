package me.brokenearthdev.manhuntplugin.main;

import me.brokenearthdev.manhuntplugin.EntityEventListeners;
import me.brokenearthdev.manhuntplugin.core.TaskManager;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.events.GameEventTriggerer;
import me.brokenearthdev.manhuntplugin.game.GameEventListener;
import me.brokenearthdev.manhuntplugin.game.Portal;
import me.brokenearthdev.manhuntplugin.stats.CachedPlayerProfile;
import me.brokenearthdev.manhuntplugin.stats.GameStats;
import me.brokenearthdev.manhuntplugin.tracker.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.createPlayerDataMap;

public final class Manhunt extends JavaPlugin {

    private static Manhunt plugin;
    
    private ConfigurationManager configManager;
    private List<ManhuntCommand> commands;
    private TaskManager manager;
    
    @Override
    public void onEnable() {
        plugin = this;
        configManager = new ConfigurationManager();
        manager = new TaskManager();
        try {
            CommandRegistryManager.INST.registerAuto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        configManager.createIfNotExist();
        configManager.initConfigs();
        configManager.recaptureEntries(false);
        Bukkit.getPluginManager().registerEvents(EntityEventListeners.speedrunner, this);
        Bukkit.getPluginManager().registerEvents(new Tracker.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameMenu.MenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameEventTriggerer(), this);
        Bukkit.getPluginManager().registerEvents(new Portal.PortalEventListener(), this);
//        commands = Arrays.asList(new TestStartCommand(), new TryKitCommand(),
//                new GameStartCommand(), new GiveTrackerCommand(), new AdminCommand(),
//                new PlayerProfileCommand(), new GamesCommand());
        // new GameStopCommand(),
    
   }
    
    public CachedPlayerProfile getPlayerProfileFor(OfflinePlayer player) {
        AtomicReference<CachedPlayerProfile> found = new AtomicReference<>(null);
        configManager.getLastCaptured().PLAYER_PROFILES.get().forEach(p -> {
            if (p.getPlayer().equals(player))
                found.set(p);
        });
        CachedPlayerProfile profile = found.get();
        if (profile == null) {
            CachedPlayerProfile created = new CachedPlayerProfile(player, createPlayerDataMap());
            configManager.getLastCaptured().PLAYER_PROFILES.get().add(created);
            return created;
        }
        return profile;
    }
    
    public GameStats getGameStatsFor(UUID uuid) {
        AtomicReference<GameStats> found = new AtomicReference<>(null);
        configManager.getLastCaptured().GAME_STATS.get().forEach(stats -> {
            if (stats.getUUID().equals(uuid))
                found.set(stats);
        });
        return found.get();
    }
    
    @Override
    public void onDisable() {
        configManager.getLastCaptured().writeAll();
        configManager.saveConfigs();
    }
    
    
    public ConfigurationManager getConfigManager() {
        return configManager;
    }
    public static Manhunt getInstance() {
        return plugin;
    }
    public TaskManager getTaskManager() {
        return manager;
    }
    
    
}
