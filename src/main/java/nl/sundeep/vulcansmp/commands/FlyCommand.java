package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("fly") public class FlyCommand {
    private final VulcanSMP plugin; public FlyCommand(VulcanSMP plugin) { this.plugin = plugin; }
    @DefaultFor("fly") @CommandPermission("vulcan.fly")
    public void fly(Player player, @Optional Player target) {
        if (target != null && !target.equals(player)) {
            if (!player.hasPermission("vulcan.fly.others")) { player.sendMessage(plugin.getMessagesConfig().prefixed("general.no-permission")); return; }
            toggleFly(target); player.sendMessage(plugin.getMessagesConfig().prefixed(target.getAllowFlight() ? "fly.enabled-other" : "fly.disabled-other", Placeholder.unparsed("player", target.getName())));
        } else toggleFly(player);
    }
    private void toggleFly(Player player) { boolean newState = !player.getAllowFlight(); player.setAllowFlight(newState); if (!newState) player.setFlying(false); player.sendMessage(plugin.getMessagesConfig().prefixed(newState ? "fly.enabled" : "fly.disabled")); }
}
