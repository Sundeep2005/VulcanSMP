package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.hooks.VaultHook;
import nl.sundeep.vulcansmp.utils.Utils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import java.util.Map;

public class EconomyCommand {
    private final VulcanSMP plugin; public EconomyCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @Command("baltop") @CommandPermission("vulcan.baltop")
    public void balanceTop(Player player, @Optional @Range(min = 1, max = 100) Integer count) {
        VaultHook vault = plugin.getVaultHook();
        if (vault == null || !vault.isEnabled()) { player.sendMessage(plugin.getMessagesConfig().prefixed("economy.no-economy")); return; }
        int limit = count != null ? count : 10;
        vault.getTopBalances(limit).thenAccept(balances -> {
            Utils.runSync(() -> {
                player.sendMessage(plugin.getMessagesConfig().prefixed("economy.baltop-header", Placeholder.unparsed("count", String.valueOf(limit))));
                int rank = 1;
                for (Map.Entry<String, Double> entry : balances) {
                    player.sendMessage(plugin.getMessagesConfig().get("economy.baltop-entry", Placeholder.unparsed("rank", String.valueOf(rank)), Placeholder.unparsed("player", entry.getKey()), Placeholder.unparsed("balance", Utils.formatMoney(entry.getValue()))));
                    rank++;
                }
            });
        });
    }

    @Command("money") @Subcommand("pay") @CommandPermission("vulcan.money.pay")
    public void pay(Player sender, Player target, double amount) {
        VaultHook vault = plugin.getVaultHook();
        if (vault == null || !vault.isEnabled()) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.no-economy")); return; }
        if (amount <= 0) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.invalid-amount")); return; }
        if (sender.equals(target)) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.cannot-pay-self")); return; }
        if (!vault.hasBalance(sender, amount)) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.insufficient-funds")); return; }
        if (vault.transfer(sender, target, amount)) {
            sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.paid", Placeholder.unparsed("player", target.getName()), Placeholder.unparsed("amount", Utils.formatMoney(amount))));
            target.sendMessage(plugin.getMessagesConfig().prefixed("economy.received", Placeholder.unparsed("player", sender.getName()), Placeholder.unparsed("amount", Utils.formatMoney(amount))));
        } else sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.transaction-failed"));
    }

    @Command("money") @Subcommand("give") @CommandPermission("vulcan.money.give")
    public void give(Player sender, OfflinePlayer target, double amount) {
        VaultHook vault = plugin.getVaultHook();
        if (vault == null || !vault.isEnabled()) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.no-economy")); return; }
        if (amount <= 0) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.invalid-amount")); return; }
        if (vault.deposit(target, amount)) sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.given", Placeholder.unparsed("player", target.getName() != null ? target.getName() : target.getUniqueId().toString()), Placeholder.unparsed("amount", Utils.formatMoney(amount))));
        else sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.transaction-failed"));
    }

    @Command("money") @Subcommand("take") @CommandPermission("vulcan.money.take")
    public void take(Player sender, OfflinePlayer target, double amount) {
        VaultHook vault = plugin.getVaultHook();
        if (vault == null || !vault.isEnabled()) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.no-economy")); return; }
        if (amount <= 0) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.invalid-amount")); return; }
        if (vault.withdraw(target, amount)) sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.taken", Placeholder.unparsed("player", target.getName() != null ? target.getName() : target.getUniqueId().toString()), Placeholder.unparsed("amount", Utils.formatMoney(amount))));
        else sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.transaction-failed"));
    }

    @Command("money") @Subcommand("set") @CommandPermission("vulcan.money.set")
    public void set(Player sender, OfflinePlayer target, double amount) {
        VaultHook vault = plugin.getVaultHook();
        if (vault == null || !vault.isEnabled()) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.no-economy")); return; }
        if (amount < 0) { sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.invalid-amount")); return; }
        if (vault.setBalance(target, amount)) sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.set", Placeholder.unparsed("player", target.getName() != null ? target.getName() : target.getUniqueId().toString()), Placeholder.unparsed("amount", Utils.formatMoney(amount))));
        else sender.sendMessage(plugin.getMessagesConfig().prefixed("economy.transaction-failed"));
    }
}
