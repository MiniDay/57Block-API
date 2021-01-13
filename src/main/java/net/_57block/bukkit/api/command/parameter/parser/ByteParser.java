package net._57block.bukkit.api.command.parameter.parser;

import net._57block.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class ByteParser extends ParameterParser {
    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        try {
            byte b = Byte.parseByte(args[index]);
            if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
                parameters.push(b);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
