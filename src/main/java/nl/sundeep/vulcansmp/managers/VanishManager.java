package nl.sundeep.vulcansmp.managers;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VanishManager {
    private final VulcanSMP plugin;
    private final Set<UUID> vanishedPlayers;

    public VanishManager(VulcanSMP plugin) { this.plugin = plugin; this.vanishedPlayers = new HashSet<>(); }

    public void toggleVanish(Player player) { if (isVanished(player)) unvanish(player); else vanish(player); }

    public void vanish(Player player) {
        vanishedPlayers.add(player.getUniqueId());
        for (Player online : Bukkit.getOnlinePlayers()) if (!online.hasPermission("vulcan.vanish.see")) online.hidePlayer(plugin, player);
        if (plugin.getConfigManager().isVanishNotifyPlayers()) {
            String msg = plugin.getConfigManager().getVanishEnterMessage().replace("{player}", player.getName());
            for (Player online : Bukkit.getOnlinePlayers()) if (!online.hasPermission("vulcan.vanish.see") && !online.equals(player)) online.sendMessage(plugin.getMessagesConfig().parse(msg));
        }
        for (Player online : Bukkit.getOnlinePlayers()) if (online.hasPermission("vulcan.vanish.see") && !online.equals(player)) online.sendMessage(plugin.getMessagesConfig().prefixed("vanish.staff-enter", Placeholder.unparsed("player", player.getName())));
        player.sendMessage(plugin.getMessagesConfig().prefixed("vanish.enabled"));
    }

    public void unvanish(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        for (Player online : Bukkit.getOnlinePlayers()) online.showPlayer(plugin, player);
        if (plugin.getConfigManager().isVanishNotifyPlayers()) {
            String msg = plugin.getConfigManager().getVanishLeaveMessage().replace("{player}", player.getName());
            for (Player online : Bukkit.getOnlinePlayers()) if (!online.hasPermission("vulcan.vanish.see") && !online.equals(player)) online.sendMessage(plugin.getMessagesConfig().parse(msg));
        }
        for (Player online : Bukkit.getOnlinePlayers()) if (online.hasPermission("vulcan.vanish.see") && !online.equals(player)) online.sendMessage(plugin.getMessagesConfig().prefixed("vanish.staff-leave", Placeholder.unparsed("player", player.getName())));
        player.sendMessage(plugin.getMessagesConfig().prefixed("vanish.disabled"));
    }

    public boolean isVanished(Player player) { return vanishedPlayers.contains(player.getUniqueId()); }
    public boolean isVanished(UUID uuid) { return vanishedPlayers.contains(uuid); }

    public void handlePlayerJoin(Player player) {
        if (!player.hasPermission("vulcan.vanish.see")) for (UUID vu : vanishedPlayers) { Player v = Bukkit.getPlayer(vu); if (v != null) player.hidePlayer(plugin, v); }
    }

    public void handlePlayerQuit(Player player) { vanishedPlayers.remove(player.getUniqueId()); }
}
