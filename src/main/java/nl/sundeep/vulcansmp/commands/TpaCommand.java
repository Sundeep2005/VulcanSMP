package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class TpaCommand {
    private final VulcanSMP plugin; public TpaCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @Command("tpa") @CommandPermission("vulcan.tpa")
    public void tpa(Player sender, Player target) {
        if (sender.equals(target)) { sender.sendMessage(plugin.getMessagesConfig().prefixed("tpa.cannot-self")); return; }
        plugin.getTeleportManager().sendTpaRequest(sender, target);
    }

    @Command("tpaccept") @CommandPermission("vulcan.tpaccept")
    public void tpaccept(Player player) { plugin.getTeleportManager().acceptTpaRequest(player); }

    @Command("tpadeny") @CommandPermission("vulcan.tpadeny")
    public void tpadeny(Player player) { plugin.getTeleportManager().denyTpaRequest(player); }

    @Command("tpall") @CommandPermission("vulcan.tpall")
    public void tpall(Player sender) {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(sender)) { plugin.getTeleportManager().teleport(player, sender.getLocation()); player.sendMessage(plugin.getMessagesConfig().prefixed("teleport.teleported-by", Placeholder.unparsed("player", sender.getName()))); count++; }
        }
        sender.sendMessage(plugin.getMessagesConfig().prefixed("teleport.teleported-all", Placeholder.unparsed("count", String.valueOf(count))));
    }
}
