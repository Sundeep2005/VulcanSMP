package nl.sundeep.vulcansmp.listeners;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {
    private final VulcanSMP plugin; public PlayerTeleportListener(VulcanSMP plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.COMMAND || cause == PlayerTeleportEvent.TeleportCause.PLUGIN || cause == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            plugin.getTeleportManager().setLastLocation(event.getPlayer(), event.getFrom());
        }
    }
}
