package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("tp") @CommandPermission("vulcan.teleport")
public class TeleportCommand {
    private final VulcanSMP plugin; public TeleportCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor("tp")
    public void teleport(Player sender, Player target, @Optional Player toPlayer) {
        if (toPlayer != null) {
            if (!sender.hasPermission("vulcan.teleport.others")) { sender.sendMessage(plugin.getMessagesConfig().prefixed("general.no-permission")); return; }
            plugin.getTeleportManager().teleport(target, toPlayer.getLocation());
            sender.sendMessage(plugin.getMessagesConfig().prefixed("teleport.teleported-other", Placeholder.unparsed("player", target.getName()), Placeholder.unparsed("target", toPlayer.getName())));
            target.sendMessage(plugin.getMessagesConfig().prefixed("teleport.teleported-to", Placeholder.unparsed("player", toPlayer.getName())));
        } else {
            plugin.getTeleportManager().teleport(sender, target.getLocation());
            sender.sendMessage(plugin.getMessagesConfig().prefixed("teleport.teleported-to", Placeholder.unparsed("player", target.getName())));
        }
    }
}
