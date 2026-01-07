package nl.sundeep.vulcansmp.listeners;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    private final VulcanSMP plugin; public PlayerJoinQuitListener(VulcanSMP plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlaytimeManager().startSession(player);
        plugin.getVanishManager().handlePlayerJoin(player);
        plugin.getStaffManager().handleStaffJoin(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlaytimeManager().endSession(player);
        plugin.getVanishManager().handlePlayerQuit(player);
        plugin.getStaffManager().handleStaffLeave(player);
        plugin.getTeleportManager().cancelPendingTeleport(player);
        plugin.getMessageManager().clearLastMessaged(player);
    }
}
