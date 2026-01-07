package nl.sundeep.vulcansmp.config;

import nl.sundeep.vulcansmp.VulcanSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessagesConfig {

    private final VulcanSMP plugin;
    private final MiniMessage miniMessage;
    private FileConfiguration messages;
    private File messagesFile;
    private final Map<String, String> messageCache;

    public MessagesConfig(VulcanSMP plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.messageCache = new HashMap<>();
        loadMessages();
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        cacheMessages();
    }

    private void cacheMessages() {
        messageCache.clear();
        for (String key : messages.getKeys(true)) {
            if (!messages.isConfigurationSection(key)) {
                messageCache.put(key, messages.getString(key, ""));
            }
        }
    }

    public void reloadMessages() {
        loadMessages();
    }

    public String getRaw(String key) {
        return messageCache.getOrDefault(key, "<red>Bericht niet gevonden: " + key);
    }

    public Component get(String key) {
        return miniMessage.deserialize(getRaw(key));
    }

    public Component get(String key, TagResolver... resolvers) {
        return miniMessage.deserialize(getRaw(key), resolvers);
    }

    public Component get(String key, String placeholder, String value) {
        return miniMessage.deserialize(getRaw(key), Placeholder.unparsed(placeholder, value));
    }

    public Component get(String key, Map<String, String> placeholders) {
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            builder.resolver(Placeholder.unparsed(entry.getKey(), entry.getValue()));
        }
        return miniMessage.deserialize(getRaw(key), builder.build());
    }

    public Component parse(String message) {
        return miniMessage.deserialize(message);
    }

    public Component parse(String message, TagResolver... resolvers) {
        return miniMessage.deserialize(message, resolvers);
    }

    public Component parse(String message, Map<String, String> placeholders) {
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            builder.resolver(Placeholder.unparsed(entry.getKey(), entry.getValue()));
        }
        return miniMessage.deserialize(message, builder.build());
    }

    public String getPrefix() {
        return getRaw("prefix");
    }

    public Component getPrefixComponent() {
        return get("prefix");
    }

    public Component prefixed(String key) {
        return miniMessage.deserialize(getPrefix() + getRaw(key));
    }

    public Component prefixed(String key, TagResolver... resolvers) {
        return miniMessage.deserialize(getPrefix() + getRaw(key), resolvers);
    }

    public Component prefixed(String key, Map<String, String> placeholders) {
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            builder.resolver(Placeholder.unparsed(entry.getKey(), entry.getValue()));
        }
        return miniMessage.deserialize(getPrefix() + getRaw(key), builder.build());
    }
}
