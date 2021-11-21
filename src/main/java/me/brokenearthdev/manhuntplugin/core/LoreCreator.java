package me.brokenearthdev.manhuntplugin.core;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Helps create an appropriate lore for an item
 */
@Deprecated
public class LoreCreator {
    
    private final List<String> lore = new LinkedList<>();
    
    public LoreCreator addEntries(String... str) {
        lore.addAll(Arrays.asList(str));
        return this;
    }
    
    public LoreCreator addEntries(List<String> list) {
        list.forEach(this::addEntries);
        return this;
    }
    
    public LoreListCreator addList(String bullet, ChatColor bulletColor, ChatColor entryColor) {
        return new LoreListCreator(this, bullet, bulletColor, entryColor);
    }
    
    public LoreListCreator addList(String bullet, ChatColor bulletColor) {
        return addList(bullet, bulletColor, ChatColor.YELLOW);
    }
    
    public LoreListCreator addList() {
        return addList("-", ChatColor.DARK_PURPLE);
    }
    
    public LoreCreator emptyLine() {
        lore.add(" ");
        return this;
    }
    
    public List<String> getLoreList() {
        return lore;
    }
    
//    private static List<String> split(String desc, int maxChar) {
//        String[] words = desc.split("\\c+");
//        StringBuilder lineBuilder = new StringBuilder();
//        List<String> lines = new LinkedList<>();
//        int sum = 0;
//        for (int i = 0; i < words.length; i++) {
//            if (words[i].length() > maxChar) {
//                lines.add(lineBuilder.toString());
//                lineBuilder = new StringBuilder();
//                lines.add(words[i]);
//                sum = 0;
//                continue;
//            }
//            sum += words[i].length();
//            if (sum > maxChar) {
//                // split
//                lines.add(lineBuilder.toString());
//                lineBuilder = new StringBuilder();
//                sum -= words[i].length();
//                i--;
//                sum = 0;
//            } else lineBuilder.append(words[i]).append(" ");
//        }
//        return lines;
//    }
    
    
    /**
     * Helps clear a lore list
     */
    @Deprecated
    public static class LoreListCreator extends LoreCreator {
        
        private final List<String> listItems = new LinkedList<>();
        private final String bullet;
        private final ChatColor bulletColor;
        private final ChatColor entryColor;
        private final LoreCreator creator;
        
        public LoreListCreator(LoreCreator creator, String bullet, ChatColor bulletColor, ChatColor entryColor) {
            this.bullet = bullet;
            this.bulletColor = bulletColor;
            this.entryColor = entryColor;
            this.creator = creator;
        }
        
        @Override
        public LoreListCreator addEntries(String... str) {
            for (String string : str) {
                this.listItems.add(bulletColor + bullet + entryColor + " " + string);
            }
            return this;
        }
    
        @Override
        public LoreListCreator addList(String bullet, ChatColor bulletColor, ChatColor entryColor) {
            return super.addList(" " + bullet, bulletColor, entryColor);
        }
    
        @Override
        public LoreListCreator addEntries(List<String> strings) {
            strings.forEach(this::addEntries);
            return this;
        }
        
        public LoreCreator createList() {
            creator.addEntries(listItems);
            return creator;
        }
        
    }
    
}
