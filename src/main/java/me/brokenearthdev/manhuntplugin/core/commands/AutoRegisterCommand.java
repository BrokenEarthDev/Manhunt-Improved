package me.brokenearthdev.manhuntplugin.core.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Automatically registers this command class. Simply annotate any class
 * with this so that a code piece will be generated, registering the command
 * class.
 * <p>
 * In order for this to work, there should be an <i>empty</i> constructor that requires
 * no arguments because {@link org.bukkit.Bukkit} requires an object for a class to get
 * registered.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRegisterCommand {
}
