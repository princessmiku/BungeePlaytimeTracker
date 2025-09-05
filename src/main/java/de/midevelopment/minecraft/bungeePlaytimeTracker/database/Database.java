// Java
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
        cfg.setMaximumPoolSize(5);          // klein halten
        cfg.setMinimumIdle(1);
        cfg.setConnectionTimeout(8000);     // ms
        cfg.setIdleTimeout(60000);          // ms
        cfg.setMaxLifetime(600000);         // ms
        // Wichtig für MySQL timeouts:
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(cfg);
    }

    public void createTables() throws SQLException {
        String ddl = """

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