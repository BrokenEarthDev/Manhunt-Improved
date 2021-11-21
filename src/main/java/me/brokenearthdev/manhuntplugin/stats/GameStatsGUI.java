package me.brokenearthdev.manhuntplugin.stats;

import me.brokenearthdev.manhuntplugin.Utils;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.LoreCreator;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.*;

public class GameStatsGUI extends GameMenu {
    
    private boolean registered = false;
    private final GameStats stats;
    
    public GameStatsGUI(GameStats stats) {
        super("Stats for game " + shortenUUID(stats.getUUID()), 3);
        this.stats = stats;
    }
    
    @Override
    public void display(HumanEntity entity) {
        if (!registered) {
            registered = true;
            setButton(new Button(4, ItemFactory.create(Material.DIAMOND)
                    .setName(ChatColor.GOLD + "Game information").emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "Game UUID: " + ChatColor.BLUE + shortenUUID(stats.getUUID()))
                    .addLoreLine(ChatColor.YELLOW + "Winner team: " + ChatColor.BLUE + stats.getWinner().toString().toLowerCase())
                    .create()));
            
            Button players = new Button(10, ItemFactory.create(Material.PLAYER_HEAD)
                    .setName(ChatColor.GOLD + "Game players").emptyLoreLine()
                    .setLore(new LoreCreator().addEntries(ChatColor.YELLOW + "CLICK to access all players").emptyLine()
                            .addEntries(ChatColor.YELLOW + "Players include:").addList().addEntries(playersIncludeList(extractOfflinePlayers(stats.getPlayers()))).createList().getLoreList()).create());
            players.addAction(a -> {
                Utils.gameStatsPlayerMenu("Participants", new ArrayList<>(stats.getPlayers()), entity, this);
            });
            setButton(players);
            Button hunters = new Button(11, ItemFactory.create(Material.BOW)
                    .setName(ChatColor.GOLD + "Hunters").emptyLoreLine()
                    .setLore(new LoreCreator().addEntries(ChatColor.YELLOW + "CLICK to access all hunters", ChatColor.YELLOW + "There are " + stats.getHunters() + " hunters").emptyLine()
                            .addEntries(ChatColor.YELLOW + "Hunters include:").addList().addEntries
                                    (playersIncludeList(extractOfflineHunters(stats.getPlayers())))
                            .createList().getLoreList()).create());
            hunters.addAction(a -> {
                Utils.gameStatsPlayerMenu("Hunters", new ArrayList<>(stats.getPlayers()), entity, this);
            });
            setButton(hunters);
            
            Button runners = new Button(12, ItemFactory.create(Material.IRON_BOOTS)
                    .setName(ChatColor.GOLD + "Runners")
                    .setLore(
                            new LoreCreator().emptyLine()
                            .addEntries(ChatColor.YELLOW + "CLICK to access all runners",
                                    ChatColor.YELLOW + "There are " + stats.getRunners() + " runners").emptyLine()
                            .addEntries(ChatColor.YELLOW + "Runners include:").addList()
                            .addEntries(playersIncludeList(extractOfflineRunners(stats.getPlayers())))
                            .createList().getLoreList()
                    ).create());
            runners.addAction(a -> {
                Utils.gameStatsPlayerMenu("Speedrunners", new ArrayList<>(stats.getPlayers()), entity, this);
            });
            setButton(runners);
            
            setButton(new Button(14, ItemFactory.create(Material.DIAMOND_SWORD)
                    .setName(ChatColor.GOLD + "Total kills")
                    .setLore(
                            new LoreCreator().emptyLine()
                            .addEntries(ChatColor.YELLOW + "There are a total of " + stats.getKills(), ChatColor.YELLOW + " kills")
                            .getLoreList()
                    ).create()));
            
            int mins = stats.getTimeElapsed() / 60;
            int secs = stats.getTimeElapsed() % 60;
            setButton(new Button(15, ItemFactory.create(Material.CLOCK)
                    .setName(ChatColor.GOLD + "Time elapsed")
                    .setLore(
                            new LoreCreator().emptyLine()
                            .addEntries(ChatColor.YELLOW + "The game took " + ChatColor.BLUE + mins + " minutes and ",
                                    ChatColor.BLUE.toString() + secs + " seconds")
                            .getLoreList()
                    ).create()));
            
            setButton(new Button(16, ItemFactory.create(Material.PAPER)
                    .setName(ChatColor.GOLD + "Game settings")
                    .setLore(
                            new LoreCreator().emptyLine()
                            .addEntries(ChatColor.YELLOW + "The game settings are as follows:")
                            .addList().addEntries(fromSettingsMap(stats.getSettings().settingsCopy())).createList()
                            .getLoreList()
                    ).create()));
            setButton(new Button(22, ItemFactory.create(Material.ARROW)
                    .setName(ChatColor.GOLD + "Runner settings")
                    .setLore(
                            new LoreCreator().emptyLine()
                            .addEntries(ChatColor.YELLOW + "The runner settings are as follows:")
                            .addList().addEntries(fromSettingsMap(stats.getRunnerSettings().settingsCopy()))
                            .createList().getLoreList()
                    ).create()));
        }
        super.display(entity);
    }
}
