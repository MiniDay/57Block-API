package net.airgame.bukkit.api.command.parameter.parser.bukkit;

import net.airgame.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class EntityTypeParser extends ParameterParser {
    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        try {
            EntityType type = EntityType.valueOf(args[index].toUpperCase());
            if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
                parameters.push(type);
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        ArrayList<String> list = new ArrayList<>();
        for (EntityType value : EntityType.values()) {
            list.add(value.name().toLowerCase());
        }
        return list;
    }
}
