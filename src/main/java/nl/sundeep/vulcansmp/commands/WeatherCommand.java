package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.World;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("weather") @CommandPermission("vulcan.weather")
public class WeatherCommand {
    private final VulcanSMP plugin;
    public WeatherCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @DefaultFor("weather")
    public void weather(Player player, @Named("@weather") String weatherType) {
        World world = player.getWorld(); String type = weatherType.toLowerCase();
        switch (type) {
            case "clear", "sun" -> { world.setStorm(false); world.setThundering(false); player.sendMessage(plugin.getMessagesConfig().prefixed("weather.set", Placeholder.unparsed("weather", "clear"))); }
            case "rain" -> { world.setStorm(true); world.setThundering(false); player.sendMessage(plugin.getMessagesConfig().prefixed("weather.set", Placeholder.unparsed("weather", "rain"))); }
            case "thunder", "storm" -> { world.setStorm(true); world.setThundering(true); player.sendMessage(plugin.getMessagesConfig().prefixed("weather.set", Placeholder.unparsed("weather", "thunder"))); }
            default -> player.sendMessage(plugin.getMessagesConfig().prefixed("weather.invalid-type"));
        }
    }
}
