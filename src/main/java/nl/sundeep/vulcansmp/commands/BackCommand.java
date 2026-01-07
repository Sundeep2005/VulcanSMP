package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("back") @CommandPermission("vulcan.back")
public class BackCommand {
    private final VulcanSMP plugin; public BackCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor("back")
    public void back(Player player) {
        Location lastLocation = plugin.getTeleportManager().getLastLocation(player);
        if (lastLocation == null) { player.sendMessage(plugin.getMessagesConfig().prefixed("back.no-location")); return; }
        plugin.getTeleportManager().teleportWithDelay(player, lastLocation, plugin.getConfigManager().getTeleportDelay(), false);
    }
}
