package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import java.util.UUID;

public class MessageCommand {
    private final VulcanSMP plugin; public MessageCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @Command({"msg", "message", "whisper", "w", "tell"}) @CommandPermission("vulcan.msg")
    public void message(Player sender, Player target, String message) {
        if (sender.equals(target)) { sender.sendMessage(plugin.getMessagesConfig().prefixed("message.cannot-self")); return; }
        plugin.getMessageManager().sendMessage(sender, target, message);
    }

    @Command({"reply", "r"}) @CommandPermission("vulcan.reply")
    public void reply(Player sender, String message) {
        UUID lastMessaged = plugin.getMessageManager().getLastMessaged(sender);
        if (lastMessaged == null) { sender.sendMessage(plugin.getMessagesConfig().prefixed("message.no-reply-target")); return; }
        Player target = Bukkit.getPlayer(lastMessaged);
        if (target == null || !target.isOnline()) { sender.sendMessage(plugin.getMessagesConfig().prefixed("message.target-offline")); return; }
        plugin.getMessageManager().sendMessage(sender, target, message);
    }
}
