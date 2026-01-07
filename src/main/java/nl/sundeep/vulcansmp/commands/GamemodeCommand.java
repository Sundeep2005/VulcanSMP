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

    @Subcommand({"creative", "c", "1"}) @CommandPermission("vulcan.gamemode.creative")
    public void creative(Player player) { setGameMode(player, GameMode.CREATIVE); }
    @Subcommand({"creative", "c", "1"}) @CommandPermission("vulcan.gamemode.others")
    public void creativeOther(Player sender, Player target) { setGameMode(target, GameMode.CREATIVE); notifySender(sender, target, GameMode.CREATIVE); }

    @Subcommand({"survival", "s", "0"}) @CommandPermission("vulcan.gamemode.survival")
    public void survival(Player player) { setGameMode(player, GameMode.SURVIVAL); }
    @Subcommand({"survival", "s", "0"}) @CommandPermission("vulcan.gamemode.others")
    public void survivalOther(Player sender, Player target) { setGameMode(target, GameMode.SURVIVAL); notifySender(sender, target, GameMode.SURVIVAL); }

    @Subcommand({"adventure", "a", "2"}) @CommandPermission("vulcan.gamemode.adventure")
    public void adventure(Player player) { setGameMode(player, GameMode.ADVENTURE); }
    @Subcommand({"adventure", "a", "2"}) @CommandPermission("vulcan.gamemode.others")
    public void adventureOther(Player sender, Player target) { setGameMode(target, GameMode.ADVENTURE); notifySender(sender, target, GameMode.ADVENTURE); }

    @Subcommand({"spectator", "sp", "3"}) @CommandPermission("vulcan.gamemode.spectator")
    public void spectator(Player player) { setGameMode(player, GameMode.SPECTATOR); }
    @Subcommand({"spectator", "sp", "3"}) @CommandPermission("vulcan.gamemode.others")
    public void spectatorOther(Player sender, Player target) { setGameMode(target, GameMode.SPECTATOR); notifySender(sender, target, GameMode.SPECTATOR); }

    private void setGameMode(Player player, GameMode mode) { player.setGameMode(mode); player.sendMessage(plugin.getMessagesConfig().prefixed("gamemode.changed", Placeholder.unparsed("gamemode", mode.name().toLowerCase()))); }
    private void notifySender(Player sender, Player target, GameMode mode) { sender.sendMessage(plugin.getMessagesConfig().prefixed("gamemode.changed-other", Placeholder.unparsed("player", target.getName()), Placeholder.unparsed("gamemode", mode.name().toLowerCase()))); }
}

@Command("gmc") @CommandPermission("vulcan.gamemode.creative")
class GmcCommand { private final VulcanSMP plugin; public GmcCommand(VulcanSMP plugin) { this.plugin = plugin; } @DefaultFor("gmc") public void execute(Player player) { player.setGameMode(GameMode.CREATIVE); player.sendMessage(plugin.getMessagesConfig().prefixed("gamemode.changed", Placeholder.unparsed("gamemode", "creative"))); } }

@Command("gms") @CommandPermission("vulcan.gamemode.survival")
class GmsCommand { private final VulcanSMP plugin; public GmsCommand(VulcanSMP plugin) { this.plugin = plugin; } @DefaultFor("gms") public void execute(Player player) { player.setGameMode(GameMode.SURVIVAL); player.sendMessage(plugin.getMessagesConfig().prefixed("gamemode.changed", Placeholder.unparsed("gamemode", "survival"))); } }

@Command("gma") @CommandPermission("vulcan.gamemode.adventure")
class GmaCommand { private final VulcanSMP plugin; public GmaCommand(VulcanSMP plugin) { this.plugin = plugin; } @DefaultFor("gma") public void execute(Player player) { player.setGameMode(GameMode.ADVENTURE); player.sendMessage(plugin.getMessagesConfig().prefixed("gamemode.changed", Placeholder.unparsed("gamemode", "adventure"))); } }

@Command("gmsp") @CommandPermission("vulcan.gamemode.spectator")
class GmspCommand { private final VulcanSMP plugin; public GmspCommand(VulcanSMP plugin) { this.plugin = plugin; } @DefaultFor("gmsp") public void execute(Player player) { player.setGameMode(GameMode.SPECTATOR); player.sendMessage(plugin.getMessagesConfig().prefixed("gamemode.changed", Placeholder.unparsed("gamemode", "spectator"))); } }
