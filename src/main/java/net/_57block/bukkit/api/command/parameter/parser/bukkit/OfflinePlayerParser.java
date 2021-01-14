package net._57block.bukkit.api.command.parameter.parser.bukkit;

import net._57block.bukkit.api.command.parameter.ParameterParser;
import net._57block.bukkit.api.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class OfflinePlayerParser extends ParameterParser {
    @Override
    @SuppressWarnings("deprecation")
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[index]);
        if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
            parameters.push(player);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        List<String> list = Arrays
                .stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .filter(s -> StringUtils.startsWithIgnoreCase(s, args[index]))
                .collect(Collectors.toList());
        if (list.size() > 10) {
            list = list.subList(0, 10);
        }
        return list;
    }
}
