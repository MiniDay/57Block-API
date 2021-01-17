package net.airgame.bukkit.api.command.parameter.parser.bukkit;

import net.airgame.bukkit.api.command.parameter.ParameterParser;
import net.airgame.bukkit.api.util.SerializeUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameModeParser extends ParameterParser {
    @Override
    public boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args, int index) {
        if (index + 1 > args.length) {
            return false;
        }
        GameMode gameMode = SerializeUtils.deserializeGameMode(args[index]);
        if (gameMode != null && getNext().parser(parameters, sender, command, label, args, index + 1)) {
            parameters.push(gameMode);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        ArrayList<String> list = new ArrayList<>();
        for (GameMode value : GameMode.values()) {
            list.add(value.name().toLowerCase());
        }
        return list;
    }
}
