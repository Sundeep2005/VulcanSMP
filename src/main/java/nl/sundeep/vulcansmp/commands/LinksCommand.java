package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class LinksCommand {
    private final VulcanSMP plugin; public LinksCommand(VulcanSMP plugin) { this.plugin = plugin; }

    @Command("discord") @CommandPermission("vulcan.discord")
    public void discord(Player player) { String link = plugin.getConfigManager().getDiscordLink(); player.sendMessage(plugin.getMessagesConfig().prefixed("links.discord", Placeholder.unparsed("link", link)).clickEvent(ClickEvent.openUrl(link))); }

    @Command({"store", "shop"}) @CommandPermission("vulcan.store")
    public void store(Player player) { String link = plugin.getConfigManager().getStoreLink(); player.sendMessage(plugin.getMessagesConfig().prefixed("links.store", Placeholder.unparsed("link", link)).clickEvent(ClickEvent.openUrl(link))); }

    @Command("vote") @CommandPermission("vulcan.vote")
    public void vote(Player player) { String link = plugin.getConfigManager().getVoteLink(); player.sendMessage(plugin.getMessagesConfig().prefixed("links.vote", Placeholder.unparsed("link", link)).clickEvent(ClickEvent.openUrl(link))); }
}
