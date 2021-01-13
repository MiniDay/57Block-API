package net._57block.bukkit.api.command.parameter.parser.bukkit;

import net._57block.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class LocationParser extends ParameterParser {
    /**
     * 匹配数字的正则表达式
     */
    private static final String regex = "-?[0-9]+(\\.[0-9]+)?";

    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        // 至少需要 3 个参数
        if (index + 3 > args.length) {
            return false;
        }

        // 如果有 4 个或以上的参数剩余
        if (index + 4 <= args.length) {
            World world = Bukkit.getWorld(args[index]);
            if (world != null) {
                try {
                    double x = Double.parseDouble(args[index + 1]);
                    double y = Double.parseDouble(args[index + 2]);
                    double z = Double.parseDouble(args[index + 3]);

                    // 如果有 6 个或以上的参数则尝试解析 yaw 和 pitch
                    if (index + 6 <= args.length) {
                        try {
                            float yaw = Float.parseFloat(args[index + 4]);
                            float pitch = Float.parseFloat(args[index + 5]);

                            // 如果取了 6 个参数之后， 后续的 parser 也能返回 ture 则成功
                            if (getNext().parser(parameters, sender, command, label, args, index + 6)) {
                                parameters.push(new Location(world, x, y, z, yaw, pitch));
                                return true;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    // 如果取了 4 个参数之后， 后续的 parser 也能返回 ture 则成功
                    if (getNext().parser(parameters, sender, command, label, args, index + 4)) {
                        parameters.push(new Location(world, x, y, z));
                        return true;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // 四个 或 六个 参数解析 Location 失败
        // 接下来以 三个 或 五个 参数尝试解析 Location
        // 需要由 CommandSender 提供 World 对象

        World world;
        if (sender instanceof Entity) {
            world = ((Entity) sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            world = ((BlockCommandSender) sender).getBlock().getWorld();
        } else {
            // 如果 CommandSender 无法提供 World 则直接失败
            return false;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[index]);
            y = Double.parseDouble(args[index + 1]);
            z = Double.parseDouble(args[index + 2]);
        } catch (NumberFormatException ignored) {
            // 如果无法解析 x y z 则直接失败
            return false;
        }

        // 如果有 5 个或以上的参数则尝试解析 yaw 和 pitch
        if (index + 5 <= args.length) {
            try {
                float yaw = Float.parseFloat(args[index + 3]);
                float pitch = Float.parseFloat(args[index + 4]);

                // 如果取了 5 个参数之后， 后续的 parser 也能返回 ture 则成功
                if (getNext().parser(parameters, sender, command, label, args, index + 5)) {
                    parameters.push(new Location(world, x, y, z, yaw, pitch));
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }


        // 如果取了 3 个参数之后， 后续的 parser 也能返回 ture 则成功
        if (getNext().parser(parameters, sender, command, label, args, index + 3)) {
            parameters.push(new Location(world, x, y, z));
            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        if (args.length - index == 1) {
            // 如果没有输入参数则返回世界名
            List<String> list = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
            if (location != null) {
                // 追加 sender 自身的坐标
                list.add(String.valueOf(location.getBlockX()));
                list.add(String.format("%.2f", location.getX()));
            }
            return list;
        }

        // 为了让下一个 LocationParser 也能判断 location 是否为 null
        // 我们不能直接修改并传递 location 的值
        Location backupLocation = location;

        // 如果不能从sender处获取坐标，或第一个参数输入的不是数字
        // 那么输入的第一个参数就必须是 world 的名称
        if (location == null || !args[index].matches(regex)) {
            // 如果找不到世界则直接 return
            World world = Bukkit.getWorld(args[index]);
            if (world == null) {
                return null;
            }
            index = index + 1;
            location = world.getSpawnLocation();
        }

        switch (args.length - index) {
            case 1: {
                if (!args[index].isEmpty() && !args[index].matches(regex)) {
                    return null;
                }
                ArrayList<String> list = new ArrayList<>();
                list.add(String.valueOf(location.getBlockX()));
                list.add(String.format("%.2f", location.getX()));
                return list;
            }
            case 2: {
                if (!args[index].matches(regex)) {
                    return null;
                }
                if (!args[index + 1].isEmpty() && !args[index + 1].matches(regex)) {
                    return null;
                }
                ArrayList<String> list = new ArrayList<>();
                list.add(String.valueOf(location.getBlockY()));
                list.add(String.format("%.2f", location.getY()));
                return list;
            }
            case 3: {
                if (!args[index].matches(regex)) {
                    return null;
                }
                if (!args[index + 1].matches(regex)) {
                    return null;
                }
                if (!args[index + 2].isEmpty() && !args[index + 2].matches(regex)) {
                    return null;
                }
                ArrayList<String> list = new ArrayList<>();
                list.add(String.valueOf(location.getBlockZ()));
                list.add(String.format("%.2f", location.getZ()));
                return list;
            }
            case 4: {
                if (!args[index].matches(regex)) {
                    return null;
                }
                if (!args[index + 1].matches(regex)) {
                    return null;
                }
                if (!args[index + 2].matches(regex)) {
                    return null;
                }
                List<String> list = getNext().tabComplete(sender, command, label, args, backupLocation, index + 3);
                if (!args[index + 3].isEmpty() && !args[index + 3].matches(regex)) {
                    return list;
                }
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(String.format("%.2f", location.getYaw()));
                return list;
            }
            case 5: {
                if (!args[index].matches(regex)) {
                    return null;
                }
                if (!args[index + 1].matches(regex)) {
                    return null;
                }
                if (!args[index + 2].matches(regex)) {
                    return null;
                }
                if (!args[index + 3].matches(regex)) {
                    return getNext().tabComplete(sender, command, label, args, backupLocation, index + 3);
                }
                if (!args[index + 4].isEmpty() && !args[index + 4].matches(regex)) {
                    return null;
                }
                return Collections.singletonList(String.format("%.2f", location.getYaw()));
            }
            default:
                return getNext().tabComplete(sender, command, label, args, backupLocation, index + 4);
        }
    }
}
