package nl.sundeep.vulcansmp.managers;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageManager {
    private final VulcanSMP plugin;
    private final Map<UUID, UUID> lastMessaged;

    public MessageManager(VulcanSMP plugin) { this.plugin = plugin; this.lastMessaged = new ConcurrentHashMap<>(); }

    public void sendMessage(Player sender, Player receiver, String message) {
        lastMessaged.put(receiver.getUniqueId(), sender.getUniqueId());
        lastMessaged.put(sender.getUniqueId(), receiver.getUniqueId());
        sender.sendMessage(plugin.getMessagesConfig().get("message.sent", Placeholder.unparsed("player", receiver.getName()), Placeholder.unparsed("message", message)));
        receiver.sendMessage(plugin.getMessagesConfig().get("message.received", Placeholder.unparsed("player", sender.getName()), Placeholder.unparsed("message", message)));
    }

    public UUID getLastMessaged(Player player) { return lastMessaged.get(player.getUniqueId()); }
    public void clearLastMessaged(Player player) { lastMessaged.remove(player.getUniqueId()); }
}
