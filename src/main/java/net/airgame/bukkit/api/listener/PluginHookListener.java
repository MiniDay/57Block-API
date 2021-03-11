package net.airgame.bukkit.api.listener;

import net.airgame.bukkit.api.hook.PointAPI;
import net.airgame.bukkit.api.hook.VaultAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.Plugin;

public class PluginHookListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        if (plugin.getName().equals("Vault")) {
            VaultAPI.reloadVaultHook();
        }
        if (plugin.getName().equals("PlayerPoints")) {
            PointAPI.reloadPlayerPointAPIHook();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if (plugin.getName().equals("Vault")) {
            VaultAPI.reloadVaultHook();
        }
        if (plugin.getName().equals("PlayerPoints")) {
            PointAPI.reloadPlayerPointAPIHook();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onServiceRegister(ServiceRegisterEvent event) {
        VaultAPI.reloadVaultHook();
    }

    @EventHandler(ignoreCancelled = true)
    public void onServiceUnregister(ServiceUnregisterEvent event) {
        VaultAPI.reloadVaultHook();
    }
}
