package nl.sundeep.vulcansmp.managers;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnManager {
    private final VulcanSMP plugin;
    private final DatabaseManager database;
    private final Map<String, Location> spawnCache;

    public SpawnManager(VulcanSMP plugin) {
        this.plugin = plugin; this.database = plugin.getDatabaseManager();
        this.spawnCache = new ConcurrentHashMap<>();
        loadSpawns();
    }

    private void loadSpawns() {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM spawns"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    World world = Bukkit.getWorld(rs.getString("world"));
                    if (world != null) spawnCache.put(rs.getString("name").toLowerCase(), new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
                }
                plugin.getLogger().info(spawnCache.size() + " spawn locaties geladen.");
            } catch (SQLException e) { plugin.getLogger().severe("Spawns laden mislukt: " + e.getMessage()); }
        });
    }

    public CompletableFuture<Boolean> setSpawn(String name, Location location) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection()) {
                String sql = database.isMySQL() ? "INSERT INTO spawns (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ?"
                        : "INSERT OR REPLACE INTO spawns (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name.toLowerCase()); stmt.setString(2, location.getWorld().getName());
                    stmt.setDouble(3, location.getX()); stmt.setDouble(4, location.getY()); stmt.setDouble(5, location.getZ());
                    stmt.setFloat(6, location.getYaw()); stmt.setFloat(7, location.getPitch());
                    if (database.isMySQL()) {
                        stmt.setString(8, location.getWorld().getName()); stmt.setDouble(9, location.getX()); stmt.setDouble(10, location.getY());
                        stmt.setDouble(11, location.getZ()); stmt.setFloat(12, location.getYaw()); stmt.setFloat(13, location.getPitch());
                    }
                    stmt.executeUpdate();
                    spawnCache.put(name.toLowerCase(), location.clone());
                    return true;
                }
            } catch (SQLException e) { plugin.getLogger().severe("Spawn instellen mislukt: " + e.getMessage()); return false; }
        });
    }

    public CompletableFuture<Boolean> removeSpawn(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM spawns WHERE name = ?")) {
                stmt.setString(1, name.toLowerCase());
                if (stmt.executeUpdate() > 0) { spawnCache.remove(name.toLowerCase()); return true; }
                return false;
            } catch (SQLException e) { plugin.getLogger().severe("Spawn verwijderen mislukt: " + e.getMessage()); return false; }
        });
    }

    public Location getSpawn(String name) { return spawnCache.get(name.toLowerCase()); }
    public Location getDefaultSpawn() { Location spawn = spawnCache.get("spawn"); return spawn != null ? spawn : Bukkit.getWorlds().get(0).getSpawnLocation(); }
    public List<String> getSpawnNames() { return new ArrayList<>(spawnCache.keySet()); }
    public boolean spawnExists(String name) { return spawnCache.containsKey(name.toLowerCase()); }
}
