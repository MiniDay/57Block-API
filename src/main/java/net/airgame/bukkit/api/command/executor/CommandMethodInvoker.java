package net.airgame.bukkit.api.command.executor;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.command.parameter.ParameterParser;
import net.airgame.bukkit.api.command.parameter.ParameterParserManager;
import net.airgame.bukkit.api.command.parameter.parser.EndParser;
import net.airgame.bukkit.api.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class CommandMethodInvoker {
    private final Object executor;
    private final Method method;

    private final String[] subName;
    private final String[] permissions;

    private ParameterParser parameterParser;

    public CommandMethodInvoker(Object executor, Method method, String[] subName, String[] permissions) {
        this.executor = executor;
        this.method = method;

        this.subName = subName;
        this.permissions = permissions;

        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalArgumentException("Command 方法必须由 public 权限修饰!");
        }

        Parameter[] parameters = method.getParameters();

        // 检查非法参数类型
        for (Parameter parameter : parameters) {
            Class<?> parameterType = parameter.getType();
            Class<? extends ParameterParser> parserType = ParameterParserManager.getParserType(parameter);
            if (parserType == null) {
                throw new IllegalArgumentException("不受支持的参数类型: " + parameterType.getName());
            }
        }

        // 按照方法的参数类型初始化解析器
        // 并链式存储
        ParameterParser lastParser = null;
        for (Parameter parameter : parameters) {
            ParameterParser parser = ParameterParserManager.getParserInstance(parameter);
            if (lastParser == null) {
                this.parameterParser = lastParser = parser;
            } else {
                lastParser.setNext(parser);
                lastParser = parser;
            }
        }

        // 如果没有找到任何解析器，则直接使用 EndParser.INSTANCE
        // 如果有解析器，则将最后一个解析器的 next 设为 EndParser.INSTANCE
        if (lastParser != null) {
            lastParser.setNext(EndParser.INSTANCE);
        } else {
            parameterParser = EndParser.INSTANCE;
        }

//         isAccessible()值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false 则指示反射的对象应该实施 Java 语言访问检查。
//
//         跳过方法权限检测可以提升反射代码运行效率
//
//         由于JDK的安全检查耗时较多.所以通过setAccessible(true)的方式关闭安全检查就可以达到提升反射速度的目的
//
//         相关链接 https://my.oschina.net/nixi0608/blog/724343
//
//         但我并未对这些信息进行过测试，具体能提升多少还不得而知
        method.setAccessible(true);

        for (int i = 0; i < this.subName.length; i++) {
            this.subName[i] = this.subName[i].toLowerCase();
        }
        for (int i = 0; i < permissions.length; i++) {
            this.permissions[i] = this.permissions[i].toLowerCase();
        }
    }

    public boolean execCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 判断 subName 长度是否匹配
        if (subName.length > args.length) {
            return false;
        }

        // 判断 subName 是否匹配
        for (int i = 0; i < subName.length; i++) {
            if (!subName[i].equalsIgnoreCase(args[i])) {
                return false;
            }
        }

        // 判断用户是否拥有全部的权限
        for (String permission : permissions) {
            if (!sender.hasPermission(permission)) {
                return false;
            }
        }

        Object[] objects = new Object[method.getParameterCount()];
        Stack<Object> stack = new Stack<>();

        if (!parameterParser.parser(stack, sender, command, label, args, subName.length)) {
            return false;
        }

        int i = 0;

        while (!stack.empty()) {
            Object o = stack.pop();
            objects[i] = o;
            i++;
        }

        try {
            method.invoke(executor, objects);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            AirGameAPI.getLogUtils().error(e, "在调用 %s 的命令方法 %s 时遇到了一个错误: ", executor.getClass(), method.getName());
        }
        return false;
    }

    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args, @Nullable Location location) {
        for (String s : permissions) {
            if (!sender.hasPermission(s)) {
                return null;
            }
        }
        // 如果 已输入的参数不能完全匹配 subName 则无法匹配该命令
        for (int i = 0; i < args.length - 1 && i < subName.length; i++) {
            if (!subName[i].equalsIgnoreCase(args[i])) {
                return null;
            }
        }

        // 如果已输入的参数的长度小于或等于 subName
        // 则先返回 subName 的补全
        if (args.length <= subName.length) {
            return Collections.singletonList(subName[args.length - 1]);
        }

        return parameterParser.tabComplete(sender, command, alias, args, location, subName.length);
    }

    @Override
    public String toString() {
        return String.format(
                "%s::%s(%s);",
                executor.getClass().getName(),
                method.getName(),
                StringUtils.join(Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toArray(), ", ")
        );
    }
}
