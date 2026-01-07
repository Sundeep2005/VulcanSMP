package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"gamemode", "gm"})
public class GamemodeCommand {

    private final VulcanSMP plugin;
    public GamemodeCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @Subcommand({"creative", "c", "1"})
    @CommandPermission("vulcan.gamemode.creative")
    public void creative(Player sender, @Optional Player target) {
        Player t = (target != null) ? target : sender;

        // als je iemand anders wil zetten, check andere perm
        if (target != null && !sender.hasPermission("vulcan.gamemode.others")) {
            sender.sendMessage(plugin.getMessagesConfig().prefixed("no-permission"));
            return;
        }

        setGameMode(t, GameMode.CREATIVE);
        if (target != null) notifySender(sender, t, GameMode.CREATIVE);
    }

    @Subcommand({"survival", "s", "0"})
    @CommandPermission("vulcan.gamemode.survival")
    public void survival(Player sender, @Optional Player target) {
        Player t = (target != null) ? target : sender;

        if (target != null && !sender.hasPermission("vulcan.gamemode.others")) {
            sender.sendMessage(plugin.getMessagesConfig().prefixed("no-permission"));
            return;
        }

        setGameMode(t, GameMode.SURVIVAL);
        if (target != null) notifySender(sender, t, GameMode.SURVIVAL);
    }

    @Subcommand({"adventure", "a", "2"})
    @CommandPermission("vulcan.gamemode.adventure")
    public void adventure(Player sender, @Optional Player target) {
        Player t = (target != null) ? target : sender;

        if (target != null && !sender.hasPermission("vulcan.gamemode.others")) {
            sender.sendMessage(plugin.getMessagesConfig().prefixed("no-permission"));
            return;
        }

        setGameMode(t, GameMode.ADVENTURE);
        if (target != null) notifySender(sender, t, GameMode.ADVENTURE);
    }

    @Subcommand({"spectator", "sp", "3"})
    @CommandPermission("vulcan.gamemode.spectator")
    public void spectator(Player sender, @Optional Player target) {
        Player t = (target != null) ? target : sender;

        if (target != null && !sender.hasPermission("vulcan.gamemode.others")) {
            sender.sendMessage(plugin.getMessagesConfig().prefixed("no-permission"));
            return;
        }

        setGameMode(t, GameMode.SPECTATOR);
        if (target != null) notifySender(sender, t, GameMode.SPECTATOR);
    }

    private void setGameMode(Player player, GameMode mode) {
        player.setGameMode(mode);
        player.sendMessage(plugin.getMessagesConfig().prefixed(
                "gamemode.changed",
                Placeholder.unparsed("gamemode", mode.name().toLowerCase())
        ));
    }

    private void notifySender(Player sender, Player target, GameMode mode) {
        sender.sendMessage(plugin.getMessagesConfig().prefixed(
                "gamemode.changed-other",
                Placeholder.unparsed("player", target.getName()),
                Placeholder.unparsed("gamemode", mode.name().toLowerCase())
        ));
    }
}

