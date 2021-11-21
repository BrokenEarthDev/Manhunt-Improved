package me.brokenearthdev.manhuntplugin.admin;

import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A game logger allows sending a particular entity a detailed message about
 * the circumstances that occurred in an ongoing manhunt game.
 * <p>
 * The game logger is useful for detecting any idiosyncrasies during the
 * game's execution, but note that it can't detect every flaw possible.
 * <p>
 * This game logger, unfortunately, will not be able show exceptions thrown.
 */
public class GameLogger {
    
    private final ManhuntGame game;
    private final Map<CommandSender, GameLogLevel> senders = new HashMap<>();
    private final List<GameLogStatement> statements = new ArrayList<>();
    private final File logFile;
    public static final File LOG_FOLDER = new File("plugin/ManhuntPlugin/GameLogs");
    
    public GameLogger(ManhuntGame game) {
        this.game = game;
        logFile = new File(LOG_FOLDER.getAbsolutePath() + "/" + game.getGameUUID() + ".log");
    }
    
    /**
     * Dump the logs to the log file
     *
     * @throws IOException If an error occurred while dumping this file
     */
    public void dumpLogs() throws IOException {
        if (!LOG_FOLDER.exists())
            LOG_FOLDER.mkdirs();
        if (!logFile.exists())
            logFile.createNewFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile), 32768)) {
            for (GameLogStatement statement : statements) {
                writer.write(statement.level.name() + ": " + statement.statement);
                writer.newLine();
            }
        }
    }
    
    /**
     * Records a statement without sending them. They will only be displayed
     * in the dumped log file.
     *
     * @param lvl The level
     * @param statement The statement
     */
    public void recordStatement(GameLogLevel lvl, String statement) {
        statements.add(new GameLogStatement(statement, lvl));
    }
    
    /**
     * Sends a statement to the registered players with a corresponding
     * {@link GameLogLevel} less than or equal to the {@link GameLogLevel}
     * passed in.
     *
     * @param lvl The game log level
     * @param statement The statement
     */
    public void sendStatement(GameLogLevel lvl, String statement) {
        statements.add(new GameLogStatement(statement, lvl));
        senders.forEach((k, v) -> {
            if (lvl.lvl <= v.lvl)
                k.sendMessage(ChatColor.AQUA + lvl.name() + ": " + ChatColor.GREEN + statement);
        });
    }
    
    /**
     * Sends a statement with {@link GameLogLevel#LOW}
     *
     * @param statement The statement
     */
    public void low(String statement) {
        sendStatement(GameLogLevel.LOW, statement);
    }
    
    /**
     * Sends a statement with {@link GameLogLevel#MEDIUM}
     *
     * @param statement The statement
     */
    public void medium(String statement) {
        sendStatement(GameLogLevel.MEDIUM, statement);
    }
    
    /**
     * Sends a statement with {@link GameLogLevel#HIGH}
     *
     * @param statement The statement
     */
    public void high(String statement) {
        sendStatement(GameLogLevel.HIGH, statement);
    }
    
    /**
     * Registers a sender so that the sender will be updated once a statement
     * is sent, where its {@link GameLogLevel} is less than or equal to the
     * sender's maximum {@link GameLogLevel}.
     *
     * @param sender A player
     * @param level  The level
     */
    public void registerSender(CommandSender sender, GameLogLevel level) {
        senders.put(sender, level);
    }
    
    /**
     * @return The manhunt game
     */
    public ManhuntGame getManhuntGame() {
        return game;
    }
    
    /**
     * Represents the game log level
     */
    public enum GameLogLevel {
    
        /**
         * Only information about kills, deaths, spawn are recorded (along with game states and other info)
         */
        LOW(0),
    
        /**
         * Information sent by {@link #LOW}, plus the command executions and tracker GUI
         * information (along with firework summons, chest spawns, etc)
         */
        MEDIUM(1),
    
        /**
         * Information sent by {@link #MEDIUM}, plus registered listeners and task manager updates.
         * <p>
         * Please note that this can spam the player.
         */
        HIGH(2);
        public final int lvl;
        GameLogLevel(int lvl) {
            this.lvl = lvl;
        }
        
    }
    
    private static class GameLogStatement {
        private final String statement;
        private final GameLogLevel level;
        private GameLogStatement(String statement, GameLogLevel lvl) {
            this.statement = statement;
            this.level = lvl;
        }
    }
    
}
