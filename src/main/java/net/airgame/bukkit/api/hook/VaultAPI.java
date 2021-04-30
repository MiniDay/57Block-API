package net.airgame.bukkit.api.hook;

import net.airgame.bukkit.api.AirGamePlugin;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Vault API
 */
@SuppressWarnings("unused")
public class VaultAPI {
    private static boolean vaultEnabled;
    private static Chat chat;
    private static Economy economy;
    private static Permission permission;

    private VaultAPI() {
    }

    public static void reloadVaultHook() {
        chat = null;
        economy = null;
        permission = null;
        vaultEnabled = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if (!vaultEnabled) {
            AirGamePlugin.getLogUtils().warning("未检测到 Vault 插件!");
            return;
        }
        AirGamePlugin.getLogUtils().info("已连接 Vault!");

        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);

        if (chatProvider != null) {
            chat = chatProvider.getProvider();
            AirGamePlugin.getLogUtils().info("聊天系统挂接成功.");
        } else {
            AirGamePlugin.getLogUtils().warning("未检测到聊天系统!");
        }

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            AirGamePlugin.getLogUtils().info("经济系统挂接成功.");
        } else {
            AirGamePlugin.getLogUtils().warning("未检测到经济系统!");
        }

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            AirGamePlugin.getLogUtils().info("权限系统挂接成功.");
        } else {
            AirGamePlugin.getLogUtils().warning("未检测到权限插件!");
        }
    }

    /**
     * 返回服务器是否安装了 Vault 插件
     *
     * @return true 代表服务器已安装
     */
    public static boolean isSetupVault() {
        return vaultEnabled;
    }

    /**
     * 返回 Vault 的 Chat 前置系统
     *
     * @return Chat 系统
     */
    public static Chat getChat() {
        return chat;
    }

    /**
     * 返回 Vault 的 Economy 前置系统
     *
     * @return Economy 系统
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * 返回 Vault 的 Permission 前置系统
     *
     * @return Permission 系统
     */
    public static Permission getPermission() {
        return permission;
    }

}
