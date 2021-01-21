package net.airgame.bukkit.api.debug;

import net.airgame.bukkit.api.PluginMain;
import net.airgame.bukkit.api.command.annotation.Command;
import net.airgame.bukkit.api.command.annotation.CommandExecutor;
import net.airgame.bukkit.api.command.annotation.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;

@CommandExecutor(
        name = "yaml",
        permission = "airGame.admin"
)
public class YamlCommand {

    @Command
    public void yaml(@Sender Player player) {
        PluginMain instance = PluginMain.getInstance();
        YamlConfiguration config = new YamlConfiguration();

        PlayerInventory inventory = player.getInventory();
        config.set("mainHand", inventory.getItemInMainHand());
        config.set("offHand", inventory.getItemInOffHand());
        config.set("location", player.getLocation());

        try {
            config.save(new File(instance.getDataFolder(), "test"));
        } catch (IOException e) {
            PluginMain.getLogUtils().error(e, "保存测试 yaml 数据至文件中时发生了一个错误: ");
        }
    }

    @Command
    public void yaml(@Sender CommandSender sender, Player player) {
        PluginMain instance = PluginMain.getInstance();
        YamlConfiguration config = new YamlConfiguration();

        PlayerInventory inventory = player.getInventory();
        config.set("mainHand", inventory.getItemInMainHand());
        config.set("offHand", inventory.getItemInOffHand());
        config.set("location", player.getLocation());

        try {
            config.save(new File(instance.getDataFolder(), "test"));
        } catch (IOException e) {
            PluginMain.getLogUtils().error(e, "保存测试 yaml 数据至文件中时发生了一个错误: ");
        }
    }
}
