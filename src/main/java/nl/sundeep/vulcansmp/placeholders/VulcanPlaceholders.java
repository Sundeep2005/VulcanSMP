package nl.sundeep.vulcansmp.placeholders;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VulcanPlaceholders extends PlaceholderExpansion {
    private final VulcanSMP plugin; public VulcanPlaceholders(VulcanSMP plugin) { this.plugin = plugin; }

    @Override public @NotNull String getIdentifier() { return "vulcan"; }
    @Override public @NotNull String getAuthor() { return String.join(", ", plugin.getPluginMeta().getAuthors()); }
    @Override public @NotNull String getVersion() { return plugin.getPluginMeta().getVersion(); }
    @Override public boolean persist() { return true; }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) return null;
        Player player = offlinePlayer.getPlayer();

        if (params.equalsIgnoreCase("playtime")) return player != null ? Utils.formatPlaytime(plugin.getPlaytimeManager().getPlaytime(player)) : "0";
        if (params.equalsIgnoreCase("playtime_raw")) return player != null ? String.valueOf(plugin.getPlaytimeManager().getPlaytime(player)) : "0";
        if (params.equalsIgnoreCase("vanished")) return String.valueOf(plugin.getVanishManager().isVanished(offlinePlayer.getUniqueId()));
        if (params.equalsIgnoreCase("friends")) { try { return String.valueOf(plugin.getFriendManager().getFriends(offlinePlayer.getUniqueId()).get().size()); } catch (Exception e) { return "0"; } }
        if (params.equalsIgnoreCase("homes")) { try { return String.valueOf(plugin.getHomeManager().getHomeCount(offlinePlayer.getUniqueId()).get()); } catch (Exception e) { return "0"; } }
        if (params.equalsIgnoreCase("home_limit")) { if (player != null) { int limit = Utils.getHomeLimit(player); return limit == Integer.MAX_VALUE ? "∞" : String.valueOf(limit); } return String.valueOf(plugin.getConfigManager().getDefaultHomeLimit()); }
        if (params.equalsIgnoreCase("balance")) return plugin.getVaultHook() != null && plugin.getVaultHook().isEnabled() ? Utils.formatMoney(plugin.getVaultHook().getBalance(offlinePlayer)) : "0";
        if (params.equalsIgnoreCase("balance_raw")) return plugin.getVaultHook() != null && plugin.getVaultHook().isEnabled() ? String.valueOf(plugin.getVaultHook().getBalance(offlinePlayer)) : "0";
        if (params.equalsIgnoreCase("is_staff")) return player != null ? String.valueOf(plugin.getStaffManager().isStaff(player)) : "false";
        if (plugin.getLuckPermsHook() != null && plugin.getLuckPermsHook().isEnabled() && player != null) {
            if (params.equalsIgnoreCase("prefix")) return plugin.getLuckPermsHook().getPrefix(player);
            if (params.equalsIgnoreCase("suffix")) return plugin.getLuckPermsHook().getSuffix(player);
            if (params.equalsIgnoreCase("group")) return plugin.getLuckPermsHook().getPrimaryGroup(player);
        }
        return null;
    }
}
