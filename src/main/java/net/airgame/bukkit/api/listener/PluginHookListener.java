package net.airgame.bukkit.api.listener;

import net.airgame.bukkit.api.util.api.PointAPI;
import net.airgame.bukkit.api.util.api.VaultAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class PluginHookListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        if (isPluginDepend(plugin, "Vault")) {
            VaultAPI.reloadVaultHook();
        }
        if (isPluginDepend(plugin, "PlayerPointAPI")) {
            PointAPI.reloadPlayerPointAPIHook();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if (isPluginDepend(plugin, "Vault")) {
            VaultAPI.reloadVaultHook();
        }
        if (isPluginDepend(plugin, "PlayerPointAPI")) {
            PointAPI.reloadPlayerPointAPIHook();
        }
    }

    private boolean isPluginDepend(Plugin plugin, String pluginName) {
        if (plugin.getName().equals(pluginName)) {
            return true;
        }
        PluginDescriptionFile description = plugin.getDescription();
        if (description.getDepend().contains(pluginName)) {
            return true;
        }
        if (description.getSoftDepend().contains(pluginName)) {
            return true;
        }
        return description.getLoadBefore().contains(pluginName);
    }
}
