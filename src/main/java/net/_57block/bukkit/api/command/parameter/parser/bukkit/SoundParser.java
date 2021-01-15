package net._57block.bukkit.api.command.parameter.parser.bukkit;

import net._57block.bukkit.api.command.parameter.ParameterParser;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class SoundParser extends ParameterParser {
    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        Sound sound = null;
        try {
            sound = Sound.valueOf(args[index].toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        if (sound == null) {
            return false;
        }
        if (getNext().parser(parameters, sender, command, label, args, index + 1)) {
            parameters.push(sound);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        return Arrays.stream(Sound.values())
                .map(Sound::name)
                .collect(Collectors.toList());
    }
}
