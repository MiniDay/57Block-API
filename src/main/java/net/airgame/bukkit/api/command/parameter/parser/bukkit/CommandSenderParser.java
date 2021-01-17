package net.airgame.bukkit.api.command.parameter.parser.bukkit;

import net.airgame.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;

public class CommandSenderParser extends ParameterParser {
    private Class<?> senderType;

    public void setSenderType(Class<?> senderType) {
        this.senderType = senderType;
    }

    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int index) {
        if (!senderType.isInstance(sender)) {
            return false;
        }
        if (getNext().parser(parameters, sender, command, label, args, index)) {
            parameters.push(sender);
            return true;
        }
        return false;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        return getNext().tabComplete(sender, command, label, args, location, index);
    }

}
