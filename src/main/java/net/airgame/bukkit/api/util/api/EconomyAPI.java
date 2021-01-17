package net.airgame.bukkit.api.util.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Vault Economy API
 */
@SuppressWarnings("unused")
public class EconomyAPI {
    private EconomyAPI() {
    }

    /**
     * 返回服务器是否安装了经济插件
     *
     * @return true代表安装了，false代表未安装
     */
    public static boolean isSetupEconomy() {
        return VaultAPI.isSetupVault() && VaultAPI.getEconomy() != null;
    }

    /**
     * 给玩家钱
     *
     * @param player 玩家
     * @param money  钱的数量
     * @return 成功则返回 true
     */
    public static boolean giveMoney(@NotNull final OfflinePlayer player, final double money) {
        if (isSetupEconomy()) {
            return VaultAPI.getEconomy().depositPlayer(player, money).transactionSuccess();
        }
        return false;
    }

    /**
     * 给玩家钱
     *
     * @param uuid  玩家的uuid
     * @param money 钱的数量
     * @return 成功则返回 true
     */
    public static boolean giveMoney(@NotNull final UUID uuid, final double money) {
        return giveMoney(Bukkit.getOfflinePlayer(uuid), money);
    }

    /**
     * 从玩家账户上扣钱
     *
     * @param player 玩家
     * @param money  钱的数量
     * @return 成功则返回 true
     */
    public static boolean takeMoney(@NotNull final OfflinePlayer player, final double money) {
        if (isSetupEconomy()) {
            return VaultAPI.getEconomy().withdrawPlayer(player, money).transactionSuccess();
        }
        return false;
    }

    /**
     * 从玩家账户上扣钱
     *
     * @param uuid  玩家的uuid
     * @param money 钱的数量
     * @return 成功则返回 true
     */
    public static boolean takeMoney(@NotNull final UUID uuid, final double money) {
        return takeMoney(Bukkit.getOfflinePlayer(uuid), money);
    }

    /**
     * 设置玩家的余额
     *
     * @param player 玩家
     * @param money  钱的数量
     * @return 成功则返回 true
     */
    public static boolean setMoney(@NotNull OfflinePlayer player, final double money) {
        if (!isSetupEconomy()) {
            return false;
        }
        double v = seeMoney(player);
        if (v > money) {
            return takeMoney(player, v - money);
        } else if (v < money) {
            return giveMoney(player, money - v);
        }
        return true;
    }

    /**
     * 设置玩家的余额
     *
     * @param uuid  玩家的uuid
     * @param money 钱的数量
     * @return 成功则返回 true
     */
    public static boolean setMoney(@NotNull final UUID uuid, final double money) {
        return setMoney(Bukkit.getOfflinePlayer(uuid), money);
    }

    /**
     * 检查玩家有多少钱
     * <p>
     * 若没有安装经济插件则返回NaN
     *
     * @param player 玩家
     * @return 玩家的余额
     */
    public static double seeMoney(@NotNull final OfflinePlayer player) {
        if (!isSetupEconomy()) {
            return Double.NaN;
        }
        return VaultAPI.getEconomy().getBalance(player);
    }

    /**
     * 检查玩家有多少钱
     * <p>
     * 若没有安装经济插件则返回NaN
     *
     * @param uuid 玩家的uuid
     * @return 玩家的余额
     */
    public static double seeMoney(@NotNull final UUID uuid) {
        return seeMoney(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * 检测玩家是否有足够的钱
     *
     * @param player 玩家
     * @param money  金钱的数量
     * @return 是否有足够的钱（若没有安装经济插件则直接返回 false
     */
    public static boolean hasMoney(@NotNull final OfflinePlayer player, final double money) {
        if (!isSetupEconomy()) {
            return false;
        }
        return VaultAPI.getEconomy().has(player, money);
    }

    /**
     * 检测玩家是否有足够的钱
     *
     * @param uuid  玩家的uuid
     * @param money 金钱的数量
     * @return 是否有足够的钱（若没有安装经济插件则直接返回 false
     */
    public static boolean hasMoney(@NotNull final UUID uuid, final double money) {
        return hasMoney(Bukkit.getOfflinePlayer(uuid), money);
    }
}
