package de.midevelopment.minecraft.bungeePlaytimeTracker;

import de.midevelopment.minecraft.bungeePlaytimeTracker.database.Database;
import de.midevelopment.minecraft.bungeePlaytimeTracker.database.PlaytimeHandler;
import de.midevelopment.minecraft.bungeePlaytimeTracker.utils.ConfigHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SharePoint {

    private final static Map<UUID, Integer> playerSessions = new HashMap<>();

    private static Database DATABASE = new Database();

    private static PlaytimeHandler PLAYTIMEHANDLER;

    public static void setPlaytimeHandler(PlaytimeHandler playtimeHandler) { PLAYTIMEHANDLER = playtimeHandler; }

    public static PlaytimeHandler getPlaytimeHandler() { return PLAYTIMEHANDLER; }

    public static Database getDatabase() { return DATABASE; }

    public static void setPlayerSession(UUID uuid, int session) { playerSessions.put(uuid, session); }
    public static int getPlayerSession(UUID uuid) { return playerSessions.getOrDefault(uuid, 0); }
    public static void removePlayerSession(UUID uuid) { playerSessions.remove(uuid); }
    public static boolean hasPlayerSession(UUID uuid) { return playerSessions.containsKey(uuid); }
    public static void clearPlayerSessions() { playerSessions.clear(); }
    public static Map<UUID, Integer> getPlayerSessions() { return playerSessions; }

}
