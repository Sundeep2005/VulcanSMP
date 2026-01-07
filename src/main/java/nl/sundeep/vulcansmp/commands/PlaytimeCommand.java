package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.utils.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("playtime") @CommandPermission("vulcan.playtime")
public class PlaytimeCommand {
    private final VulcanSMP plugin; public PlaytimeCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor("playtime")
    public void playtime(Player sender, @Optional String targetName) {
        if (targetName != null) {
            if (!sender.hasPermission("vulcan.playtime.others")) { sender.sendMessage(plugin.getMessagesConfig().prefixed("general.no-permission")); return; }
            Player onlineTarget = Bukkit.getPlayer(targetName);
            if (onlineTarget != null) { sender.sendMessage(plugin.getMessagesConfig().prefixed("playtime.other", Placeholder.unparsed("player", onlineTarget.getName()), Placeholder.unparsed("time", Utils.formatPlaytime(plugin.getPlaytimeManager().getPlaytime(onlineTarget))))); return; }
            @SuppressWarnings("deprecation") OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
            if (offlineTarget.hasPlayedBefore()) {
                plugin.getPlaytimeManager().getPlaytime(offlineTarget.getUniqueId()).thenAccept(playtime -> Utils.runSync(() -> sender.sendMessage(plugin.getMessagesConfig().prefixed("playtime.other", Placeholder.unparsed("player", targetName), Placeholder.unparsed("time", Utils.formatPlaytime(playtime))))));
            } else sender.sendMessage(plugin.getMessagesConfig().prefixed("general.player-not-found", Placeholder.unparsed("player", targetName)));
        } else sender.sendMessage(plugin.getMessagesConfig().prefixed("playtime.self", Placeholder.unparsed("time", Utils.formatPlaytime(plugin.getPlaytimeManager().getPlaytime(sender)))));
    }
}
