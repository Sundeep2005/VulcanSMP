package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"invsee", "inv"}) @CommandPermission("vulcan.invsee")
public class InvseeCommand {
    private final VulcanSMP plugin; public InvseeCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor({"invsee", "inv"})
    public void invsee(Player sender, Player target) {
        if (sender.equals(target)) { sender.sendMessage(plugin.getMessagesConfig().prefixed("invsee.cannot-self")); return; }
        sender.openInventory(target.getInventory()); sender.sendMessage(plugin.getMessagesConfig().prefixed("invsee.opened", Placeholder.unparsed("player", target.getName())));
    }
}
