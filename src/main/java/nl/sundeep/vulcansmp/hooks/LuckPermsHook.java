package nl.sundeep.vulcansmp.hooks;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

public class LuckPermsHook {
    private final VulcanSMP plugin;
    private LuckPerms luckPerms;
    private boolean enabled;

    public LuckPermsHook(VulcanSMP plugin) {
        this.plugin = plugin;
        try { this.luckPerms = LuckPermsProvider.get(); this.enabled = true; } catch (IllegalStateException e) { this.enabled = false; }
    }

    public boolean isEnabled() { return enabled && luckPerms != null; }

    public String getPrefix(Player player) {
        if (!isEnabled()) return "";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId()); if (user == null) return "";
        String prefix = user.getCachedData().getMetaData().getPrefix(); return prefix != null ? prefix : "";
    }

    public String getSuffix(Player player) {
        if (!isEnabled()) return "";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId()); if (user == null) return "";
        String suffix = user.getCachedData().getMetaData().getSuffix(); return suffix != null ? suffix : "";
    }

    public String getPrimaryGroup(Player player) {
        if (!isEnabled()) return "";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId()); return user != null ? user.getPrimaryGroup() : "";
    }
}
