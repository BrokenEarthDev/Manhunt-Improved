package me.brokenearthdev.manhuntplugin.utils;

import me.brokenearthdev.manhuntplugin.core.CounterTask;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.commands.ManhuntCommand;
import me.brokenearthdev.manhuntplugin.game.players.OfflineGamePlayer;
import me.brokenearthdev.manhuntplugin.main.ConfigurationManager;
import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.core.TaskManager;
import me.brokenearthdev.manhuntplugin.core.config.Entries;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import me.brokenearthdev.manhuntplugin.game.players.Speedrunner;
import me.brokenearthdev.manhuntplugin.stats.CachedPlayerProfile;
import me.brokenearthdev.manhuntplugin.stats.GameStats;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Contains important functions that are very relevant to shorten code. It is
 * recommended to statically import this class.
 */
public abstract class StaticImports {
   
    private StaticImports() {}
    
    /**
     * Plays an error sound for a player
     *
     * @param player The player
     */
    public static void playErrorSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_DEATH, 1, 2);
    }
    
    /**
     * Plays a confirmed "pling" for a player
     *
     * @param player The player
     */
    public static void playConfirmedSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
    }
    
    /**
     * Plays a cancelled sound for a player
     *
     * @param player The player
     */
    public static void playCancelledSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 2);
    }
    
    /**
     * Plays a lightning (thunder) sound for a player
     *
     * @param player The player
     */
    public static void playLightningSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 2);
    }
    
    /**
     * Plays a bell sound for a player
     *
     * @param player The player
     */
    public static void playBellSound(Player player) {
        player.playNote(player.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.A));
    }
    
    /**
     * Creates a head with the same head as a player
     *
     * @param owningPlayer The player
     * @param name The item's name
     * @return The player head
     */
    @SuppressWarnings("deprecation")
    public static ItemStack createPlayerHead(OfflinePlayer owningPlayer, String name) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setOwningPlayer(owningPlayer);
        stack.setItemMeta(meta);
        return stack;
    }
    
    /**
     * @return The current {@link ManhuntGame}
     */
    public static ManhuntGame currentGame() {
        return ManhuntGame.getManhuntGame();
    }
    
    /**
     * @return The plugin instance
     */
    public static Manhunt plugin() {
        return Manhunt.getInstance();
    }
    
    /**
     * @return The main task manager instance
     */
    public static TaskManager taskManager() {
        return plugin().getTaskManager();
    }
    
    /**
     * @return The main {@link ConfigurationManager} instance
     */
    public static ConfigurationManager configManager() {
        return plugin().getConfigManager();
    }
    
    /**
     * @return The configuration entries
     */
    public static Entries configEntries() {
        return configManager().getLastCaptured();
    }
    
    /**
     * Finds a player profile for an {@link OfflinePlayer}
     *
     * @param forPlayer The player
     * @return The returned {@link CachedPlayerProfile}
     */
    public static CachedPlayerProfile findPlayerProfile(OfflinePlayer forPlayer) {
        return plugin().getPlayerProfileFor(forPlayer);
    }
    
    /**
     * Converts a {@link Location} object ot a user friendly string that
     * can be understood by players.
     *
     * @param loc The loc object
     * @param world Whether to include world name
     * @return The user friendly string
     */
    public static String userFriendlyLoc(Location loc, boolean world) {
        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();
        String worldName = loc.getWorld() != null && world ? " in " +
                loc.getWorld().getEnvironment().name().toLowerCase().replace("_", " ") : "";
        return "(" + x + ", " + y + ", " + z + ")" +  worldName;
    }
    
    /**
     * Finds the game stats
     *
     * @param uuid The game uuid
     * @return The found game stats
     */
    public static GameStats findGameStats(UUID uuid) {
        return plugin().getGameStatsFor(uuid);
    }
    
    /**
     * Creates a players include list to shorten the
     * displayed player names
     *
     * @param players The players
     * @return The players include list
     */
    public static List<String> playersIncludeList(Set<OfflinePlayer> players) {
        List<String> possiblePlayers = new ArrayList<>();
        for (OfflinePlayer player : players) {
            if (possiblePlayers.size() == 5) {
                possiblePlayers.add("... and more players");
                break;
            }
            possiblePlayers.add(player.getName());
        }
        return possiblePlayers;
    }
    
    /**
     * Converts settings {@link Map} to a settings {@link List}
     *
     * @param map The settings {@link Map}
     * @return A list
     */
    public static List<String> fromSettingsMap(Map<String, Object> map) {
        List<String> settingsList = new ArrayList<>();
        map.forEach((k, v) -> settingsList.add(k + ": " + v));
        return settingsList;
    }
    
    /**
     * @return The logger
     */
    public static Logger logger() {
        return plugin().getLogger();
    }
    
    public static InventoryItems COMMON_ITEMS = new InventoryItems();
    public static InventoryItems.StartMenuItems START_MENU_ITEMS = new InventoryItems.StartMenuItems();
    public static InventoryItems.GameLoggerMenuItems GAME_LOGGER_MENU_ITEMS = new InventoryItems.GameLoggerMenuItems();
    public static InventoryItems.HuntersOptionsMenuItems HUNTERS_OPTIONS_MENU_ITEMS = new InventoryItems.HuntersOptionsMenuItems();
    public static InventoryItems.PlayerSelectorOptionsMenuItems PLAYER_SELECTOR_OPTIONS_MENU_ITEMS = new InventoryItems.PlayerSelectorOptionsMenuItems();
    public static InventoryItems.GracePeriodMenuItems GRACE_PERIOD_MENU_ITEMS = new InventoryItems.GracePeriodMenuItems();
    
    /**
     * Filters {@link Hunter} objects from a set of {@link GamePlayer}s,
     * from which {@link Hunter} objects are found.
     *
     * @param players The game players
     * @return The filtered set of {@link Hunter}s
     */
    public static Set<Hunter> filterHunters(Set<GamePlayer> players) {
        Set<Hunter> hunters = new HashSet<>();
        players.forEach(player -> {
            if (player instanceof Hunter)
                hunters.add((Hunter) player);
        });
        return hunters;
    }
    
    /**
     * Filters {@link Speedrunner} objects from a set of {@link GamePlayer}s,
     * from which {@link Speedrunner} objects are found.
     *
     * @param players The game players
     * @return The filtered set of {@link Speedrunner}s
     */
    public static Set<Speedrunner> filterRunners(Set<GamePlayer> players) {
        Set<Speedrunner> runners = new HashSet<>();
        players.forEach(player -> {
            if (player instanceof Speedrunner)
                runners.add((Speedrunner) player);
        });
        return runners;
    }
    
    /**
     * Checks whether two worlds are equal by comparing the path from the
     * world folder
     * <p>
     * Using {@link World#equals(Object)} is thought to not work due to the
     * fact that Bukkit can store more than one instances {@link World}. Also,
     * there are some reports that the same methods may not have been overrided.
     *
     * @param w1 World one
     * @param w2 World two
     * @return Whether the world is equal or not
     */
    public static boolean worldEqual(World w1, World w2) {
        return  w1 != null && w2 != null &&
                w1.getName().equals(w2.getName());
    }
    
    /**
     * Checks whether an array of an object contains an object through
     * the {@link Object#equals(Object)} method
     *
     * @param arr The array
     * @param obj The object
     * @param <T> The type
     * @return Whether the array passed in contains the object
     */
    public static <T> boolean arrayContains(T[] arr, T obj) {
        for (T inst : arr) {
            if (inst.equals(obj))
                return true;
        }
        return false;
    }
    
    /**
     * Creates an empty player data map from scratch.
     *
     * @return The empty map
     */
    public static Map<String, Object> createPlayerDataMap() {
        Map<String, Object> map = new HashMap<>();
        String[] keysInt = {"kills, deaths, wins, losses, games_speedrunner, games_hunter"};
        String[] keysDouble = {"avg_runner_survival_sec"};
        for (String ki : keysInt)
            map.put(ki, 0);
        for (String kd : keysDouble)
            map.put(kd, 0.0);
        return map;
    }
    
    /**
     * Shortens the task name
     *
     * @param name The task name
     * @return The shortened task name
     */
    public static String shortenTaskName(String name) {
        return name.length() < 30 ? name : name.substring(0, 27) + "...";
    }
    
    /**
     * Shortens the UUID
     *
     * @param uuid The uuid
     * @return The shortened uuid
     */
    public static String shortenUUID(UUID uuid) {
        String str = uuid.toString();
        return str.length() < 25 ? str : str.substring(0, 22) + "...";
    }
    
    /**
     * Extracts offline players from a set of {@link OfflineGamePlayer}s
     *
     * @param players The offline game players
     * @return The extracted result
     */
    public static Set<OfflinePlayer> extractOfflinePlayers(Set<OfflineGamePlayer> players) {
        Set<OfflinePlayer> list = new HashSet<>();
        players.forEach(offlineGamePlayer -> list.add(offlineGamePlayer.getOfflinePlayer()));
        return list;
    }
    
    /**
     * Extracts offline hunters from a collection of offline game players
     *
      * @param players The offline game players
     * @return The hunters
     */
    public static Set<OfflinePlayer> extractOfflineHunters(Set<OfflineGamePlayer> players) {
        Set<OfflinePlayer> list = new HashSet<>();
        players.forEach(offlineGamePlayer -> {
            if (offlineGamePlayer.isHunter())
                list.add(offlineGamePlayer.getOfflinePlayer());
        });
        return list;
    }
    
    /**
     * Extracts offline runners from a collection of offline game players
     *
     * @param players The offline game players
     * @return The runners
     */
    public static Set<OfflinePlayer> extractOfflineRunners(Set<OfflineGamePlayer> players) {
        Set<OfflinePlayer> list = new HashSet<>();
        players.forEach(offlineGamePlayer -> {
            if (offlineGamePlayer.isRunner())
                list.add(offlineGamePlayer.getOfflinePlayer());
        });
        return list;
    }
    
    /**
     * Creates a {@link CounterTask} which helps to count from a number
     * (in seconds) and send and update to a {@link List} of {@link Player}s
     * <p>
     * The time between messages will be depending on how many seconds that
     * remain until 0
     *
     * @param name The name of the task
     * @param seconds Time in seconds
     * @param update The players to send
     * @param textIntConsumer The integer (seconds left) which will be passed in to
     *                        return a {@link String} which will be sent to the {@link List}
     *                        of {@link Player}s
     * @return The created {@link CounterTask}
     */
    public static CounterTask createCounter(String name, int seconds, List<Player> update,
                                            Function<Integer, String> textIntConsumer) {
        CounterTask task = new CounterTask(name, (i -> {}), seconds * 20, 0);
        int[][] updaters = new int[][]{{0, 80, 20}, {100, 600, 100}, {800, 1200, 200}, {1500, 2400, 300},
                {2900, 6400, 500}, {7300, 18100, 900}, {19300, Integer.MAX_VALUE, 1200}};
        Consumer<Integer> func = n -> {
            String str = textIntConsumer.apply(n / 20);
            update.forEach(p -> Message.NORMAL(str).send(p));
        };
        for (int[] updater : updaters) {
            task.addCounterUpdater(new CounterTask.CounterUpdater(updater[0], updater[1], updater[2], func));
        }
        task.setTaskManager(taskManager());
        return task;
    }
    
    /**
     * Counts from a certain number to zero, updating players every few seconds depending on
     * time remaining until 0 by sending them a message.
     * <p>
     * Unlike {@link #createCounter(String, int, List, Function)}, this method sends built-in messages
     * to the {@link List} of {@link Player}s
     *
     * @param name The name of the task
     * @param seconds Seconds remaining
     * @param update The players to be updated
     * @param presend Executed just before message is sent
     * @return A {@link CounterTask} instance.
     */
    public static CounterTask createCounter(String name, int seconds, List<Player> update, Consumer<Integer> presend) {
        String text = "%time% seconds left";
        Function<Integer, String> function = (integer -> {
            presend.accept(integer);
           if (integer <= 5)
               return text.replace("%time%", ChatColor.RED.toString() + integer);
           else if (integer <= 15)
               return text.replace("%time%", ChatColor.YELLOW.toString() + integer);
           else
               return text.replace("%time%", ChatColor.GREEN.toString() + integer);
        });
        return createCounter(name, seconds, update, function);
    }
    
    /*
                 
                 GENERATOR
                
     -- THIS IS ONLY USED INTERNALLY WHEN PREPARED BY THE DEVELOPER. THIS ASSUMES THAT THIS PLUGIN
        IS ON THE DEVELOPER'S COMPUTER. THE PURPOSE OF THE GENERATOR IS TO SCAN CLASSES WITH
        A CERTAIN ANNOTATION AND AUTOMATICALLY GENERATE A NEW CLASS THAT REGISTERS THEM. --
        
     -- HENCE, USING THIS WILL GUARANTEE THAT THIS WILL NOT WORK (UNLESS A VERY SIMILAR PATH
        STRUCTURE IS PRESENT). --
        
     */
    
    public static final File PACK_LOC = new File("D:\\IntelliJ\\IdeaProjects\\untitled\\src\\main\\java\\me\\brokenearthdev\\manhuntplugin");
    public static final File SRC = new File("D:\\IntelliJ\\IdeaProjects\\untitled\\src\\main\\java\\");
    public static final String PACK_MAIN = "me.brokenearthdev.manhuntplugin";
    public static final File MAIN_PACKAGE = new File("D:\\IntelliJ\\IdeaProjects\\untitled\\src\\main\\java\\me\\brokenearthdev\\manhuntplugin\\main");
    public static final File GENERATED_FILE = new File("D:\\IntelliJ\\IdeaProjects\\untitled\\src\\main\\java\\me\\brokenearthdev\\manhuntplugin\\main\\Generated$AutoRegisteredCommands.java");
    
    /**
     * Loads java files under the main package (<i>me.brokenearthdev.manhuntplugin</i>) where
     * all java classes are located under.
     * <p>
     * This only works if the code runs on the developer's pc.
     *
     * @return The java files
     */
    public static File[] loadJavaFiles() {
        return files(PACK_LOC, new ArrayList<>());
    }
    
    /**
     * Generates a registry class
     *
     * @param cmds The commands
     * @return The new registry class
     */
    public static String generateRegistry(List<Class<?>> cmds) {
        StringBuilder builder = new StringBuilder();
        String[] imports = {ManhuntCommand.class.getName(), List.class.getName(), Arrays.class.getName()};
        // IMPORT & package
        builder.append("package ").append(PACK_MAIN).append(".main;").append("\n\n");
        for (String import_ : imports)
            builder.append("import ").append(import_).append(";").append("\n");
        builder.append("\n").append("//Date generated: ").append(new Date().toString()).append("\n")
                .append("class Generated$AutoRegisteredCommands {").append("\n");
        appendStrListField(builder, cmds, "foundCmds");
        builder.append("\n");
        builder.append("\n");
        // reg method
        builder.append("  void register() throws Exception {\n" +
                "      for (String cmd : foundCmds) {\n" +
                "          ManhuntCommand command = (ManhuntCommand) Class.forName(cmd).newInstance();\n" +
                "          CommandRegistryManager.INST.registerCommand(command, false);\n" +
                "      }\n" +
                "  }");
        builder.append("\n").append("}");
        return builder.toString();
    }
    
    private static void appendStrListField(StringBuilder builder, List<Class<?>> clazzes, String name) {
        builder.append("  private final List<String> ").append(name).append(" = Arrays.asList(");
        for (int i = 0; i < clazzes.size(); i++) {
            builder.append("\"").append(clazzes.get(i).getName()).append("\"");
            if (i + 1 != clazzes.size())
                builder.append(", ");
        }
        builder.append(");");
    }
    
    private static File[] files(File under, List<File> jfiles) {
        File[] files = under.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().endsWith(".java"))
                    jfiles.addAll(Arrays.asList(files(file, new ArrayList<>())));
                else jfiles.add(file);
            }
        }
        File[] retrieved = new File[jfiles.size()];
        for (int i = 0; i < jfiles.size(); i++)
            retrieved[i] = jfiles.get(i);
        return retrieved;
    }
    
    /*
            END
     */
    
}
