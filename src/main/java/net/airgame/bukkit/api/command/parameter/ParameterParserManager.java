package net.airgame.bukkit.api.command.parameter;

import net.airgame.bukkit.api.AirGamePlugin;
import net.airgame.bukkit.api.annotation.Sender;
import net.airgame.bukkit.api.command.parameter.parser.EndParser;
import net.airgame.bukkit.api.command.parameter.parser.bukkit.CommandSenderParser;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 *
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParameterParserManager {
    private static final HashMap<Class<?>, Class<? extends ParameterParser>> supportParameters;
    private static final HashMap<Class<?>, Class<? extends ParameterParser>> supportAnnotationParameters;

    static {
        supportParameters = new HashMap<>();
        supportAnnotationParameters = new HashMap<>();
    }

    /**
     * 这个类不允许被实例化
     */
    private ParameterParserManager() {
    }

    /**
     * 注册参数解析器
     *
     * @param parameterClass 参数类型
     * @param parserClass    解析器类型
     * @return 注册成功则返回 true
     */
    public static boolean registerParser(@NotNull Class<?> parameterClass, @NotNull Class<? extends ParameterParser> parserClass) {
        if (!checkParser(parserClass)) {
            throw new IllegalArgumentException("ParameterParser 类必须拥有一个公共无参的构造方法!");
        }
        if (supportParameters.containsKey(parameterClass)) {
            AirGamePlugin.getLogUtils().warning(
                    "无法注册 %s 为 %s 类型的解析器，因为已经存在另一个解析器 %s 用于解析该类型参数了.",
                    parserClass.getName(),
                    parameterClass.getName(),
                    supportParameters.get(parameterClass).getName()
            );
            return false;
        }
        supportParameters.put(parameterClass, parserClass);
        AirGamePlugin.getLogUtils().info(
                "已注册 %s 为 %s 类型参数的解析器",
                parserClass.getSimpleName(),
                parameterClass.getSimpleName()
        );
        return true;
    }

    /**
     * 注销参数解析器
     *
     * @param parameterClass 参数类型
     * @param parserClass    解析器类型
     * @return 注销成功则返回 true
     */
    public static boolean unregisterParser(@NotNull Class<?> parameterClass, @NotNull Class<? extends ParameterParser> parserClass) {
        if (!supportParameters.containsKey(parameterClass)) {
            return false;
        }
        AirGamePlugin.getLogUtils().warning(
                "已为参数类型 %s 注销参数解析器 %s",
                parameterClass.getSimpleName(),
                parserClass.getSimpleName()
        );
        return supportParameters.remove(parameterClass, parserClass);
    }

    /**
     * 注销参数解析器
     *
     * @param parameterClass 参数类型
     * @return 注销成功则返回 true
     */
    public static boolean unregisterParser(@NotNull Class<?> parameterClass) {
        if (supportParameters.containsKey(parameterClass)) {
            return false;
        }
        Class<? extends ParameterParser> parserClass = supportParameters.remove(parameterClass);
        if (parserClass == null) {
            return false;
        }
        AirGamePlugin.getLogUtils().warning(
                "已为参数类型 %s 注销参数解析器 %s",
                parameterClass.getSimpleName(),
                parserClass.getSimpleName()
        );
        return true;
    }

    /**
     * 根据参数类型返回参数解析器的类型
     *
     * @param parameter 参数
     * @return 参数解析器的类
     */
    @Nullable
    public static Class<? extends ParameterParser> getParserType(@NotNull Parameter parameter) {
        if (parameter.isAnnotationPresent(Sender.class)) {
            if (!CommandSender.class.isAssignableFrom(parameter.getType())) {
                throw new IllegalArgumentException("@Sender 标注的参数必须是 CommandSender 的子类!");
            }
            return CommandSenderParser.class;
        }
        return supportParameters.get(parameter.getType());
    }

    /**
     * 根据参数类型返回参数解析器实例
     * <p>
     * 若没有任何已注册的解析器可以解析该参数类型
     * <p>
     * 则返回 EndParser.INSTANCE
     *
     * @param parameter 参数
     * @return 解析器实例
     * @see EndParser#INSTANCE
     */
    @NotNull
    public static ParameterParser getParserInstance(@NotNull Parameter parameter) {
        Class<? extends ParameterParser> parserClass = getParserType(parameter);
        if (parserClass == null) {
            return EndParser.INSTANCE;
        }
        Class<?> parameterType = parameter.getType();
        try {
            ParameterParser parser = parserClass.newInstance();
            if (parser instanceof CommandSenderParser) {
                ((CommandSenderParser) parser).setSenderType(parameterType);
            }
            return parser;
        } catch (InstantiationException | IllegalAccessException e) {
            AirGamePlugin.getLogUtils().error(e, "构建 %s 的参数解析器时出现了一个错误: ", parameterType.getSimpleName());
        }

        return EndParser.INSTANCE;
    }

    /**
     * 检查解析器类是否合法
     * <p>
     * 解析器需要有一个公共且无参的构造方法才算合法
     *
     * @param parserClass 解析器的类
     * @return true 代表合法
     */
    private static boolean checkParser(Class<? extends ParameterParser> parserClass) {
        for (Constructor<?> constructor : parserClass.getConstructors()) {
            if (!Modifier.isPublic(constructor.getModifiers())) {
                continue;
            }
            if (constructor.getParameterCount() > 1) {
                continue;
            }
            return true;
        }
        return false;
    }

}
