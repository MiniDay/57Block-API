package net.airgame.bukkit.api.util;

import net.airgame.bukkit.api.AirGamePlugin;
import net.airgame.bukkit.api.listener.ConversationListener;
import net.airgame.bukkit.api.listener.SignEditListener;
import net.airgame.bukkit.api.object.Calculator;
import net.airgame.bukkit.api.object.SignEditFuture;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * 未分类的工具方法
 */
@SuppressWarnings("unused")
public class AirGameUtils {
    public static final String NMS_VERSION;
    private static final Calculator CALCULATOR;

    static {
        NMS_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
        CALCULATOR = new Calculator();
    }

    public static String getMCVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public static String getNMSVersion() {
        return NMS_VERSION;
    }

    public static Package getNMSPackage() {
        return Package.getPackage("net.minecraft.server." + getNMSVersion());
    }

    public static Class<?> getNMSClass(@NotNull String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + NMS_VERSION + "." + className);
    }

    /**
     * 计算一条字符串数学公式
     *
     * @param s 要计算的数学运算
     * @return 计算结果
     */
    public static double calculate(@NotNull String s) {
        return CALCULATOR.calculate(s.replace(" ", ""));
    }

    /**
     * 让玩家以最高权限执行命令
     *
     * @param player  玩家
     * @param command 要执行的命令
     */
    public static void opCommand(@NotNull Player player, @NotNull String command) {
        boolean isOp = player.isOp();
        player.setOp(true);
        Bukkit.dispatchCommand(player, command);
        player.setOp(isOp);
    }

    /**
     * 取消注册监听器中的所有事件处理器
     *
     * @param listener 监听器对象
     */
    public static void unregisterEvents(@NotNull Listener listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (method.getParameterCount() != 1) {
                continue;
            }
            if (!method.isAnnotationPresent(EventHandler.class)) {
                continue;
            }
            Class<?> eventClass = method.getParameterTypes()[0];
            try {
                HandlerList handlerList = (HandlerList) eventClass.getMethod("getHandlerList").invoke(null);
                handlerList.unregister(listener);
            } catch (Exception e) {
                AirGamePlugin.getLogUtils().error(e, "为监听器 %s 取消注册事件 %s 时出错!", listener, eventClass.getName());
            }
        }
    }

    /**
     * 给玩家打开一个牌子编辑器
     *
     * @param player 玩家
     * @param lines  牌子内容
     * @return 玩家编辑内容的 Future 对象
     */
    @NotNull
    public static SignEditFuture getPlayerInputBySignEdit(@NotNull Player player, @NotNull String[] lines) {
        return SignEditListener.getPlayerInput(player, lines);
    }

    public static void internalPlayerInputBySignEdit(@NotNull Player player) {
        SignEditListener.internalPlayerInput(player);
    }

    /**
     * 获取玩家的输入
     *
     * @param player 玩家
     * @return 玩家输入内容的 Future 对象
     */
    @NotNull
    public static CompletableFuture<String> getPlayerInput(@NotNull HumanEntity player) {
        return ConversationListener.getPlayerInput(player);
    }

    /**
     * 中断玩家的输入
     *
     * @param player 玩家
     */
    public static void internalPlayerInput(@NotNull HumanEntity player) {
        ConversationListener.internalPlayerInput(player);
    }

    /**
     * 比较两个坐标所指的方块是否相同
     *
     * @param location1 坐标1
     * @param location2 坐标2
     * @return true代表相同
     */
    public static boolean equalBlockLocation(@NotNull Location location1, @NotNull Location location2) {
        if (location1.getWorld() != location2.getWorld()) {
            return false;
        }
        if (location1.getBlockX() != location2.getBlockX()) {
            return false;
        }
        if (location1.getBlockY() != location2.getBlockY()) {
            return false;
        }
        return location1.getBlockZ() == location2.getBlockZ();
    }

    /**
     * 向玩家发送一条 actionBar 消息
     *
     * @param player  玩家
     * @param message 要发送的消息
     */
    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
