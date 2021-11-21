package me.brokenearthdev.manhuntplugin.commands;

import me.brokenearthdev.manhuntplugin.core.commands.AutoRegisterCommand;
import me.brokenearthdev.manhuntplugin.game.GameSettings;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.core.commands.CommandResponse;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.tracker.Tracker3;
import me.brokenearthdev.manhuntplugin.kits.Kits;
import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import me.brokenearthdev.manhuntplugin.game.players.Speedrunner;
import me.brokenearthdev.manhuntplugin.game.players.SpeedrunnerSettings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static me.brokenearthdev.manhuntplugin.core.commands.CommandResponse.*;

@AutoRegisterCommand
public final class TestStartCommand extends ManhuntCommand {
    
    public TestStartCommand() {
        super("mhts", Message.ERROR_PREFIX("Invalid Usage! Use /mhts sr or /mhts hr"), 1,1,
                false);
        register();
    }
    
    @Override
    public CommandResponse.CompletedResponse processCommand(CommandSender sender, String[] arguments) {
        Player player = (Player) sender;
        Set<Speedrunner> sr = new HashSet<>();
        Set<Hunter> hr = new HashSet<>();
        Player b = null;
        SpeedrunnerSettings srs = new SpeedrunnerSettings.Builder().setAutoSmeltProbability(30).setExtraHearts(5)
                .setAlertProximityRadius(12).setExtraDamage(0).setSpeedBoost(2).setSpeedBoostDuration(60).build();
        for (Player p : Bukkit.getOnlinePlayers())
            if (!p.equals(player))
                b = p;
        if (arguments[0].equalsIgnoreCase("sr")) {
            sr.add(new Speedrunner(srs, player, Kits.WARRIOR_KIT));
            hr.add(new Hunter(b, new Tracker3(b), Kits.BOUNCER_KIT));
        } else if (arguments[0].equalsIgnoreCase("hr")) {
            sr.add(new Speedrunner(srs, b, Kits.WARRIOR_KIT));
            hr.add(new Hunter(player, new Tracker3(player), Kits.BOUNCER_KIT));
        } else return INVALID_USAGE(sender).queueDefaultMessages().execResponse(this);
        
        new ManhuntGame(new GameSettings(), srs,
                sr, hr, Bukkit.getWorld("world"), Bukkit.getWorld("world_the_end")).startGame();
        return OK_RESPONSE(sender).execResponse(this);
    }
}
