package de.midevelopment.minecraft.bungeePlaytimeTracker.spigot;

import de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint;
import de.midevelopment.minecraft.bungeePlaytimeTracker.database.PlaytimeHandler;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.LocaleHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import static de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint.getDatabase;

public class BungeePlaytimeTrackerSpigot extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        reloadConfig();
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        LocaleHandler.loadLocale(config.getString("language"));
        SharePoint.getDatabase().init(
                config.getString("database.host"),
                config.getInt("database.port"),
                config.getString("database.database"),
                config.getString("database.username"),
                config.getString("database.password"),
                false
        );
        SharePoint.setPlaytimeHandler(new PlaytimeHandler(
                SharePoint.getDatabase(),
                config.getStringList("exclude-servers")
        ));
        new PlaceholderAPIWrapper().register();
    }

    @Override
    public void onDisable() {

    }

}
