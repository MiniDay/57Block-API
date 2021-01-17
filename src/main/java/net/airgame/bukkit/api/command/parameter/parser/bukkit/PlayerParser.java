package net.airgame.bukkit.api.command.parameter.parser.bukkit;

import net.airgame.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class PlayerParser extends ParameterParser {
    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        Player player = Bukkit.getPlayer(args[index]);
        if (player == null) {
            return false;
        }
        if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
            parameters.push(player);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
