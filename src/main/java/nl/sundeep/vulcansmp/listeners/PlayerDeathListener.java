package nl.sundeep.vulcansmp.listeners;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final VulcanSMP plugin; public PlayerDeathListener(VulcanSMP plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) { plugin.getTeleportManager().setLastLocation(event.getEntity(), event.getEntity().getLocation()); }
}
