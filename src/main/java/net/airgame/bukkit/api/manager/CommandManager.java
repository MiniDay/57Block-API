package net.airgame.bukkit.api.manager;

import net.airgame.bukkit.api.AirGamePlugin;
import net.airgame.bukkit.api.annotation.Command;
import net.airgame.bukkit.api.annotation.CommandExecutor;
import net.airgame.bukkit.api.command.executor.CommandHandler;
import net.airgame.bukkit.api.command.executor.CommandMethodInvoker;
import net.airgame.bukkit.api.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CommandManager {
    private static Method getFileMethod;
    private static Method getClassLoaderMethod;
    private static Method findClassMethod;
    private static SimpleCommandMap commandMap;

    private CommandManager() {
    }

    /**
     * 初始化这个命令管理器
     *
     * @param classLoader JavaPluginLoader 对象
     */
    public static void init(ClassLoader classLoader) {
        AirGamePlugin.getLogUtils().info("开始初始化命令管理器.");
        try {
            getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            AirGamePlugin.getLogUtils().info("已获取 getFile 方法: %s", getFileMethod);

            getClassLoaderMethod = JavaPlugin.class.getDeclaredMethod("getClassLoader");
            getClassLoaderMethod.setAccessible(true);
            AirGamePlugin.getLogUtils().info("已获取 getClassLoader 方法: %s", getClassLoaderMethod);

            findClassMethod = classLoader.getClass().getDeclaredMethod("findClass", String.class);
            findClassMethod.setAccessible(true);
            AirGamePlugin.getLogUtils().info("已获取 findClass 方法: %s", findClassMethod);

            SimplePluginManager manager = (SimplePluginManager) Bukkit.getPluginManager();
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(manager);
            AirGamePlugin.getLogUtils().info("已获取命令管理器: %s", commandMap);
        } catch (Exception e) {
            AirGamePlugin.getLogUtils().error(e, "初始化命令管理器时遇到了一个错误: ");
        }
        AirGamePlugin.getLogUtils().info("命令管理器初始化完成.");
    }

    public static void registerPluginCommand(JavaPlugin plugin, String packageName) throws IOException, InvocationTargetException, IllegalAccessException {
        AirGamePlugin.getLogUtils().info("扫描插件 %s 中的命令类.", plugin.getName());

        Enumeration<JarEntry> entries = new JarFile((File) getFileMethod.invoke(plugin)).entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();

            if (!entryName.endsWith(".class")) {
                continue;
            }

            // 类所在的路径
            String classPath = entryName.replace("/", ".");
            // 去掉 .class 后缀变成全限定类名
            String className = classPath.substring(0, classPath.length() - 6);

            // 舍弃不是以 packageName 开头的类
            if (!className.startsWith(packageName)) {
                continue;
            }
            // 跳过匿名内部类
            if (className.contains("$")) {
                continue;
            }

            try {
                CommandManager.registerCommand(plugin, className);
            } catch (IllegalAccessException e) {
                AirGamePlugin.getLogUtils().debug("扫描到类 %s 没有添加 CommandExecutor 注解, 取消注册该类命令!", className);
            } catch (Exception | Error e) {
                AirGamePlugin.getLogUtils().error(e, "在为插件 %s 注册命令 %s 时遇到了一个错误: ", plugin.getName(), className);
            }
        }
    }

    /**
     * @param plugin    插件对象
     * @param className 命令类的全限定名称
     * @throws InvocationTargetException 一般不会报这个错
     * @throws IllegalAccessException    如果传入的类的没有公共权限修饰符的构造方法则会抛出该错误
     * @throws InstantiationException    如果传入的类的没有无参的构造方法则会抛出该错误
     */
    public static void registerCommand(JavaPlugin plugin, String className) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> clazz = (Class<?>) findClassMethod.invoke(getClassLoaderMethod.invoke(plugin), className);
        registerCommand(plugin, clazz);
    }

    /**
     * @param plugin 插件对象
     * @param clazz  命令类
     * @throws IllegalAccessException 如果传入的类的没有公共权限修饰符的构造方法则会抛出该错误
     * @throws InstantiationException 如果传入的类的没有无参的构造方法则会抛出该错误
     */
    public static void registerCommand(JavaPlugin plugin, Class<?> clazz) throws InstantiationException, IllegalAccessException {
        CommandHandler handler = generatorCommandHandler(clazz);
        commandMap.register(plugin.getName(), handler);
        AirGamePlugin.getLogUtils().info("  已成功注册命令类: %s", clazz.getSimpleName());
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
     * 构建它的全部 CommandMethodInvoker 对象
     * <p>
     * 并递归搜索它的内部类
     *
     * @param executor      CommandExecutor 实例
     * @param addSubName    子命令前缀，也许这个传入的类是另一个 CommandExecutor 的内部类？
     * @param addPermission 命令权限追加，也许这个传入的类是另一个 CommandExecutor 的内部类？
     * @return 构建的 CommandMethodInvoker 对象集合
     */
    public static ArrayList<CommandMethodInvoker> generatorClassInvokers(@NotNull Object executor, @NotNull String[] addSubName, @NotNull String[] addPermission) {
        Class<?> executorClass = executor.getClass();
        AirGamePlugin.getLogUtils().debug("  开始扫描命令类 %s", executorClass.getName());

        ArrayList<CommandMethodInvoker> invokers = generatorMethodInvokers(executor, addSubName, addPermission);

        for (Class<?> innerClass : executorClass.getDeclaredClasses()) {
            CommandExecutor annotation = innerClass.getAnnotation(CommandExecutor.class);
            if (annotation == null) {
                continue;
            }
            if (!Modifier.isStatic(innerClass.getModifiers())) {
                AirGamePlugin.getLogUtils().info("  跳过非 static 修饰的内部类: %s", innerClass.getSimpleName());
            }
            if (!Modifier.isPublic(innerClass.getModifiers())) {
                AirGamePlugin.getLogUtils().info("  跳过非 public 修饰的内部类: %s", innerClass.getSimpleName());
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
                    AirGamePlugin.getLogUtils().error(e, "构造内部类 %s 的实例时出现了一个错误: ");
                }
            }
        }
        return invokers;
    }

    /**
     * 传入一个 CommandExecutor 实例
     * <p>
     * 构建出这个类下面全部的 CommandMethodInvoker 对象
     *
     * @param executor      CommandExecutor 实例
     * @param addSubName    子命令前缀，这些参数会添加在 Command 注解中 subName 的前面
     * @param addPermission 命令权限追加，这些参数会添加在 Command 注解中 permission 的前面
     * @return 构建的 CommandMethodInvoker 对象集合
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
            AirGamePlugin.getLogUtils().debug(
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
                AirGamePlugin.getLogUtils().error(e, "  在构建命令执行器 %s 的命令 %s 时出现了一个错误: ", executorClass, method.getName());
            }
        }
        return invokers;
    }

}
