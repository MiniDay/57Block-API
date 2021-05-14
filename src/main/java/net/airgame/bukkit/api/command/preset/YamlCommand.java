package net.airgame.bukkit.api.command.preset;

import net.airgame.bukkit.api.AirGamePlugin;
import net.airgame.bukkit.api.annotation.Command;
import net.airgame.bukkit.api.annotation.CommandExecutor;
import net.airgame.bukkit.api.annotation.Sender;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandExecutor(
        name = "yaml",
        aliases = "yml",
        permission = "airGame.admin"
)
@SuppressWarnings({"unused", "ConstantConditions"})
public class YamlCommand {

    @Command(priority = 9)
    public void yaml(@Sender Player player) {
        YamlConfiguration config = new YamlConfiguration();

        PlayerInventory inventory = player.getInventory();
        config.set("mainHand", inventory.getItemInMainHand());
        config.set("offHand", inventory.getItemInOffHand());
        config.set("location", player.getLocation());

        saveTestItem(config);

        try {
            config.save(new File(AirGamePlugin.getInstance().getDataFolder(), "test.yml"));
            player.sendMessage("§a物品已保存至 test.yml 中.");
        } catch (IOException e) {
            AirGamePlugin.getLogUtils().error(e, "保存测试 yaml 数据至文件中时发生了一个错误: ");
        }
    }

    @Command
    public void yaml(@Sender CommandSender sender) {
        YamlConfiguration config = new YamlConfiguration();

        saveTestItem(config);

        try {
            config.save(new File(AirGamePlugin.getInstance().getDataFolder(), "test.yml"));
            sender.sendMessage("§a物品已保存至 test.yml 中.");
        } catch (IOException e) {
            AirGamePlugin.getLogUtils().error(e, "保存测试 yaml 数据至文件中时发生了一个错误: ");
        }
    }

    @Command
    public void yaml(@Sender CommandSender sender, Player player) {
        YamlConfiguration config = new YamlConfiguration();

        PlayerInventory inventory = player.getInventory();
        config.set("mainHand", inventory.getItemInMainHand());
        config.set("offHand", inventory.getItemInOffHand());
        config.set("location", player.getLocation());

        saveTestItem(config);

        try {
            config.save(new File(AirGamePlugin.getInstance().getDataFolder(), "test.yml"));
            player.sendMessage("§a物品已保存至 test.yml 中.");
        } catch (IOException e) {
            AirGamePlugin.getLogUtils().error(e, "保存测试 yaml 数据至文件中时发生了一个错误: ");
        }
    }

    private void saveTestItem(ConfigurationSection config) {

        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("test lore");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        meta.setDisplayName("test item");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        try {
            meta.setCustomModelData(1);
        } catch (Exception | Error ignored) {
        }
        meta.setLocalizedName("LocalizedName");
        try {
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("test1", 10, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "test2", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        } catch (Exception | Error ignored) {
        }

        meta.addItemFlags(ItemFlag.values());
        stack.setItemMeta(meta);
        config.set("testItem", stack);
    }

}
