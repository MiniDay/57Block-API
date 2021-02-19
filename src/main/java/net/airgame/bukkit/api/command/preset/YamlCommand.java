package net.airgame.bukkit.api.command.preset;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.annotation.Command;
import net.airgame.bukkit.api.annotation.CommandExecutor;
import net.airgame.bukkit.api.annotation.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;

@CommandExecutor(
        name = "yaml",
        aliases = "yml",
        permission = "airGame.admin"
)
@SuppressWarnings("unused")
public class YamlCommand {

    @Command
    public void yaml(@Sender Player player) {
        AirGameAPI instance = AirGameAPI.getInstance();
        YamlConfiguration config = new YamlConfiguration();

        PlayerInventory inventory = player.getInventory();
        config.set("mainHand", inventory.getItemInMainHand());
        config.set("offHand", inventory.getItemInOffHand());
        config.set("location", player.getLocation());

        try {
            config.save(new File(instance.getDataFolder(), "test.yml"));
            player.sendMessage("§a物品已保存至 test.yml 中.");
        } catch (IOException e) {
            AirGameAPI.getLogUtils().error(e, "保存测试 yaml 数据至文件中时发生了一个错误: ");
        }
    }

    @Command
    public void yaml(@Sender CommandSender sender, Player player) {
        AirGameAPI instance = AirGameAPI.getInstance();
        YamlConfiguration config = new YamlConfiguration();

        PlayerInventory inventory = player.getInventory();
        config.set("mainHand", inventory.getItemInMainHand());
        config.set("offHand", inventory.getItemInOffHand());
        config.set("location", player.getLocation());

        try {
            config.save(new File(instance.getDataFolder(), "test.yml"));
            player.sendMessage("§a物品已保存至 test.yml 中.");
        } catch (IOException e) {
            AirGameAPI.getLogUtils().error(e, "保存测试 yaml 数据至文件中时发生了一个错误: ");
        }
    }
}
