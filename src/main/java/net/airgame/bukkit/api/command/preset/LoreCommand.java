package net.airgame.bukkit.api.command.preset;

import net.airgame.bukkit.api.annotation.Command;
import net.airgame.bukkit.api.annotation.CommandExecutor;
import net.airgame.bukkit.api.annotation.Sender;
import net.airgame.bukkit.api.util.ItemUtils;
import net.airgame.bukkit.api.util.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@CommandExecutor(
        name = "lore",
        permission = "airGame.admin"
)
@SuppressWarnings("unused")
public class LoreCommand {

    @Command(subName = "name")
    public void name(@Sender Player player, String name) {
        PlayerInventory inventory = player.getInventory();
        ItemStack stack = inventory.getItemInMainHand();
        if (ItemUtils.isEmptyItemStack(stack)) {
            player.sendMessage("§c请先手持一个物品再使用这个命令!");
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            player.sendMessage("§c请先手持一个物品再使用这个命令!");
            return;
        }
        name = StringUtils.replaceColorCode(name);
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        player.sendMessage(String.format("§a物品已命名为 %s §a.", name));
    }

    @Command(subName = "add")
    public void add(@Sender Player player, String lore) {
        PlayerInventory inventory = player.getInventory();
        ItemStack stack = inventory.getItemInMainHand();
        if (ItemUtils.isEmptyItemStack(stack)) {
            player.sendMessage("§c请先手持一个物品再使用这个命令!");
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            player.sendMessage("§c请先手持一个物品再使用这个命令!");
            return;
        }
        lore = StringUtils.replaceColorCode(lore);
        List<String> list = meta.getLore();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(lore);
        meta.setLore(list);
        stack.setItemMeta(meta);
        player.sendMessage(String.format("§aLore %s 已添加至物品.", lore));
    }

    @Command(subName = "remove")
    public void remove(@Sender Player player, int line) {
        PlayerInventory inventory = player.getInventory();
        ItemStack stack = inventory.getItemInMainHand();
        if (ItemUtils.isEmptyItemStack(stack)) {
            player.sendMessage("§c请先手持一个物品再使用这个命令!");
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            player.sendMessage("§c请先手持一个物品再使用这个命令!");
            return;
        }
        List<String> list = meta.getLore();
        if (list == null) {
            player.sendMessage("§c该物品没有 Lore!");
            return;
        }
        if (list.size() < line - 1) {
            player.sendMessage(String.format("§c该物品没有第 %d 行 Lore ! (总共 %d 行)", line, list.size()));
            return;
        }
        String remove = list.remove(line - 1);
        meta.setLore(list);
        stack.setItemMeta(meta);
        player.sendMessage(String.format("§a已删除第 %d 行Lore: %s §a.", line, remove));
    }
}
