package net._57block.bukkit.api.command.parameter.parser;

import net._57block.bukkit.api.command.parameter.ParameterParser;
import net._57block.bukkit.api.util.SerializeUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BooleanParser extends ParameterParser {
    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        Boolean b = SerializeUtils.deserializeBoolean(args[index], false);
        if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
            parameters.push(b);
            return true;
        }
        return false;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        if (args.length - 1 == index) {
            ArrayList<String> list = new ArrayList<>();
            list.add("true");
            list.add("false");
            list.add("yes");
            list.add("no");
            list.add("on");
            list.add("off");
            list.add("1");
            list.add("0");
            return list;
        }
        return super.tabComplete(sender, command, label, args, location, index);
    }
}
