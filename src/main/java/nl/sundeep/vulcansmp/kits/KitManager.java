package nl.sundeep.vulcansmp.kits;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.database.DatabaseManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class KitManager {
    private final VulcanSMP plugin;
    private final DatabaseManager database;
    private final Map<String, Kit> kitCache;
    private final Map<UUID, Map<String, Long>> cooldownCache;

    public KitManager(VulcanSMP plugin) {
        this.plugin = plugin; this.database = plugin.getDatabaseManager();
        this.kitCache = new ConcurrentHashMap<>(); this.cooldownCache = new ConcurrentHashMap<>();
        loadKits();
    }

    private void loadKits() {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM kits"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemStack[] items = deserializeItems(rs.getString("items"));
                    if (items != null) {
                        String createdByStr = rs.getString("created_by");
                        kitCache.put(rs.getString("name").toLowerCase(), new Kit(rs.getString("name"), items, createdByStr != null ? UUID.fromString(createdByStr) : null));
                    }
                }
                plugin.getLogger().info(kitCache.size() + " kits geladen.");
            } catch (SQLException e) { plugin.getLogger().severe("Kits laden mislukt: " + e.getMessage()); }
        });
    }

    public CompletableFuture<Boolean> createKit(String name, Player creator) {
        ItemStack[] hotbarItems = new ItemStack[9];
        for (int i = 0; i < 9; i++) { ItemStack item = creator.getInventory().getItem(i); if (item != null && item.getType() != Material.AIR) hotbarItems[i] = item.clone(); }
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO kits (name, items, created_by) VALUES (?, ?, ?)")) {
                String itemsData = serializeItems(hotbarItems); if (itemsData == null) return false;
                stmt.setString(1, name.toLowerCase()); stmt.setString(2, itemsData); stmt.setString(3, creator.getUniqueId().toString());
                stmt.executeUpdate();
                kitCache.put(name.toLowerCase(), new Kit(name.toLowerCase(), hotbarItems, creator.getUniqueId()));
                return true;
            } catch (SQLException e) { plugin.getLogger().severe("Kit aanmaken mislukt: " + e.getMessage()); return false; }
        });
    }

    public CompletableFuture<Boolean> deleteKit(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM kits WHERE name = ?")) {
                stmt.setString(1, name.toLowerCase());
                if (stmt.executeUpdate() > 0) { kitCache.remove(name.toLowerCase()); return true; }
                return false;
            } catch (SQLException e) { plugin.getLogger().severe("Kit verwijderen mislukt: " + e.getMessage()); return false; }
        });
    }

    public Kit getKit(String name) { return kitCache.get(name.toLowerCase()); }
    public List<Kit> getAllKits() { return new ArrayList<>(kitCache.values()); }
    public List<String> getKitNames() { return new ArrayList<>(kitCache.keySet()); }
    public boolean kitExists(String name) { return kitCache.containsKey(name.toLowerCase()); }

    public void giveKit(Player player, Kit kit) {
        for (ItemStack item : kit.getItems()) {
            if (item != null && item.getType() != Material.AIR) {
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());
                for (ItemStack drop : leftover.values()) player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        }
    }

    public boolean isOnCooldown(Player player, String kitName) {
        if (player.hasPermission("vulcan.kit.bypass")) return false;
        int cooldownSeconds = plugin.getConfigManager().getKitCooldown(kitName); if (cooldownSeconds <= 0) return false;
        Map<String, Long> playerCooldowns = cooldownCache.get(player.getUniqueId()); if (playerCooldowns == null) return false;
        Long lastUsed = playerCooldowns.get(kitName.toLowerCase()); if (lastUsed == null) return false;
        return System.currentTimeMillis() < lastUsed + (cooldownSeconds * 1000L);
    }

    public long getCooldownRemaining(Player player, String kitName) {
        int cooldownSeconds = plugin.getConfigManager().getKitCooldown(kitName); if (cooldownSeconds <= 0) return 0;
        Map<String, Long> playerCooldowns = cooldownCache.get(player.getUniqueId()); if (playerCooldowns == null) return 0;
        Long lastUsed = playerCooldowns.get(kitName.toLowerCase()); if (lastUsed == null) return 0;
        return Math.max(0, (lastUsed + (cooldownSeconds * 1000L) - System.currentTimeMillis()) / 1000);
    }

    public void applyCooldown(Player player, String kitName) {
        cooldownCache.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>()).put(kitName.toLowerCase(), System.currentTimeMillis());
    }

    private String serializeItems(ItemStack[] items) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); BukkitObjectOutputStream data = new BukkitObjectOutputStream(out)) {
            data.writeInt(items.length); for (ItemStack item : items) data.writeObject(item);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) { return null; }
    }

    private ItemStack[] deserializeItems(String data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(data)); BukkitObjectInputStream dataIn = new BukkitObjectInputStream(in)) {
            int length = dataIn.readInt(); ItemStack[] items = new ItemStack[length];
            for (int i = 0; i < length; i++) items[i] = (ItemStack) dataIn.readObject();
            return items;
        } catch (Exception e) { return null; }
    }

    public static class Kit {
        private final String name; private final ItemStack[] items; private final UUID createdBy;
        public Kit(String name, ItemStack[] items, UUID createdBy) { this.name = name; this.items = items; this.createdBy = createdBy; }
        public String getName() { return name; }
        public ItemStack[] getItems() { return items; }
    }
}
