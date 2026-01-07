package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"broadcast", "bc"}) @CommandPermission("vulcan.broadcast")
public class BroadcastCommand {
    private final VulcanSMP plugin; public BroadcastCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor({"broadcast", "bc"})
    public void broadcast(Player sender, String message) {
        String format = plugin.getMessagesConfig().getRaw("broadcast.format");
        Bukkit.broadcast(plugin.getMessagesConfig().parse(format.replace("{message}", message)));
    }
}
