package nl.sundeep.vulcansmp.hooks;

import nl.sundeep.vulcansmp.VulcanSMP;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook {
    private final VulcanSMP plugin;
    private boolean enabled;

    public PlaceholderAPIHook(VulcanSMP plugin) { this.plugin = plugin; this.enabled = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null; }
    public boolean isEnabled() { return enabled; }
    public String setPlaceholders(Player player, String text) { return enabled ? PlaceholderAPI.setPlaceholders(player, text) : text; }
}
