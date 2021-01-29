package net.airgame.bukkit.api.util;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.math.Calculator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * 未分类的工具方法
 */
@SuppressWarnings("unused")
public class AirUtils {
    public static final String nmsVersion;
    private static final Calculator calculator;

    static {
        nmsVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
        calculator = new Calculator();
    }

    /**
     * 计算一条字符串数学公式
     *
     * @param s 要计算的数学运算
     * @return 计算结果
     */
    public static double calculate(String s) {
        return calculator.calculate(s);
    }

    /**
     * 让玩家以最高权限执行命令
     *
     * @param player  玩家
     * @param command 要执行的命令
     */
    public static void opCommand(Player player, String command) {
        boolean isOp = player.isOp();
        player.setOp(true);
        Bukkit.dispatchCommand(player, command);
        player.setOp(isOp);
    }

    public static String getMCVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public static String getNMSVersion() {
        return nmsVersion;
    }

    public static Package getNMSPackage() {
        String nmsVersion = getNMSVersion();
        return Package.getPackage("net.minecraft.server." + nmsVersion);
    }

    public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + nmsVersion + "." + className);
    }


    /**
     * 取消注册监听器中的所有事件处理器
     *
     * @param listener 监听器对象
     */
    @SuppressWarnings("unchecked")
    public static void unregisterEvents(@NotNull Listener listener) {
        try {
            for (Method method : listener.getClass().getMethods()) {
                if (method.getParameterCount() != 1) {
                    continue;
                }
                if (!method.isAnnotationPresent(EventHandler.class)) {
                    continue;
                }
                Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
                HandlerList handlerList = (HandlerList) eventClass.getMethod("getHandlerList").invoke(null);
                handlerList.unregister(listener);
            }
        } catch (Exception e) {
            AirGameAPI.getLogUtils().error(e, "取消注册监听器 %s 时出错!", listener);
        }
    }
}
