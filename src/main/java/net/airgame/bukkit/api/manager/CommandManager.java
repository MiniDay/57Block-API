package net.airgame.bukkit.api.manager;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.command.annotation.Command;
import net.airgame.bukkit.api.command.annotation.CommandExecutor;
import net.airgame.bukkit.api.command.executor.CommandHandler;
import net.airgame.bukkit.api.command.executor.CommandMethodInvoker;
import net.airgame.bukkit.api.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandManager {
    private static ClassLoader classLoader;
    private static SimpleCommandMap commandMap;
    private static Method findClassMethod;


    private CommandManager() {
    }

    /**
     * 初始化这个命令管理器
     *
     * @param classLoader JavaPluginLoader 对象
     */
    public static void init(ClassLoader classLoader) {
        AirGameAPI.getLogUtils().info("开始初始化命令管理器.");
        CommandManager.classLoader = classLoader;
        AirGameAPI.getLogUtils().info("已设定类加载器: %s", classLoader.toString());
        try {
            SimplePluginManager manager = (SimplePluginManager) Bukkit.getPluginManager();
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(manager);
            AirGameAPI.getLogUtils().info("命令管理器已挟持: %s", commandMap.toString());
            findClassMethod = classLoader.getClass().getDeclaredMethod("findClass", String.class);
            findClassMethod.setAccessible(true);
            AirGameAPI.getLogUtils().info("类加载方法已初始化: %s", findClassMethod);
        } catch (Exception e) {
            AirGameAPI.getLogUtils().error(e, "初始化命令管理器时遇到了一个错误: ");
        }
        AirGameAPI.getLogUtils().info("命令管理器初始化完成.");
    }

    /**
     * @param plugin    插件对象
     * @param className 命令类的全限定名称
     * @throws InvocationTargetException 一般不会报这个错
     * @throws IllegalAccessException    如果传入的类的没有公共权限修饰符的构造方法则会抛出该错误
     * @throws InstantiationException    如果传入的类的没有无参的构造方法则会抛出该错误
     */
    public static void registerCommand(Plugin plugin, String className) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> clazz = (Class<?>) findClassMethod.invoke(classLoader, className);
        registerCommand(plugin, clazz);
    }

    /**
     * @param plugin 插件对象
     * @param clazz  命令类
     * @throws IllegalAccessException 如果传入的类的没有公共权限修饰符的构造方法则会抛出该错误
     * @throws InstantiationException 如果传入的类的没有无参的构造方法则会抛出该错误
     */
    public static void registerCommand(Plugin plugin, Class<?> clazz) throws InstantiationException, IllegalAccessException {
        CommandHandler handler = generatorCommandHandler(clazz);
        commandMap.register(plugin.getName(), handler);
        AirGameAPI.getLogUtils().info("  已成功注册命令: %s", clazz.getSimpleName());
    }

    /**
     * 传入一个添加了 CommandExecutor 注解的类
     * <p>
     * 返回一个 CommandHandler 对象
     *
     * @param executorClass 添加了 CommandExecutor 注解的类
     * @return CommandHandler 对象
     * @throws IllegalAccessException 如果传入的类的没有公共权限修饰符的构造方法则会抛出该错误
     * @throws InstantiationException 如果传入的类的没有无参的构造方法则会抛出该错误
     */
    @NotNull
    public static CommandHandler generatorCommandHandler(@NotNull Class<?> executorClass) throws IllegalAccessException, InstantiationException {
        CommandExecutor annotation = executorClass.getAnnotation(CommandExecutor.class);
        if (annotation == null) {
            throw new IllegalArgumentException("只有添加了 CommandExecutor 注解的类才能用于构建 CommandHandler 对象!");
        }

        return new CommandHandler(
                annotation.name(),
                StringUtils.join(annotation.description(), " "),
                StringUtils.join(annotation.usage(), " "),
                Arrays.asList(annotation.aliases()),
                annotation.permission(),
                executorClass.newInstance()
        );
    }

    /**
     * 传入一个 CommandExecutor 的实例
     * <p>
     * 把这它的命令方法的执行器添加到 invokers 中
     * <p>
     * 并递归搜索它的内部类
     *
     * @param executor      CommandExecutor 实例
     * @param addSubName    子命令前缀，也许这个传入的类是另一个 CommandExecutor 的内部类？
     * @param addPermission 命令权限追加，也许这个传入的类是另一个 CommandExecutor 的内部类？
     */
    public static ArrayList<CommandMethodInvoker> generatorClassInvokers(@NotNull Object executor, @NotNull String[] addSubName, @NotNull String[] addPermission) {
        Class<?> executorClass = executor.getClass();
        AirGameAPI.getLogUtils().info("  开始扫描命令类 %s", executorClass.getName());

        ArrayList<CommandMethodInvoker> invokers = generatorMethodInvokers(executor, addSubName, addPermission);

        for (Class<?> innerClass : executorClass.getDeclaredClasses()) {
            CommandExecutor annotation = innerClass.getAnnotation(CommandExecutor.class);
            if (annotation == null) {
                continue;
            }
            if (!Modifier.isStatic(innerClass.getModifiers())) {
                AirGameAPI.getLogUtils().info("  跳过非 static 修饰的内部类: %s", innerClass.getSimpleName());
            }
            if (!Modifier.isPublic(innerClass.getModifiers())) {
                AirGameAPI.getLogUtils().info("  跳过非 public 修饰的内部类: %s", innerClass.getSimpleName());
            }

            // 把 command 的 name 和 aliases 存入 commandNames 中
            ArrayList<String> commandNames = new ArrayList<>();
            commandNames.add(annotation.name());
            Collections.addAll(commandNames, annotation.aliases());

            for (String s : commandNames) {
                String[] subName = Arrays.copyOf(addSubName, addSubName.length + 1);
                subName[addSubName.length] = s;

                ArrayList<String> permission = new ArrayList<>();
                permission.addAll(Arrays.asList(addPermission));
                permission.addAll(Arrays.asList(annotation.permission()));

                try {
                    invokers.addAll(
                            generatorClassInvokers(
                                    innerClass.newInstance(),
                                    subName,
                                    permission.toArray(new String[0])
                            )
                    );
                } catch (InstantiationException | IllegalAccessException e) {
                    AirGameAPI.getLogUtils().error(e, "构造内部类 %s 的实例时出现了一个错误: ");
                }
            }
        }
        return invokers;
    }

    /**
     * 传入一个 CommandExecutor 实例
     * <p>
     * 把它的命令方法的执行器添加到 invokers 中
     *
     * @param executor      CommandExecutor 实例
     * @param addSubName    子命令前缀，这些参数会添加在 Command 注解中 subName 的前面
     * @param addPermission 命令权限追加，这些参数会添加在 Command 注解中 permission 的前面
     */
    public static ArrayList<CommandMethodInvoker> generatorMethodInvokers(@NotNull Object executor, @NotNull String[] addSubName, @NotNull String[] addPermission) {
        Class<?> executorClass = executor.getClass();

        List<Method> methods = Arrays.asList(executorClass.getDeclaredMethods());
        Collections.reverse(methods);

        ArrayList<CommandMethodInvoker> invokers = new ArrayList<>();
        for (Method method : methods) {
            Command annotation = method.getAnnotation(Command.class);
            if (annotation == null) {
                continue;
            }
            AirGameAPI.getLogUtils().info(
                    "    已读取方法 %s::%s(%s);",
                    executorClass.getSimpleName(),
                    method.getName(),
                    StringUtils.join(Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toArray(), ", ")
            );

            ArrayList<String> subName = new ArrayList<>();
            subName.addAll(Arrays.asList(addSubName));
            subName.addAll(Arrays.asList(annotation.subName()));

            ArrayList<String> permission = new ArrayList<>();
            permission.addAll(Arrays.asList(addPermission));
            permission.addAll(Arrays.asList(annotation.permission()));


            try {
                CommandMethodInvoker invoker = new CommandMethodInvoker(
                        executor,
                        method,
                        subName.toArray(new String[0]),
                        permission.toArray(new String[0])
                );
                invokers.add(invoker);
            } catch (Exception e) {
                AirGameAPI.getLogUtils().error(e, "  在构建命令执行器 %s 的命令 %s 时出现了一个错误: ", executorClass, method.getName());
            }
        }
        return invokers;
    }

}
