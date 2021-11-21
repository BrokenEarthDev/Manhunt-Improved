package me.brokenearthdev.manhuntplugin.main;

import me.brokenearthdev.manhuntplugin.core.config.Entries;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Manages configuration files
 */
public class ConfigurationManager {

    // Files
    private final File dataFolder = new File("plugins/ManhuntPlugin");
    private final File settingsFile = new File("plugins/ManhuntPlugin/settings.yml");
    private final File dataFile = new File("plugins/ManhuntPlugin/data.yml");

    private YamlConfiguration settingsConfig;
    private YamlConfiguration dataConfig;
    
    private Entries lastCaptured;
    
    public YamlConfiguration getSettingsConfig() {
        return settingsConfig;
    }
    public YamlConfiguration getDataConfig() {
        return dataConfig;
    }
    
    private boolean configRunning = false;
    
    public boolean createIfNotExist() {
        try {
            boolean created = createIfNotExistWException();
            configRunning = created;
            return created;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean createIfNotExistWException() throws Exception {
        if (!createFileIfNotExists(dataFolder, true)) {
            Manhunt.getInstance().getLogger().warning("Data folder couldn't be created");
            Manhunt.getInstance().getLogger().warning("Please inspect this issue");
            Manhunt.getInstance().getLogger().warning("It can be because of the options set by the server");
            Manhunt.getInstance().getLogger().warning("No files can be created");
        } else if (!createFileIfNotExists(settingsFile, false))
            Manhunt.getInstance().getLogger().warning("Settings file can't be created");
        else if (!createFileIfNotExists(dataFile, false))
            Manhunt.getInstance().getLogger().warning("Data file couldn't be created");
        else return true;
        return false;
    }
    
    public boolean initConfigs() {
        if (!configRunning) return false;
        settingsConfig = YamlConfiguration.loadConfiguration(settingsFile);
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        return true;
    }
    
    public boolean saveConfigs() {
        try {
            Manhunt.getInstance().getLogger().info("Attempting to save config");
            settingsConfig.save(settingsFile);
            Manhunt.getInstance().getLogger().fine("Configs successfully saved");
        } catch (IOException e) {
            Manhunt.getInstance().getLogger().warning("Can't save the settings config file");
            Manhunt.getInstance().getLogger().warning("The last copy will be saved");
            e.printStackTrace();
        }
    
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            Manhunt.getInstance().getLogger().warning("Can't save the data config file");
            Manhunt.getInstance().getLogger().warning("The last copy will be saved");
            e.printStackTrace();
        }
        return true;
    }
    
    private boolean createFileIfNotExists(File file, boolean folder) throws Exception {
        if (!file.exists()) {
            if (folder)
                return file.mkdir();
            else return file.createNewFile();
        }
        return true;
    }
    
    public void recaptureEntries(boolean write) {
        if (write)
            lastCaptured.writeAll();
        lastCaptured = new Entries();
    }
    
    public Entries getLastCaptured() {
        return lastCaptured;
    }
    
}
