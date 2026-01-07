package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("spawn")
public class SpawnCommand {
    private final VulcanSMP plugin;
    public SpawnCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @DefaultFor("spawn") @CommandPermission("vulcan.spawn")
    public void spawn(Player player) {
        plugin.getTeleportManager().teleportWithDelay(player, plugin.getSpawnManager().getDefaultSpawn(), plugin.getConfigManager().getTeleportDelay());
        player.sendMessage(plugin.getMessagesConfig().prefixed("spawn.teleporting"));
    }

    @Command("setspawn") @CommandPermission("vulcan.setspawn")
    public void setSpawn(Player player, @Optional @Named("@spawn") String spawnName) {
        String name = spawnName != null ? spawnName : "spawn";
        plugin.getSpawnManager().setSpawn(name, player.getLocation()).thenAccept(success -> player.sendMessage(plugin.getMessagesConfig().prefixed(success ? "spawn.set" : "spawn.set-failed", Placeholder.unparsed("spawn", name))));
    }

    @Command("removespawn") @CommandPermission("vulcan.removespawn")
    public void removeSpawn(Player player, @Named("@spawn") String spawnName) {
        if (!plugin.getSpawnManager().spawnExists(spawnName)) { player.sendMessage(plugin.getMessagesConfig().prefixed("spawn.not-found", Placeholder.unparsed("spawn", spawnName))); return; }
        plugin.getSpawnManager().removeSpawn(spawnName).thenAccept(success -> player.sendMessage(plugin.getMessagesConfig().prefixed(success ? "spawn.removed" : "spawn.remove-failed", Placeholder.unparsed("spawn", spawnName))));
    }
}
