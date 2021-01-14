package net._57block.bukkit.api.command.parameter.parser.bukkit;

import net._57block.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class WorldParser extends ParameterParser {

    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        World world = Bukkit.getWorld(args[index]);
        if (world == null) {
            return false;
        }
        if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
            parameters.push(world);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }
}
