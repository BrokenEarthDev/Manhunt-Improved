package me.brokenearthdev.manhuntplugin.kits;

import me.brokenearthdev.manhuntplugin.core.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

class KitItems {
    
    // rookie kit
    static List<ItemStack> rookieRunners = Arrays.asList(ItemFactory.create(Material.WOODEN_SWORD).create(),
            ItemFactory.create(Material.WOODEN_PICKAXE).create(), ItemFactory.create(Material.WOODEN_AXE).create(),
            ItemFactory.create(Material.OAK_LOG).setAmount(8).create());
    static List<ItemStack> rookieHunters = Arrays.asList(ItemFactory.create(Material.OAK_WOOD).setAmount(6).create(),
            ItemFactory.create(Material.STICK).setAmount(4).create());
    
    // knight kit
    static List<ItemStack> knightRunners = Arrays.asList(ItemFactory.create(Material.STONE_SWORD)
                    .addEnchant(Enchantment.DAMAGE_ALL, 1).create(), ItemFactory.create(Material.IRON_CHESTPLATE).create(),
            ItemFactory.create(Material.COOKED_BEEF).setAmount(2).create());
    static List<ItemStack> knightHunters = Arrays.asList(ItemFactory.create(Material.WOODEN_SWORD).create(),
            ItemFactory.create(Material.ANVIL).create());
    // Bouncer kit
    static  List<ItemStack> bouncerRunners = Arrays.asList(jumpPotion(),
            ItemFactory.create(Material.SLIME_BLOCK).setAmount(32).create(), ItemFactory.create(Material.IRON_BOOTS)
                    .addEnchant(Enchantment.PROTECTION_FALL, 3).create());
    static  List<ItemStack> bouncerHunters = Arrays.asList(jumpPotionNoSpeed(), ItemFactory.create(Material.SLIME_BLOCK)
            .setAmount(8).create(), ItemFactory.create(Material.GOLDEN_BOOTS).addEnchant(Enchantment.PROTECTION_FALL, 1)
            .create());
    
    // Warrior Kit
    static  List<ItemStack> warriorRunners = Arrays.asList(ItemFactory.create(Material.GOLDEN_SWORD).addEnchant(Enchantment.DAMAGE_ALL,
            5).create(), ItemFactory.create(Material.GOLDEN_APPLE).create(), ItemFactory.create(Material.GOLDEN_CHESTPLATE)
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5).create());
    static List<ItemStack> warriorHunters = Arrays.asList(ItemFactory.create(Material.IRON_SWORD).create(), ItemFactory.create(Material.IRON_CHESTPLATE)
            .create());
    
    private static ItemStack jumpPotion() {
        ItemStack stack = new ItemStack(Material.POTION, 3);
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 60 * 20, 4, false), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 3, false), true);
        meta.setDisplayName(ChatColor.GREEN + "Bouncer potion");
        stack.setItemMeta(meta);
        return stack;
    }
    
    private static ItemStack jumpPotionNoSpeed() {
        ItemStack stack = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 60 * 20, 2, false), true);
        meta.setDisplayName(ChatColor.GREEN + "Bouncer Potion");
        stack.setItemMeta(meta);
        return stack;
    }
    
}
