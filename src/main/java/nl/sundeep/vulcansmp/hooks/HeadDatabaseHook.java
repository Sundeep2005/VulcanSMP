package nl.sundeep.vulcansmp.hooks;

import nl.sundeep.vulcansmp.VulcanSMP;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class HeadDatabaseHook {
    private final VulcanSMP plugin;
    private Object api;
    private Method getItemHeadMethod;
    private boolean enabled;

    public HeadDatabaseHook(VulcanSMP plugin) {
        this.plugin = plugin;
        try {
            Class<?> apiClass = Class.forName("com.arcaniax.hdb.api.HeadDatabaseAPI");
            this.api = apiClass.getDeclaredConstructor().newInstance();
            this.getItemHeadMethod = apiClass.getMethod("getItemHead", String.class);
            this.enabled = true;
        } catch (Exception e) {
            this.enabled = false;
        }
    }

    public boolean isEnabled() { return enabled && api != null; }

    public ItemStack getHead(String id) {
        if (!isEnabled()) return null;
        try {
            return (ItemStack) getItemHeadMethod.invoke(api, id);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isHeadDatabaseId(String id) {
        return getHead(id) != null;
    }
}