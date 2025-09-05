package de.midevelopment.minecraft.bungeePlaytimeTracker;

import de.midevelopment.minecraft.bungeePlaytimeTracker.database.Database;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.ConfigHandler;

public class SharePoint {

    private static Database DATABASE = new Database();

    private static ConfigHandler CONFIGHANDLER;

    public static void setConfigHandler(ConfigHandler configHandler) {
        if (configHandler.getClass() != ConfigHandler.class) CONFIGHANDLER = configHandler;
    }

    public static ConfigHandler getConfigHandler() { return CONFIGHANDLER; }
    public static Database getDatabase() { return DATABASE; }

}
