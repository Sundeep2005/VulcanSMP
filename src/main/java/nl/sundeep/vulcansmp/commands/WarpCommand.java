package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.utils.Utils;
import nl.sundeep.vulcansmp.warps.WarpManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("warp")
public class WarpCommand {
    private final VulcanSMP plugin;
    public WarpCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @DefaultFor("warp") @CommandPermission("vulcan.warp")
    public void warp(Player player, @Named("@warp") String warpName) {
        WarpManager.Warp warp = plugin.getWarpManager().getWarp(warpName);
        if (warp == null) { player.sendMessage(plugin.getMessagesConfig().prefixed("warp.not-found", Placeholder.unparsed("warp", warpName))); return; }
        plugin.getTeleportManager().teleportWithDelay(player, warp.getLocation(), plugin.getConfigManager().getTeleportDelay());
        player.sendMessage(plugin.getMessagesConfig().prefixed("warp.teleported", Placeholder.unparsed("warp", warp.getName())));
    }

    @Subcommand("list") @CommandPermission("vulcan.warp.list")
    public void list(Player player) {
        var warps = plugin.getWarpManager().getAllWarps();
        if (warps.isEmpty()) { player.sendMessage(plugin.getMessagesConfig().prefixed("warp.no-warps")); return; }
        player.sendMessage(plugin.getMessagesConfig().prefixed("warp.list-header"));
        for (WarpManager.Warp warp : warps) player.sendMessage(plugin.getMessagesConfig().get("warp.list-entry", Placeholder.unparsed("warp", warp.getName()), Placeholder.unparsed("location", Utils.formatLocation(warp.getLocation()))));
    }

    @Subcommand("create") @CommandPermission("vulcan.warp.create")
    public void create(Player player, String warpName) {
        if (plugin.getWarpManager().warpExists(warpName)) { player.sendMessage(plugin.getMessagesConfig().prefixed("warp.already-exists", Placeholder.unparsed("warp", warpName))); return; }
        plugin.getWarpManager().createWarp(warpName, player.getLocation(), player.getUniqueId()).thenAccept(success -> player.sendMessage(plugin.getMessagesConfig().prefixed(success ? "warp.created" : "warp.create-failed", Placeholder.unparsed("warp", warpName))));
    }

    @Subcommand("delete") @CommandPermission("vulcan.warp.delete")
    public void delete(Player player, @Named("@warp") String warpName) {
        if (!plugin.getWarpManager().warpExists(warpName)) { player.sendMessage(plugin.getMessagesConfig().prefixed("warp.not-found", Placeholder.unparsed("warp", warpName))); return; }
        plugin.getWarpManager().deleteWarp(warpName).thenAccept(success -> player.sendMessage(plugin.getMessagesConfig().prefixed(success ? "warp.deleted" : "warp.delete-failed", Placeholder.unparsed("warp", warpName))));
    }
}
