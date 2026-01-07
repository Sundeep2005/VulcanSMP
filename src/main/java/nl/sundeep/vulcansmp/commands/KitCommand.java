package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.kits.KitManager;
import nl.sundeep.vulcansmp.utils.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("kit")
public class KitCommand {
    private final VulcanSMP plugin;
    public KitCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @DefaultFor("kit") @CommandPermission("vulcan.kit")
    public void kit(Player player, @Named("@kit") String kitName) {
        KitManager.Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) { player.sendMessage(plugin.getMessagesConfig().prefixed("kit.not-found", Placeholder.unparsed("kit", kitName))); return; }
        if (!player.hasPermission("vulcan.kit." + kitName.toLowerCase()) && !player.hasPermission("vulcan.kit.*")) { player.sendMessage(plugin.getMessagesConfig().prefixed("kit.no-permission", Placeholder.unparsed("kit", kitName))); return; }
        if (plugin.getKitManager().isOnCooldown(player, kitName)) { player.sendMessage(plugin.getMessagesConfig().prefixed("kit.cooldown", Placeholder.unparsed("kit", kitName), Placeholder.unparsed("time", Utils.formatTime(plugin.getKitManager().getCooldownRemaining(player, kitName))))); return; }
        plugin.getKitManager().giveKit(player, kit); plugin.getKitManager().applyCooldown(player, kitName);
        player.sendMessage(plugin.getMessagesConfig().prefixed("kit.received", Placeholder.unparsed("kit", kitName)));
    }

    @Subcommand("create") @CommandPermission("vulcan.kit.create")
    public void create(Player player, String kitName) {
        if (plugin.getKitManager().kitExists(kitName)) { player.sendMessage(plugin.getMessagesConfig().prefixed("kit.already-exists", Placeholder.unparsed("kit", kitName))); return; }
        plugin.getKitManager().createKit(kitName, player).thenAccept(success -> player.sendMessage(plugin.getMessagesConfig().prefixed(success ? "kit.created" : "kit.create-failed", Placeholder.unparsed("kit", kitName))));
    }

    @Subcommand("delete") @CommandPermission("vulcan.kit.delete")
    public void delete(Player player, @Named("@kit") String kitName) {
        if (!plugin.getKitManager().kitExists(kitName)) { player.sendMessage(plugin.getMessagesConfig().prefixed("kit.not-found", Placeholder.unparsed("kit", kitName))); return; }
        plugin.getKitManager().deleteKit(kitName).thenAccept(success -> player.sendMessage(plugin.getMessagesConfig().prefixed(success ? "kit.deleted" : "kit.delete-failed", Placeholder.unparsed("kit", kitName))));
    }

    @Subcommand("list") @CommandPermission("vulcan.kit")
    public void list(Player player) {
        var kits = plugin.getKitManager().getAllKits();
        if (kits.isEmpty()) { player.sendMessage(plugin.getMessagesConfig().prefixed("kit.no-kits")); return; }
        player.sendMessage(plugin.getMessagesConfig().prefixed("kit.list-header"));
        for (KitManager.Kit kit : kits) player.sendMessage(plugin.getMessagesConfig().get("kit.list-entry", Placeholder.unparsed("kit", kit.getName()), Placeholder.unparsed("cooldown", plugin.getConfigManager().getKitCooldown(kit.getName()) > 0 ? Utils.formatTime(plugin.getConfigManager().getKitCooldown(kit.getName())) : "geen")));
    }
}
