package me.brokenearthdev.manhuntplugin.stats;


import me.brokenearthdev.manhuntplugin.Utils;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.createPlayerHead;

public class PlayerProfileGUI extends GameMenu {
    
    private boolean registered = false;
    private final CachedPlayerProfile profile;
    
    public PlayerProfileGUI(CachedPlayerProfile profile) {
        super(profile.getPlayer().getName() + "'s stats", 3);
        this.profile = profile;
    }
    
    @Override
    public void display(HumanEntity entity) {
        if (!registered) {
            registered = true;
            setButton(new Button(4, ItemFactory.create(createPlayerHead(profile.getPlayer(), ChatColor.AQUA + ChatColor.BOLD.toString() + profile.getPlayer().getName() + "'s profile"))
                    .emptyLoreLine().addLoreLine(ChatColor.GOLD + ChatColor.BOLD.toString() + "RATING: " + ChatColor.WHITE + profile.getRating())
                    .create()));
            setButton(new Button(10, ItemFactory.create(Material.IRON_SWORD)
                    .setName(ChatColor.GOLD + "Kills").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "This player has " + profile.getKills() + " kills").emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the number of players killed")
                    .addLoreLine(ChatColor.YELLOW + "by this player").create()));
            setButton(new Button(11, ItemFactory.create(Material.BONE)
                    .setName(ChatColor.GOLD + "Deaths").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "This player has " + profile.getDeaths() + " deaths").emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the number of times the player")
                    .addLoreLine(ChatColor.YELLOW + "had died").create()));
            setButton(new Button(12, ItemFactory.create(Material.BOW)
                    .setName(ChatColor.GOLD + "Kill/Death ratio").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "This player has a KDR of " + Utils.twoDigits(((double)profile.getKills() / (double)profile.getDeaths()))).emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the ratio of the player's kills to deaths")
                    .addLoreLine(ChatColor.YELLOW + "(no. of kills" + ChatColor.RED +  " / " + ChatColor.YELLOW + "no. of deaths)")
                    .create()));
            setButton(new Button(14, ItemFactory.create(Material.FIREWORK_ROCKET)
                    .setName(ChatColor.GOLD + "Wins").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "This player has " + profile.getWins() + " wins").emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the number of wins the player")
                    .addLoreLine(ChatColor.YELLOW + "has").create()));
            setButton(new Button(15, ItemFactory.create(Material.PLAYER_HEAD)
                    .setName(ChatColor.GOLD + "Losses").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "This player has " + profile.getLosses() + " losses").emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the number of times the player")
                    .addLoreLine(ChatColor.YELLOW + "had lost").create()));
            setButton(new Button(16, ItemFactory.create(Material.COMPASS)
                    .setName(ChatColor.GOLD + "Win/Loss ratio").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "This player has a WLR of " + Utils.twoDigits(((double) profile.getWins() / (double) profile.getLosses()))).emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the ratio of the player's wins to losses")
                    .addLoreLine(ChatColor.YELLOW + "(no. of wins" + ChatColor.RED +  " / " + ChatColor.YELLOW + "no. of losses)").create()));
            setButton(new Button(20, ItemFactory.create(Material.ARROW)
                    .setName(ChatColor.GOLD + "Games as hunter").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "The player has played as a hunter in " + profile.getGamesHunter() + " games").emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the number of games the player has")
                    .addLoreLine(ChatColor.YELLOW + "played as a hunter").create()));
            setButton(new Button(22, ItemFactory.create(Material.MAP)
                    .setName(ChatColor.GOLD + "Number of games").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "This player has played a total of " + (profile.getGamesHunter() + profile.getGamesSpeedrunner()) + " games").emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the total number of games the").addLoreLine(ChatColor.YELLOW + "player has spent as a hunter or a runner").emptyLoreLine()
                    .create()));
            setButton(new Button(24, ItemFactory.create(Material.ARROW)
                    .setName(ChatColor.GOLD + "Games as runner").emptyLoreLine()
                    .addLoreLine(ChatColor.BLUE + "The player has played as a runner in " + profile.getGamesSpeedrunner() + " games").emptyLoreLine()
                    .addLoreLine(ChatColor.YELLOW + "This is the number of games the player has")
                    .addLoreLine(ChatColor.YELLOW + "played as a speedrunner").create()));
    
        }
        super.display(entity);
    }
}
