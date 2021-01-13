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
        Class<?> executorClass = executor.getClass();
        List<Method> methods = Arrays.asList(executorClass.getDeclaredMethods());
        Collections.reverse(methods);
        for (Method method : methods) {
            BlockAPIPlugin.getLogUtils().debug(
                    "%s::%s(%s);",
                    executorClass,
                    method.getName(),
                    StringUtils.join(Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toArray(), ", ")
            );
            Command annotation = method.getAnnotation(Command.class);
            if (annotation == null) {
                continue;
            }
            try {
                CommandMethodInvoker invoker = new CommandMethodInvoker(
                        executor,
                        method,
                        annotation.subName(),
                        annotation.permission()
                );
                invokers.add(invoker);
            } catch (Exception e) {
                BlockAPIPlugin.getLogUtils().error(e, "在构建命令执行器 %s 的命令 %s 时出现了一个错误: ", executorClass, method.getName());
            }
        }

        if (invokers.isEmpty()) {
            BlockAPIPlugin.getLogUtils().warning("命令执行器 %s 中没有扫描到任何命令执行方法!", executorClass.getSimpleName());
        }

        for (int i = 0; i < this.permissions.length; i++) {
            this.permissions[i] = this.permissions[i].toLowerCase();
        }
    }

    public static CommandHandler generatorCommandHandler(Object commandExecutor) {
        CommandExecutor annotation = commandExecutor.getClass().getAnnotation(CommandExecutor.class);
        if (annotation == null) {
            throw new IllegalArgumentException("只有添加了 CommandExecutor 注解的类才能用于构建 CommandHandler 对象!");
        }
        String name = annotation.name();

        StringBuilder description = new StringBuilder();
        for (String s : annotation.description()) {
            description.append(s);
        }

        StringBuilder usage = new StringBuilder();
        for (String s : annotation.usage()) {
            usage.append(s);
        }

        return new CommandHandler(
                name,
                description.toString(),
                usage.toString(),
                Arrays.asList(annotation.aliases()),
                annotation.permission(),
                commandExecutor
        );
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
