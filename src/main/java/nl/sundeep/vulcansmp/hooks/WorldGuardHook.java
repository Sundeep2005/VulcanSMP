package nl.sundeep.vulcansmp.hooks;

import nl.sundeep.vulcansmp.VulcanSMP;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldGuardHook {
    private final VulcanSMP plugin;
    private boolean enabled;
    private WorldGuard worldGuard;

    public WorldGuardHook(VulcanSMP plugin) {
        this.plugin = plugin;
        try { this.worldGuard = WorldGuard.getInstance(); this.enabled = true; } catch (Exception e) { this.enabled = false; }
    }

    public boolean isEnabled() { return enabled && worldGuard != null; }

    public boolean canBuild(Player player, Location location) {
        if (!isEnabled()) return true;
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld())); if (regions == null) return true;
        BlockVector3 position = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        return regions.getApplicableRegions(position).testState(WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
    }

    public Set<String> getRegions(Location location) {
        if (!isEnabled()) return Set.of();
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld())); if (regions == null) return Set.of();
        BlockVector3 position = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        return regions.getApplicableRegions(position).getRegions().stream().map(r -> r.getId()).collect(Collectors.toSet());
    }
}
