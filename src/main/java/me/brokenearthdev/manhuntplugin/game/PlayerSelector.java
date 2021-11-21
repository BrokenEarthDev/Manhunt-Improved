package me.brokenearthdev.manhuntplugin.game;

import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.game.players.Hunter;
import me.brokenearthdev.manhuntplugin.game.players.Speedrunner;
import me.brokenearthdev.manhuntplugin.game.players.SpeedrunnerSettings;
import me.brokenearthdev.manhuntplugin.tracker.Tracker;
import me.brokenearthdev.manhuntplugin.kits.Kit;
import me.brokenearthdev.manhuntplugin.tracker.TrackerType;
import org.bukkit.entity.Player;

import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Selects players based on the options.
 */
public final class PlayerSelector {
    
    private final Set<Player> speedrunners = new HashSet<>();
    private final Set<Player> hunters = new HashSet<>();
    private final Set<Player> players = new HashSet<>();
    
    private int maxRunners = 1;
    private int maxHunters = Integer.MAX_VALUE;
    
    public PlayerSelector(Set<Player> runners, Set<Player> hunters, Set<Player> players) {
        this.speedrunners.addAll(runners);
        this.hunters.addAll(hunters);
        this.players.addAll(players);
    }
    
    public PlayerSelector() {
    }
    
    /**
     * Adds a runner. If the player is a hunter, they will be moved
     * into the runner set.
     *
     * @param player Runner
     */
    public void addRunner(Player player) {
        hunters.remove(player);
        speedrunners.add(player);
        players.add(player);
    }
    
    /**
     * Adds a hunter. If the player is a runner, they will be moved
     * into the hunter set.
     *
     * @param player Hunter
     */
    public void addHunter(Player player) {
        speedrunners.remove(player);
        hunters.add(player);
        players.add(player);
    }
    
    /**
     * Removes the {@link Player} from the manually selected
     * {@link Hunter} set but doesn't remove the player from
     * the manually included list.
     *
     * @param player The player
     */
    public void removeHunter(Player player) {
        hunters.remove(player);
    }
    
    /**
     * Removes the {@link Player} from the manually selected
     * {@link Speedrunner} set but doesn't remove the player
     * rom the manually included list.
     *
     * @param player The player
     */
    public void removeRunner(Player player) {
        speedrunners.remove(player);
    }
    
    /**
     * Removes the {@link Player} from the manually selected
     * participant set.
     *
     * @param player The player
     */
    public void removeParticipant(Player player) {
        removeHunter(player);
        removeRunner(player);
        players.remove(player);
    }
    
    /**
     * Adds a player. Whether they will be hunters or speedrunners will be
     * determined from selection randomly (if there are more space)
     *
     * @param player player
     */
    public void addPlayer(Player player) {
        players.add(player);
    }
    
    /**
     * Checks whether the {@link Player} is <i>manually</i> selected
     * to be a hunter
     *
     * @param player The player
     * @return Whether the player is selected (manually) to be a hunter
     */
    public boolean isHunter(Player player) {
        return hunters.contains(player);
    }
    
    /**
     * Checks whether the {@link Player} is <i>manually</i> selected
     * to be a runner
     *
     * @param player The player
     * @return Whether the player is selected (manually) to be a runner
     */
    public boolean isRunner(Player player) {
        return speedrunners.contains(player);
    }
    
    /**
     * Checks whether the {@link Player} is <i>manually</i> selected to
     * participate in a game
     *
     * @param player The player
     * @return Whether the {@link Player} is selected (manually) to
     * participate in a game
     */
    public boolean isIncluded(Player player) {
        return players.contains(player);
    }
    
    public int getSelectedHunters() {
        return hunters.size();
    }
    
    public int getSelectedRunners() {
        return speedrunners.size();
    }
    
    public int getIncludedSize() {
        return players.size();
    }
    
    
    /**
     * @param max The maximum number of runners
     */
    public void setMaxRunners(int max) {
        if (max >= 1)
            maxRunners = max;
    }
    
    /**
     * @param max The maximum number of hunters
     */
    public void setMaxHunters(int max) {
        if (max >= 1)
            maxHunters = max;
    }
    
    /**
     * @return The maximum number of runners
     */
    public int getMaxRunners() {
        return maxRunners;
    }
    
    /**
     * @return The maximum number of hunters
     */
    public int getMaxHunters() {
        return maxHunters;
    }
    
    /**
     * Selects players based on the options set previously.
     * <p>
     * The information required will be used to initialize {@link GamePlayer}
     * objects.
     *
     * @param settings The runner settings
     * @param hunterTracker The tracker
     * @param runnerKit Runner's kit
     * @param hunterKit Hunter's kit
     * @return Selected set of players
     */
    public Set<GamePlayer> selectPlayers(SpeedrunnerSettings settings, TrackerType hunterTracker,
                                         Kit runnerKit, Kit hunterKit) {
        // selected players
        Set<Player> runners = select(speedrunners, players, hunters, maxRunners);
        Set<Player> hunters = select(this.hunters, players, speedrunners, maxHunters);
        // Converted
        Set<Speedrunner> gameRunners = new HashSet<>();
        runners.forEach(p -> gameRunners.add(new Speedrunner(settings, p, runnerKit)));
        Set<Hunter> gameHunters = new HashSet<>();
        hunters.forEach(p -> gameHunters.add(new Hunter(p, Tracker.newTracker(hunterTracker, p), hunterKit)));
        Set<GamePlayer> sum = new HashSet<>(gameRunners);
        sum.addAll(gameHunters);
        return sum;
    }
    
    public Set<Player> selectRunners() {
        return select(speedrunners, players, hunters, maxRunners);
    }
    
    public Set<Player> selectHunters() {
        return select(hunters, players, speedrunners, maxHunters);
    }
    
    /**
     * Selects a group of players given all players from the set, the excluded players, and
     * the players that are already selected.
     * <p>
     * A possible use of this is when a set of speedrunners or hunters is selected.
     *
     * @param from The set that contains the selected
     * @param all The set that contains all possible candidates
     * @param not The set that should not include entries that are selected
     * @param max The maximum selected players count
     * @return The selected players.
     */
    public static Set<Player> select(Set<Player> from, Set<Player> all, Set<Player> not, int max) {
        Set<Player> chosen = new HashSet<>();
        List<Player> fromList = new ArrayList<>(from);
        if (from.size() >= max) {
            for (int i = 0; i < max; i++)
                chosen.add(fromList.get(i));
        } else {
            chosen.addAll(from);
            int space = max - chosen.size();
            chosen.addAll(selectRandomPlayers(all, not, space));
        }
        return chosen;
    }
    
    /**
     * Randomly select players
     *
     * @param all All possible candidates
     * @param not Exclude players
     * @param count The amount 
     * @return The randomly selected players
     */
    private static Set<Player> selectRandomPlayers(Set<Player> all, Set<Player> not, int count) {
        if (not.size() == all.size() || count > all.size())
            // can't select if count is greater than the player set size
            return new HashSet<>();
        // 2, 1, 1
        // 50, 5, 5
//        while (all.size() - not.size() > count) {
//            count--;
//        }
        HashSet<Player> selectedPlayers = new HashSet<>();
        ArrayList<Player> fromList = new ArrayList<>(all);
        List<Integer> tried = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int random = ThreadLocalRandom.current().nextInt(0, all.size());
            Player selected = fromList.get(random);
            while (selectedPlayers.contains(selected) || not.contains(selected)) {
                if (tried.size() == (all.size() - not.size()) && tried.contains(random)) {
                    return selectedPlayers;
                }
                tried.add(random);
                random = ThreadLocalRandom.current().nextInt(0, all.size());
                selected = fromList.get(random);
            }
            selectedPlayers.add(selected);
            if (selectedPlayers.size() == count)
                break;
        }
        return selectedPlayers;
    }
    
}
