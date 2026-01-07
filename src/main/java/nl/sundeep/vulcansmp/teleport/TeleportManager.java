package nl.sundeep.vulcansmp.teleport;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {

    private final VulcanSMP plugin;
    private final Map<UUID, Location> lastLocations;
    private final Map<UUID, TpaRequest> tpaRequests;
    private final Map<UUID, BukkitTask> pendingTeleports;
    private final Map<UUID, Long> teleportCooldowns;

    public TeleportManager(VulcanSMP plugin) {
        this.plugin = plugin;
        this.lastLocations = new ConcurrentHashMap<>();
        this.tpaRequests = new ConcurrentHashMap<>();
        this.pendingTeleports = new ConcurrentHashMap<>();
        this.teleportCooldowns = new ConcurrentHashMap<>();
    }

    public void teleport(Player player, Location location) { teleport(player, location, true); }

    public void teleport(Player player, Location location, boolean saveLastLocation) {
        if (saveLastLocation) lastLocations.put(player.getUniqueId(), player.getLocation().clone());
        player.teleportAsync(location);
    }

    public void teleportWithDelay(Player player, Location location, int delay) { teleportWithDelay(player, location, delay, true); }

    public void teleportWithDelay(Player player, Location location, int delay, boolean saveLastLocation) {
        if (delay <= 0) { teleport(player, location, saveLastLocation); return; }
        if (isOnCooldown(player)) {
            player.sendMessage(plugin.getMessagesConfig().prefixed("teleport.cooldown", Placeholder.unparsed("time", String.valueOf(getCooldownRemaining(player)))));
            return;
        }
        cancelPendingTeleport(player);
        Location startLocation = player.getLocation().clone();
        player.sendMessage(plugin.getMessagesConfig().prefixed("teleport.warming-up", Placeholder.unparsed("seconds", String.valueOf(delay))));

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            pendingTeleports.remove(player.getUniqueId());
            if (plugin.getConfigManager().isTeleportCancelOnMove() && hasMoved(startLocation, player.getLocation())) {
                player.sendMessage(plugin.getMessagesConfig().prefixed("teleport.cancelled-moved"));
                return;
            }
            applyCooldown(player);
            teleport(player, location, saveLastLocation);
            player.sendMessage(plugin.getMessagesConfig().prefixed("teleport.teleported"));
        }, delay * 20L);
        pendingTeleports.put(player.getUniqueId(), task);
    }

    public void cancelPendingTeleport(Player player) { BukkitTask task = pendingTeleports.remove(player.getUniqueId()); if (task != null) task.cancel(); }
    private boolean hasMoved(Location from, Location to) { return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ(); }
    public Location getLastLocation(Player player) { return lastLocations.get(player.getUniqueId()); }
    public void setLastLocation(Player player, Location location) { lastLocations.put(player.getUniqueId(), location.clone()); }

    public void sendTpaRequest(Player requester, Player target) {
        TpaRequest request = new TpaRequest(requester.getUniqueId(), target.getUniqueId(), System.currentTimeMillis());
        tpaRequests.put(target.getUniqueId(), request);
        requester.sendMessage(plugin.getMessagesConfig().prefixed("tpa.request-sent", Placeholder.unparsed("player", target.getName())));
        target.sendMessage(plugin.getMessagesConfig().prefixed("tpa.request-received", Placeholder.unparsed("player", requester.getName())));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            TpaRequest stored = tpaRequests.get(target.getUniqueId());
            if (stored != null && stored.equals(request)) {
                tpaRequests.remove(target.getUniqueId());
                Player req = plugin.getServer().getPlayer(requester.getUniqueId());
                if (req != null) req.sendMessage(plugin.getMessagesConfig().prefixed("tpa.request-expired", Placeholder.unparsed("player", target.getName())));
            }
        }, plugin.getConfigManager().getTpaExpireTime() * 20L);
    }

    public void acceptTpaRequest(Player target) {
        TpaRequest request = tpaRequests.remove(target.getUniqueId());
        if (request == null) { target.sendMessage(plugin.getMessagesConfig().prefixed("tpa.no-pending-request")); return; }
        Player requester = plugin.getServer().getPlayer(request.getRequester());
        if (requester == null) { target.sendMessage(plugin.getMessagesConfig().prefixed("tpa.requester-offline")); return; }
        teleportWithDelay(requester, target.getLocation(), plugin.getConfigManager().getTeleportDelay());
        target.sendMessage(plugin.getMessagesConfig().prefixed("tpa.accepted", Placeholder.unparsed("player", requester.getName())));
        requester.sendMessage(plugin.getMessagesConfig().prefixed("tpa.request-accepted", Placeholder.unparsed("player", target.getName())));
    }

    public void denyTpaRequest(Player target) {
        TpaRequest request = tpaRequests.remove(target.getUniqueId());
        if (request == null) { target.sendMessage(plugin.getMessagesConfig().prefixed("tpa.no-pending-request")); return; }
        target.sendMessage(plugin.getMessagesConfig().prefixed("tpa.denied"));
        Player requester = plugin.getServer().getPlayer(request.getRequester());
        if (requester != null) requester.sendMessage(plugin.getMessagesConfig().prefixed("tpa.request-denied", Placeholder.unparsed("player", target.getName())));
    }

    public boolean isOnCooldown(Player player) { if (player.hasPermission("vulcan.teleport.bypass")) return false; Long end = teleportCooldowns.get(player.getUniqueId()); return end != null && System.currentTimeMillis() < end; }
    public long getCooldownRemaining(Player player) { Long end = teleportCooldowns.get(player.getUniqueId()); return end == null ? 0 : Math.max(0, (end - System.currentTimeMillis()) / 1000); }
    public void applyCooldown(Player player) { int cd = plugin.getConfigManager().getTeleportCooldown(); if (cd > 0) teleportCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (cd * 1000L)); }

    public static class TpaRequest {
        private final UUID requester, target; private final long timestamp;
        public TpaRequest(UUID requester, UUID target, long timestamp) { this.requester = requester; this.target = target; this.timestamp = timestamp; }
        public UUID getRequester() { return requester; }
    }
}
