package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"vanish", "v"}) @CommandPermission("vulcan.vanish")
public class VanishCommand {
    private final VulcanSMP plugin; public VanishCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor({"vanish", "v"}) public void vanish(Player player) { plugin.getVanishManager().toggleVanish(player); }
}
