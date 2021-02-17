package net.airgame.bukkit.api.util.api;

import net.airgame.bukkit.api.AirGameAPI;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * PlayerPointsAPI
 */
@SuppressWarnings("unused")
public class PointAPI {
    private static PlayerPointsAPI playerPointsAPI;

    private PointAPI() {
    }

    /**
     * 重载 PlayerPointAPI 点券系统挂接
     */
    public static void reloadPlayerPointAPIHook() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerPoints");
        if (plugin == null) {
            AirGameAPI.getLogUtils().warning("未检测到 PlayerPointAPI 插件...");
            return;
        }
        playerPointsAPI = ((PlayerPoints) plugin).getAPI();
        AirGameAPI.getLogUtils().info("PlayerPointAPI 挂接成功!");
    }

    /**
     * 获取 PlayerPointsAPI 实例
     *
     * @return PlayerPointsAPI 实例
     */
    public static PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }

    /**
     * 返回服务器是否安装了 PlayerPointAPI 插件
     *
     * @return true代表安装了，false代表未安装
     */
    public static boolean isSetupPlayerPointAPI() {
        return playerPointsAPI != null;
    }

    /**
     * 给予玩家点券
     *
     * @param player 玩家
     * @param point  点券数量
     */
    public static void givePoint(final OfflinePlayer player, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.give(player.getUniqueId(), point);
        }
    }

    /**
     * 给予玩家点券
     *
     * @param uuid  玩家的uuid
     * @param point 点券数量
     */
    public static void givePoint(final UUID uuid, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.give(uuid, point);
        }
    }

    /**
     * 扣除玩家点券
     *
     * @param player 玩家
     * @param point  点券数量
     */
    public static void takePoint(final OfflinePlayer player, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.take(player.getUniqueId(), point);
        }
    }

    /**
     * 扣除玩家点券
     *
     * @param uuid  玩家的uuid
     * @param point 点券数量
     */
    public static void takePoint(final UUID uuid, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.take(uuid, point);
        }
    }

    /**
     * 设置玩家的点券
     *
     * @param player 玩家
     * @param point  点券数量
     */
    public static void setPoint(final OfflinePlayer player, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.set(player.getUniqueId(), point);
        }
    }

    /**
     * 设置玩家的点券
     *
     * @param uuid  玩家的uuid
     * @param point 点券数量
     */
    public static void setPoint(final UUID uuid, final int point) {
        if (playerPointsAPI != null) {
            playerPointsAPI.set(uuid, point);
        }
    }

    /**
     * 查看玩家的点券数量
     *
     * @param player 玩家
     * @return 玩家的点券数量
     */
    public static int seePoint(final OfflinePlayer player) {
        if (playerPointsAPI != null) {
            return playerPointsAPI.look(player.getUniqueId());
        }
        return 0;
    }

    /**
     * 查看玩家的点券数量
     *
     * @param uuid 玩家的uuid
     * @return 玩家的点券数量
     */
    public static int seePoint(final UUID uuid) {
        if (playerPointsAPI != null) {
            return playerPointsAPI.look(uuid);
        }
        return 0;
    }

    /**
     * 检查玩家是否有足够的点券
     *
     * @param player 玩家
     * @param point  点券数量
     * @return 若未安装 PlayerPointAPI 则直接返回 false
     */
    public static boolean hasPoint(final OfflinePlayer player, final int point) {
        if (playerPointsAPI != null) {
            return playerPointsAPI.look(player.getUniqueId()) >= point;
        }
        return false;
    }

    /**
     * 检查玩家是否有足够的点券
     *
     * @param uuid  玩家的uuid
     * @param point 点券数量
     * @return 若未安装 PlayerPointAPI 则直接返回 false
     */
    public static boolean hasPoint(final UUID uuid, final int point) {
        if (playerPointsAPI != null) {
            return playerPointsAPI.look(uuid) >= point;
        }
        return false;
    }
}
