package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"enderchest", "ec"}) @CommandPermission("vulcan.enderchest")
public class EnderchestCommand {
    private final VulcanSMP plugin; public EnderchestCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor({"enderchest", "ec"})
    public void enderchest(Player sender, @Optional Player target) {
        if (target != null && !target.equals(sender)) {
            if (!sender.hasPermission("vulcan.enderchest.others")) { sender.sendMessage(plugin.getMessagesConfig().prefixed("general.no-permission")); return; }
            sender.openInventory(target.getEnderChest()); sender.sendMessage(plugin.getMessagesConfig().prefixed("enderchest.opened-other", Placeholder.unparsed("player", target.getName())));
        } else { sender.openInventory(sender.getEnderChest()); sender.sendMessage(plugin.getMessagesConfig().prefixed("enderchest.opened")); }
    }
}
