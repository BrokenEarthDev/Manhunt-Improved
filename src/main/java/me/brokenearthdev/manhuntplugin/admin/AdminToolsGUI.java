package me.brokenearthdev.manhuntplugin.admin;

import me.brokenearthdev.manhuntplugin.main.Manhunt;
import me.brokenearthdev.manhuntplugin.Utils;
import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import me.brokenearthdev.manhuntplugin.core.LoreCreator;
import me.brokenearthdev.manhuntplugin.core.Message;
import me.brokenearthdev.manhuntplugin.core.RepeatedRunnable;
import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.ListPaginatedMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.PlayerSelectorMenu;
import me.brokenearthdev.manhuntplugin.game.ManhuntGame;
import me.brokenearthdev.manhuntplugin.game.players.GamePlayer;
import me.brokenearthdev.manhuntplugin.kits.KitSelector;
import me.brokenearthdev.manhuntplugin.menu.StartMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.brokenearthdev.manhuntplugin.utils.StaticImports.shortenTaskName;

public class AdminToolsGUI extends GameMenu {
    
    private Button headerButton;
    private Button viewInv;
    private Button giveKit;
    private Button gameLog;
    private Button stopGame;
    private Button abortGame;
    private Button manipTask;
    private Button removePlayer;
    private Button startButton;
    
   // private Button runnableMenu_manipAll;
    
    private ListPaginatedMenu<OfflinePlayer> playerInv;
    private ListPaginatedMenu<OfflinePlayer> giveKitInv;
    private ListPaginatedMenu<OfflinePlayer> remPlayerInv;
    private ListPaginatedMenu<RepeatedRunnable> runnableMenu;
    private GameMenu gameLogMenu;
    private final KitSelector selector;
    private final StartMenu menu = new StartMenu();
    
    private AdminTools tools;
    
    public AdminToolsGUI(AdminTools tools) {
        super("Admin Tools", 5);
        this.tools = tools;
        this.selector = new KitSelector();
        if (ManhuntGame.getManhuntGame() != null) {
            playerInv = new PlayerSelectorMenu("Chose player", new ArrayList<>(ManhuntGame.getManhuntGame().getAllPlayers()));
            playerInv.addOnItemClick((a, b) -> {
                GamePlayer player = ManhuntGame.getManhuntGame().getPlayer(a.getPlayer());
                if (player == null) {
                    Message.ERROR_PREFIX("This player is not in the game").send(b.getWhoClicked());
                } else {
                    tools.viewInventory((Player) b.getWhoClicked(), player);
                    ((Player) b.getWhoClicked()).playSound(b.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                }
            });
            giveKitInv = new PlayerSelectorMenu("Chose player", new ArrayList<>(ManhuntGame.getManhuntGame().getAllPlayers()));
            giveKitInv.addOnItemClick((a, b) -> {
                selector.display(b.getWhoClicked());
                selector.addOnItemClick((c, d) -> {
                    tools.giveKit(ManhuntGame.getManhuntGame().getPlayer(a.getPlayer()), c);
                    ((Player) b.getWhoClicked()).playSound(b.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                    Message.GOOD_PREFIX("Kit given to player").send(b.getWhoClicked());
                });
            });
            
            remPlayerInv = new PlayerSelectorMenu("Chose player", new ArrayList<>(ManhuntGame.getManhuntGame().getAllPlayers()));
            remPlayerInv.addOnItemClick((player, event) -> {
                GamePlayer player1 = ManhuntGame.getManhuntGame().getPlayer(player.getPlayer());
                if (player1 != null) {
                    ManhuntGame.getManhuntGame().removePlayer(player1);
                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                    Message.GOOD_PREFIX("Successfully removed " + player1.getPlayer().getName() + " from the game");
                } else {
                    Message.ERROR_PREFIX("Can't find player");
                }
                display(event.getWhoClicked());
            });
            gameLogMenu = new GameMenu("Game logger menu", 4);
            Button high = new Button(16, ItemFactory.create(Material.RED_CONCRETE).setName(ChatColor.RED + "High level")
                                .create());
            Button medium = new Button(13, ItemFactory.create(Material.YELLOW_CONCRETE)
                                .setName(ChatColor.YELLOW + "Medium level").create());
            Button low = new Button(10, ItemFactory.create(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "low level")
                                .create());
            Button back = new Button(31, ItemFactory.create(Material.ARROW).setName(ChatColor.WHITE + "Back").create());
            back.addAction(action -> {
                display(action.getWhoClicked());
            });
            high.addAction(action -> {
               ManhuntGame.getManhuntGame().gameLogger.registerSender(action.getWhoClicked(), GameLogger.GameLogLevel.HIGH);
                ((Player) action.getWhoClicked()).playSound(action.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                display(action.getWhoClicked());
            });
            medium.addAction(action -> {
                ManhuntGame.getManhuntGame().gameLogger.registerSender(action.getWhoClicked(), GameLogger.GameLogLevel.MEDIUM);
                ((Player) action.getWhoClicked()).playSound(action.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                display(action.getWhoClicked());
            });
            low.addAction(action -> {
                ManhuntGame.getManhuntGame().gameLogger.registerSender(action.getWhoClicked(), GameLogger.GameLogLevel.LOW);
                ((Player) action.getWhoClicked()).playSound(action.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                display(action.getWhoClicked());
            });
            gameLogMenu.setButton(high).setButton(low).setButton(medium).setButton(back);
            playerInv.setReturntoGui(this);
            giveKitInv.setReturntoGui(this);
            remPlayerInv.setReturntoGui(this);
        }
        runnableMenu = new ListPaginatedMenu<>(title, Manhunt.getInstance().getTaskManager().getRepeatedRunnables(), (a) ->
                ItemFactory.create(Material.GRASS_BLOCK).setName(a.isPaused() ? ChatColor.RED + a.toString() + " (disabled)" :
                        ChatColor.GREEN + a.toString() + " (enabled)").emptyLoreLine().addLoreLine(ChatColor.YELLOW + "CLICK to enable/disable task")
                        .create());
        runnableMenu.addOnItemClick((runnable, event) -> {
            runnable.setPaused(!runnable.isPaused());
            runnableMenu.display(event.getWhoClicked());
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            if (runnable.isPaused())
                Message.GOOD_PREFIX("Successfully stopped task \"" + runnable.toString() + "\"");
            else Message.GOOD_PREFIX("Successfully enabled task \"" + runnable.toString() + "\"");
        });
        runnableMenu.setReturntoGui(this);
        initButtons();
        addFunctions();
        registerButtons();
    }
    
    private void addFunctions() {
        viewInv.addAction(a -> {
            if (ManhuntGame.getManhuntGame() == null) {
                Message.ERROR_PREFIX("Sorry, but this only works if a game is running").send(a.getWhoClicked());
            } else playerInv.display(a.getWhoClicked());
        });
        giveKit.addAction(a -> {
            if (ManhuntGame.getManhuntGame() == null) {
                Message.ERROR_PREFIX("Sorry, but this only works if a game is running").send(a.getWhoClicked());
            } else giveKitInv.display(a.getWhoClicked());
            
        });
        // todo add action for enableDebug
        manipTask.addAction(a -> runnableMenu.display(a.getWhoClicked()));
        removePlayer.addAction(a -> {
            if (ManhuntGame.getManhuntGame() == null)
                Message.ERROR_PREFIX("Sorry, but this only works if a game is running").send(a.getWhoClicked());
            else remPlayerInv.display(a.getWhoClicked());
        });
    
        stopGame.addAction(a -> {
            if (ManhuntGame.getManhuntGame() == null)
                Message.ERROR_PREFIX("Sorry, there are no games to stop").send(a.getWhoClicked());
            else {
                Message.GOOD_PREFIX("Stopping game...").send(a.getWhoClicked());
                ManhuntGame.getManhuntGame().stopGame();
                ManhuntGame.GameState state = ManhuntGame.getManhuntGame().getGameState();
                if (state == ManhuntGame.GameState.ENDED) {
                    Message.GOOD_PREFIX("Successfully stopped game").send(a.getWhoClicked());
                    ((Player) a.getWhoClicked()).playSound(a.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                } else Message.ERROR_PREFIX("Couldn't stop game").send(a.getWhoClicked());
            }
        });
        abortGame.addAction(a -> {
            if (ManhuntGame.getManhuntGame() == null)
                Message.ERROR_PREFIX("Sorry, there are no games to start").send(a.getWhoClicked());
            else {
                Message.GOOD_PREFIX("Aborting game...").send(a.getWhoClicked());
                ManhuntGame.getManhuntGame().abortGame(); // todo add abort function
                ManhuntGame.GameState state = ManhuntGame.getManhuntGame().getGameState();
                if (state == ManhuntGame.GameState.ABORTED) {
                    Message.GOOD_PREFIX("Successfully aborted game").send(a.getWhoClicked());
                    ((Player) a.getWhoClicked()).playSound(a.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                } else Message.ERROR_PREFIX("Couldn't abort game").send(a.getWhoClicked());
            }
        });
        gameLog.addAction(action -> {
           gameLogMenu.display(action.getWhoClicked());
        });
        
        startButton.addAction(a -> {
            if (ManhuntGame.getManhuntGame() != null) {
                Message.ERROR_PREFIX("Sorry, a game is already running. Please ensure that the current game is stopped first!").send(a.getWhoClicked());
            } else menu.display(a.getWhoClicked());
        });
        
//        runnableMenu_manipAll.addAction(a -> {
//            if (a.isRightClick())
//                Manhunt.getInstance().getTaskManager().getRepeatedRunnables().forEach(r -> r.setPaused(true));
//            else Manhunt.getInstance().getTaskManager().getRepeatedRunnables().forEach(r -> r.setPaused(false));
//        });
    }
    
    private void registerButtons() {
        setButton(viewInv).setButton(giveKit).setButton(gameLog).setButton(stopGame).setButton(abortGame).setButton(manipTask)
                .setButton(removePlayer).setButton(headerButton).setButton(startButton);
    }
    
    private void initButtons() {
        headerButton = new Button(4, ItemFactory.create(Material.COMPASS).setName(ChatColor.RED + "Admin tools inventory").emptyLoreLine()
        .addLoreLine(ChatColor.YELLOW + "Manage options here").create());
        if (ManhuntGame.getManhuntGame() != null) {
            viewInv = new Button(19, ItemFactory.create(Material.CHEST)
                    .setName(ChatColor.GOLD + "View player inventories")
                    .setLore(new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to " +
                            "view player inventories").addEntries(ChatColor.YELLOW + "This option includes...").addList()
                            .addEntries(ChatColor.BLUE + "Viewing player inventories", ChatColor.BLUE + "Altering player inventories").createList().getLoreList())
                    .create());
    
            giveKit = new Button(20, ItemFactory.create(Material.GOLDEN_SWORD)
                    .setName(ChatColor.GOLD + "Give kits")
                    .setLore(new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to give kits to players",
                            ChatColor.YELLOW + "This option includes...").addList().addEntries(ChatColor.BLUE + "Choosing to give a " +
                            "player a certain kit").addEntries(ChatColor.BLUE + "Choosing to give all players a certain kit").createList().getLoreList()).create());
            removePlayer = new Button(28, ItemFactory.create(Material.PLAYER_HEAD).setName(ChatColor.GOLD + "Exclude player")
                    .setLore(new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to chose what player to exclude", ChatColor.YELLOW + "This option includes...")
                            .addList().addEntries(ChatColor.BLUE + "Removing certain player(s) from the game").createList().getLoreList()).create());
        } else {
            viewInv = new Button(19, ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create());
            giveKit = new Button(20, ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create());
            removePlayer = new Button(28, ItemFactory.create(Material.RED_STAINED_GLASS_PANE).setName(" ").create());
        }
        gameLog = new Button(21, ItemFactory.create(Material.RED_STAINED_GLASS_PANE)
                .setName(ChatColor.GOLD + "Modify game log settings")
                .setLore(
                        new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to modify game logger settings",
                                ChatColor.YELLOW + "The game logger settings will only affect", ChatColor.YELLOW + "you")
                        .addEntries(ChatColor.YELLOW + "Game logger levels include...").addList()
                        .addEntries(ChatColor.GREEN + "LOW", ChatColor.YELLOW + "MEDIUM", ChatColor.RED + "HIGH").createList()
                        .addEntries(ChatColor.BLUE + "Each ascending logger level gets closer", ChatColor.BLUE + "to code-level and is more thorough")
                        .getLoreList()
                        
                )
                .create());
        
        stopGame = new Button(23, ItemFactory.create(Material.ACTIVATOR_RAIL).setName(ChatColor.GOLD + "Stop game")
                .setLore(new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to stop game", ChatColor.YELLOW + "This option includes...")
                .addList().addEntries(ChatColor.BLUE + "Choosing to stop the game", ChatColor.BLUE + "Dumping game data (if enabled)",
                                ChatColor.BLUE + "Saving player data (if enabled)").createList().getLoreList()).create());
        
        abortGame = new Button(24, ItemFactory.create(Material.REDSTONE).setName(ChatColor.GOLD + "Abort game")
                .setLore(new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to abort game", ChatColor.YELLOW + "This option includes...")
                .addList().addEntries(ChatColor.BLUE + "Force stopping the game", ChatColor.BLUE + "Not being able to save data").createList().getLoreList()).create());
        
        manipTask = new Button(25, ongoingTasks());
        
        startButton = new Button(34, ItemFactory.create(Material.GRASS).setName(ChatColor.GOLD + "Start game")
                    .setLore(new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to open game start GUI", ChatColor.YELLOW + "This option includes...")
                            .addList().addEntries(ChatColor.BLUE + "Choosing specific options", ChatColor.BLUE + "Selecting players",
                                    ChatColor.BLUE + "Selecting kits", ChatColor.BLUE + "Choosing speedrunner and other game options").createList().getLoreList()).create());
     
        //runnableMenu_manipAll = new Button(runnableMenu.getSize() - 5, ItemFactory.create(Material.HOPPER).setName(ChatColor.YELLOW + "Apply for all")
              //  .setLore(new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "LEFT CLICK to enable all", ChatColor.YELLOW + "RIGHT CLICK to disable all").getLoreList())
              //  .create());
    }
    
    private ItemStack ongoingTasks() {
        List<RepeatedRunnable> list = Manhunt.getInstance().getTaskManager().getRepeatedRunnables();
        List<String> names = new ArrayList<>();
        list.forEach(r -> {
            String name = ChatColor.BLUE + r.toString();
            if (names.size() <= 5) {
                if (r.isPaused())
                    name += " " + ChatColor.RED + "(disabled)";
                else name += " " + ChatColor.GREEN + "(enabled)";
                names.add(shortenTaskName(name));
            } else if (names.size() == 6) {
                names.add(ChatColor.BLUE + "... and more");
            }
        });
        List<String> lore = new LoreCreator().emptyLine().addEntries(ChatColor.YELLOW + "CLICK to manipulate repeated tasks",
                ChatColor.YELLOW + "Registered repeated tasks include...").addList().addEntries(names).createList().emptyLine()
                .addEntries(ChatColor.RED + "Note -:", ChatColor.YELLOW + "Tampering with the options can produce unexpected outcomes",
                        ChatColor.YELLOW + "This doesn't include actions that are triggered by events").getLoreList();
        return ItemFactory.create(Material.BELL).setName(ChatColor.GOLD + "Disable/Enable tasks").setLore(lore).create();
    }
    
}
