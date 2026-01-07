package nl.sundeep.vulcansmp.homes;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {
    private final VulcanSMP plugin;
    private final DatabaseManager database;
    private final Map<UUID, Map<String, Home>> homeCache;

    public HomeManager(VulcanSMP plugin) { this.plugin = plugin; this.database = plugin.getDatabaseManager(); this.homeCache = new ConcurrentHashMap<>(); }

    public CompletableFuture<Boolean> setHome(Player player, String name) { return setHome(player.getUniqueId(), name, player.getLocation()); }

    public CompletableFuture<Boolean> setHome(UUID uuid, String name, Location location) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection()) {
                String checkSql = "SELECT id FROM homes WHERE uuid = ? AND name = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, uuid.toString()); checkStmt.setString(2, name.toLowerCase());
                    if (checkStmt.executeQuery().next()) {
                        String updateSql = "UPDATE homes SET world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE uuid = ? AND name = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, location.getWorld().getName()); updateStmt.setDouble(2, location.getX()); updateStmt.setDouble(3, location.getY());
                            updateStmt.setDouble(4, location.getZ()); updateStmt.setFloat(5, location.getYaw()); updateStmt.setFloat(6, location.getPitch());
                            updateStmt.setString(7, uuid.toString()); updateStmt.setString(8, name.toLowerCase()); updateStmt.executeUpdate();
                        }
                    } else {
                        String insertSql = "INSERT INTO homes (uuid, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, uuid.toString()); insertStmt.setString(2, name.toLowerCase()); insertStmt.setString(3, location.getWorld().getName());
                            insertStmt.setDouble(4, location.getX()); insertStmt.setDouble(5, location.getY()); insertStmt.setDouble(6, location.getZ());
                            insertStmt.setFloat(7, location.getYaw()); insertStmt.setFloat(8, location.getPitch()); insertStmt.executeUpdate();
                        }
                    }
                }
                homeCache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(name.toLowerCase(), new Home(name.toLowerCase(), location));
                return true;
            } catch (SQLException e) { plugin.getLogger().severe("Home instellen mislukt: " + e.getMessage()); return false; }
        });
    }

    public CompletableFuture<Boolean> deleteHome(UUID uuid, String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM homes WHERE uuid = ? AND name = ?")) {
                stmt.setString(1, uuid.toString()); stmt.setString(2, name.toLowerCase());
                if (stmt.executeUpdate() > 0) { Map<String, Home> homes = homeCache.get(uuid); if (homes != null) homes.remove(name.toLowerCase()); return true; }
                return false;
            } catch (SQLException e) { plugin.getLogger().severe("Home verwijderen mislukt: " + e.getMessage()); return false; }
        });
    }

    public CompletableFuture<Home> getHome(UUID uuid, String name) {
        Map<String, Home> homes = homeCache.get(uuid);
        if (homes != null && homes.containsKey(name.toLowerCase())) return CompletableFuture.completedFuture(homes.get(name.toLowerCase()));
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM homes WHERE uuid = ? AND name = ?")) {
                stmt.setString(1, uuid.toString()); stmt.setString(2, name.toLowerCase());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) { Home home = homeFromResultSet(rs); if (home != null) homeCache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(name.toLowerCase(), home); return home; }
                return null;
            } catch (SQLException e) { plugin.getLogger().severe("Home ophalen mislukt: " + e.getMessage()); return null; }
        });
    }

    public CompletableFuture<List<Home>> getHomes(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<Home> homes = new ArrayList<>();
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM homes WHERE uuid = ? ORDER BY name")) {
                stmt.setString(1, uuid.toString()); ResultSet rs = stmt.executeQuery();
                Map<String, Home> cache = homeCache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
                while (rs.next()) { Home home = homeFromResultSet(rs); if (home != null) { homes.add(home); cache.put(home.getName(), home); } }
            } catch (SQLException e) { plugin.getLogger().severe("Homes ophalen mislukt: " + e.getMessage()); }
            return homes;
        });
    }

    public CompletableFuture<Integer> getHomeCount(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM homes WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString()); ResultSet rs = stmt.executeQuery(); if (rs.next()) return rs.getInt(1);
            } catch (SQLException e) { plugin.getLogger().severe("Home count mislukt: " + e.getMessage()); }
            return 0;
        });
    }

    private Home homeFromResultSet(ResultSet rs) throws SQLException {
        World world = Bukkit.getWorld(rs.getString("world")); if (world == null) return null;
        return new Home(rs.getString("name"), new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
    }

    public void saveAll() {}

    public static class Home {
        private final String name; private final Location location;
        public Home(String name, Location location) { this.name = name; this.location = location; }
        public String getName() { return name; }
        public Location getLocation() { return location; }
    }
}
