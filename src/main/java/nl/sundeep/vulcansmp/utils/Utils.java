package nl.sundeep.vulcansmp.utils;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Utils {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component colorize(String message) {
        return miniMessage.deserialize(message);
    }

    public static Component colorize(String message, TagResolver... resolvers) {
        return miniMessage.deserialize(message, resolvers);
    }

    public static Component colorize(String message, Map<String, String> placeholders) {
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            builder.resolver(Placeholder.unparsed(entry.getKey(), entry.getValue()));
        }
        return miniMessage.deserialize(message, builder.build());
    }

    public static String serializeLocation(Location location) {
        if (location == null || location.getWorld() == null) return null;
        return String.format("%s;%.2f;%.2f;%.2f;%.2f;%.2f",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    public static Location deserializeLocation(String serialized) {
        if (serialized == null || serialized.isEmpty()) return null;
        String[] parts = serialized.split(";");
        if (parts.length < 6) return null;

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;

        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "u " + minutes + "m";
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + "d " + hours + "u";
        }
    }

    public static String formatPlaytime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" dag").append(days > 1 ? "en" : "").append(" ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append(" uur ");
        }
        sb.append(minutes).append(" minuten");
        return sb.toString().trim();
    }

    public static String formatMoney(double amount) {
        VulcanSMP plugin = VulcanSMP.getInstance();
        String symbol = plugin.getConfigManager().getCurrencySymbol();
        String format = plugin.getConfigManager().getCurrencyFormat();
        DecimalFormat df = new DecimalFormat(format);
        return symbol + df.format(amount);
    }

    public static String formatLocation(Location location) {
        if (location == null) return "Onbekend";
        return String.format("%s: %.1f, %.1f, %.1f",
                location.getWorld() != null ? location.getWorld().getName() : "Onbekend",
                location.getX(),
                location.getY(),
                location.getZ());
    }

    public static boolean isValidUsername(String name) {
        if (name == null || name.length() < 3 || name.length() > 16) return false;
        return name.matches("^[a-zA-Z0-9_]+$");
    }

    public static int getHomeLimit(Player player) {
        VulcanSMP plugin = VulcanSMP.getInstance();
        
        if (player.hasPermission("vulcan.home.unlimited")) {
            return Integer.MAX_VALUE;
        }

        Map<String, Integer> limits = plugin.getConfigManager().getHomeLimits();
        int maxLimit = plugin.getConfigManager().getDefaultHomeLimit();

        for (Map.Entry<String, Integer> entry : limits.entrySet()) {
            if (player.hasPermission("vulcan.home.limit." + entry.getKey())) {
                maxLimit = Math.max(maxLimit, entry.getValue());
            }
        }

        return maxLimit;
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(VulcanSMP.getInstance(), runnable);
    }

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(VulcanSMP.getInstance(), runnable);
    }

    public static void runLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(VulcanSMP.getInstance(), runnable, delay);
    }

    public static void runLaterAsync(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(VulcanSMP.getInstance(), runnable, delay);
    }
}
