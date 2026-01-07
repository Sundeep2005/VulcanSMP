package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.homes.HomeManager;
import nl.sundeep.vulcansmp.utils.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("home")
public class HomeCommand {
    private final VulcanSMP plugin;
    public HomeCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @DefaultFor("home") @CommandPermission("vulcan.home")
    public void teleportHome(Player player, @Optional @Named("@home") String homeName) {
        String name = homeName != null ? homeName : "home";
        plugin.getHomeManager().getHome(player.getUniqueId(), name).thenAccept(home -> {
            if (home == null) { player.sendMessage(plugin.getMessagesConfig().prefixed("home.not-found", Placeholder.unparsed("home", name))); return; }
            Utils.runSync(() -> plugin.getTeleportManager().teleportWithDelay(player, home.getLocation(), plugin.getConfigManager().getTeleportDelay()));
        });
    }

    @Subcommand("set") @CommandPermission("vulcan.home.set")
    public void setHome(Player player, @Optional String homeName) {
        String name = homeName != null ? homeName : "home";
        plugin.getHomeManager().getHomeCount(player.getUniqueId()).thenAccept(count -> {
            int limit = Utils.getHomeLimit(player);
            plugin.getHomeManager().getHome(player.getUniqueId(), name).thenAccept(existingHome -> {
                if (existingHome == null && count >= limit) { player.sendMessage(plugin.getMessagesConfig().prefixed("home.limit-reached", Placeholder.unparsed("limit", String.valueOf(limit)))); return; }
                plugin.getHomeManager().setHome(player, name).thenAccept(success -> {
                    player.sendMessage(plugin.getMessagesConfig().prefixed(success ? "home.set" : "home.set-failed", Placeholder.unparsed("home", name)));
                });
            });
        });
    }

    @Subcommand("delete") @CommandPermission("vulcan.home.delete")
    public void deleteHome(Player player, @Named("@home") String homeName) {
        plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName).thenAccept(success -> {
            player.sendMessage(plugin.getMessagesConfig().prefixed(success ? "home.deleted" : "home.not-found", Placeholder.unparsed("home", homeName)));
        });
    }

    @Subcommand("list") @CommandPermission("vulcan.home.list")
    public void listHomes(Player player) {
        plugin.getHomeManager().getHomes(player.getUniqueId()).thenAccept(homes -> {
            if (homes.isEmpty()) { player.sendMessage(plugin.getMessagesConfig().prefixed("home.no-homes")); return; }
            player.sendMessage(plugin.getMessagesConfig().prefixed("home.list-header"));
            for (HomeManager.Home home : homes) player.sendMessage(plugin.getMessagesConfig().get("home.list-entry", Placeholder.unparsed("home", home.getName()), Placeholder.unparsed("location", Utils.formatLocation(home.getLocation()))));
        });
    }
}
