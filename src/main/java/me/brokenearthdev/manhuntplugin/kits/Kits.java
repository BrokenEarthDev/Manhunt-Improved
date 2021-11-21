package me.brokenearthdev.manhuntplugin.kits;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Kits {
    
    public static RookieKit ROOKIE_KIT;
    public static KnightKit KNIGHT_KIT;
    public static BouncerKit BOUNCER_KIT;
    public static WarriorKit WARRIOR_KIT;
    
    // Exclude from group of kits
    public static NoKit NO_KIT = new NoKit();
    
    public static final Map<String, Kit> stringKitMap;
    
    static {
        WARRIOR_KIT = new WarriorKit();
        KNIGHT_KIT = new KnightKit();
        ROOKIE_KIT = new RookieKit();
        BOUNCER_KIT = new BouncerKit();
        stringKitMap = new HashMap<>();
        stringKitMap.put("rookie", ROOKIE_KIT);
        stringKitMap.put("knight", KNIGHT_KIT);
        stringKitMap.put("warrior", WARRIOR_KIT);
        stringKitMap.put("bouncer", BOUNCER_KIT);
    }
    
    public static List<Kit> getAllKits() {
        List<Kit> kits = new ArrayList<>();
        stringKitMap.forEach((key, value) -> kits.add(value));
        return kits;
    }
    
    public static class NoKit extends Kit {
        public NoKit() {
            super(Material.BARRIER, "No kit", new ArrayList<>());
        }
    }
    
    public static class RookieKit extends Kit {
        public RookieKit() {
            super(Material.WOODEN_SWORD, "rookie", Arrays.asList(ItemFactory.create(Material.WOODEN_SWORD).create(),
                    ItemFactory.create(Material.WOODEN_PICKAXE).create(), ItemFactory.create(Material.WOODEN_AXE).create(),
                    ItemFactory.create(Material.OAK_LOG).setAmount(8).create()));
            containsName.clear();
            containsName = Arrays.asList("1x Wooden sword", "1x Wooden pickaxe", "1x Wooden axe", "8x Oak logs");
        }
    }
    
    public static class KnightKit extends Kit {
        public KnightKit() {
            super(Material.STONE_SWORD, "knight",  Arrays.asList(ItemFactory.create(Material.STONE_SWORD)
                            .addEnchant(Enchantment.DAMAGE_ALL, 1).create(), ItemFactory.create(Material.IRON_CHESTPLATE).create(),
                    ItemFactory.create(Material.COOKED_BEEF).setAmount(2).create()));
            containsName.clear();
            containsName = Arrays.asList("1x Stone sword (Damage I)", "1x Iron chestplate", "2x Cooked beef");
        }
    }
    
    public static class BouncerKit extends Kit {
        public BouncerKit() {
            super(Material.SLIME_BLOCK, "bouncer", Arrays.asList(jumpPotion(),
                    ItemFactory.create(Material.SLIME_BLOCK).setAmount(32).create(), ItemFactory.create(Material.IRON_BOOTS)
                            .addEnchant(Enchantment.PROTECTION_FALL, 3).create()));
            containsName.clear();
            containsName = Arrays.asList("1x Bouncer's potion", "32x Slime blocks", "1x Iron boots (Fall protection III)");
        }
    }
    
    public static class WarriorKit extends Kit {
        public WarriorKit() {
            super(Material.GOLDEN_SWORD, "warrior", Arrays.asList(ItemFactory.create(Material.GOLDEN_SWORD).addEnchant(Enchantment.DAMAGE_ALL,
                    5).create(), ItemFactory.create(Material.GOLDEN_APPLE).create(), ItemFactory.create(Material.GOLDEN_CHESTPLATE)
                    .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5).create()));
            containsName.clear();
            containsName = Arrays.asList("1x Golden sword (Damage V)", "1x Golden apple", "1x Golden chestplate (Protection V)");
        }
    }
    
    public static Kit parseKit(String name) {
        return stringKitMap.get(name);
    }
    
    private static ItemStack jumpPotion() {
        ItemStack stack = new ItemStack(Material.POTION, 3);
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 60 * 20, 4, false), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 3, false), true);
        meta.setDisplayName(ChatColor.GREEN + "Bouncer potion");
        stack.setItemMeta(meta);
        return stack;
    }
    
}
