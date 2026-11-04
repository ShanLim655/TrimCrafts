package com.trimcraft.managers;

import com.trimcraft.TrimCraftPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final TrimCraftPlugin plugin;
    private Connection connection;
    private final String dbType;

    public DatabaseManager(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        this.dbType = plugin.getConfig().getString("database.type", "sqlite").toLowerCase();
    }

    public synchronized void connect() {
        try {
            if ("mysql".equals(dbType)) {
                connectMySQL();
            } else if ("sqlite".equals(dbType)) {
                connectSQLite();
            } else {
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
            }

            createTables();
            plugin.getLogger().info("Database connected (" + dbType.toUpperCase() + ").");
        } catch (SQLException | ClassNotFoundException | IllegalArgumentException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to the database.", exception);
        }
    }

    private void connectSQLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        File dbFile = new File(plugin.getDataFolder(),
                plugin.getConfig().getString("database.sqlite.file", "trimcraft.db"));
        File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new SQLException("Could not create database directory: " + parent);
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA journal_mode=WAL");
            statement.execute("PRAGMA busy_timeout=5000");
            statement.execute("PRAGMA foreign_keys=ON");
        }
    }

    private void connectMySQL() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String host = plugin.getConfig().getString("database.mysql.host", "localhost");
        int port = plugin.getConfig().getInt("database.mysql.port", 3306);
        String database = plugin.getConfig().getString("database.mysql.database", "trimcraft");
        String username = plugin.getConfig().getString("database.mysql.username", "root");
        String password = plugin.getConfig().getString("database.mysql.password", "");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&autoReconnect=true&characterEncoding=UTF-8";
        connection = DriverManager.getConnection(url, username, password);
    }

    private void createTables() throws SQLException {
        String autoIncrement = "mysql".equals(dbType) ? "AUTO_INCREMENT" : "AUTOINCREMENT";
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tc_cooldowns (" +
                            "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                            "uuid VARCHAR(36) NOT NULL," +
                            "ability_key VARCHAR(64) NOT NULL," +
                            "expiry_ms BIGINT NOT NULL," +
                            "UNIQUE(uuid, ability_key)" +
                            ")"
            );
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tc_players (" +
                            "uuid VARCHAR(36) PRIMARY KEY," +
                            "last_trim VARCHAR(64)," +
                            "total_uses INT DEFAULT 0," +
                            "last_seen BIGINT" +
                            ")"
            );
        }
    }

    public synchronized Map<String, Long> loadCooldowns(UUID uuid) {
        Map<String, Long> result = new HashMap<>();
        if (!isConnected()) return result;

        String sql = "SELECT ability_key, expiry_ms FROM tc_cooldowns WHERE uuid = ? AND expiry_ms > ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setLong(2, System.currentTimeMillis());
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    result.put(rows.getString("ability_key"), rows.getLong("expiry_ms"));
                }
            }
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to load cooldowns for " + uuid + ".", exception);
        }
        return result;
    }

    public void saveCooldownAsync(UUID uuid, String abilityKey, long expiryMs) {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveCooldown(uuid, abilityKey, expiryMs);
            }
        }.runTaskAsynchronously(plugin);
    }

    private synchronized void saveCooldown(UUID uuid, String abilityKey, long expiryMs) {
        if (!isConnected()) return;

        String sql = "mysql".equals(dbType)
                ? "INSERT INTO tc_cooldowns (uuid, ability_key, expiry_ms) VALUES (?,?,?) ON DUPLICATE KEY UPDATE expiry_ms=VALUES(expiry_ms)"
                : "INSERT OR REPLACE INTO tc_cooldowns (uuid, ability_key, expiry_ms) VALUES (?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, abilityKey);
            statement.setLong(3, expiryMs);
            statement.executeUpdate();
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to save cooldown for " + uuid + ".", exception);
        }
    }

    public void saveAllCooldownsAsync(Map<UUID, Map<String, Long>> allCooldowns) {
        Map<UUID, Map<String, Long>> snapshot = copyCooldowns(allCooldowns);
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAllCooldowns(snapshot);
            }
        }.runTaskAsynchronously(plugin);
    }

    public synchronized void saveAllCooldowns(Map<UUID, Map<String, Long>> allCooldowns) {
        for (Map.Entry<UUID, Map<String, Long>> entry : allCooldowns.entrySet()) {
            for (Map.Entry<String, Long> cooldown : entry.getValue().entrySet()) {
                saveCooldown(entry.getKey(), cooldown.getKey(), cooldown.getValue());
            }
        }
    }

    public void savePlayerDataAsync(UUID uuid, String lastTrim, int totalUses) {
        new BukkitRunnable() {
            @Override
            public void run() {
                savePlayerData(uuid, lastTrim, totalUses);
            }
        }.runTaskAsynchronously(plugin);
    }

    private synchronized void savePlayerData(UUID uuid, String lastTrim, int totalUses) {
        if (!isConnected()) return;

        String sql = "mysql".equals(dbType)
                ? "INSERT INTO tc_players (uuid, last_trim, total_uses, last_seen) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE last_trim=VALUES(last_trim), total_uses=VALUES(total_uses), last_seen=VALUES(last_seen)"
                : "INSERT OR REPLACE INTO tc_players (uuid, last_trim, total_uses, last_seen) VALUES (?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, lastTrim);
            statement.setInt(3, totalUses);
            statement.setLong(4, System.currentTimeMillis());
            statement.executeUpdate();
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to save player data for " + uuid + ".", exception);
        }
    }

    public void deleteCooldown(UUID uuid, String abilityKey) {
        new BukkitRunnable() {
            @Override
            public void run() {
                deleteCooldownSync(uuid, abilityKey);
            }
        }.runTaskAsynchronously(plugin);
    }

    private synchronized void deleteCooldownSync(UUID uuid, String abilityKey) {
        if (!isConnected()) return;

        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM tc_cooldowns WHERE uuid = ? AND ability_key = ?")) {
            statement.setString(1, uuid.toString());
            statement.setString(2, abilityKey);
            statement.executeUpdate();
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to delete cooldown for " + uuid + ".", exception);
        }
    }

    public synchronized boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException exception) {
            return false;
        }
    }

    public synchronized void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database disconnected.");
            }
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to close the database connection.", exception);
        }
    }

    private Map<UUID, Map<String, Long>> copyCooldowns(Map<UUID, Map<String, Long>> source) {
        Map<UUID, Map<String, Long>> copy = new HashMap<>();
        source.forEach((uuid, cooldowns) -> copy.put(uuid, new HashMap<>(cooldowns)));
        return copy;
    }
}
