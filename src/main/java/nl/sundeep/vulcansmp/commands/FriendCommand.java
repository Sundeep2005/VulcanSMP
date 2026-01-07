package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.utils.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import java.util.UUID;

@Command("friend") @CommandPermission("vulcan.friend")
public class FriendCommand {
    private final VulcanSMP plugin; public FriendCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @Subcommand("add")
    public void add(Player sender, Player target) {
        if (sender.equals(target)) { sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.cannot-self")); return; }
        plugin.getFriendManager().areFriends(sender.getUniqueId(), target.getUniqueId()).thenAccept(areFriends -> {
            if (areFriends) { sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.already-friends", Placeholder.unparsed("player", target.getName()))); return; }
            if (plugin.getFriendManager().hasPendingRequest(sender.getUniqueId(), target.getUniqueId())) {
                plugin.getFriendManager().removePendingRequest(sender.getUniqueId(), target.getUniqueId());
                plugin.getFriendManager().addFriend(sender.getUniqueId(), target.getUniqueId()).thenAccept(success -> {
                    if (success) { sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.accepted", Placeholder.unparsed("player", target.getName()))); target.sendMessage(plugin.getMessagesConfig().prefixed("friend.request-accepted", Placeholder.unparsed("player", sender.getName()))); }
                });
                return;
            }
            if (plugin.getFriendManager().hasPendingRequest(target.getUniqueId(), sender.getUniqueId())) { sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.request-pending", Placeholder.unparsed("player", target.getName()))); return; }
            plugin.getFriendManager().sendFriendRequest(sender.getUniqueId(), target.getUniqueId());
            sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.request-sent", Placeholder.unparsed("player", target.getName())));
            target.sendMessage(plugin.getMessagesConfig().prefixed("friend.request-received", Placeholder.unparsed("player", sender.getName())));
        });
    }

    @Subcommand("remove")
    public void remove(Player sender, String targetName) {
        @SuppressWarnings("deprecation") OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        plugin.getFriendManager().areFriends(sender.getUniqueId(), target.getUniqueId()).thenAccept(areFriends -> {
            if (!areFriends) { sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.not-friends", Placeholder.unparsed("player", targetName))); return; }
            plugin.getFriendManager().removeFriend(sender.getUniqueId(), target.getUniqueId()).thenAccept(success -> {
                if (success) {
                    sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.removed", Placeholder.unparsed("player", targetName)));
                    Player onlineTarget = Bukkit.getPlayer(target.getUniqueId());
                    if (onlineTarget != null) onlineTarget.sendMessage(plugin.getMessagesConfig().prefixed("friend.removed-by", Placeholder.unparsed("player", sender.getName())));
                }
            });
        });
    }

    @Subcommand("list")
    public void list(Player sender) {
        plugin.getFriendManager().getFriends(sender.getUniqueId()).thenAccept(friends -> {
            Utils.runSync(() -> {
                if (friends.isEmpty()) { sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.no-friends")); return; }
                sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.list-header", Placeholder.unparsed("count", String.valueOf(friends.size()))));
                for (UUID friendUuid : friends) {
                    OfflinePlayer friend = Bukkit.getOfflinePlayer(friendUuid);
                    String name = friend.getName() != null ? friend.getName() : friendUuid.toString();
                    sender.sendMessage(plugin.getMessagesConfig().get("friend.list-entry", Placeholder.unparsed("player", name), Placeholder.unparsed("status", friend.isOnline() ? "<green>Online" : "<gray>Offline")));
                }
            });
        });
    }

    @DefaultFor("friend") public void help(Player sender) { sender.sendMessage(plugin.getMessagesConfig().prefixed("friend.help")); }
}
