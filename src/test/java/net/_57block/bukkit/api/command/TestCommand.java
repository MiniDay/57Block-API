package net._57block.bukkit.api.command;

import net._57block.bukkit.api.command.annotation.Command;
import net._57block.bukkit.api.command.annotation.CommandExecutor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.Before;

@SuppressWarnings("unused")
@CommandExecutor(name = "essentials", permission = "user")
public class TestCommand {

    @Before
    public void before() {

    }

    @Command(subName = "tp", permission = "essentials.tp")
    public void tp(CommandSender sender, World world) {
        Player player = (Player) sender;
    }

    @Command(subName = "tp", permission = "essentials.tp")
    public void tp(CommandSender sender, World world, double x, double y, double z) {
        Location location = new Location(world, x, y, z);

    }

    @Command(subName = "tp", permission = "essentials.tp")
    public void tp(CommandSender sender, Player player) {
        ((Player) sender).teleport(player, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Command(subName = "tp", permission = "essentials.tp.other")
    public void tp(Player player, Location location) {
    }

    @Command(subName = "tp", permission = "essentials.tp.other")
    public void tp(Player player1, Player player2) {
        player1.teleport(player2);
    }

    @Command(subName = "sell", permission = "essentials.tp")
    public void sell(CommandSender sender) {
        sell(sender, 1);
    }

    @Command(subName = "sell", permission = "essentials.tp")
    public void sell(CommandSender sender, int count) {

    }
}
