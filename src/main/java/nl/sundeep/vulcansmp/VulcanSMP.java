package nl.sundeep.vulcansmp;

import nl.sundeep.vulcansmp.commands.*;
import nl.sundeep.vulcansmp.config.ConfigManager;
import nl.sundeep.vulcansmp.config.MessagesConfig;
import nl.sundeep.vulcansmp.database.DatabaseManager;
import nl.sundeep.vulcansmp.friends.FriendManager;
import nl.sundeep.vulcansmp.hooks.*;
import nl.sundeep.vulcansmp.homes.HomeManager;
import nl.sundeep.vulcansmp.kits.KitManager;
import nl.sundeep.vulcansmp.listeners.*;
import nl.sundeep.vulcansmp.managers.*;
import nl.sundeep.vulcansmp.placeholders.VulcanPlaceholders;
import nl.sundeep.vulcansmp.teleport.TeleportManager;
import nl.sundeep.vulcansmp.warps.WarpManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public class VulcanSMP extends JavaPlugin {

    private static VulcanSMP instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private ConfigManager configManager;
    private MessagesConfig messagesConfig;
    private DatabaseManager databaseManager;
    private TeleportManager teleportManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private KitManager kitManager;
    private FriendManager friendManager;
    private VanishManager vanishManager;
    private StaffManager staffManager;
    private PlaytimeManager playtimeManager;
    private MessageManager messageManager;
    private SpawnManager spawnManager;
    private VaultHook vaultHook;
    private PlaceholderAPIHook placeholderAPIHook;
    private LuckPermsHook luckPermsHook;
    private WorldGuardHook worldGuardHook;
    private HeadDatabaseHook headDatabaseHook;
    private BukkitCommandHandler commandHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.messagesConfig = new MessagesConfig(this);

        this.databaseManager = new DatabaseManager(this);
        if (!databaseManager.initialize()) {
            getLogger().severe("Database initialisatie mislukt! Plugin wordt uitgeschakeld...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize hooks
        initializeHooks();
        initializeManagers();

        registerCommands();
        registerListeners();

        if (placeholderAPIHook != null && placeholderAPIHook.isEnabled()) {
            new VulcanPlaceholders(this).register();
        }

        getLogger().info("VulcanSMP is succesvol ingeschakeld!");
    }

    @Override
    public void onDisable() {
        if (homeManager != null) homeManager.saveAll();
        if (warpManager != null) warpManager.saveAll();
        if (friendManager != null) friendManager.saveAll();
        if (playtimeManager != null) playtimeManager.saveAll();

        if (databaseManager != null) databaseManager.close();

        if (commandHandler != null) commandHandler.unregisterAllCommands();

        getLogger().info("VulcanSMP is uitgeschakeld!");
    }

    private void initializeHooks() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            this.vaultHook = new VaultHook(this);
            if (vaultHook.setupEconomy()) {
                getLogger().info("Vault economy succesvol gekoppeld!");
            }
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderAPIHook = new PlaceholderAPIHook(this);
            getLogger().info("PlaceholderAPI succesvol gekoppeld!");
        }

        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            this.luckPermsHook = new LuckPermsHook(this);
            getLogger().info("LuckPerms succesvol gekoppeld!");
        }

        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            this.worldGuardHook = new WorldGuardHook(this);
            getLogger().info("WorldGuard succesvol gekoppeld!");
        }

        if (getServer().getPluginManager().getPlugin("HeadDatabase") != null) {
            this.headDatabaseHook = new HeadDatabaseHook(this);
            getLogger().info("HeadDatabase succesvol gekoppeld!");
        }
    }

    private void initializeManagers() {
        this.teleportManager = new TeleportManager(this);
        this.homeManager = new HomeManager(this);
        this.warpManager = new WarpManager(this);
        this.kitManager = new KitManager(this);
        this.friendManager = new FriendManager(this);
        this.vanishManager = new VanishManager(this);
        this.staffManager = new StaffManager(this);
        this.playtimeManager = new PlaytimeManager(this);
        this.messageManager = new MessageManager(this);
        this.spawnManager = new SpawnManager(this);
    }

    private void registerCommands() {
        this.commandHandler = BukkitCommandHandler.create(this);

        CommandRegistrar.registerResolvers(commandHandler, this);

        commandHandler.register(
            new GamemodeCommand(this),
            new FlyCommand(this),
            new TeleportCommand(this),
            new TpaCommand(this),
            new BackCommand(this),
            new HomeCommand(this),
            new EconomyCommand(this),
            new WarpCommand(this),
            new HealFeedCommand(this),
            new WeatherCommand(this),
            new SpawnCommand(this),
            new BroadcastCommand(this),
            new EnderchestCommand(this),
            new InvseeCommand(this),
            new KitCommand(this),
            new MessageCommand(this),
            new PlaytimeCommand(this),
            new SkullCommand(this),
            new VanishCommand(this),
            new LinksCommand(this),
            new FriendCommand(this)
        );
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }

    public static VulcanSMP getInstance() {
        return instance;
    }

    public static MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public StaffManager getStaffManager() {
        return staffManager;
    }

    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public PlaceholderAPIHook getPlaceholderAPIHook() {
        return placeholderAPIHook;
    }

    public LuckPermsHook getLuckPermsHook() {
        return luckPermsHook;
    }

    public WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }

    public HeadDatabaseHook getHeadDatabaseHook() {
        return headDatabaseHook;
    }

    public BukkitCommandHandler getCommandHandler() {
        return commandHandler;
    }
}
