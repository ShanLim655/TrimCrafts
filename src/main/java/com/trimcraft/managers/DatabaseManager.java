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

    public void connect() {
        try {
            if (dbType.equals("mysql")) {
                connectMySQL();
            } else {
                connectSQLite();
            }
            createTables();
            plugin.getLogger().info("Database connected (" + dbType.toUpperCase() + ").");
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database: " + e.getMessage(), e);
        }
    }

    private void connectSQLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        File dbFile = new File(plugin.getDataFolder(), plugin.getConfig().getString("database.sqlite.file", "trimcraft.db"));
        dbFile.getParentFile().mkdirs();
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        try (Statement s = connection.createStatement()) {
            s.execute("PRAGMA journal_mode=WAL");
        }
    }

    private void connectMySQL() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String host = plugin.getConfig().getString("database.mysql.host", "localhost");
        int port = plugin.getConfig().getInt("database.mysql.port", 3306);
        String db = plugin.getConfig().getString("database.mysql.database", "trimcraft");
        String user = plugin.getConfig().getString("database.mysql.username", "root");
        String pass = plugin.getConfig().getString("database.mysql.password", "password");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&autoReconnect=true&characterEncoding=UTF-8";
        connection = DriverManager.getConnection(url, user, pass);
    }

    private void createTables() throws SQLException {
        String autoIncrement = dbType.equals("mysql") ? "AUTO_INCREMENT" : "AUTOINCREMENT";
        try (Statement s = connection.createStatement()) {
            s.execute(
                "CREATE TABLE IF NOT EXISTS tc_cooldowns (" +
                "  id INTEGER PRIMARY KEY " + autoIncrement + "," +
                "  uuid VARCHAR(36) NOT NULL," +
                "  ability_key VARCHAR(64) NOT NULL," +
                "  expiry_ms BIGINT NOT NULL," +
                "  UNIQUE(uuid, ability_key)" +
                ")"
            );
            s.execute(
                "CREATE TABLE IF NOT EXISTS tc_players (" +
                "  uuid VARCHAR(36) PRIMARY KEY," +
                "  last_trim VARCHAR(64)," +
                "  total_uses INT DEFAULT 0," +
                "  last_seen BIGINT" +
                ")"
            );
        }
    }

    public Map<String, Long> loadCooldowns(UUID uuid) {
        Map<String, Long> result = new HashMap<>();
        if (!isConnected()) return result;
        String sql = "SELECT ability_key, expiry_ms FROM tc_cooldowns WHERE uuid = ? AND expiry_ms > ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setLong(2, System.currentTimeMillis());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("ability_key"), rs.getLong("expiry_ms"));
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to load cooldowns for " + uuid + ": " + e.getMessage());
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

    private void saveCooldown(UUID uuid, String abilityKey, long expiryMs) {
        if (!isConnected()) return;
        String sql = dbType.equals("mysql")
            ? "INSERT INTO tc_cooldowns (uuid, ability_key, expiry_ms) VALUES (?,?,?) ON DUPLICATE KEY UPDATE expiry_ms=VALUES(expiry_ms)"
            : "INSERT OR REPLACE INTO tc_cooldowns (uuid, ability_key, expiry_ms) VALUES (?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, abilityKey);
            ps.setLong(3, expiryMs);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to save cooldown: " + e.getMessage());
        }
    }

    public void saveAllCooldownsAsync(Map<UUID, Map<String, Long>> allCooldowns) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Map<String, Long>> entry : allCooldowns.entrySet()) {
                    for (Map.Entry<String, Long> cd : entry.getValue().entrySet()) {
                        saveCooldown(entry.getKey(), cd.getKey(), cd.getValue());
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void savePlayerDataAsync(UUID uuid, String lastTrim, int totalUses) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isConnected()) return;
                String sql = dbType.equals("mysql")
                    ? "INSERT INTO tc_players (uuid, last_trim, total_uses, last_seen) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE last_trim=VALUES(last_trim), total_uses=VALUES(total_uses), last_seen=VALUES(last_seen)"
                    : "INSERT OR REPLACE INTO tc_players (uuid, last_trim, total_uses, last_seen) VALUES (?,?,?,?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, lastTrim);
                    ps.setInt(3, totalUses);
                    ps.setLong(4, System.currentTimeMillis());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().warning("Failed to save player data: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void deleteCooldown(UUID uuid, String abilityKey) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isConnected()) return;
                try (PreparedStatement ps = connection.prepareStatement(
                        "DELETE FROM tc_cooldowns WHERE uuid = ? AND ability_key = ?")) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, abilityKey);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().warning("Failed to delete cooldown: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void disconnect() {
        try {
            if (isConnected()) {
                connection.close();
                plugin.getLogger().info("Database disconnected.");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Error closing database: " + e.getMessage());
        }
    }
}
