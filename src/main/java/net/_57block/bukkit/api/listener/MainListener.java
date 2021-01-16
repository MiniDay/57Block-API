package net._57block.bukkit.api.listener;

import net._57block.bukkit.api.util.PointAPI;
import net._57block.bukkit.api.util.VaultAPI;
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
