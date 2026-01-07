package nl.sundeep.vulcansmp.commands;

import nl.sundeep.vulcansmp.VulcanSMP;
import nl.sundeep.vulcansmp.homes.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.autocomplete.SuggestionProvider;

import java.util.*;
import java.util.stream.Collectors;

public class CommandRegistrar {
    public static void registerResolvers(BukkitCommandHandler handler, VulcanSMP plugin) {
        handler.getAutoCompleter().registerSuggestion("players", SuggestionProvider.map(
                () -> Bukkit.getOnlinePlayers(),
                Player::getName
        ));

        handler.getAutoCompleter().registerSuggestion("home", (args, sender, command) -> {
            if (sender instanceof Player player) {
                try {
                    return plugin.getHomeManager().getHomes(player.getUniqueId()).get()
                            .stream()
                            .map(HomeManager.Home::getName)
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    return Collections.emptyList();
                }
            }
            return Collections.emptyList();
        });

        handler.getAutoCompleter().registerSuggestion("warp", (args, sender, command) ->
                plugin.getWarpManager().getWarpNames()
        );

        handler.getAutoCompleter().registerSuggestion("kit", (args, sender, command) ->
                plugin.getKitManager().getKitNames()
        );

        handler.getAutoCompleter().registerSuggestion("spawn", (args, sender, command) ->
                plugin.getSpawnManager().getSpawnNames()
        );

        handler.getAutoCompleter().registerSuggestion("weather",
                SuggestionProvider.of("clear", "sun", "rain", "thunder")
        );
    }
}
