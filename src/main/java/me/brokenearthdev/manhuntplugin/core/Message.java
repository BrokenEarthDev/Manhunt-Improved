package me.brokenearthdev.manhuntplugin.core;



import org.bukkit.entity.Entity;

import java.util.LinkedList;
import java.util.List;

import static org.bukkit.ChatColor.*;

/**
 * Represents a built-in message.
 */
public class Message {
    
    public static final String PREFIX = AQUA.toString() + BOLD + "[Manhunt]" + WHITE;
    
    /**
     * Contains default {@link MessageStructure}s
     */
    public enum DefaultMessageType {
        ERROR_PREFIX(new MessageStructure(PREFIX + RED + " ")),
        NORMAL_PREFIX(new MessageStructure(PREFIX + WHITE + " ")),
        INFO_PREFIX(new MessageStructure(PREFIX + BLUE + " ")),
        GOOD_PREFIX(new MessageStructure(PREFIX + GREEN + " ")),
        ERROR(new MessageStructure(RED.toString())),
        NORMAL(new MessageStructure("")),
        INFO(new MessageStructure(BLUE.toString())),
        GOOD(new MessageStructure(GREEN.toString()));
        
        private final MessageStructure struct;
        DefaultMessageType(MessageStructure struct) {
            this.struct = struct;
        }
        
        public MessageStructure getStruct() {
            return struct;
        }
        
    }
    
    private final List<String> lines = new LinkedList<>();
    private final MessageStructure struct;
    
    public Message(MessageStructure struct) {
        if (struct == null) this.struct = DefaultMessageType.NORMAL.getStruct();
        else this.struct = struct;
    }
    
    public Message addLine(String msg) {
        lines.add(struct.constructMessage(msg));
        return this;
    }
    
    public Message addFirst(String msg) {
        String str = struct.constructMessage(msg);
        lines.add(0, str);
        return this;
    }
    
    public Message append(String part) {
        if (lines.size() != 0) {
            String str = lines.get(lines.size() - 1);
            lines.set(lines.size() - 1, str + part);
        } else {
            addLine(part);
        }
        return this;
    }
    
    public Message clear() {
        lines.clear();
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        lines.forEach(line -> builder.append(line).append("\n"));
        return lines.size() > 0 ? builder.substring(0, builder.length() - 1) : "";
    }
    
    public void send(Entity entity) {
        lines.forEach(entity::sendMessage);
    }
    
    public static class MessageStructure {
        
        private final String precedingStr;
        public MessageStructure(String precedingStr) {
            this.precedingStr = precedingStr;
        }
        
        public String constructMessage(String msg) {
            return precedingStr + msg;
        }
        
    }
    
    public static Message ERROR(String msg) {
        return new Message(DefaultMessageType.ERROR.getStruct()).addLine(msg);
    }
    
    public static Message ERROR_PREFIX(String msg) {
        return new Message(DefaultMessageType.ERROR_PREFIX.getStruct()).addLine(msg);
    }
    
    public static Message NORMAL_PREFIX(String msg) {
        return new Message(DefaultMessageType.NORMAL_PREFIX.getStruct()).addLine(msg);
    }
    
    public static Message INFO_PREFIX(String msg) {
        return new Message(DefaultMessageType.INFO_PREFIX.getStruct()).addLine(msg);
    }
    
    public static Message GOOD_PREFIX(String msg) {
        return new Message(DefaultMessageType.GOOD_PREFIX.getStruct()).addLine(msg);
    }
    
    public static Message NORMAL(String msg) {
        return new Message(DefaultMessageType.NORMAL.getStruct()).addLine(msg);
    }
    
    public static Message INFO(String msg) {
        return new Message(DefaultMessageType.INFO.getStruct()).addLine(msg);
    }
    
    public static Message GOOD(String msg) {
        return new Message(DefaultMessageType.GOOD.getStruct()).addLine(msg);
    }
    
}