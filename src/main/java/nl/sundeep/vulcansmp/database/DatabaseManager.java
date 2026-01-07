package nl.sundeep.vulcansmp.database;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.config.ConfigManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final VulcanSMP plugin;
    private HikariDataSource dataSource;
    private boolean isMySQL;

    public DatabaseManager(VulcanSMP plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        ConfigManager config = plugin.getConfigManager();
        String type = config.getDatabaseType().toLowerCase();

        HikariConfig hikariConfig = new HikariConfig();

        if (type.equals("mysql") || type.equals("mariadb")) {
            isMySQL = true;
            String host = config.getDatabaseHost();
            int port = config.getDatabasePort();
            String database = config.getDatabaseName();
            String username = config.getDatabaseUsername();
            String password = config.getDatabasePassword();

            hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + 
                    "?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true");
            hikariConfig.setUsername(username);
            hikariConfig.setPassword(password);
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } else {
            isMySQL = false;
            File dbFile = new File(plugin.getDataFolder(), "vulcansmp.db");
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
        }

        hikariConfig.setPoolName("VulcanSMP-Pool");
        hikariConfig.setMaximumPoolSize(config.getDatabasePoolSize());
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            dataSource = new HikariDataSource(hikariConfig);
            createTables();
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Database initialisatie mislukt: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void createTables() {
        String autoIncrement = isMySQL ? "AUTO_INCREMENT" : "AUTOINCREMENT";
        String textType = "TEXT";

        execute("""
            CREATE TABLE IF NOT EXISTS homes (
                id INTEGER PRIMARY KEY %s,
                uuid VARCHAR(36) NOT NULL,
                name VARCHAR(64) NOT NULL,
                world VARCHAR(64) NOT NULL,
                x DOUBLE NOT NULL,
                y DOUBLE NOT NULL,
                z DOUBLE NOT NULL,
                yaw FLOAT NOT NULL,
                pitch FLOAT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(uuid, name)
            )
            """.formatted(autoIncrement));

        execute("""
            CREATE TABLE IF NOT EXISTS warps (
                id INTEGER PRIMARY KEY %s,
                name VARCHAR(64) NOT NULL UNIQUE,
                world VARCHAR(64) NOT NULL,
                x DOUBLE NOT NULL,
                y DOUBLE NOT NULL,
                z DOUBLE NOT NULL,
                yaw FLOAT NOT NULL,
                pitch FLOAT NOT NULL,
                created_by VARCHAR(36),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """.formatted(autoIncrement));

        execute("""
            CREATE TABLE IF NOT EXISTS spawns (
                id INTEGER PRIMARY KEY %s,
                name VARCHAR(64) NOT NULL UNIQUE,
                world VARCHAR(64) NOT NULL,
                x DOUBLE NOT NULL,
                y DOUBLE NOT NULL,
                z DOUBLE NOT NULL,
                yaw FLOAT NOT NULL,
                pitch FLOAT NOT NULL
            )
            """.formatted(autoIncrement));

        execute("""
            CREATE TABLE IF NOT EXISTS playtime (
                uuid VARCHAR(36) PRIMARY KEY,
                username VARCHAR(16) NOT NULL,
                playtime BIGINT DEFAULT 0,
                last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);

        execute("""
            CREATE TABLE IF NOT EXISTS friends (
                id INTEGER PRIMARY KEY %s,
                uuid VARCHAR(36) NOT NULL,
                friend_uuid VARCHAR(36) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(uuid, friend_uuid)
            )
            """.formatted(autoIncrement));

        execute("""
            CREATE TABLE IF NOT EXISTS kit_cooldowns (
                id INTEGER PRIMARY KEY %s,
                uuid VARCHAR(36) NOT NULL,
                kit_name VARCHAR(64) NOT NULL,
                last_used TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(uuid, kit_name)
            )
            """.formatted(autoIncrement));

        execute("""
            CREATE TABLE IF NOT EXISTS kits (
                id INTEGER PRIMARY KEY %s,
                name VARCHAR(64) NOT NULL UNIQUE,
                items %s NOT NULL,
                created_by VARCHAR(36),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """.formatted(autoIncrement, textType));

        plugin.getLogger().info("Database tabellen succesvol aangemaakt/geverifieerd!");
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void execute(String sql) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            plugin.getLogger().severe("SQL uitvoering mislukt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void executeAsync(String sql, Object... params) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Async SQL uitvoering mislukt: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public <T> CompletableFuture<T> queryAsync(String sql, ResultSetHandler<T> handler, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    return handler.handle(rs);
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Async query mislukt: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        });
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    @FunctionalInterface
    public interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }
}
