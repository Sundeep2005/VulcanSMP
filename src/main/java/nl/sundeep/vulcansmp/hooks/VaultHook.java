package nl.sundeep.vulcansmp.hooks;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VaultHook {
    private final VulcanSMP plugin;
    private Economy economy;
    private boolean enabled;

    public VaultHook(VulcanSMP plugin) { this.plugin = plugin; this.enabled = false; }

    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        enabled = economy != null;
        return enabled;
    }

    public boolean isEnabled() { return enabled && economy != null; }
    public double getBalance(OfflinePlayer player) { return isEnabled() ? economy.getBalance(player) : 0; }
    public boolean hasBalance(OfflinePlayer player, double amount) { return isEnabled() && economy.has(player, amount); }
    public boolean withdraw(OfflinePlayer player, double amount) { return isEnabled() && economy.withdrawPlayer(player, amount).transactionSuccess(); }
    public boolean deposit(OfflinePlayer player, double amount) { return isEnabled() && economy.depositPlayer(player, amount).transactionSuccess(); }

    public boolean transfer(OfflinePlayer from, OfflinePlayer to, double amount) {
        if (!isEnabled() || !hasBalance(from, amount)) return false;
        if (withdraw(from, amount)) { if (deposit(to, amount)) return true; else { deposit(from, amount); return false; } }
        return false;
    }

    public boolean setBalance(OfflinePlayer player, double amount) {
        if (!isEnabled()) return false;
        double current = getBalance(player);
        double diff = amount - current;
        return diff > 0 ? deposit(player, diff) : diff < 0 ? withdraw(player, Math.abs(diff)) : true;
    }

    public CompletableFuture<List<Map.Entry<String, Double>>> getTopBalances(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isEnabled()) return Collections.emptyList();
            Map<String, Double> balances = new HashMap<>();
            for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) if (player.getName() != null) balances.put(player.getName(), getBalance(player));
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(balances.entrySet());
            sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            return sorted.subList(0, Math.min(limit, sorted.size()));
        });
    }

    public String format(double amount) { return isEnabled() ? economy.format(amount) : String.valueOf(amount); }
}
