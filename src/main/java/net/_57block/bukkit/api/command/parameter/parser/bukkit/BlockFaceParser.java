package net._57block.bukkit.api.command.parameter.parser.bukkit;

import net._57block.bukkit.api.command.parameter.ParameterParser;
import net._57block.bukkit.api.util.SerializeUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BlockFaceParser extends ParameterParser {

    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        BlockFace face = SerializeUtils.deserializeBlockFace(args[index]);
        if (face == null) {
            return false;
        }
        if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
            parameters.push(face);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        ArrayList<String> list = new ArrayList<>();
        list.add("north");
        list.add("east");
        list.add("south");
        list.add("west");
        list.add("up");
        list.add("down");
        list.add("n");
        list.add("e");
        list.add("s");
        list.add("w");
        list.add("ne");
        list.add("nw");
        list.add("se");
        list.add("sw");
        list.add("self");
        return list;
    }
}
