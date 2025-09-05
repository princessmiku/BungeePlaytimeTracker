package de.midevelopment.minecraft.bungeePlaytimeTracker;

import de.midevelopment.minecraft.bungeePlaytimeTracker.database.Database;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.ConfigHandler;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;

import static de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint.*;

public final class BungeePlaytimeTracker extends Plugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // load config
        setConfigHandler(new ConfigHandler(
                this.getClass(),                // Class of the plugin
                getDataFolder(),                // Plugin directory
                "config.yml",                   // config file name
                getLogger()                     // Logger for logging messages and errors
        ));
        ConfigHandler configHandler = getConfigHandler();
        getDatabase().init(
                configHandler.get("database.host"),
                configHandler.get("database.port"),
                configHandler.get("database.database"),
                configHandler.get("database.username"),
                configHandler.get("database.password"),
                false
        );
        try {
            getDatabase().createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
