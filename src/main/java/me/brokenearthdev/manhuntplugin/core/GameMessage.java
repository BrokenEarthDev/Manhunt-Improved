package me.brokenearthdev.manhuntplugin.core;

import org.bukkit.ChatColor;

public class GameMessage extends Message {
    
    private GameMessage(MessageStructure struct) {
        super(struct);
    }
    
    public static GameMessage WIN(String text) {
        return (GameMessage) new GameMessage(DefaultMessageType.GOOD.getStruct()).addLine(text);
    }
    
    public static GameMessage LOSS(String text) {
        return (GameMessage) new GameMessage(DefaultMessageType.ERROR.getStruct()).addLine(text);
    }
    
    public static GameMessage INFO(String text) {
        return (GameMessage) new GameMessage(new MessageStructure(ChatColor.YELLOW.toString())).addLine(text);
    }
    
    
    
}
