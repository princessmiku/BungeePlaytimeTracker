package de.midevelopment.minecraft.bungeePlaytimeTracker.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private HikariDataSource dataSource;

    public void init(String host, int port, String database, String user, String pass, boolean useSSL) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL + "&characterEncoding=utf8");
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(1);
        cfg.setConnectionTimeout(8000);
        cfg.setIdleTimeout(60000);
        cfg.setMaxLifetime(600000);
        cfg.setAutoCommit(true);
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(cfg);
    }

    public void createTables() throws SQLException {
        String ddl = """
                create table if not exists mi_bungee_player_playtime
                (
                    uuid     varchar(36)              not null,
                    username varchar(16)              null,
                    playtime int unsigned default 0 null,
                    primary key (uuid)
                );
                
                create index index_playtime
                    on mi_bungee_player_playtime (playtime desc);
                
                create table if not exists mi_bungee_player_playtime_sessions
                (
                    id          int unsigned auto_increment
                        primary key,
                    player_uuid varchar(36)                         not null,
                    servername  varchar(32)                         null,
                    start_time  timestamp default (utc_timestamp()) null,
                    end_time    timestamp default (utc_timestamp()) null,
                    diff_time   int as (timestampdiff(SECOND, `start_time`, `end_time`)) stored
                )
                    comment 'Past sessions of the player inside the bungee network';
                
                create index index_player_uuid
                    on mi_bungee_player_playtime_sessions (player_uuid);
                """;
        try (Connection con = getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(ddl);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("Database not initialized");
        }
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null) dataSource.close();
    }
}