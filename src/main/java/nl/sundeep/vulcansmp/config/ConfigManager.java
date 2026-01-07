package nl.sundeep.vulcansmp.config;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final VulcanSMP plugin;
    private FileConfiguration config;
    private File configFile;

    private String databaseType;
    private String databaseHost;
    private int databasePort;
    private String databaseName;
    private String databaseUsername;
    private String databasePassword;
    private int databasePoolSize;

    private int teleportDelay;
    private int teleportCooldown;
    private int tpaExpireTime;
    private boolean teleportCancelOnMove;

    private int defaultHomeLimit;
    private Map<String, Integer> homeLimits;

    private String currencySymbol;
    private String currencyFormat;
    private double startingBalance;

    private Map<String, Integer> kitCooldowns;

    private String discordLink;
    private String storeLink;
    private String voteLink;

    private String staffJoinMessage;
    private String staffLeaveMessage;
    private boolean staffNotifyJoin;
    private boolean staffNotifyLeave;

    private String vanishEnterMessage;
    private String vanishLeaveMessage;
    private boolean vanishNotifyPlayers;

    public ConfigManager(VulcanSMP plugin) {
        this.plugin = plugin;
        this.homeLimits = new HashMap<>();
        this.kitCooldowns = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        parseConfig();
    }

    private void parseConfig() {
        databaseType = config.getString("database.type", "sqlite");
        databaseHost = config.getString("database.host", "localhost");
        databasePort = config.getInt("database.port", 3306);
        databaseName = config.getString("database.name", "vulcansmp");
        databaseUsername = config.getString("database.username", "root");
        databasePassword = config.getString("database.password", "");
        databasePoolSize = config.getInt("database.pool-size", 10);

        teleportDelay = config.getInt("teleport.delay", 3);
        teleportCooldown = config.getInt("teleport.cooldown", 5);
        tpaExpireTime = config.getInt("teleport.tpa-expire-time", 60);
        teleportCancelOnMove = config.getBoolean("teleport.cancel-on-move", true);

        defaultHomeLimit = config.getInt("homes.default-limit", 3);
        homeLimits.clear();
        if (config.isConfigurationSection("homes.limits")) {
            for (String key : config.getConfigurationSection("homes.limits").getKeys(false)) {
                homeLimits.put(key, config.getInt("homes.limits." + key));
            }
        }

        currencySymbol = config.getString("economy.currency-symbol", "€");
        currencyFormat = config.getString("economy.format", "#.##0,00");
        startingBalance = config.getDouble("economy.starting-balance", 0.0);

        kitCooldowns.clear();
        if (config.isConfigurationSection("kits.cooldowns")) {
            for (String kit : config.getConfigurationSection("kits.cooldowns").getKeys(false)) {
                kitCooldowns.put(kit.toLowerCase(), config.getInt("kits.cooldowns." + kit));
            }
        }

        discordLink = config.getString("links.discord", "https://discord.gg/jouwserver");
        storeLink = config.getString("links.store", "https://store.jouwserver.nl");
        voteLink = config.getString("links.vote", "https://vote.jouwserver.nl");

        staffJoinMessage = config.getString("staff.join-message", "<yellow>[Staff] <white>{player} <gray>is online gekomen");
        staffLeaveMessage = config.getString("staff.leave-message", "<yellow>[Staff] <white>{player} <gray>is offline gegaan");
        staffNotifyJoin = config.getBoolean("staff.notify-join", true);
        staffNotifyLeave = config.getBoolean("staff.notify-leave", true);

        vanishEnterMessage = config.getString("vanish.enter-message", "<gray>{player} heeft de server verlaten");
        vanishLeaveMessage = config.getString("vanish.leave-message", "<gray>{player} is online gekomen");
        vanishNotifyPlayers = config.getBoolean("vanish.notify-players", true);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Kon config niet opslaan: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public FileConfiguration getConfig() { return config; }
    public String getDatabaseType() { return databaseType; }
    public String getDatabaseHost() { return databaseHost; }
    public int getDatabasePort() { return databasePort; }
    public String getDatabaseName() { return databaseName; }
    public String getDatabaseUsername() { return databaseUsername; }
    public String getDatabasePassword() { return databasePassword; }
    public int getDatabasePoolSize() { return databasePoolSize; }
    public int getTeleportDelay() { return teleportDelay; }
    public int getTeleportCooldown() { return teleportCooldown; }
    public int getTpaExpireTime() { return tpaExpireTime; }
    public boolean isTeleportCancelOnMove() { return teleportCancelOnMove; }
    public int getDefaultHomeLimit() { return defaultHomeLimit; }
    public Map<String, Integer> getHomeLimits() { return homeLimits; }
    public String getCurrencySymbol() { return currencySymbol; }
    public String getCurrencyFormat() { return currencyFormat; }
    public double getStartingBalance() { return startingBalance; }
    public Map<String, Integer> getKitCooldowns() { return kitCooldowns; }
    public int getKitCooldown(String kitName) { return kitCooldowns.getOrDefault(kitName.toLowerCase(), 0); }
    public String getDiscordLink() { return discordLink; }
    public String getStoreLink() { return storeLink; }
    public String getVoteLink() { return voteLink; }
    public String getStaffJoinMessage() { return staffJoinMessage; }
    public String getStaffLeaveMessage() { return staffLeaveMessage; }
    public boolean isStaffNotifyJoin() { return staffNotifyJoin; }
    public boolean isStaffNotifyLeave() { return staffNotifyLeave; }
    public String getVanishEnterMessage() { return vanishEnterMessage; }
    public String getVanishLeaveMessage() { return vanishLeaveMessage; }
    public boolean isVanishNotifyPlayers() { return vanishNotifyPlayers; }
}
