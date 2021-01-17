package net.airgame.bukkit.api.listener;

import net.airgame.bukkit.api.util.api.PointAPI;
import net.airgame.bukkit.api.util.api.VaultAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class MainListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPluginEnable(PluginEnableEvent event) {
        VaultAPI.reloadVaultHook();
        PointAPI.reloadPlayerPointAPIHook();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        VaultAPI.reloadVaultHook();
        PointAPI.reloadPlayerPointAPIHook();
    }
}
