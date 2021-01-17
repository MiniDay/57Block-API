package net.airgame.bukkit.api.command.parameter.parser;

import net.airgame.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;

/**
 * 无论如何都直接返回 true 的参数解析器
 */
public class EndParser extends ParameterParser {
    public static final EndParser INSTANCE = new EndParser();

    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
//        return true;
        return index == args.length;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        return null;
    }
}
