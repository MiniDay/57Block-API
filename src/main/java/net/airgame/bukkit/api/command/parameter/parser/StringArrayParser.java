package net.airgame.bukkit.api.command.parameter.parser;

import net.airgame.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Stack;

public class StringArrayParser extends ParameterParser {
    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        for (int i = args.length; i >= index; i--) {
            if (getNext().parser(parameters, sender, command, label, args, i)) {
                String[] array = Arrays.copyOfRange(args, index, i);
                parameters.push(array);
                return true;
            }
        }
        return false;
    }
}
