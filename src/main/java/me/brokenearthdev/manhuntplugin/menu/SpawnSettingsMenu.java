package me.brokenearthdev.manhuntplugin.menu;

import me.brokenearthdev.manhuntplugin.core.gui.buttons.Button;
import me.brokenearthdev.manhuntplugin.core.gui.menu.DerivativeMenu;
import me.brokenearthdev.manhuntplugin.core.gui.menu.GameMenu;
import me.brokenearthdev.manhuntplugin.core.gui.options.DynamicOption;
import static me.brokenearthdev.manhuntplugin.GameItems.SpawnSettingsMenuGUI.*;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class SpawnSettingsMenu extends DerivativeMenu {
    
    // Spawn distance modifiers
    private DynamicOption[] dSpawnMod = new DynamicOption[8];
    
    // Hunter distance modifiers
    private DynamicOption[] dHunterMod = new DynamicOption[8];
    
    // Spawn & hunter distance
    private final DynamicOption dSpawn = new DynamicOption(this, 20) {
        @Override
        public ItemStack refreshItem() {
            return distanceFromSpawn(spawnDist);
        }
    };
    
    private final DynamicOption dHunter = new DynamicOption(this, 24) {
        @Override
        public ItemStack refreshItem() {
            return distanceFromHunter(hunterDist);
        }
    };
    
    private int spawnDist, hunterDist;
    
    private boolean first = true;
    
    public SpawnSettingsMenu(GameMenu fallBack, int spawnDist, int hunterDist) {
        super("Spawn Settings", 6, fallBack, new Button(49, back()));
        this.spawnDist = spawnDist;
        this.hunterDist = hunterDist;
        createModifiers();
    }
    
    public int getSpawnDistance() {
        return spawnDist;
    }
    
    public int getHunterDistance() {
        return hunterDist;
    }
    
    @Override
    public void display(HumanEntity entity) {
        if (first) {
            dSpawn.getButton().setItem(dSpawn.refreshItem());
            dHunter.getButton().setItem(dHunter.refreshItem());
            first = false;
        }
        super.display(entity);
    }
    
    /**
     * Creates modifiers so that players can alter the spawn distance
     * from the original spawn and hunters
     */
    private void createModifiers() {
        int[] dSpawnSlots = {1, 2, 3, 11, 29, 37, 38, 39};
        int[] dInc = {16, 64, 1024, 1, 1, 16, 64, 1024};
        for (int i = 0; i < dSpawnMod.length; i++) {
            int finalI = i;
            dSpawnMod[i] = new DynamicOption(this, dSpawnSlots[i]) {
                public ItemStack refreshItem() {
                    return dChange(finalI > 3 ? -dInc[finalI] : dInc[finalI]);
                }
            };
            
            dSpawnMod[i].addAction(e -> {
                int change = finalI > 3? -dInc[finalI] : dInc[finalI];
                if (change + spawnDist < 0)
                    change = 0;
                else spawnDist += change;
                dSpawnMod[finalI].getButton().setItem(dSpawnMod[finalI].refreshItem());
                dSpawn.getButton().setItem(dSpawn.refreshItem());
                display(e.getWhoClicked());
            });
            
            dHunterMod[i] = new DynamicOption(this, dSpawnSlots[i] + 4) {
                public ItemStack refreshItem() {
                    return dSpawnMod[finalI].getButton().getItem();
                }
            };
            
            dHunterMod[i].addAction(e -> {
                int change = finalI > 3? -dInc[finalI] : dInc[finalI];
                if (change + hunterDist < 0)
                    change = hunterDist;
                hunterDist += change;
                dHunterMod[finalI].getButton().setItem(dHunterMod[finalI].refreshItem());
                dHunter.getButton().setItem(dHunter.refreshItem());
                display(e.getWhoClicked());
            });
        }
    }
    
    
}

/*]
private final Button back;
    
    // todo support max and min

    private final Button[] distanceFromSpawnButtons = new Button[8];
    private final Button[] distanceFromHunterButtons = new Button[8];
    
    private int distFromSpawn, distFromHunter;
    
    private DynamicOption distanceFromSpawnOption;
    private DynamicOption distanceFromHunterOption;
    
    public SpawnSettingsMenu(int initDistFromSpawn, int initDistFromHunter) {
        super("Spawn settings", 6);
        this.distFromSpawn = initDistFromSpawn;
        this.distFromHunter = initDistFromHunter;
        distanceFromSpawnButtons[0] = new Button(1, Utils.niButtonOf(16));
        distanceFromSpawnButtons[1] = new Button(2, Utils.niButtonOf(64));
        distanceFromSpawnButtons[2] = new Button(3, Utils.niButtonOf(1024));
        distanceFromSpawnButtons[3] = new Button(11, Utils.niButtonOf(1));
        distanceFromSpawnButtons[4] = new Button(29, Utils.ndButtonOf(1));
        distanceFromSpawnButtons[5] = new Button(37, Utils.ndButtonOf(16));
        distanceFromSpawnButtons[6] = new Button(38, Utils.ndButtonOf(64));
        distanceFromSpawnButtons[7] = new Button(39, Utils.ndButtonOf(1024));
        for (int i = 0; i < distanceFromSpawnButtons.length; i++) {
            distanceFromHunterButtons[i] = new Button(distanceFromSpawnButtons[i].getSlot() + 4, distanceFromSpawnButtons[i].getItem());
            int finalI = i;
            distanceFromSpawnButtons[i].addAction(action -> {
               distFromSpawn += distanceFromSpawnButtons[finalI].getItem().getAmount();
               distanceFromSpawnOption.refreshItem();
               display(action.getWhoClicked());
            });
            distanceFromHunterButtons[i].addAction(action -> {
                distFromHunter += distanceFromHunterButtons[finalI].getItem().getAmount();
                distanceFromHunterOption.refreshItem();
                display(action.getWhoClicked());
            });
            setButton(distanceFromHunterButtons[i]).setButton(distanceFromSpawnButtons[i]);
        }
        distanceFromSpawnOption = new DynamicOption(this, 20) {
            @Override
            public ItemStack refreshItem() {
                return ItemFactory.create(Material.RAIL)
                        .setName(ChatColor.GOLD + "Distance from world spawn (" + distFromSpawn + "m)")
                        .create();
            }
        };
        distanceFromHunterOption = new DynamicOption(this, 24) {
            @Override
            public ItemStack refreshItem() {
                return ItemFactory.create(Material.POWERED_RAIL)
                        .setName(ChatColor.GOLD + "Distance from hunter spawn (" + distFromHunter + "m)")
                        .create();
            }
        };
        back = new Button(49, ItemFactory.create(Material.GREEN_CONCRETE)
                            .setName(ChatColor.GREEN + "Back")
                            .create());
        
    }
    
    public int getDistFromSpawn() {
        return distFromSpawn;
    }
    
    public int getDistFromHunter() {
        return distFromHunter;
    }
    
    public Button getBackButton() {
        return back;
    }
 */
