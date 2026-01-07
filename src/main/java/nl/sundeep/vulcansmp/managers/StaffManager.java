package nl.sundeep.vulcansmp.managers;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffManager {
    private final VulcanSMP plugin;

    public StaffManager(VulcanSMP plugin) { this.plugin = plugin; }

    public boolean isStaff(Player player) { return player.hasPermission("vulcan.staff"); }

    public void handleStaffJoin(Player player) {
        if (!isStaff(player) || !plugin.getConfigManager().isStaffNotifyJoin()) return;
        String msg = plugin.getConfigManager().getStaffJoinMessage().replace("{player}", player.getName());
        for (Player online : Bukkit.getOnlinePlayers()) if (isStaff(online) && !online.equals(player)) online.sendMessage(plugin.getMessagesConfig().parse(msg));
    }

    public void handleStaffLeave(Player player) {
        if (!isStaff(player) || !plugin.getConfigManager().isStaffNotifyLeave()) return;
        String msg = plugin.getConfigManager().getStaffLeaveMessage().replace("{player}", player.getName());
        for (Player online : Bukkit.getOnlinePlayers()) if (isStaff(online) && !online.equals(player)) online.sendMessage(plugin.getMessagesConfig().parse(msg));
    }
}
