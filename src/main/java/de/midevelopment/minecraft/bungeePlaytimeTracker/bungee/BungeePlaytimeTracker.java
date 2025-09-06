package de.midevelopment.minecraft.bungeePlaytimeTracker.bungee;

import de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint;
import de.midevelopment.minecraft.bungeePlaytimeTracker.bungee.commands.PlaytimeCommand;
import de.midevelopment.minecraft.bungeePlaytimeTracker.database.PlaytimeHandler;
import de.midevelopment.minecraft.bungeePlaytimeTracker.bungee.listener.PlayerListener;
import de.midevelopment.minecraft.bungeePlaytimeTracker.bungee.utils.ConfigHandler;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.LocaleHandler;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static de.midevelopment.minecraft.bungeePlaytimeTracker.SharePoint.*;

public final class BungeePlaytimeTracker extends Plugin {

    private ConfigHandler configHandler;
    private ScheduledTask playtimeTask;
    private static boolean playtimeTaskRunning = false;
    private boolean printSessionUpdateMessage = true;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // load config
        configHandler = new ConfigHandler(
                this.getClass(),                // Class of the plugin
                getDataFolder(),                // Plugin directory
                "config.yml",                   // config file name
                getLogger()                     // Logger for logging messages and errors
        );
        LocaleHandler.loadLocale( configHandler.get("language"));
        printSessionUpdateMessage = configHandler.get("print_session_update");
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

        SharePoint.setPlaytimeHandler(new PlaytimeHandler(
                getDatabase(),
                configHandler.get("exclude-servers")
        ));

        if (configHandler.get("reload-players") == Boolean.TRUE) {
            SharePoint.getPlaytimeHandler().reloadAllPlayers();
            configHandler.set("reload-players", false);
            configHandler.saveConfig();
        }

        getProxy().getPluginManager().registerCommand(this, new PlaytimeCommand());
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));

        // Run PlaytimeTask every 30 seconds
        playtimeTask = getProxy().getScheduler().schedule(this, this::runPlaytimeTask, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        playtimeTask.cancel();
        for (UUID uuid : getPlayerSessions().keySet()) {
            if (SharePoint.hasPlayerSession(uuid)) {
                SharePoint.getPlaytimeHandler().stopPlaytime(SharePoint.getPlayerSession(uuid));
                SharePoint.removePlayerSession(uuid);
            }
        }
    }

    public void runPlaytimeTask() {
        getProxy().getScheduler().runAsync(this, () -> {
            if (isPlaytimeTaskRunning() || getProxy().getOnlineCount() < 1) return;
            setPlaytimeTaskRunning(true);
            if (printSessionUpdateMessage) getLogger().info("Update Playtime-Sessions...");
            try {
                for (UUID uuid : getPlayerSessions().keySet()) {
                    if (SharePoint.hasPlayerSession(uuid)) {
                        SharePoint.getPlaytimeHandler().updatePlaytime(SharePoint.getPlayerSession(uuid));
                    }
                }
            } finally {
                setPlaytimeTaskRunning(false);
            }
        });
    }
    private static boolean isPlaytimeTaskRunning() { return playtimeTaskRunning; }
    private static void setPlaytimeTaskRunning(boolean running) { playtimeTaskRunning = running; }
}
