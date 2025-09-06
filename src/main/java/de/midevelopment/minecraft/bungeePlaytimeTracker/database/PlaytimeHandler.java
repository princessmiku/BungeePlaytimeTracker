package de.midevelopment.minecraft.bungeePlaytimeTracker.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class PlaytimeHandler {

    private final Database database;
    private final String excludedServers;

    public PlaytimeHandler(Database database, List<String> excludedServers) {
        this.database = database;
        if (excludedServers.isEmpty()) {
            this.excludedServers = "";
        } else {
            this.excludedServers = excludedServers.stream().reduce("", (a, b) -> a + "'" + b + "',");
        }
    }

    /**
     * Registers a player in the database or updates their username if the UUID already exists.
     *
     * @param uuid       The unique identifier of the player.
     * @param playerName The current username of the player.
     */
    public void registerPlayer(UUID uuid, String playerName) {
        String sql = """
                INSERT INTO mi_bungee_player_playtime (uuid, username) VALUES (?, ?)
                ON DUPLICATE KEY UPDATE username = ?;
                """;
        try (Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, uuid.toString());
            ps.setString(2, playerName);
            ps.setString(3, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerPlaytime(UUID uuid) {
        String sql = """
                SELECT playtime from mi_bungee_player_playtime WHERE uuid = ?;
                """;
        try (Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, uuid.toString());
            ps.executeQuery();
            ps.getResultSet().next();
            return ps.getResultSet().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getPlayerCurrentPlaytime(UUID uuid) {
        String sql_select;
        if (excludedServers.isEmpty()) {
            sql_select = """
                SELECT
                  ? AS player_uuid,
                  SUM(
                    CASE
                      WHEN end_time IS NULL
                        THEN TIMESTAMPDIFF(SECOND, start_time, UTC_TIMESTAMP())
                      ELSE TIMESTAMPDIFF(SECOND, start_time, end_time)
                    END
                  ) AS total_seconds
                FROM mi_bungee_player_playtime_sessions
                WHERE player_uuid = ?
                """;
        } else {
            sql_select = """
                SELECT
                  ? AS player_uuid,
                  SUM(
                    CASE
                      WHEN end_time IS NULL
                        THEN TIMESTAMPDIFF(SECOND, start_time, UTC_TIMESTAMP())
                      ELSE TIMESTAMPDIFF(SECOND, start_time, end_time)
                    END
                  ) AS total_seconds
                FROM mi_bungee_player_playtime_sessions
                WHERE player_uuid = ?
                AND servername NOT IN (%s)
                """.formatted(excludedServers.substring(0, excludedServers.length() - 1));
        }

        String sql_update = """
                UPDATE mi_bungee_player_playtime SET playtime = ? WHERE uuid = ?;
                """;
        try (Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql_select);
            ps.setString(1, uuid.toString());
            ps.setString(2, uuid.toString());
            ps.executeQuery();
            ps.getResultSet().next();
            int calcPlaytime = ps.getResultSet().getInt(2);
            PreparedStatement ps2 = connection.prepareStatement(sql_update);
            ps2.setInt(1, calcPlaytime);
            ps2.setString(2, uuid.toString());
            ps2.executeUpdate();
            return calcPlaytime;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int startPlaytime(UUID uuid, String serverName) {
        String sql_insert = """
                Insert into mi_bungee_player_playtime_sessions (player_uuid, servername) values (?, ?);
                """;
        String sql_select = """
                SELECT id FROM mi_bungee_player_playtime_sessions
                          WHERE player_uuid = ? ORDER BY id DESC LIMIT 1;
                """;
        try (Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql_insert);
            ps.setString(1, uuid.toString());
            ps.setString(2, serverName);
            ps.executeUpdate();

            PreparedStatement pst = connection.prepareStatement(sql_select);
            pst.setString(1, uuid.toString());
            pst.executeQuery();
            pst.getResultSet().next();
            return pst.getResultSet().getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void updatePlaytime(int sessionId) {
        stopPlaytime(sessionId);
    }

    public void stopPlaytime(int sessionId) {
        String sql = """
                UPDATE mi_bungee_player_playtime_sessions SET end_time = UTC_TIMESTAMP() WHERE id = ?;
                """;
        try (Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reloadAllPlayers() {
        String sql = """
                SELECT uuid from mi_bungee_player_playtime
                """;
        try (Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                UUID uuid = UUID.fromString(ps.getResultSet().getString(1));
                getPlayerCurrentPlaytime(uuid);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> getTopPlayers(int amount) {
        String sql = """
                SELECT username, playtime FROM mi_bungee_player_playtime ORDER BY playtime DESC LIMIT ?;
                """;
        try(Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, amount);
            ps.executeQuery();
            Map<String, Integer> resultMap = new HashMap<>();
            while (ps.getResultSet().next()) {
                resultMap.put(ps.getResultSet().getString(1), ps.getResultSet().getInt(2));
            }
            return resultMap;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public PlaytimeSession getPlaytimeSession(int sessionId) {
        String sql = """
                SELECT * FROM mi_bungee_player_playtime_sessions WHERE id = ?;;
                """;
        try (Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, sessionId);
            ps.executeQuery();
            ps.getResultSet().next();
            return new PlaytimeSession(
                    ps.getResultSet().getInt(1),
                    ps.getResultSet().getString(2),
                    ps.getResultSet().getString(3),
                    ps.getResultSet().getTimestamp(4),
                    ps.getResultSet().getTimestamp(5),
                    ps.getResultSet().getInt(6)
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PlaytimeSession> getPlaytimeSessions(UUID uuid) {
        String sql = """
                SELECT * FROM mi_bungee_player_playtime_sessions WHERE player_uuid = ?;
                """;
        try (Connection connection = database.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, uuid.toString());
            ps.executeQuery();
            List<PlaytimeSession> resultList = new ArrayList<>();
            while (ps.getResultSet().next()) {
                resultList.add(new PlaytimeSession(
                        ps.getResultSet().getInt(1),
                        ps.getResultSet().getString(2),
                        ps.getResultSet().getString(3),
                        ps.getResultSet().getTimestamp(4),
                        ps.getResultSet().getTimestamp(5),
                        ps.getResultSet().getInt(6)
                ));
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    static class PlaytimeSession {

        public int sessionId;
        public String playerUuid;
        public String serverName;
        public Timestamp start_time;
        public Timestamp end_time;
        public int diff_time;

        public PlaytimeSession(int sessionId, String playerUuid, String serverName, Timestamp start_time, Timestamp end_time, int diff_time) {
            this.sessionId = sessionId;
            this.playerUuid = playerUuid;
            this.serverName = serverName;
            this.start_time = start_time;
            this.end_time = end_time;
            this.diff_time = diff_time;
        }
    }


}
