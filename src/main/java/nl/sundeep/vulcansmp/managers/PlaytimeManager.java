package nl.sundeep.vulcansmp.managers;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.database.DatabaseManager;
import org.bukkit.entity.Player;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeManager {
    private final VulcanSMP plugin;
    private final DatabaseManager database;
    private final Map<UUID, Long> sessionStartTimes;
    private final Map<UUID, Long> cachedPlaytimes;

    public PlaytimeManager(VulcanSMP plugin) {
        this.plugin = plugin; this.database = plugin.getDatabaseManager();
        this.sessionStartTimes = new ConcurrentHashMap<>(); this.cachedPlaytimes = new ConcurrentHashMap<>();
    }

    public void startSession(Player player) {
        sessionStartTimes.put(player.getUniqueId(), System.currentTimeMillis());
        CompletableFuture.runAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT playtime FROM playtime WHERE uuid = ?")) {
                stmt.setString(1, player.getUniqueId().toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) cachedPlaytimes.put(player.getUniqueId(), rs.getLong("playtime"));
                else {
                    try (PreparedStatement ins = conn.prepareStatement("INSERT INTO playtime (uuid, username, playtime) VALUES (?, ?, 0)")) {
                        ins.setString(1, player.getUniqueId().toString()); ins.setString(2, player.getName()); ins.executeUpdate();
                    }
                    cachedPlaytimes.put(player.getUniqueId(), 0L);
                }
            } catch (SQLException e) { plugin.getLogger().severe("Speeltijd laden mislukt: " + e.getMessage()); }
        });
    }

    public void endSession(Player player) {
        UUID uuid = player.getUniqueId();
        Long startTime = sessionStartTimes.remove(uuid);
        if (startTime != null) {
            long sessionTime = System.currentTimeMillis() - startTime;
            long total = cachedPlaytimes.getOrDefault(uuid, 0L) + sessionTime;
            cachedPlaytimes.put(uuid, total);
            savePlaytime(uuid, player.getName(), total);
        }
    }

    private void savePlaytime(UUID uuid, String username, long playtime) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = database.getConnection()) {
                String sql = database.isMySQL() ? "INSERT INTO playtime (uuid, username, playtime, last_seen) VALUES (?, ?, ?, NOW()) ON DUPLICATE KEY UPDATE username = ?, playtime = ?, last_seen = NOW()"
                        : "INSERT OR REPLACE INTO playtime (uuid, username, playtime, last_seen) VALUES (?, ?, ?, datetime('now'))";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, uuid.toString()); stmt.setString(2, username); stmt.setLong(3, playtime);
                    if (database.isMySQL()) { stmt.setString(4, username); stmt.setLong(5, playtime); }
                    stmt.executeUpdate();
                }
            } catch (SQLException e) { plugin.getLogger().severe("Speeltijd opslaan mislukt: " + e.getMessage()); }
        });
    }

    public long getPlaytime(Player player) {
        UUID uuid = player.getUniqueId();
        long stored = cachedPlaytimes.getOrDefault(uuid, 0L);
        Long startTime = sessionStartTimes.get(uuid);
        if (startTime != null) stored += System.currentTimeMillis() - startTime;
        return stored;
    }

    public CompletableFuture<Long> getPlaytime(UUID uuid) {
        Long cached = cachedPlaytimes.get(uuid);
        Long startTime = sessionStartTimes.get(uuid);
        if (cached != null) { long total = cached; if (startTime != null) total += System.currentTimeMillis() - startTime; return CompletableFuture.completedFuture(total); }
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT playtime FROM playtime WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString()); ResultSet rs = stmt.executeQuery(); if (rs.next()) return rs.getLong("playtime");
            } catch (SQLException e) { plugin.getLogger().severe("Speeltijd ophalen mislukt: " + e.getMessage()); }
            return 0L;
        });
    }

    public void saveAll() {
        for (Map.Entry<UUID, Long> entry : sessionStartTimes.entrySet()) {
            UUID uuid = entry.getKey(); long sessionTime = System.currentTimeMillis() - entry.getValue();
            long total = cachedPlaytimes.getOrDefault(uuid, 0L) + sessionTime;
            Player player = plugin.getServer().getPlayer(uuid);
            savePlaytime(uuid, player != null ? player.getName() : "Unknown", total);
        }
    }
}
