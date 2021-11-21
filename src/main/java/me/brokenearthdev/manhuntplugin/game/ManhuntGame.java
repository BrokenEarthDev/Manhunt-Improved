package me.brokenearthdev.manhuntplugin.game;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.admin.GameLogger;
import me.brokenearthdev.manhuntplugin.core.*;
import me.brokenearthdev.manhuntplugin.events.GameEndEvent;
import me.brokenearthdev.manhuntplugin.events.GameStateChangeEvent;
import me.brokenearthdev.manhuntplugin.game.players.*;
import me.brokenearthdev.manhuntplugin.stats.CachedPlayerProfile;
import me.brokenearthdev.manhuntplugin.stats.GameStats;
import me.brokenearthdev.manhuntplugin.tracker.Tracker;
import me.brokenearthdev.manhuntplugin.utils.StaticImports;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.*;

/**
 * Represents a single Manhunt game. There can only be one game while the plugin
 * is running. Here, there are many useful game controls.
 */
public final class ManhuntGame {
    
    // The running game.
    private static ManhuntGame runningGame = null;
    // The game settings
    public final GameSettings gameSettings;
    // The runner settings
    public final SpeedrunnerSettings runnerSettings;
    // The game logger
    public final GameLogger gameLogger;
    // The game uuid
    private final UUID uuid;
    // Speedrunner set
    private final Set<Speedrunner> speedrunners;
    
    // Hunter set
    private final Set<Hunter> hunters;
    
    // The main and end world
    private final World mainWorld, endWorld;
    // Game stats
    private final GameStats stats;
    // All players
    private final Set<Player> allPlayers;
    // Time elapsed since the game started
    private int timeElapsed;
    // Whether the grace period is ongoing
    private boolean gracePeriodOngoing;
    // The current game state
    private GameState state = GameState.NOT_STARTED;
    // Grace period left
    private int gpLeft;
    
    // Number of kills
    private int kills;
    
    // Winner
    private Team winner;
    
    // Chests
    private Map<Block, Inventory> chestInventoryMap = new HashMap<>();
    
    // Portals
    private Set<Portal> portals = new HashSet<>();
    
    // Tasks
    private RepeatedRunnable elapsedCounterTask = null;
    private RepeatedRunnable aprWarner = null;
    private RepeatedRunnable subWarner = null;
    private RepeatedRunnable gracePeriod = null;
    private RepeatedRunnable hunterReminder = null;
    private RepeatedRunnable allReminder = null;
    
    public ManhuntGame(GameSettings settings, SpeedrunnerSettings runnerSettings, Set<Speedrunner> speedrunners,
                       Set<Hunter> hunters, World mainWorld, World endWorld) {
        this.gameSettings = settings;
        this.runnerSettings = runnerSettings;
        this.speedrunners = speedrunners;
        this.hunters = hunters;
        this.mainWorld = mainWorld;
        this.endWorld = endWorld;
        this.uuid = UUID.randomUUID();
        allPlayers = new HashSet<>();
        speedrunners.forEach(sr -> allPlayers.add(sr.getPlayer()));
        hunters.forEach(hr -> allPlayers.add(hr.getPlayer()));
        stats = new GameStats(uuid, new HashMap<>());
        gameLogger = new GameLogger(this);
    }
    
    public static ManhuntGame getManhuntGame() {
        return runningGame;
    }
    
    public static void setManhuntGame(ManhuntGame game) {
        runningGame = game;
    }
    
    public void removePlayer(GamePlayer player) {
        allPlayers.remove(player.getPlayer());
        if (player instanceof Hunter)
            hunters.remove(player);
        else if (player instanceof Speedrunner) speedrunners.remove(player);
        player.getPlayer().setHealth(0);
    }
    
    /**
     * Starts this game
     */
    public void startGame() {
        runningGame = this;
        beginState();
        resetPlayers();
        resetWorld();
        setCalcSpawnLoc();
        countTimeElapsed();
        speedrunners.forEach(sr -> {
            CachedPlayerProfile profile = Manhunt.getInstance().getPlayerProfileFor(sr.getPlayer());
            profile.setGamesSpeedrunner(profile.getGamesSpeedrunner() + 1);
        });
        hunters.forEach(hr -> {
            CachedPlayerProfile profile = Manhunt.getInstance().getPlayerProfileFor(hr.getPlayer());
            profile.setGamesHunter(profile.getGamesHunter() + 1);
        });
        speedrunners.forEach(Speedrunner::giveBenefits);
        speedrunners.forEach(Speedrunner::giveKit);
        sendMessage(Message.INFO("Grace period will start"));
        if (getGameSettings().gracePeriodLength >= 1)
            this.gracePeriod = createGracePeriodTask();
        allReminder = createAllReminderTask();
        //gracePeriodOngoing = false;
        hunters.forEach(Hunter::giveKit);
    }
    
    /**
     * Stops this game
     */
    public void stopGame() {
        runningGame = null;
        Manhunt.getInstance().getLogger().log(Level.INFO, "Stopping game...");
        endState(winner);
        taskManager().removeTask(aprWarner);
        taskManager().removeTask(subWarner);
        taskManager().removeTask(gracePeriod);
        taskManager().removeTask(elapsedCounterTask);
        taskManager().removeTask(hunterReminder);
        taskManager().removeTask(allReminder);
        logger().log(Level.INFO, "Successfully removed tasks");
        if (gameSettings.recordStats)
            writeStats();
        hunters.clear();
        allPlayers.clear();
        speedrunners.clear();
    }
    
    public void abortGame() {
        runningGame = null;
        Manhunt.getInstance().getLogger().log(Level.INFO, "Aborting game...");
        abortState();
        taskManager().removeTask(aprWarner);
        taskManager().removeTask(subWarner);
        taskManager().removeTask(gracePeriod);
        taskManager().removeTask(elapsedCounterTask);
        taskManager().removeTask(hunterReminder);
        taskManager().removeTask(allReminder);
        logger().log(Level.INFO, "Successfully flushed tasks");
        
    }
    
    private void writeStats() {
        stats.setHunters(hunters.size());
        stats.setRunners(speedrunners.size());
        stats.setKills(kills);
        Set<OfflineGamePlayer> offlineGamePlayers = new HashSet<>();
        hunters.forEach(h -> offlineGamePlayers.add(new OfflineGamePlayer.OfflineHunter(h.getPlayer())));
        speedrunners.forEach(s -> offlineGamePlayers.add(new OfflineGamePlayer.OfflineRunner(s.getPlayer())));
        stats.setPlayers(offlineGamePlayers);
        stats.setTimeElapsed(timeElapsed);
        stats.setSettings(gameSettings);
        stats.setRunnerSettings(runnerSettings);
    }
    
    
    /**
     * Clears inventory, resets food and heart level, resets potion effects, and sets
     * the game mode into survival
     */
    public void resetPlayers() {
        // set up
        allPlayers.forEach(e -> e.getInventory().clear());
        allPlayers.forEach(e -> e.setSaturation(20));
        allPlayers.forEach(e -> e.setFoodLevel(20));
        allPlayers.forEach(e -> e.setHealth(20));
        allPlayers.forEach(e -> e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20));
        allPlayers.forEach(e -> e.giveExpLevels(-e.getTotalExperience()));
        allPlayers.forEach(e -> e.undiscoverRecipes(e.getDiscoveredRecipes()));
        allPlayers.forEach(e -> e.getActivePotionEffects().forEach(f -> e.removePotionEffect(f.getType())));
        allPlayers.forEach(e -> e.setGameMode(GameMode.SURVIVAL));
        allPlayers.forEach(e -> {
            Iterator<Advancement> advancements = Bukkit.advancementIterator();
            advancements.forEachRemaining(adv -> {
                AdvancementProgress progress = e.getAdvancementProgress(adv);
                progress.getAwardedCriteria().forEach(progress::revokeCriteria);
            });
        });
    }
    
    public void resetWorld() {
        mainWorld.setTime(0);
        mainWorld.setDifficulty(Difficulty.NORMAL);
        mainWorld.setStorm(false);
        // Reset enderdragpon battle
        DragonBattle battle = endWorld.getEnderDragonBattle();
        battle.initiateRespawn();
        battle.resetCrystals();
    }
    
    public boolean duringGracePeriod() {
        return gracePeriodOngoing;
    }
    
    /**
     * Counts the time elapsed in the game
     */
    public void countTimeElapsed() {
        elapsedCounterTask = new RepeatedRunnable("elapsed_counter_task") {
            @Override
            public void run() {
                timeElapsed += 2;
            }
        }.setPeriod(40).setTaskManager(Manhunt.getInstance().getTaskManager());
    }
    
    public GameSettings getGameSettings() {
        return gameSettings;
    }
    
    public void resetTimeElapsed() {
        this.timeElapsed = 0;
    }
    
    /**
     * Retrieves the game's unique id, which can be used to identify
     * a {@link ManhuntGame} both internally and externally.
     * <p>
     * This is especially important for recording game statistics
     *
     * @return The game UUID
     */
    public UUID getGameUUID() {
        return uuid;
    }
    
    /**
     * Retrieves all runner objects. Note that modifying the list
     * will not change anything as this is a copy.
     *
     * @return The speedrunners
     */
    public Set<Speedrunner> getRunners() {
        return new HashSet<>(speedrunners);
    }
    
    /**
     * Retrieves all hunter objects. Note that modifying the list
     * will not change anything as this is a copy.
     *
     * @return The hunters
     */
    public Set<Hunter> getHunters() {
        return new HashSet<>(hunters);
    }
    
    /**
     * Retrieves all player objects. Note that modifying the list
     * will not change anything as this is a copy.
     *
     * @return The players
     */
    public Set<Player> getAllPlayers() {
        return new HashSet<>(allPlayers);
    }
    
    /**
     * Retrieves all spawned {@link Chest}s and their corresponding {@link Inventory}.
     * Please note that the inventory value in the map is never equal to {@link Chest#getInventory()}
     * because the corresponding {@link Inventory} is custom.
     *
     * @return A map with chests and inventories
     */
    public Map<Block, Inventory> getSpawnedChests() {
        return chestInventoryMap;
    }
    
    public Set<Portal> getPortals() {
        return new HashSet<>(portals);
    }
    
    /**
     * Registers a new portal
     *
     * @param portal The portals
     */
    public void recordPortal(Portal portal) {
        if (portals.contains(portal)) {
            // possible duplicate
            portals.forEach(tp -> {
                if (tp.equals(portal))
                    tp.getTravelled().addAll(portal.getTravelled());
            });
        } else portals.add(portal);
    }
    
    /**
     * Scans all the possible portals and finds a portal
     * with equal to and from location
     *
     * @param from The from location (never {@link org.bukkit.World.Environment#NORMAL})
     * @param to   The to location
     * @return A portal, or {@code null} if not found
     */
    @Nullable
    public Portal getPortal(Location from, Location to) {
        for (Portal portal : portals) {
            if (portal.getTo().equals(to) && portal.getFrom().equals(from))
                return portal;
        }
        return null;
    }
    
    /**
     * Gets the portal hte player travelled through
     *
     * @param player The player
     * @return The portal
     */
    @Nullable
    public Portal getPortal(GamePlayer player) {
        for (Portal portal : portals) {
            if (portal.getTravelled().contains(player))
                return portal;
        }
        return null;
    }
    
    /**
     * Calculates the target location as if player "a"
     * is tracking player "b", which gives information about
     * where player "b" should go inorder to go to player "a"
     *
     * @param a The "from" player
     * @param b The "to" player
     * @return The location
     */
    public Location calcTargetLoc(GamePlayer a, GamePlayer b) {
        // check portals
        System.out.println(portals);
        Portal pa = getPortal(a); // Brokenearth
        Portal pb = getPortal(b); // celpeast
        System.out.println(pa + " || " + pb);
        if (a.getPlayer().getWorld().equals(b.getPlayer().getWorld()) || (pa == null && pb == null))
            return b.getPlayer().getLocation();
        if (pa == null)
            // then a isn't through a portal (world)
            return pb.getFrom();
        else
            // then b isn't through a portal (world)
            // and a is through a portal
            // or both are at different worlds
            return pa.getTo();
    }
    
    /**
     * Calculates the distance between player "a" and "b" (through the
     * 3d distance formula).
     * <p>
     * If the players are in a different world, the distance between the
     * closest portal and one of the player is calculated. Then, it'll be
     * incremented by the distance between the another player and the portal
     * in the different world.
     *
     * @param a Player 1
     * @param b Player 2
     * @return The distance
     */
    public double calcDistance(GamePlayer a, GamePlayer b) {
        Location target = calcTargetLoc(a, b);
        System.out.println("Distance from " + a.getPlayer().getName() + " to " + b.getPlayer().getName());
        System.out.println("Target is " + target.getWorld());
        System.out.println("A's loc == " + a.getPlayer().getLocation());
        double distance = target.distance(a.getPlayer().getLocation());
        
        if (!target.getWorld().equals(b.getPlayer().getWorld())) {
            // then the player is through a portal
            distance += calcTargetLoc(b, a).distance(b.getPlayer().getLocation());
        }
        return distance;
    }
    
    /**
     * Gets the tracker for the hunter.
     *
     * @param player The hunter
     * @return The tracker object. Null if the player isn't a hunter.
     */
    public Tracker getTracker(Player player) {
        Hunter hunter = getHunter(player);
        return hunter == null ? null : hunter.getTracker();
    }
    
    public Set<Tracker> getTrackers() {
        Set<Tracker> trackers = new HashSet<>();
        hunters.forEach(h -> trackers.add(h.getTracker()));
        return trackers;
    }
    
    public void giveTrackers() {
        hunters.forEach(h -> h.getTracker().giveTracker());
    }
    
    /**
     * Gets the speedrunner object for the {@link Player}
     *
     * @param player The speedrunner
     * @return The speedrunner object. {@code null} if player is
     * not a speedrunner in the game.
     */
    public Speedrunner getRunner(Player player) {
        if (player == null)
            return null;
        Speedrunner speedrunner = null;
        for (Speedrunner runner : speedrunners) {
            if (runner.getPlayer().equals(player)) {
                speedrunner = runner;
                break;
            }
        }
        return speedrunner;
    }
    
    /**
     * Gets the hunter object for the {@link Player}
     *
     * @param player The hunter
     * @return The hunter object. {@code null} if player is not a
     * hunter in the game.
     */
    public Hunter getHunter(Player player) {
        if (player == null)
            return null;
        Hunter hunter = null;
        for (Hunter hun : hunters) {
            if (hun.getPlayer().equals(player)) {
                hunter = hun;
                break;
            }
        }
        return hunter;
    }
    
    public GamePlayer getPlayer(Player player) {
        Hunter hunter = getHunter(player);
        Speedrunner runner = getRunner(player);
        return hunter == null ? (speedrunners == null ? null : runner) : hunter;
    }
    
    public int getKills() {
        return kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public boolean isRunner(Player player) {
        return getRunner(player) != null;
    }
    
    public boolean isHunter(Player player) {
        return getHunter(player) != null;
    }
    
    public boolean isPlayer(Player player) {
        return player != null && allPlayers.contains(player);
    }
    
    /**
     * Checks if:
     * <ol>
     *     <li>The player is a {@link GamePlayer}</>
     *     <li>The player is a participant</li>
     * </ol>
     *
     * @param player The player
     * @return Whether if the player is a participant or not
     */
    public boolean isParticipant(Player player) {
        GamePlayer gamePlayer = getPlayer(player);
        if (gamePlayer == null)
            return false;
        return gamePlayer.isParticipant();
    }
    
    /**
     * @return Time elapsed since the start of the game
     */
    public int getTimeElapsed() {
        return timeElapsed;
    }
    
    /**
     * @return The runner settings
     */
    public SpeedrunnerSettings getRunnerSettings() {
        return runnerSettings;
    }
    
    /**
     * @return The game state
     */
    public GameState getGameState() {
        return state;
    }
    
    /**
     * Sends a message to all of the players in the game.
     *
     * @param message The message
     */
    public void sendMessage(Message message) {
        allPlayers.forEach(message::send);
    }
    
    public void sendMessageForHunters(Message message) {
        hunters.forEach(h -> message.send(h.getPlayer()));
    }
    
    public void sendMessageForRunners(Message message) {
        speedrunners.forEach(s -> message.send(s.getPlayer()));
    }
    
    /**
     * Spawns a chest at a player's location and best should be invoked after
     * a player's death.
     *
     * @param player The player
     * @param stacks The items
     * @return {@code false} if the world is {@code null} or the player is not a game player
     */
    public boolean spawnChestAt(Player player, List<ItemStack> stacks) {
        if (!isParticipant(player))
            return false;
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (world == null)
            return false;
        Block block = world.getBlockAt(loc);
        block.setType(Material.CHEST);
        block.getState().setType(Material.CHEST);
        chestInventoryMap.put(block, createChestInventory(player, stacks));
        return true;
    }
    
    private Inventory createChestInventory(Player victim, Collection<ItemStack> items) {
        Inventory inventory = Bukkit.createInventory(null, 54, victim.getName() + ChatColor.GRAY.toString() + "'s items");
        items.forEach(inventory::addItem);
        return inventory;
    }
    
    /**
     * Scans the map of chests and inventory and finds a chest
     * with the same location as the one passed in. If a spawned
     * chest exists, then the method will return {@code true}
     *
     * @param location The location
     * @return Whether a spawned chest exists at that location
     * or not
     */
    public boolean containsChest(Location location) {
        for (Block block : getSpawnedChests().keySet()) {
            if (block.getLocation().equals(location))
                return true;
        }
        return false;
    }
    
    /**
     * Spawns a chest at the location and sends a message to every
     * player about the chest's whereabouts.
     *
     * @param player The dead player
     * @param stacks The items
     * @return Whether a chest has spawned or not
     */
    public boolean registerDeathAt(Player player, List<ItemStack> stacks) {
        if (!spawnChestAt(player, stacks))
            return false;
        Location location = player.getLocation();
        gameLogger.medium("Spawned chest at " + location);
        allPlayers.forEach(StaticImports::playLightningSound);
        return true;
    }
    
    public void summonFireworksForPlayers(Team type) {
        allPlayers.forEach(p -> spawnFireworks(p, type));
    }
    
    private void spawnFireworks(Player player, Team forTeam) {
        Color color = forTeam == Team.HUNTER ? Color.AQUA : Color.RED;
        Location loc = player.getLocation();
        for (int i = 0; i < 12; i++) {
            // gen random
            int dz = ThreadLocalRandom.current().nextInt(4, 24);
            int dx = ThreadLocalRandom.current().nextInt(4, 24);
            Location newLoc = loc.add(dx, 16, dz);
            Firework firework = (Firework) player.getWorld().spawnEntity(newLoc, EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffect(FireworkEffect.builder().trail(true).withColor(color).withFade(color).flicker(true).build());
            meta.setPower(5);
            firework.setFireworkMeta(meta);
            firework.detonate();
            gameLogger.medium("Summoned firework near " + player.getName() + " for " + forTeam + " at " + newLoc);
        }
    }
    
    public void setWinner(Team team) {
        if (team.equals(Team.SPEEDRUNNER)) {
            sendMessage(GameMessage.WIN("The speedrunners have won the game!!!"));
        } else sendMessage(GameMessage.WIN("The hunters have won the game!!!"));
        summonFireworksForPlayers(team);
        winner = team;
    }
    
    private void beginState() {
        GameState old = this.state;
        this.state = GameState.STARTED;
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, old, state));
        gameLogger.low("Game begin state is called");
    }
    
    private void endState(Team winner) {
        GameState old = state;
        this.state = GameState.ENDED;
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this, old, winner));
        gameLogger.low("Game end state is called");
    }
    
    private void abortState() {
        GameState old = state;
        this.state = GameState.ABORTED;
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, old, state));
        gameLogger.low("Game abort state is called");
    }
    
    public World getMainWorld() {
        return mainWorld;
    }
    
    /**
     * Counts how many alive runners are left.
     *
     * @return Alive runners
     */
    public int getRemainingRunners() {
        AtomicInteger remaining = new AtomicInteger(speedrunners.size());
        speedrunners.forEach(runner -> {
            if (!runner.isParticipant())
                remaining.getAndDecrement();
        });
        return remaining.get();
    }
    
    /**
     * Removes the runner out of the game
     *
     * @param runner The runner
     */
    public void setLostSpeedrunner(Speedrunner runner) {
        //this.speedrunners.remove(runner);
        runner.setParticipant(false);
        runner.getPlayer().setGameMode(GameMode.SPECTATOR);
        GameMessage.LOSS("You were killed and now have to be in spectator mode.").addLine("Use the compass to track players.")
                .send(runner.getPlayer());
        sendMessage(Message.NORMAL(ChatColor.RED + runner.getPlayer().getName() + ChatColor.YELLOW + " is eliminated!"));
    }
    
    /**
     * Calculates the closest runner. Works only in the
     * same world as the hunter.
     *
     * @param hunter The hunter
     * @return The closest runner if available
     */
    public Speedrunner calcClosestRunner(Hunter hunter) {
        Speedrunner closest = null;
        double distanceClosest = Double.MAX_VALUE;
        for (Speedrunner r : getRunners()) {
            double distance = calcDistance(hunter, r);
            if (closest != null && distance < distanceClosest) {
                distanceClosest = distance;
                closest = r;
            } else if (closest == null) {
                closest = r;
                distanceClosest = distance;
            }
        }
        return closest;
    }
    
    /**
     * Sends the speedrunner a message that the hunter is in their
     * radii if:
     * <ol>
     *     <li>The hunter is in the radius as set by the {@link SpeedrunnerSettings}</li>
     *     <li>The alertProximityRadius is set to be a number greater than zero</li>
     * </ol>
     *
     * @param sr The speedrunner
     * @param hr The hunter
     */
    public void sendInRadiiMsg(Speedrunner sr, Hunter hr) {
        int apr = getRunnerSettings().alertProximityRadius;
        if (apr > 0) {
            int distance = (int) calcDistance(sr, hr);//player.getLocation().distance(hunter.getLocation());
            if (aprWarner == null)
                this.aprWarner = createWarnerTask(sr, hr, distance, apr);
            if (subWarner == null)
                this.subWarner = createSubWarnerTask(sr, hr, apr);
        }
    }
    
    private double findPeriod(int dist, int apr) {
        return 100.0 * (double) dist / (double) apr;
    }
    
    private RepeatedRunnable createWarnerTask(Speedrunner player, Hunter hunter, int distance, int apr) {
        return new RepeatedRunnable("apr_warner_bell_task") {
            @Override
            public void run() {
                int distance = (int) calcDistance(player, hunter);//player.getLocation().distance(hunter.getLocation());
                if (distance <= apr && (player.getPlayer().getWorld().equals(hunter.getPlayer().getWorld())) && !player.getPlayer().isDead() && !hunter.getPlayer().isDead()) {
                    playBellSound(player.getPlayer());
                }
                setPeriod((int) findPeriod(distance, apr));
            }
        }.setPeriod((int) findPeriod(distance, apr)).setTaskManager(taskManager());
    }
    
    private RepeatedRunnable createSubWarnerTask(Speedrunner player, Hunter hunter, int apr) {
        return new RepeatedRunnable("apr_warner_text_task") {
            @Override
            public void run() {
                int distance = (int) calcDistance(player, hunter);// player.getLocation().distance(hunter.getLocation());
                if (distance <= apr && (player.getPlayer().getWorld().equals(hunter.getPlayer().getWorld())) && !player.getPlayer().isDead() && !hunter.getPlayer().isDead()) {
                    player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + ChatColor.BOLD.toString() +
                            "A hunter is "
                            + distance + "m away"));
                }
            }
        }.setPeriod(20).setTaskManager(taskManager());
    }
    
    private RepeatedRunnable createHunterReminderTask() {
        return new RepeatedRunnable("hunter_reminder_task") {
            @Override
            public void run() {
                hunters.forEach(hunter -> GameMessage.GOOD("Lost your tracker?")
                        .addLine("Use /mhtracker to retrieve your tracker")
                        .send(hunter.getPlayer()));
            }
        }.setPeriod(300 * 20).setTaskManager(taskManager());
    }
    
    private RepeatedRunnable createAllReminderTask() {
        return new RepeatedRunnable("all_reminder_task") {
            @Override
            public void run() {
                hunters.forEach(hunter -> GameMessage.GOOD(ChatColor.GOLD +
                        "The objective of your role is to track and kill the speedrunner before they " +
                        "win the game")
                        .send(hunter.getPlayer()));
                speedrunners.forEach(runner -> GameMessage.GOOD(ChatColor.GOLD +
                        "The objective of your role is to kill the enderdragon before you die")
                        .addLine("Remember you are being chased and tracked by a hunter!")
                        .send(runner.getPlayer()));
            }
        }.setPeriod(450 * 20).setTaskManager(taskManager());
    }
    
    /**
     * Calculates and sets the new spawn locations based on the {@link GameSettings}
     */
    public void setCalcSpawnLoc() {
        Location spawnLoc = mainWorld.getSpawnLocation();
        
        // tp spawn hunters
        int spawnH_x = gameSettings.randomSpawnDistanceMax == gameSettings.randomSpawnDistanceMin ? gameSettings.randomSpawnDistanceMin :
                ThreadLocalRandom.current().nextInt(gameSettings.randomSpawnDistanceMin, gameSettings.randomSpawnDistanceMax + 1);
        int spawnH_z = gameSettings.randomSpawnDistanceMin == gameSettings.randomSpawnDistanceMax ? gameSettings.randomSpawnDistanceMax :
                ThreadLocalRandom.current().nextInt(gameSettings.randomSpawnDistanceMin, gameSettings.randomSpawnDistanceMax + 1);
        int spawnH_y = 64;
        for (int y = 30; y < 130; y++) {
            Block block = mainWorld.getBlockAt((int) (spawnLoc.getX() + spawnH_x), y, (int) (spawnLoc.getZ() + spawnH_z));
            if (block.getType() == Material.AIR) {
                spawnH_y = y + 1;
                break;
            }
        }
        Location newHunterLoc = new Location(mainWorld, spawnH_x + spawnLoc.getX(), spawnH_y, spawnH_z + spawnLoc.getZ());
        hunters.forEach(e -> e.getPlayer().setBedSpawnLocation(newHunterLoc, true));
        hunters.forEach(e -> e.getPlayer().teleport(newHunterLoc));
        
        // tp spawn runners
        int spawnR_x = gameSettings.runnerSpawnDistanceMax == gameSettings.runnerSpawnDistanceMin ? gameSettings.runnerSpawnDistanceMin :
                ThreadLocalRandom.current().nextInt(gameSettings.runnerSpawnDistanceMin, gameSettings.runnerSpawnDistanceMax + 1);
        int spawnR_z = gameSettings.runnerSpawnDistanceMax == gameSettings.runnerSpawnDistanceMin ? gameSettings.runnerSpawnDistanceMin :
                ThreadLocalRandom.current().nextInt(gameSettings.runnerSpawnDistanceMin, gameSettings.runnerSpawnDistanceMax + 1);
        int spawnR_y = 64;
        for (int y = 30; y < 100; y++) {
            Block block = mainWorld.getBlockAt((int) (newHunterLoc.getX() + spawnR_x), y, (int) (newHunterLoc.getZ() + spawnR_z));
            if (block.getType() == Material.AIR) {
                spawnR_y = y + 1;
                break;
            }
        }
        Location newRunnerLoc = new Location(mainWorld, spawnR_x + newHunterLoc.getX(), spawnR_y, spawnR_z + newHunterLoc.getZ());
        speedrunners.forEach(e -> e.getPlayer().setBedSpawnLocation(newRunnerLoc, true));
        speedrunners.forEach(e -> e.getPlayer().teleport(newRunnerLoc));
    }
    
    
    private RepeatedRunnable createGracePeriodTask() {
        gpLeft = gameSettings.gracePeriodLength;
        gracePeriodOngoing = true;
        gameLogger.low("Grace period had started");
        return createCounter("grace_period_task", gpLeft * 20, new ArrayList<>(allPlayers),
                integer -> {
                    if (!gracePeriodOngoing) {
                        taskManager().removeTask(gracePeriod);
                        return;
                    }
                    if (integer <= 15)
                        allPlayers.forEach(StaticImports::playConfirmedSound);
                    if (integer <= 0) {
                        sendMessage(GameMessage.INFO(ChatColor.RED + "Grace period ended!"));
                        hunters.forEach(player -> player.getPlayer().sendTitle(ChatColor.AQUA + "Chase the Speedrunner Down",
                                null, 10, 30, 15));
                        speedrunners.forEach(sr -> sr.getPlayer().sendTitle(ChatColor.GREEN + "Good Luck", null, 10, 30, 15));
                        gracePeriodOngoing = false;
                        giveTrackers();
                        gameLogger.low("Grace period ended");
                        hunterReminder = createHunterReminderTask();
                        taskManager().removeTask(gracePeriod);
                        //removeTask(Manhunt.getInstance().getTaskManager());
                    }
                }).setTaskManager(taskManager());
//        return new RepeatedRunnable("grace_period_task") {
//            @Override
//            public void run() {
//                if (!gracePeriodOngoing) {
//                    this.removeTask(taskManager());
//                    return;
//                }
//                gpLeft -= getPeriod() / 20;
//                sendMessage(GameMessage.INFO(ChatColor.GOLD.toString()).append(String.valueOf(gpLeft) + ChatColor.GREEN + " seconds left"));
//                setPeriod(getPeriodForGP(gpLeft) * 20); // todo check manipulation if it works
//                if (gpLeft <= 0) {
//                    sendMessage(GameMessage.INFO(ChatColor.RED + "Grace period ended!"));
//                    hunters.forEach(player -> player.getPlayer().sendTitle(ChatColor.AQUA + "Chase the Speedrunner Down",
//                            null, 10, 30, 15));
//                    speedrunners.forEach(sr -> sr.getPlayer().sendTitle(ChatColor.GREEN + "Good Luck", null, 10, 30, 15));
//                    gracePeriodOngoing = false;
//                    giveTrackers();
//                    gameLogger.low("Grace period ended");
//                    hunterReminder = createHunterReminderTask();
//                    this.removeTask(Manhunt.getInstance().getTaskManager());
//                }
//            }
//        }.setPeriod(getPeriodForGP(gpLeft) * 20).setTaskManager(taskManager());
    }
    
    /**
     * An enum containing all possible game states.
     */
    public enum GameState {
        
        /**
         * Means that the game wasn't started and is pending startup
         */
        NOT_STARTED,
        
        /**
         * Means that the game has started (ie. no errors are occurring)
         */
        STARTED,
        
        /**
         * Means that the game has ended
         */
        ENDED,
        
        /**
         * Means that the game has aborted, usually means that the game was
         * either force stopped or that a fatal error had occurred
         */
        ABORTED
    }
    
}