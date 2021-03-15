package net.airgame.bukkit.api.command.executor;

import net.airgame.bukkit.api.AirGamePlugin;
import net.airgame.bukkit.api.manager.CommandManager;
import net.airgame.bukkit.api.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API 生成的命令执行器
 */
public class CommandHandler extends Command {
    private final ArrayList<CommandMethodInvoker> invokers;

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
    public CommandHandler(@NotNull String name, @NotNull String description, @NotNull String usage, @NotNull List<String> aliases, @NotNull String[] permissions, @NotNull Object executor) {
        super(
                name.toLowerCase(),
                description,
                usage,
                // 把 aliases 转换成小写
                aliases.stream().map(String::toLowerCase).collect(Collectors.toList())
        );

        invokers = CommandManager.generatorClassInvokers(executor, new String[0], permissions);

        if (invokers.isEmpty()) {
            AirGamePlugin.getLogUtils().warning("  命令执行器 %s 中没有扫描到任何命令执行方法!", executor.getClass().getSimpleName());
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        long startTime = System.currentTimeMillis();
        runCommand(sender, label, args);
        AirGamePlugin.getLogUtils().debug(
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
            }
        }

        String tabArg = args[args.length - 1];

        // 去除重复的补全参数
        list = list.stream()
                .filter(s -> StringUtils.startsWithIgnoreCase(s, tabArg)) // 去除不以输入参数开头的补全
                .distinct() // 去重
                .limit(10) // 补全最多显示 10 个
                .collect(Collectors.toList());

        AirGamePlugin.getLogUtils().debug(
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
        for (CommandMethodInvoker invoker : invokers) {
            if (invoker.execCommand(sender, this, label, args)) {
                return;
            }
        }
        sender.sendMessage(usageMessage);
    }

}
