package nl.sundeep.vulcansmp.listeners;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {
    private final VulcanSMP plugin; public InventoryListener(VulcanSMP plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory().getHolder() instanceof Player target && !target.equals(player)) {
            if (!player.hasPermission("vulcan.invsee.modify")) event.setCancelled(true);
        }
    }
}
