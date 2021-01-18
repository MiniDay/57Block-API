package net.airgame.bukkit.api.command.parameter.parser;

import net.airgame.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class IntegerParser extends ParameterParser {
    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        try {
            int i = Integer.parseInt(args[index]);
            if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
                parameters.push(i);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}