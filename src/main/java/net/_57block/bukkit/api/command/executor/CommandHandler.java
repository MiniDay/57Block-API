package net._57block.bukkit.api.command.executor;

import net._57block.bukkit.api.BlockAPIPlugin;
import net._57block.bukkit.api.command.annotation.Command;
import net._57block.bukkit.api.command.annotation.CommandExecutor;
import net._57block.bukkit.api.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API 生成的命令执行器
 */
public class CommandHandler extends org.bukkit.command.Command {
    private final ArrayList<CommandMethodInvoker> invokers;
    private final String[] permissions;

    /**
     * CommandHandler 的构造方法
     *
     * @param name        命令的名称
     * @param description 命令的描述
     * @param usage       命令的使用方法
     * @param aliases     命令的别名列表
     * @param permissions 命令的权限列表
     * @param executor    命令执行器实例
     */
    private CommandHandler(@NotNull String name, @NotNull String description, @NotNull String usage, @NotNull List<String> aliases, @NotNull String[] permissions, @NotNull Object executor) {
        super(
                name.toLowerCase(),
                description,
                usage,
                // 把 aliases 转换成小写
                aliases.stream().map(String::toLowerCase).collect(Collectors.toList())
        );
        this.permissions = permissions;
        invokers = new ArrayList<>();

        generatorClassInvokers(executor);

        if (invokers.isEmpty()) {
            BlockAPIPlugin.getLogUtils().warning("  命令执行器 %s 中没有扫描到任何命令执行方法! （忘记添加 @Command 了？）", executor.getClass().getSimpleName());
        }

        for (int i = 0; i < this.permissions.length; i++) {
            this.permissions[i] = this.permissions[i].toLowerCase();
        }
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
     * 传入一个 CommandExecutor 实例
     * <p>
     * 把它的命令方法的执行器添加到 invokers 中
     *
     * @param executor   CommandExecutor 实例
     * @param addSubName 子命令前缀，这些参数会添加在 Command 注解中 subName 的前面
     */
    private void generatorMethodInvokers(@NotNull Object executor, @NotNull String... addSubName) {
        Class<?> executorClass = executor.getClass();

        List<Method> methods = Arrays.asList(executorClass.getDeclaredMethods());
        Collections.reverse(methods);

        for (Method method : methods) {
            Command annotation = method.getAnnotation(Command.class);
            if (annotation == null) {
                continue;
            }
            BlockAPIPlugin.getLogUtils().debug(
                    "    %s::%s(%s);",
                    executorClass,
                    method.getName(),
                    StringUtils.join(Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toArray(), ", ")
            );

            ArrayList<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(addSubName));
            list.addAll(Arrays.asList(annotation.subName()));

            try {
                CommandMethodInvoker invoker = new CommandMethodInvoker(
                        executor,
                        method,
                        list.toArray(new String[0]),
                        annotation.permission()
                );
                invokers.add(invoker);
            } catch (Exception e) {
                BlockAPIPlugin.getLogUtils().error(e, "  在构建命令执行器 %s 的命令 %s 时出现了一个错误: ", executorClass, method.getName());
            }
        }
    }

    /**
     * 传入一个 CommandExecutor 的实例
     * <p>
     * 把这它的命令方法的执行器添加到 invokers 中
     * <p>
     * 并递归搜索它的内部类
     *
     * @param executor   CommandExecutor 实例
     * @param addSubName 子命令前缀，也许这个传入的类是另一个 CommandExecutor 的内部类？
     */
    private void generatorClassInvokers(@NotNull Object executor, @NotNull String... addSubName) {
        Class<?> executorClass = executor.getClass();
        CommandExecutor commandExecutor = executorClass.getAnnotation(CommandExecutor.class);
        if (commandExecutor == null) {
            return;
        }
        BlockAPIPlugin.getLogUtils().debug("  找到命令执行器类: %s", executorClass.getSimpleName());

        generatorMethodInvokers(executor, addSubName);

        String[] copyOf = Arrays.copyOf(addSubName, addSubName.length + 1);
        copyOf[addSubName.length] = commandExecutor.name();

        for (Class<?> innerClass : executorClass.getDeclaredClasses()) {
            if (!Modifier.isStatic(innerClass.getModifiers())) {
                BlockAPIPlugin.getLogUtils().info("  跳过非 static 修饰的内部类: %s", innerClass.getSimpleName());
            }
            if (!Modifier.isPublic(innerClass.getModifiers())) {
                BlockAPIPlugin.getLogUtils().info("  跳过非 public 修饰的内部类: %s", innerClass.getSimpleName());
            }
            try {
                generatorClassInvokers(innerClass.newInstance(), copyOf);
            } catch (InstantiationException | IllegalAccessException e) {
                BlockAPIPlugin.getLogUtils().error(e, "  构造内部类 %s 的实例时出现了一个错误: ");
            }
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        long startTime = System.currentTimeMillis();
        runCommand(sender, label, args);
        BlockAPIPlugin.getLogUtils().debug(
                "命令 [/%s %s] 执行完成，共计耗时: %d ms",
                getName(),
                StringUtils.join(args, " "),
                System.currentTimeMillis() - startTime
        );
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return tabComplete(sender, alias, args, null);
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) {
        long startTime = System.currentTimeMillis();

        // 把所有的 invoker 生成的补全列表保存起来
        List<String> list = new ArrayList<>();
        for (CommandMethodInvoker invoker : invokers) {
            List<String> tab = invoker.tabComplete(sender, this, alias, args, location);
            if (tab != null) {
                list.addAll(tab);
                BlockAPIPlugin.getLogUtils().debug("%s: %s", invoker, tab);
            }
        }

        // 去除不以输入参数开头的补全
        list = StringUtils.startsWith(list, args[args.length - 1]);
        // 去除重复的补全参数
        list = list.stream().distinct().collect(Collectors.toList());
        BlockAPIPlugin.getLogUtils().debug(
                "命令 [/%s %s] 的 tab 补全生成完成，共计耗时: %d ms",
                getName(),
                StringUtils.join(args, " "),
                System.currentTimeMillis() - startTime
        );
        return list;
    }

    /**
     * 执行命令
     *
     * @param sender 命令执行者
     * @param label  命令别名
     * @param args   命令附加参数
     */
    private void runCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        for (String permission : permissions) {
            if (!sender.hasPermission(permission)) {
                return;
            }
        }
        for (CommandMethodInvoker invoker : invokers) {
            if (invoker.execCommand(sender, this, label, args)) {
                return;
            }
        }
        sender.sendMessage(usageMessage);
    }

}
