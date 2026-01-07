package nl.sundeep.vulcansmp.friends;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class FriendManager {
    private final VulcanSMP plugin;
    private final DatabaseManager database;
    private final Map<UUID, Set<UUID>> friendCache;
    private final Map<UUID, Set<UUID>> pendingRequests;

    public FriendManager(VulcanSMP plugin) {
        this.plugin = plugin; this.database = plugin.getDatabaseManager();
        this.friendCache = new ConcurrentHashMap<>(); this.pendingRequests = new ConcurrentHashMap<>();
    }

    public CompletableFuture<Boolean> addFriend(UUID player, UUID friend) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO friends (uuid, friend_uuid) VALUES (?, ?), (?, ?)")) {
                stmt.setString(1, player.toString()); stmt.setString(2, friend.toString());
                stmt.setString(3, friend.toString()); stmt.setString(4, player.toString());
                stmt.executeUpdate();
                friendCache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet()).add(friend);
                friendCache.computeIfAbsent(friend, k -> ConcurrentHashMap.newKeySet()).add(player);
                return true;
            } catch (SQLException e) { plugin.getLogger().severe("Vriend toevoegen mislukt: " + e.getMessage()); return false; }
        });
    }

    public CompletableFuture<Boolean> removeFriend(UUID player, UUID friend) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM friends WHERE (uuid = ? AND friend_uuid = ?) OR (uuid = ? AND friend_uuid = ?)")) {
                stmt.setString(1, player.toString()); stmt.setString(2, friend.toString());
                stmt.setString(3, friend.toString()); stmt.setString(4, player.toString());
                if (stmt.executeUpdate() > 0) {
                    Set<UUID> pf = friendCache.get(player); if (pf != null) pf.remove(friend);
                    Set<UUID> ff = friendCache.get(friend); if (ff != null) ff.remove(player);
                    return true;
                }
                return false;
            } catch (SQLException e) { plugin.getLogger().severe("Vriend verwijderen mislukt: " + e.getMessage()); return false; }
        });
    }

    public CompletableFuture<List<UUID>> getFriends(UUID player) {
        Set<UUID> cached = friendCache.get(player);
        if (cached != null) return CompletableFuture.completedFuture(new ArrayList<>(cached));
        return CompletableFuture.supplyAsync(() -> {
            List<UUID> friends = new ArrayList<>();
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT friend_uuid FROM friends WHERE uuid = ?")) {
                stmt.setString(1, player.toString()); ResultSet rs = stmt.executeQuery();
                Set<UUID> friendSet = ConcurrentHashMap.newKeySet();
                while (rs.next()) { UUID fu = UUID.fromString(rs.getString("friend_uuid")); friends.add(fu); friendSet.add(fu); }
                friendCache.put(player, friendSet);
            } catch (SQLException e) { plugin.getLogger().severe("Vrienden ophalen mislukt: " + e.getMessage()); }
            return friends;
        });
    }

    public CompletableFuture<Boolean> areFriends(UUID player1, UUID player2) {
        Set<UUID> cached = friendCache.get(player1);
        if (cached != null) return CompletableFuture.completedFuture(cached.contains(player2));
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM friends WHERE uuid = ? AND friend_uuid = ?")) {
                stmt.setString(1, player1.toString()); stmt.setString(2, player2.toString());
                return stmt.executeQuery().next();
            } catch (SQLException e) { return false; }
        });
    }

    public void sendFriendRequest(UUID requester, UUID target) { pendingRequests.computeIfAbsent(target, k -> ConcurrentHashMap.newKeySet()).add(requester); }
    public boolean hasPendingRequest(UUID target, UUID requester) { Set<UUID> req = pendingRequests.get(target); return req != null && req.contains(requester); }
    public void removePendingRequest(UUID target, UUID requester) { Set<UUID> req = pendingRequests.get(target); if (req != null) req.remove(requester); }
    public Set<UUID> getPendingRequests(UUID target) { return pendingRequests.getOrDefault(target, Collections.emptySet()); }
    public void saveAll() {}
}
