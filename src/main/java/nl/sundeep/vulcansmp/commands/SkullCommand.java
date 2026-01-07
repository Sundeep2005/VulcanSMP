package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.hooks.HeadDatabaseHook;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("skull") @CommandPermission("vulcan.skull")
public class SkullCommand {
    private final VulcanSMP plugin; public SkullCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor("skull")
    public void skull(Player sender, String name) {
        HeadDatabaseHook hdbHook = plugin.getHeadDatabaseHook();
        if (hdbHook != null && hdbHook.isEnabled()) { ItemStack hdbHead = hdbHook.getHead(name); if (hdbHead != null) { giveItem(sender, hdbHead); sender.sendMessage(plugin.getMessagesConfig().prefixed("skull.received-hdb", Placeholder.unparsed("id", name))); return; } }
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD); SkullMeta meta = (SkullMeta) skull.getItemMeta();
        @SuppressWarnings("deprecation") org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(name);
        meta.setOwningPlayer(target); skull.setItemMeta(meta); giveItem(sender, skull);
        sender.sendMessage(plugin.getMessagesConfig().prefixed("skull.received", Placeholder.unparsed("player", name)));
    }
    private void giveItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) { player.getWorld().dropItemNaturally(player.getLocation(), item); player.sendMessage(plugin.getMessagesConfig().prefixed("skull.dropped")); }
        else player.getInventory().addItem(item);
    }
}
