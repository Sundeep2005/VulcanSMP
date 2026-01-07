package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class HealFeedCommand {
    private final VulcanSMP plugin; public HealFeedCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @Command("heal") @CommandPermission("vulcan.heal")
    public void heal(Player sender, @Optional Player target) {
        if (target != null && !target.equals(sender)) {
            if (!sender.hasPermission("vulcan.heal.others")) { sender.sendMessage(plugin.getMessagesConfig().prefixed("general.no-permission")); return; }
            healPlayer(target); sender.sendMessage(plugin.getMessagesConfig().prefixed("heal.healed-other", Placeholder.unparsed("player", target.getName())));
        } else { healPlayer(sender); sender.sendMessage(plugin.getMessagesConfig().prefixed("heal.healed")); }
    }

    @Command("feed") @CommandPermission("vulcan.feed")
    public void feed(Player sender, @Optional Player target) {
        if (target != null && !target.equals(sender)) {
            if (!sender.hasPermission("vulcan.feed.others")) { sender.sendMessage(plugin.getMessagesConfig().prefixed("general.no-permission")); return; }
            feedPlayer(target); sender.sendMessage(plugin.getMessagesConfig().prefixed("feed.fed-other", Placeholder.unparsed("player", target.getName())));
        } else { feedPlayer(sender); sender.sendMessage(plugin.getMessagesConfig().prefixed("feed.fed")); }
    }

    private void healPlayer(Player player) { player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); player.setFireTicks(0); player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType())); }
    private void feedPlayer(Player player) { player.setFoodLevel(20); player.setSaturation(20f); player.setExhaustion(0f); }
}
