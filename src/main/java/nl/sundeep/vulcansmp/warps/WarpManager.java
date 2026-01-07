package nl.sundeep.vulcansmp.warps;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class WarpManager {
    private final VulcanSMP plugin;
    private final DatabaseManager database;
    private final Map<String, Warp> warpCache;

    public WarpManager(VulcanSMP plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabaseManager();
        this.warpCache = new ConcurrentHashMap<>();
        loadWarps();
    }

    private void loadWarps() {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM warps"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    World world = Bukkit.getWorld(rs.getString("world"));
                    if (world != null) {
                        Location loc = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
                        String createdByStr = rs.getString("created_by");
                        UUID createdBy = createdByStr != null ? UUID.fromString(createdByStr) : null;
                        warpCache.put(rs.getString("name").toLowerCase(), new Warp(rs.getString("name"), loc, createdBy));
                    }
                }
                plugin.getLogger().info(warpCache.size() + " warps geladen.");
            } catch (SQLException e) { plugin.getLogger().severe("Warps laden mislukt: " + e.getMessage()); }
        });
    }

    public CompletableFuture<Boolean> createWarp(String name, Location location, UUID createdBy) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO warps (name, world, x, y, z, yaw, pitch, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, name.toLowerCase()); stmt.setString(2, location.getWorld().getName());
                stmt.setDouble(3, location.getX()); stmt.setDouble(4, location.getY()); stmt.setDouble(5, location.getZ());
                stmt.setFloat(6, location.getYaw()); stmt.setFloat(7, location.getPitch());
                stmt.setString(8, createdBy != null ? createdBy.toString() : null);
                stmt.executeUpdate();
                warpCache.put(name.toLowerCase(), new Warp(name.toLowerCase(), location, createdBy));
                return true;
            } catch (SQLException e) { plugin.getLogger().severe("Warp aanmaken mislukt: " + e.getMessage()); return false; }
        });
    }

    public CompletableFuture<Boolean> deleteWarp(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM warps WHERE name = ?")) {
                stmt.setString(1, name.toLowerCase());
                if (stmt.executeUpdate() > 0) { warpCache.remove(name.toLowerCase()); return true; }
                return false;
            } catch (SQLException e) { plugin.getLogger().severe("Warp verwijderen mislukt: " + e.getMessage()); return false; }
        });
    }

    public Warp getWarp(String name) { return warpCache.get(name.toLowerCase()); }
    public List<Warp> getAllWarps() { return new ArrayList<>(warpCache.values()); }
    public List<String> getWarpNames() { return new ArrayList<>(warpCache.keySet()); }
    public boolean warpExists(String name) { return warpCache.containsKey(name.toLowerCase()); }
    public void saveAll() {}

    public static class Warp {
        private final String name; private final Location location; private final UUID createdBy;
        public Warp(String name, Location location, UUID createdBy) { this.name = name; this.location = location; this.createdBy = createdBy; }
        public String getName() { return name; }
        public Location getLocation() { return location; }
        public UUID getCreatedBy() { return createdBy; }
    }
}
